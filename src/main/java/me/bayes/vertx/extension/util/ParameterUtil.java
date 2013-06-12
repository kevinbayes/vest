/**
 * 
 */
package me.bayes.vertx.extension.util;

import java.lang.annotation.Annotation;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;

import org.vertx.java.core.http.HttpServerRequest;

/**
 * A util to inject parameters into the methods handling REST requests.
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public final class ParameterUtil {
	
	private ParameterUtil() { }
	
	
	/**
	 * Using the context we try resolve a object that can be used for this parameter type.
	 * 
	 * @param parameterType that needs to be resolved
	 * @param annotations of the parameter
	 * @param request from vertx
	 * @return object to populate
	 */
	public static Object resolveParameter(final Class<?> parameterType, final Annotation[] annotations, final HttpServerRequest request) {
		
		Object returnObject = null;
		
		for(Annotation annotation : annotations) {
			
			if(annotation.annotationType().equals(PathParam.class)) {
				PathParam pathParam = (PathParam) annotation;
				returnObject = resolvePathParameter(pathParam.value(), request);
			} else if(annotation.annotationType().equals(DefaultValue.class)) {
				DefaultValue defaultValue = (DefaultValue) annotation;
				returnObject = defaultValue.value();
			}
			
			if(returnObject != null) {
				break;
			}
		}
		
		
		return returnObject;
	}
	
	private static Object resolvePathParameter(final String key, final HttpServerRequest request) {
		return request.params().get(key);
	}

}
