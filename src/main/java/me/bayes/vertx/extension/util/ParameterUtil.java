/**
 * 
 */
package me.bayes.vertx.extension.util;

import java.lang.annotation.Annotation;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.HttpServerRequest;

/**
 * <pre>
 * A util to inject parameters into the methods handling REST requests.
 * 
 * TODO: Add support for {@link MatrixParam}
 * TODO: Add support for {@link CookieParam}
 * </pre>
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public final class ParameterUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ParameterUtil.class);
	
	private ParameterUtil() { }
	
	/**
	 * <pre>
	 * Using the context we try resolve a object that can be used for this parameter type.
	 * </pre>
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
				//Path Parameters
				PathParam pathParam = (PathParam) annotation;
				returnObject = request.params().get(pathParam.value());
			} else if(annotation.annotationType().equals(HeaderParam.class)) {
				//Query Parameters
				QueryParam queryParam = (QueryParam) annotation;
				returnObject = request.params().get(queryParam.value());
			} else if(annotation.annotationType().equals(HeaderParam.class)) {
				//HTTP Headers
				HeaderParam pathParam = (HeaderParam) annotation;
				returnObject = request.headers().get(pathParam.value());
			} else if(annotation.annotationType().equals(DefaultValue.class)) {
				DefaultValue defaultValue = (DefaultValue) annotation;
				returnObject = defaultValue.value();
			} else if(annotation.annotationType().equals(MatrixParam.class) ||
					annotation.annotationType().equals(CookieParam.class) ||
					annotation.annotationType().equals(FormParam.class)) {
				LOG.warn("Matrix & cookie && form parameters are not supported.");
			}
			
			if(returnObject != null) {
				break;
			}
		}
		
		return returnObject;
	}

}
