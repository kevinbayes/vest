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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.CookieParam;
import javax.ws.rs.MatrixParam;

import com.github.drapostolos.typeparser.TypeParser;
import me.bayes.vertx.vest.VestApplication;

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
public final class DefaultParameterResolver implements ParameterResolver {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultParameterResolver.class);
	
	private final VestApplication application;
	
	public DefaultParameterResolver(VestApplication application) { 
		this.application = application;
	}
	
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
	public Object resolve(final Method method, final Class<?> parameterType, final Annotation[] annotations, final HttpServerRequest request) throws IOException, ReflectiveOperationException {
		
		if(parameterType.equals(HttpServerResponse.class)) {
			return new HttpServerResponseParameterHandler().handle(method, parameterType, annotations, request);
		} else {
			TypeParser typeParser = application.getSingleton(TypeParser.class);
			return new JaxrsAnnotationParameterHandler(typeParser)
					.handle(method, parameterType, annotations, request);
		}
		
	}
	
}
