/**
 * Copyright 2014 Bayes Technologies
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
package refer.api.jaxrs.vest.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refer.api.jaxrs.vest.VestApplication;
import refer.api.jaxrs.vest.util.ContextUtil;
import refer.api.jaxrs.vest.util.HttpUtils;
import refer.api.jaxrs.vest.util.UriPathUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Kevin Bayes
 * @since 1.1
 * @version 1.1
 */
public class DefaultRouteBindingHolderFactory implements
		RouteBindingHolderFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRouteBindingHolderFactory.class);
	
	private VestApplication application;
	private RouteBindingHolder bindingHolder;
	 
	public DefaultRouteBindingHolderFactory(VestApplication application) {
		super();
		this.application = application;
		this.bindingHolder = new RouteBindingHolder();
	}

	/* (non-Javadoc)
	 * @see me.bayes.vertx.vest.binding.RouteBindingHolderFactory#build()
	 */
	public RouteBindingHolder build() throws Exception {
		
		this.bindingHolder = new RouteBindingHolder();
		
		final Set<Class<?>> classes = application.getClasses();
		
		final String applicationContextPath = UriPathUtil.getApplicationContext(application);
		
		//loop through classes and add then to the route matcher
		for(Class<?> clazz : classes) {
			addClassBindings(clazz, applicationContextPath);
		}
		
		return bindingHolder;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param contextPath
	 * @throws Exception
	 */
	private void addClassBindings(final Class<?> clazz, String contextPath) throws Exception {
		
		final Path pathAnnotation = clazz.getAnnotation(Path.class);
		
		if(pathAnnotation == null) {
			return;
		}
		
		Object instance = clazz.getConstructor().newInstance();
		
		ContextUtil.assignContextFields(clazz, instance, application);
		
		
		for(Method method : clazz.getMethods()) {
			
			if(!method.getReturnType().equals(Void.TYPE)) { 
				//Carry on if return type is not void as we are interested in async.
				//3.3.3	Return Type
				continue;
			}
			
			addMethodBindings(clazz, 
					instance,
					UriPathUtil.concatPaths(contextPath, pathAnnotation.value()), 
					method);
		}
		
	}
	
	/**
	 * 
	 * @param clazz
	 * @param instance 
	 * @param path
	 * @param method
	 * @throws Exception
	 */
	private void addMethodBindings(Class<?> clazz, final Object instance, String path, Method method) throws Exception {
		
		final Path pathAnnotation = method.getAnnotation(Path.class);
		final HttpMethod httpMethod = HttpUtils.resolveHttpType(method);
		
		if(httpMethod == null) {
			return;
		}
		
		//3.3.1	Visibility Only public methods may be exposed as resource methods.
		if(method.getModifiers() != Method.PUBLIC) {
			LOG.warn("Method {} is not public and is annotated with @Path.", method.getName());
		}
		
		addBinding(clazz, method, instance, httpMethod, 
				UriPathUtil.concatPaths(path, 
						(pathAnnotation == null) ? "" : pathAnnotation.value()));
	}
	
	
	/**
	 * The simplest case adds routes and delegates the execution to the method annotated with the {@link javax.ws.rs.Path} annotation.
	 * 
	 * @param clazz
	 * @param method
	 * @param httpMethod
	 * @param path
	 * @throws Exception
	 */
	private void addBinding(final Class<?> clazz, final Method method, final Object instance, final HttpMethod httpMethod, String path) throws Exception {

		final Produces produces = method.getAnnotation(Produces.class);
		final Consumes consumes = method.getAnnotation(Consumes.class);
		
		final String finalPath = UriPathUtil.convertPath(path);
		
		bindingHolder.addBinding(httpMethod, finalPath, consumes, produces, instance, clazz, method);
	}
	

}
