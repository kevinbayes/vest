/**
 * 
 */
package me.bayes.vertx.vest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * @author kevinbayes
 *
 */
public final class HttpUtils {
	
	/**
	 * Look for the HTTP verb which should be {@link GET}, {@link POST}, {@link PUT}, {@link DELETE}, {@link OPTIONS} or {@link HEAD}.
	 * 
	 * @param method - that potentially has an annotation.
	 * @return {@link HttpMethod} or null.
	 */
	public static HttpMethod resolveHttpType(Method method) {
		for(Annotation annotation : method.getDeclaredAnnotations()) {
			final HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
			if(httpMethod != null) {
				return httpMethod;
			}
		}
		return null;
	}

}
