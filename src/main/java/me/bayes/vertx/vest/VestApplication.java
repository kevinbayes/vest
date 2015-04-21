/**
 * Copyright 2013 Bayes Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.bayes.vertx.vest;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Priority;
import javax.tools.JavaFileObject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import me.bayes.vertx.vest.deploy.RootContextVestApplication;

import org.apache.commons.lang3.ObjectUtils;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.impl.java.PackageHelper;

import com.github.drapostolos.typeparser.TypeParser;


/**
 * <pre>
 * An abstract implementation of a jaxrs {@link Application} for the Vest framework.
 * 
 * This class should be extended by any application using vest inorder to set the 
 * {@link ApplicationPath} annotation for the starting context of your application.
 * Example can be seen in the {@link RootContextVestApplication} where the {@link ApplicationPath}
 * annotation is used to ensure that the rest service is located on '/'.
 * </pre>
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public abstract class VestApplication extends Application {

	private static final Integer DEFAULT_PROVIDER_PRIORITY = 2000;

	private final static Logger LOG = LoggerFactory
			.getLogger(VestApplication.class);

	public VestApplication() {
		 TypeParser typeParser = TypeParser
		 .newBuilder()
		 .unregisterParser(Object.class)
		 .unregisterParser(File.class)
		 .build();
		 singletons.add(typeParser);
	}

	/*
	 * Classloader to load scanned classes.
	 */
	private ClassLoader classLoader = Thread.currentThread()
			.getContextClassLoader();

	/*
	 * Used if you want to scan packages for all classes annotated with
	 * 
	 * @Path.
	 */
	private Set<String> packagesToScan = new HashSet<String>(0);

	/*
	 * Used to specify the classes annotated with @Path.
	 */
	private final Set<Class<?>> endpointClasses = new HashSet<Class<?>>(0);

	/*
	 * Store all the singleton instances.
	 */
	private final Set<Object> singletons = new HashSet<Object>(0);

	/*
	 * Map of shared properties
	 */
	private final Map<String, Object> properties = new HashMap<String, Object>(
			0);

	/*
	 * Used at initialization time to store provider classes.
	 */
	private SortedMap<Integer, List<Class<?>>> providerClasses = new TreeMap<>();
	/*
	 * Used to 
	 */
	private Map<Class<?>, List<?>> providersCache = new HashMap<>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		return endpointClasses;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	public <T> T getSingleton(Class<? extends T> type) {
		for (Object object : singletons) {
			if (type.isInstance(object)) {
				return (T) object;
			}
		}

		return null;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	public Set<String> getPackagesToScan() {
		return packagesToScan;
	}

	/**
	 * @param packagesToScan
	 *            for classes annotated with {@link Path}.
	 */
	public void addPackagesToScan(Collection<String> packagesToScan) {
		this.packagesToScan.addAll(packagesToScan);
		addScannedClasses();
	}

	public void addPackagesToScan(String... packagesToScan) {
		this.packagesToScan.addAll(Arrays.asList(packagesToScan));
		addScannedClasses();
	}

	public Set<Class<?>> getEndpointClasses() {
		return endpointClasses;
	}

	/**
	 * @param endpointClasses
	 *            to add to the classes annotated with {@link Path}. If not
	 *            annotated with {@link Path} it will be ignored.
	 */
	public void addEndpointClasses(Collection<Class<?>> endpointClasses) {
		for (Class<?> clazz : endpointClasses) {
			if (clazz.getAnnotation(Path.class) != null) {
				this.endpointClasses.add(clazz);
			}
		}
	}

	public void addEndpointClasses(Class<?>... endpointClasses) {
		for (Class<?> clazz : endpointClasses) {
			if (clazz.getAnnotation(Path.class) != null) {
				this.endpointClasses.add(clazz);
			}
		}
	}

	/*
	 * Use vertx's built in PackageHelper to scan for classes in the classpath.
	 * Sub packages are left out for now.
	 * 
	 * TODO: Think about rather using Reflections jar, but this adds a
	 * dependency.
	 */
	private void addScannedClasses() {

		PackageHelper helper = new PackageHelper(classLoader);

		for (String packageName : packagesToScan) {

			final String folderLocationName = packageName
					.replaceAll("[.]", "/");

			try {

				for (JavaFileObject javaObject : helper.find(packageName)) {

					try {

						final String clazzUriString = javaObject.toUri()
								.toString();
						final String clazzName = clazzUriString.substring(
								clazzUriString.lastIndexOf(folderLocationName),
								clazzUriString.lastIndexOf('.')).replaceAll(
								"/", ".");
						final Class<?> clazz = classLoader.loadClass(clazzName);

						final Annotation[] annotations = clazz.getAnnotations();
						Integer priority = DEFAULT_PROVIDER_PRIORITY;
						Class<?> provider = null;
						for (Annotation annotation : annotations) {
							if (annotation.annotationType().equals(Path.class)) {
								this.endpointClasses.add(clazz);
								break;
							} else if (annotation.annotationType().equals(
									Provider.class)) {
								provider = clazz;
							} else if (annotation.annotationType().equals(
									Priority.class)) {
								priority = ((Priority) annotation).value();
							}
						}
						if (provider != null) {
							addProvider(priority, provider);
						}
					} catch (ClassNotFoundException e) {
						LOG.error(String.format(
								"Error occurred scanning package %s.",
								packageName), e);
					}
				}

			} catch (IOException e) {
				LOG.error(String.format("Error occurred scanning package %s.",
						packageName), e);
			}
		}

	}

	/**
	 * 
	 * @param priority
	 *            - provider priority - will be set to DEFAULT_PROVIDER_PRIORITY
	 *            if not set.
	 * @param providerClass
	 */
	private void addProvider(Integer priority, Class<?> providerClass) {
		priority = (Integer) ObjectUtils.defaultIfNull(priority,
				DEFAULT_PROVIDER_PRIORITY);
		List<Class<?>> classes = providerClasses.get(priority);
		LOG.info("Registered provider: " + providerClass + " with priority: " + priority);
		if (classes != null) {
			LOG.warn("Already registered provider with priority: " + priority);
			classes.add(providerClass);
		} else {
			classes = new ArrayList<Class<?>>(1);
			classes.add(providerClass);
			providerClasses.put(priority, classes);
		}
	}
	
	/**
	 * 
	 * @param <T>
	 * @param clazz
	 *            - class or superclass of provider class.
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public <T> List<T> getProviders(Class<T> clazz) {
		if (!providersCache.containsKey(clazz)) {
			List<T> matched = new ArrayList<>();
			for (Entry<Integer, List<Class<?>>> providersEntry : providerClasses.entrySet()) {
				for (Class<?> providerClass : providersEntry.getValue()) {
					if (clazz.isAssignableFrom(providerClass)) {
						try {
							@SuppressWarnings("unchecked")
							T newInstance = (T)providerClass.newInstance();
							matched.add(newInstance);
						} catch (InstantiationException | IllegalAccessException e) {
							throw new IllegalArgumentException("Chech if Provider class " + providerClass + " has public nullary constructor.", e);
						}
					}
				}
			}
			providersCache.put(clazz, matched);
		}
		@SuppressWarnings("unchecked")
		List<T> providers = (List<T>) providersCache.get(clazz);
		return providers;
	}

	/**
	 * TODO: WARN on same type insertions.
	 * 
	 * @param singleton
	 */
	public void addSingleton(Object... singleton) {
		for (Object obj : singleton) {

			if (this.singletons.contains(obj)) {
				LOG.warn("Object " + obj + " already exists.");
			}

			this.singletons.add(obj);
		}
	}

	/**
	 * Add properties to the shared list.
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, Object value) {
		if (this.properties.containsKey(key)) {
			LOG.warn("Property " + key + " already exists.");
		}

		this.properties.put(key, value);
	}

	public Application getPropertyValue(String key, Class<Application> clazz) {
		return clazz.cast(properties.get(key));
	}

}
