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
package me.bayes.vertx.vest.handler;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.core.Context;


/**
 * Extend this class to ensure that your handler has access to the {@link Vertx} instance and the
 * {@link JsonObject} config.
 * 
 * @author Kevin Bayes
 * @version 1.0
 * @since 1.0
 *
 */
public abstract class VertxAwareHandler {
	
	@Context
	private Vertx vertx;
	
	@Context 
	private JsonObject config;

	public Vertx getVertx() {
		return vertx;
	}

	public void setVertx(Vertx vertx) {
		this.vertx = vertx;
	}

	public JsonObject getConfig() {
		return config;
	}

	public void setConfig(JsonObject config) {
		this.config = config;
	}
	
}
