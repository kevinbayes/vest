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
package me.bayes.vertx.vest.util;

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
import org.vertx.java.core.http.HttpServerResponse;

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
		Object defaultValue = null;
		
		if(parameterType.equals(HttpServerResponse.class)) {
			returnObject = request.response();
		} else {
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
					DefaultValue defaultValueAnnotation = (DefaultValue) annotation;
					defaultValue = defaultValueAnnotation.value();
				} else if(annotation.annotationType().equals(MatrixParam.class) ||
					annotation.annotationType().equals(CookieParam.class) ||
					annotation.annotationType().equals(FormParam.class)) {
					LOG.warn("Matrix & cookie && form parameters are not supported.");
				}
				
				if(returnObject != null) {
					break;
				}
			}
		}
		
		return returnObject == null ? defaultValue : returnObject;
	}

}
