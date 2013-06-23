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

import org.vertx.java.core.json.JsonObject;

/**
 * @author kevinbayes
 *
 */
public interface VestService {
	
	//Properties
	final String LISTEN_HOST = "host";
	final String LISTEN_PORT = "port";
		
	//Default values
	final int DEFAULT_PORT = 8080;
	
	/**
	 * <pre>
	 * Create an application for your jaxrs route builder.
	 * </pre>
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	VestApplication createApplication(final JsonObject config) throws Exception;

	/**
	 * <pre>
	 * Create a route builder.
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	RouteMatcherBuilder createBuilder(final VestApplication application) throws Exception;

}
