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
package refer.api.jaxrs.vest.util;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.apex.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class JaxrsAnnotationParamterHandler implements ParameterHandler<Object> {
	
	private static final Logger LOG = LoggerFactory.getLogger(JaxrsAnnotationParamterHandler.class);

	@Override
	public Object handle(final Method method, Class<?> parameterType, Annotation[] annotations,
                         final RoutingContext routingContext) {
		
		Object returnObject = null;
		Object defaultValue = null;
		HttpServerRequest request = routingContext.request();

		for(Annotation annotation : annotations) {
			
			if(annotation.annotationType().equals(PathParam.class)) {
				//Path Parameters
				PathParam pathParam = (PathParam) annotation;
				returnObject = request.params().get(pathParam.value());
			} else if(annotation.annotationType().equals(QueryParam.class)) {
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
		
		return returnObject == null ? defaultValue : returnObject;
	}

}
