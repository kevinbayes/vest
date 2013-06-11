package me.bayes.vertx.extension.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import me.bayes.vertx.extension.AbstractRouteMatcherBuilder;
import me.bayes.vertx.extension.BuilderContext;
import me.bayes.vertx.extension.RouteMatcherBuilder;
import me.bayes.vertx.extension.util.ContextUtil;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

/**
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
 * TODO: {@link PathParam} annotation support.
 * TODO: {@link Context} annotation support.
 * TODO: {@link Consumes}
 * TODO: {@link Produces}
 * TODO: Add verticle reference injection through {@link Context} annotation.
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 *
 */
public class JaxrsRouteMatcherBuilder extends AbstractRouteMatcherBuilder {

	/**
	 * Requires a {@link BuilderContext}.
	 * @param context
	 */
	public JaxrsRouteMatcherBuilder(final BuilderContext context) {
		super(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see me.bayes.vertx.extension.RouteMatcherBuilder#build(me.bayes.vertx.extension.BuilderContext)
	 */
	protected RouteMatcher buildInternal() throws Exception {
		
		final Application jaxrsApplication = context.getPropertyValue(BuilderContextProperty.JAXRS_APPLICATION, Application.class);
		
		final Set<Class<?>> classes = jaxrsApplication.getClasses();
		
		//loop through classes and add then to the route matcher
		for(Class<?> clazz : classes) {
			addClassRoutes(routeMatcher, clazz);
		}
		
		return routeMatcher;
	}
	
	/**
	 * 
	 * @param routeMatcher
	 * @param clazz
	 * @throws Exception
	 */
	private void addClassRoutes(final RouteMatcher routeMatcher, final Class<?> clazz) throws Exception {
		
		final Path pathAnnotation = clazz.getAnnotation(Path.class);
		
		if(pathAnnotation == null) {
			return;
		}
		
		for(Method method : clazz.getMethods()) {
			addMethodRoutes(routeMatcher, clazz, pathAnnotation.value(), method);
		}
		
	}
	
	
	/**
	 * 
	 * @param routeMatcher
	 * @param clazz
	 * @param path
	 * @param method
	 * @throws Exception
	 */
	private void addMethodRoutes(final RouteMatcher routeMatcher, Class<?> clazz, String path, Method method) throws Exception {
		
		final Path pathAnnotation = method.getAnnotation(Path.class);
		
		path += (pathAnnotation == null) ? "" : pathAnnotation.value();
		
		final HttpMethod httpMethod = resolveHttpType(method);
		
		if(httpMethod == null) {
			return;
		}
		
		addRoute(routeMatcher, clazz, method, httpMethod, path);
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
	private void addRoute(final RouteMatcher routeMatcher, final Class<?> clazz, final Method method, final HttpMethod httpMethod, String path) throws Exception {

		final Method routeMatcherMethod = RouteMatcher.class.getMethod(
				httpMethod.value().toLowerCase(), 
				String.class,
				Handler.class);
		
		routeMatcherMethod.invoke(routeMatcher, JaxrsToVertxPathConverter.convertPath(path.replaceAll("//", "/")), //crude way to remove double "//"
				new Handler<HttpServerRequest>() {
		
			private final Object delegate;
			
			{
				delegate = clazz.getConstructor().newInstance();
				
				ContextUtil.assignContextFields(clazz, delegate, context);
			}
			
			public void handle(HttpServerRequest request) {
				try {
					
					method.invoke(delegate, request);
						
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
			
	}
	
}
