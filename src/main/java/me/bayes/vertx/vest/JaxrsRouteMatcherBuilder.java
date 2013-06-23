package me.bayes.vertx.vest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import me.bayes.vertx.vest.util.ContextUtil;
import me.bayes.vertx.vest.util.ParameterUtil;
import me.bayes.vertx.vest.util.UriPathUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

/**
 * <pre>
 * The {@link JaxrsRouteMatcherBuilder} is a basic {@link RouteMatcherBuilder} that
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
 * </pre>
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 *
 */
public class JaxrsRouteMatcherBuilder extends AbstractRouteMatcherBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(JaxrsRouteMatcherBuilder.class);

	/**
	 * Requires a {@link VestContext}.
	 * @param context
	 */
	public JaxrsRouteMatcherBuilder(final VestApplication application) {
		super(application);
	}
	
	/*
	 * (non-Javadoc)
	 * @see me.bayes.vertx.extension.RouteMatcherBuilder#build(me.bayes.vertx.extension.BuilderContext)
	 */
	protected RouteMatcher buildInternal() throws Exception {
		
		final Set<Class<?>> classes = application.getClasses();
		
		final String applicationContextPath = UriPathUtil.getApplicationContext(application);
		
		//loop through classes and add then to the route matcher
		for(Class<?> clazz : classes) {
			addClassRoutes(routeMatcher, clazz, applicationContextPath);
		}
		
		return routeMatcher;
	}
	
	/**
	 * 
	 * @param routeMatcher
	 * @param clazz
	 * @throws Exception
	 */
	private void addClassRoutes(final RouteMatcher routeMatcher, final Class<?> clazz, String contextPath) throws Exception {
		
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
			
			addMethodRoutes(routeMatcher, 
					clazz, 
					instance,
					UriPathUtil.concatPaths(contextPath, pathAnnotation.value()), 
					method);
		}
		
	}
	
	
	/**
	 * 
	 * @param routeMatcher
	 * @param clazz
	 * @param instance 
	 * @param path
	 * @param method
	 * @throws Exception
	 */
	private void addMethodRoutes(final RouteMatcher routeMatcher, Class<?> clazz, final Object instance, String path, Method method) throws Exception {
		
		final Path pathAnnotation = method.getAnnotation(Path.class);
		final HttpMethod httpMethod = resolveHttpType(method);
		
		if(httpMethod == null) {
			return;
		}
		
		//3.3.1	Visibility Only public methods may be exposed as resource methods.
		if(method.getModifiers() != Method.PUBLIC) {
			LOG.warn("Method {} is not public and is annotated with @Path.", method.getName());
		}
		
		addRoute(routeMatcher, clazz, method, instance, httpMethod, 
				UriPathUtil.concatPaths(path, 
						(pathAnnotation == null) ? "" : pathAnnotation.value()));
	}
	
	
	/**
	 * Look for the HTTP verb which should be {@link GET}, {@link POST}, {@link PUT}, {@link DELETE}, {@link OPTIONS} or {@link HEAD}.
	 * 
	 * @param method - that potentially has an annotation.
	 * @return {@link HttpMethod} or null.
	 */
	private HttpMethod resolveHttpType(Method method) {
		for(Annotation annotation : method.getDeclaredAnnotations()) {
			final HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			if(httpMethod != null) {
				return httpMethod;
			}
		}
		return null;
	}
	
	
	
	/**
	 * The simplest case adds routes and delegates the execution to the method annotated with the {@link Path} annotation. 
	 * 
	 * @param routeMatcher
	 * @param clazz
	 * @param method
	 * @param httpMethod
	 * @param path
	 * @throws Exception
	 */
	private void addRoute(final RouteMatcher routeMatcher, final Class<?> clazz, final Method method, final Object instance, final HttpMethod httpMethod, String path) throws Exception {

		//3.7 Matching Requests to Resource Methods is delegated to vertx route matcher.
		final Method routeMatcherMethod = RouteMatcher.class.getMethod(
				httpMethod.value().toLowerCase(), 
				String.class,
				Handler.class);

		//If the method does not have the right signature then just warn the user and return.
		final Class<?>[] parameterTypes = method.getParameterTypes();
		if(parameterTypes.length == 0 || !parameterTypes[0].equals(HttpServerRequest.class)) {
			LOG.warn("Classes marked with a HttpMethod must have at least one parameter. The first parameter should be HttpServerRequest.");
			return;
		}
		
		final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		
		final String finalPath = JaxrsToVertxPathConverter.convertPath(path);
		
		routeMatcherMethod.invoke(routeMatcher, finalPath,
				new Handler<HttpServerRequest>() {
		
			private final Object delegate;
			
			{
				this.delegate = instance;
			}
			
			public void handle(final HttpServerRequest request) {
				try {
					
					final Object[] parameters = new Object[parameterTypes.length];
					parameters[0] = request;
						
					for(int i = 1; i < parameters.length; i++) {
						if(parameterAnnotations[i].length > 0) {
							parameters[i] = ParameterUtil.resolveParameter(parameterTypes[i], parameterAnnotations[i], request);
						}
					}
					
					method.invoke(delegate, parameters);
						
				} catch (Exception e) {
					//3.3.4	Exceptions TODO: Handle exception better.
					LOG.error("Exception occurred.", e);
					request.response.statusCode = 500;
					request.response.statusMessage = "Internal server error";
					request.response.end(e.getMessage());
				}
			}
			
		});
			
	}

}