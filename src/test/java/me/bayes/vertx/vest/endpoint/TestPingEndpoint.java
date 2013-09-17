package me.bayes.vertx.vest.endpoint;

import static org.junit.Assert.*;
import me.bayes.vertx.vest.deploy.VestVerticle;

import org.junit.Test;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

public class TestPingEndpoint extends TestVerticle {
	
	private static final String JSON_CONFIG_CLASSES_TO_ADD = "{\"vestClasses\":[\"me.bayes.vertx.vest.endpoint.PingEndpoint\"]}";

	@Test
	public void testPing() {
		
		JsonObject config = new JsonObject(JSON_CONFIG_CLASSES_TO_ADD);

		container.deployVerticle("me.bayes.vertx.vest.deploy.VestVerticle", config);
			
		VertxAssert.testComplete();
		
	}

}
