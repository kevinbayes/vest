package me.bayes.vertx.extension.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import me.bayes.vertx.extension.BuilderContext;
import me.bayes.vertx.extension.RouteMatcherBuilder;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

public class SimpleRouteMatcherBuilder implements RouteMatcherBuilder {

	public RouteMatcher build(BuilderContext context) throws Exception {
		
		final RouteMatcher routeMatcher = new RouteMatcher();
		
		Application jaxrsApplication = context.getPropertyValue(BuilderContext.JAXRS_APPLICATION, Application.class);
		
		final Set<Class<?>> classes = jaxrsApplication.getClasses();
		
		for(Class<?> clazz : classes) {
			addClassRoutes(routeMatcher, clazz);
		}
		
		return routeMatcher;
	}
	
	private void addClassRoutes(final RouteMatcher routeMatcher, final Class<?> clazz) throws Exception {
		
		final Path pathAnnotation = clazz.getAnnotation(Path.class);
		
		final String path = 
				(pathAnnotation == null) ? "" : pathAnnotation.value();
		
		for(Method method : clazz.getMethods()) {
			addMethodRoutes(routeMatcher, clazz, path, method);
		}
		
	}
	
	private void addMethodRoutes(final RouteMatcher routeMatcher, Class<?> clazz, String path, Method method) throws Exception {
		
		final Path pathAnnotation = method.getAnnotation(Path.class);
		path += (pathAnnotation == null) ? "" : pathAnnotation.value();
		
		
		
		HttpMethod httpMethod = resolveHttpType(method);
		
		if(httpMethod == null) {
			return;
		}
		
		addRoute(routeMatcher, clazz, method, httpMethod, path);
	}
	
	private HttpMethod resolveHttpType(Method method) {
		for(Annotation annotation : method.getDeclaredAnnotations()) {
			final HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			if(httpMethod != null) {
				return httpMethod;
			}
		}
		return null;
	}
	
	
	private void addRoute(final RouteMatcher routeMatcher, final Class<?> clazz, final Method method, final HttpMethod httpMethod, String path) throws Exception {

		final Method routeMatcherMethod = RouteMatcher.class.getMethod(
				httpMethod.value().toLowerCase(), 
				String.class,
				Handler.class);
		
		routeMatcherMethod.invoke(routeMatcher, path, new Handler<HttpServerRequest>() {
		
			Object delegate = clazz.getConstructor().newInstance();
			
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
