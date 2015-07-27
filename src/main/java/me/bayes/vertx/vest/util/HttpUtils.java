/**
 * 
 */
package me.bayes.vertx.vest.util;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author kevinbayes
 *
 */
public final class HttpUtils {
	
	/**
	 * Look for the HTTP verb which should be {@link javax.ws.rs.GET}, {@link javax.ws.rs.POST}, {@link javax.ws.rs.PUT}, {@link javax.ws.rs.DELETE}, {@link javax.ws.rs.OPTIONS} or {@link javax.ws.rs.HEAD}.
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
