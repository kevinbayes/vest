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
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.apex.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author kevinbayes
 *
 */
public class HttpServerResponseParameterHandler implements ParameterHandler<HttpServerResponse> {

	public HttpServerResponse handle(final Method method, Class<?> parameterType,
			Annotation[] annotations, RoutingContext context) {
		return context.request().response();
	}
}
