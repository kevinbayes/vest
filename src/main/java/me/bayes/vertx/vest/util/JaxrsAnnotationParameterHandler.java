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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.github.drapostolos.typeparser.TypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.HttpServerRequest;

public class JaxrsAnnotationParameterHandler implements ParameterHandler<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(JaxrsAnnotationParameterHandler.class);

	private TypeParser typeParser;

	public JaxrsAnnotationParameterHandler(TypeParser typeParser) {
		this.typeParser = typeParser;
	}

	@Override
	public Object handle(final Method method, Class<?> parameterType, Annotation[] annotations,
			HttpServerRequest request) throws IOException, ReflectiveOperationException {

		Object returnObject;
		String param = null;
		Object defaultValue = null;

		for(Annotation annotation : annotations) {

			if(annotation.annotationType().equals(PathParam.class)) {
				//Path Parameters
				PathParam pathParam = (PathParam) annotation;
				param = request.params().get(pathParam.value());
			} else if(annotation.annotationType().equals(QueryParam.class)) {
				//Query Parameters
				QueryParam queryParam = (QueryParam) annotation;
				param = request.params().get(queryParam.value());
			} else if(annotation.annotationType().equals(HeaderParam.class)) {
				//HTTP Headers
				HeaderParam pathParam = (HeaderParam) annotation;
				param = request.headers().get(pathParam.value());
			} else if(annotation.annotationType().equals(DefaultValue.class)) {
				DefaultValue defaultValueAnnotation = (DefaultValue) annotation;
				defaultValue = defaultValueAnnotation.value();
			} else if(annotation.annotationType().equals(MatrixParam.class) ||
				annotation.annotationType().equals(CookieParam.class) ||
				annotation.annotationType().equals(FormParam.class)) {
				LOG.warn("Matrix & cookie && form parameters are not supported.");
			}

			if(param != null) {
				break;
			}
		}

		if (param != null) {
            returnObject = typeParser.parseType(param, parameterType);
		} else {
			returnObject = defaultValue;
		}

		return returnObject;
	}

}
