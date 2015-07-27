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

import io.vertx.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <p>
 * The parameter resolver is a interface that all classes that resolve parameters
 * in handlers should implement.
 * </p>
 * 
 * @author Kevin Bayes
 *
 */
public interface ParameterResolver {
	
	/**
	 * <p>
	 * Resolve a parameter to a value.
	 * </p>
	 * 
	 * @param method that the parameter belongs too
	 * @param parameterType of the parameter to resolve
	 * @param annotations that are on the parameter
	 * @param routingContext which is the vertx {@link RoutingContext}
	 * @return the resolved object
	 */
	Object resolve(final Method method, final Class<?> parameterType, final Annotation[] annotations, final RoutingContext routingContext);

}
