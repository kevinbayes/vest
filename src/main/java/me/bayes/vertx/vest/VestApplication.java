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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaFileObject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import me.bayes.vertx.vest.deploy.RootContextVestApplication;

import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.impl.java.PackageHelper;

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
	
	private final static Logger LOG = LoggerFactory.getLogger(VestApplication.class);
	
	/*
	 * Classloader to load scanned classes.
	 */
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	/*
	 * Used if you want to scan packages for all classes annotated with
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
	private final Map<String, Object> properties = new HashMap<String, Object>(0);
	
	/*
	 * (non-Javadoc)
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
	
	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	
	public Set<String> getPackagesToScan() {
		return packagesToScan;
	}

	
	/**
	 * @param packagesToScan for classes annotated with {@link Path}. 
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
	 * @param endpointClasses to add to the classes annotated with {@link Path}. 
	 * 		  If not annotated with {@link Path} it will be ignored.
	 */
	public void addEndpointClasses(Collection<Class<?>> endpointClasses) {
		for(Class<?> clazz : endpointClasses) {
			if(clazz.getAnnotation(Path.class) != null) {
				this.endpointClasses.add(clazz);
			}
		}
	}
	
	public void addEndpointClasses(Class<?>... endpointClasses) {
		for(Class<?> clazz : endpointClasses) {
			if(clazz.getAnnotation(Path.class) != null) {
				this.endpointClasses.add(clazz);
			}
		}
	}

	/*
	 * Use vertx's built in PackageHelper to scan for classes in the classpath. Sub packages are left out for now.
	 * 
	 * TODO: Think about rather using Reflections jar, but this adds a dependency.
	 */
	private void addScannedClasses() {
		
		PackageHelper helper = new PackageHelper(classLoader);

		for(String packageName : packagesToScan) {
			
			final String folderLocationName = packageName.replaceAll("[.]", "/");
			
			try {
				
				for(JavaFileObject javaObject : helper.find(packageName)) {
					
					try {
						
						final String clazzUriString = javaObject.toUri().toString();
						final String clazzName = clazzUriString.substring(
								clazzUriString.lastIndexOf(folderLocationName),
								clazzUriString.lastIndexOf('.'))
									.replaceAll("/", ".");
						final Class<?> clazz = classLoader.loadClass(clazzName);
						
						final Annotation[] annotations = clazz.getAnnotations();
						for(Annotation annotation : annotations) {
							if(annotation.annotationType().equals(Path.class)) {
								this.endpointClasses.add(clazz);
								break;
							}
						}
						
					} catch (ClassNotFoundException e) {
						LOG.error(String.format("Error occurred scanning package %s.", packageName), e);
					}
				}
				
			} catch (IOException e) {
				LOG.error(String.format("Error occurred scanning package %s.", packageName), e);
			}
		}

	}
	
	/**
	 * Add singleton classes to access at a later stage
	 * @param singleton
	 */
	public void addSingleton(Object... singleton) {
		for(Object obj : singleton) {
			
			if(this.singletons.contains(obj)) {
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
		if(this.properties.containsKey(key)) {
			LOG.warn("Property " + key + " already exists.");
		}
		
		this.properties.put(key, value);
	}

	public Application getPropertyValue(String key, Class<Application> clazz) {
		return clazz.cast(properties.get(key));
	}

}
