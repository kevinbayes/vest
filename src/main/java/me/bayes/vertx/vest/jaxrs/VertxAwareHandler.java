/**
 * 
 */
package me.bayes.vertx.vest.jaxrs;

import javax.ws.rs.core.Context;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

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
