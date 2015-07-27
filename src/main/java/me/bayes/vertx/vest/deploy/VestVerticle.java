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
package me.bayes.vertx.vest.deploy;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import me.bayes.vertx.vest.AbstractVestVerticle;
import me.bayes.vertx.vest.DefaultRouterBuilder;
import me.bayes.vertx.vest.RouterBuilder;
import me.bayes.vertx.vest.VestApplication;



/**
 * <pre>
 * This is a default verticle you can use for a basic REST service platform. Using the config
 * you need to specify packages to scan or classes to add. The verticle will then handle everthing
 * else for you.
 * </pre>
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public class VestVerticle extends AbstractVestVerticle {

	public VestApplication createApplication(final JsonObject config) throws Exception {
		
		final JsonArray vestPackagesToScan = config.getJsonArray("vestPackagesToScan");
		final JsonArray vestClasses = config.getJsonArray("vestClasses");
		final String applicationClass = config.getString("applicationClass", "me.bayes.vertx.vest.deploy.RootContextVestApplication");
		final VestApplication application = (VestApplication) Class.forName(applicationClass).newInstance();
		
		//Add packages to scan
		if(vestPackagesToScan != null) {
			for(Object obj : vestPackagesToScan) {
				application.addPackagesToScan(String.valueOf(obj));
			}
		}
		
		//Add classes
		if(vestClasses != null) {
			for(Object obj : vestClasses) {
				Class<?> clazz = Class.forName((String)obj);
				application.addEndpointClasses(clazz);
			}
		}
		
		application.addSingleton(vertx);
		application.addSingleton(getVertx().getOrCreateContext());
		application.addSingleton(getVertx().getOrCreateContext().config());
		
		return application;
	}


	public RouterBuilder createBuilder(VestApplication application)
			throws Exception {
		return new DefaultRouterBuilder(application);
	}

}
