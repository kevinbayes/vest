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
package me.bayes.vertx.vest;




import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;

/**
 * The {@link RouteMatcherBuilder} builds a {@link RouteMatcher} that vertx uses to 
 * route messages.
 * 
 * The {@link RouteMatcherBuilder} is an implementation of the strategy pattern allowing
 * you to extend this to an implementation best suited for your use case.
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public interface RouteMatcherBuilder {

	
	/**
	 * Executing this method ensures that a {@link RouteMatcher} is built.
	 * 
	 * @return
	 * @throws Exception
	 */
	RouteMatcher build() throws Exception;
	
	
	/**
	 * Set the application that the builder must use to build the jaxrs services.
	 * 
	 * @param application
	 */
	void setApplication(VestApplication application);
	
	/**
	 * This method should be overridden if a better no match is needed besides the 
	 * default of a 404.
	 * 
	 * @param handler
	 */
	void setNoRouteHandler(Handler<HttpServerRequest> handler);
	
}
