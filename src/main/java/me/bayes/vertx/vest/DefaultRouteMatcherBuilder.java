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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import me.bayes.vertx.vest.binding.DefaultRouteBindingHolderFactory;
import me.bayes.vertx.vest.binding.Function;
import me.bayes.vertx.vest.binding.RouteBindingHolder.MethodBinding;
import me.bayes.vertx.vest.util.DefaultParameterResolver;
import me.bayes.vertx.vest.util.ParameterResolver;
import me.bayes.vertx.vest.util.UriPathUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;

/**
 * <pre>
 * The {@link DefaultRouteMatcherBuilder} is a basic {@link RouteMatcherBuilder} that
 * uses the {@link Application} to get classes that are candidates for adding to
 * the {@link RouteMatcher}.
 * 
 * This simple logic searches the given classes for the {@link Path} annotation
 * and uses that with the combination of the {@link Path} anotation on method level
 * to create the route that the method will handle. Once the path is established
 * a {@link Handler} is added to the route matcher for the specific path. The method
 * associated with the {@link Handler} is derived from either one of the {@link GET},
 * {@link POST}, {@link PUT}, {@link DELETE}, {@link OPTIONS} xor {@link HEAD} annotations
 * on the method.
 * 
 * Spec: 3.3.5 HEAD and OPTIONS is only supported for explicit request
 *
 * TODO: {@link Context} annotation support.
 * TODO: {@link Consumes}
 * TODO: {@link Produces}
 * TODO: Add verticle reference injection through {@link Context} annotation.
 * TODO: Add default behaviour for HEAD and OPTIONS according to 3.3.5.
 * TODO: Handle exceptions better. 3.3.4 Exceptions
 * TODO: 3.8 Determining the MediaType of Responses
 * TODO: Allow multiple endpoint implementation to have the same uri but different consumes and produces parameters.
 * </pre>
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 *
 */
public class DefaultRouteMatcherBuilder extends AbstractRouteMatcherBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRouteMatcherBuilder.class);
	
	private final ParameterResolver parameterResolver;
	
	/**
	 * Requires a {@link VestContext}.
	 * @param context
	 */
	public DefaultRouteMatcherBuilder(final VestApplication application) {
		super(application, new DefaultRouteBindingHolderFactory(application));
		this.parameterResolver = new DefaultParameterResolver(application);
	}

	
	/*
	 * (non-Javadoc)
	 * @see me.bayes.vertx.extension.RouteMatcherBuilder#build(me.bayes.vertx.extension.BuilderContext)
	 */
	protected RouteMatcher buildInternal() throws Exception {

		addRoutes(routeMatcher);
		
		//loop through classes and add then to the route matcher
		
		//TODO: Iterate over holder and add routes.
//		for(Class<?> clazz : classes) {
//			addRoutes(routeMatcher, clazz, applicationContextPath);
//		}
		
		return routeMatcher;
	}
	/**
	 * The simplest case adds routes and delegates the execution to the method annotated with the {@link Path} annotation. 
	 * 
	 * @param routeMatcher
	 * @throws Exception
	 */
	private void addRoutes(final RouteMatcher routeMatcher) throws Exception {
		this.bindingHolder.foreach(new Function() {
			public void apply(final String method, final String key, final List<MethodBinding> bindings) throws Exception {
				
				//3.7 Matching Requests to Resource Methods is delegated to vertx route matcher.
				final Method routeMatcherMethod = RouteMatcher.class.getMethod(
							method.toLowerCase(), 
							String.class,
							Handler.class);
				
				final String finalPath = UriPathUtil.convertPath(key);
				
				routeMatcherMethod.invoke(routeMatcher, finalPath,
						new Handler<HttpServerRequest>() {
					
					private List<MethodBinding> delegates;
					
					{
						delegates = bindings;
					}
					
					public void handle(HttpServerRequest request) {
						
						try {
						
							MethodBinding binding = null;
							Method method = null;
							
							String acceptsHeader = request.headers().get(HttpHeaders.ACCEPT);
							String contentTypeHeader = request.headers().get(HttpHeaders.CONTENT_TYPE);
							
							if(!request.headers().isEmpty()) {
								for(MethodBinding binding_ : bindings) {
									if(binding_.hasConsumes(contentTypeHeader) && binding_.hasProduces(acceptsHeader)) {
										binding = binding_;
										break;
									}
									if(binding_.hasConsumes(contentTypeHeader) && acceptsHeader == null) {
										binding = binding_;
										break;
									}
									if(contentTypeHeader == null && binding_.hasProduces(acceptsHeader)) {
										binding = binding_;
										break;
									}
								}
								
							}
								
							if(binding == null && delegates.size() > 0) {
								binding = delegates.get(0);
							} else if(binding == null){
								throw new Exception("No route that supports accepts given HTTP parameters");
							}
							
							method = binding.getMethod();
							
							final Class<?>[] parameterTypes = method.getParameterTypes();
							if(parameterTypes.length == 0 || !parameterTypes[0].equals(HttpServerRequest.class)) {
								LOG.warn("Classes marked with a HttpMethod must have at least one parameter. The first parameter should be HttpServerRequest.");
								return;
							}
							
							final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
							final String[] produces = binding.getProduces();
							final String[] consumes = binding.getConsumes();
							String producesMediaType = null;
							
							if(acceptsHeader != null) {
								if(produces != null && produces.length > 0) {
									for(String type : produces) {
										if(acceptsHeader.contains(type)) {
											producesMediaType = type;
											break;
										}
									}
								}
							}
							
							if(producesMediaType == null) {
								producesMediaType = 
										(produces != null && produces.length == 1) ? 
												produces[0] : MediaType.TEXT_PLAIN;
							} 
							
							if(contentTypeHeader != null) {
								if(consumes != null && consumes.length > 0) {
									for(String type : consumes) {
										if(acceptsHeader.contains(type)) {
											contentTypeHeader = type;
											break;
										}
									}
								}
							}
							
							request.response().headers().set(HttpHeaders.CONTENT_TYPE, producesMediaType);
							
							final Object[] parameters = new Object[parameterTypes.length];
							parameters[0] = request;
							
							boolean isBodyResolved = false;
							int objectParameterIndex = -1;
							
							if(parameters.length > 1) {
								for(int i = 1; i < parameters.length; i++) {
									if(parameterAnnotations[i].length > 0 || 
											HttpServerResponse.class.equals(parameterTypes[i])) {
										
										parameters[i] = parameterResolver.resolve(method, parameterTypes[i], parameterAnnotations[i], request);
										
									} else if(JsonObject.class.equals(parameterTypes[i])) {
										objectParameterIndex  = i;
										isBodyResolved = true;
									}
								}
							}
							
							if(isBodyResolved) {
								resolvedBodyDelegate(binding.getDelegate(), request, objectParameterIndex, method, parameters);
							} else {
								delegate(binding.getDelegate(), method, parameters);
							}
						
						} catch (Exception e) {
							LOG.error("Exception occurred.", e);
							
							if(exceptionHandler != null) {
								exceptionHandler.handle(request);
							} else {
								request.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
								request.response().setStatusCode(500);
								request.response().setStatusMessage("Internal server error");
								request.response().end();
							}
						}
						
								
					}
					
					private void delegate(Object delegate, Method method, Object[] parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
						method.invoke(delegate, parameters);
					}
					
					private void resolvedBodyDelegate(final Object delegate, final HttpServerRequest request, final int objectParameterIndex, final Method method, final Object[] parameters) {
						
						final Buffer buffer = new Buffer();
						
						request.dataHandler(new Handler<Buffer>() {

							@Override
							public void handle(Buffer internalBuffer) {
								buffer.appendBuffer(internalBuffer);
							}
							
						});
						
						request.endHandler(new Handler<Void>() {

							@Override
							public void handle(Void event) {
								parameters[objectParameterIndex] = new JsonObject(buffer.toString());
								try {
									method.invoke(delegate, parameters);
								} catch (Exception e) {
									LOG.error("Exception occurred.", e);
									
									if(exceptionHandler != null) {
										exceptionHandler.handle(request);
									} else {
										request.response().setStatusCode(500);
										request.response().setStatusMessage("Internal server error");
										request.response().end();
									}
								}
							}
						});
					}
					
				});
				
			}
		});
	}

}