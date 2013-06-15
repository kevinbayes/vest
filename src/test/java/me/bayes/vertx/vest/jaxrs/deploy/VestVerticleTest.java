/**
 * 
 */
package me.bayes.vertx.vest.jaxrs.deploy;

import static org.junit.Assert.*;

import javax.ws.rs.core.Application;

import me.bayes.vertx.vest.jaxrs.sample.PingEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

/**
 * @author kevinbayes
 *
 */
public class VestVerticleTest {

	private static final String JSON_CONFIG_PACKAGES_TO_SCAN = "{\"vestPackagesToScan\":[\"me.bayes.vertx.vest.jaxrs.sample\"]}";
	
	private static final String JSON_CONFIG_CLASSES_TO_ADD = "{\"vestClasses\":[\"me.bayes.vertx.vest.jaxrs.sample.PingEndpoint\"]}";
	
	private VestVerticle vestVerticle;
	
	@Before
	public void setUp() {
		vestVerticle = new VestVerticle();
	}
	
	
	/**
	 * Test method for {@link me.bayes.vertx.vest.jaxrs.deploy.VestVerticle#createApplication(org.vertx.java.core.json.JsonObject)}.
	 */
	@Test
	public void testCreateApplicationWithPackageScan() {
		//Given
		JsonObject config = new JsonObject(JSON_CONFIG_PACKAGES_TO_SCAN);
		Application application = null;
		
		try {
		
			//When
			application = vestVerticle.createApplication(config);
		 
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		if(application == null) {
			fail("Application not initialized.");
		}
		
		//Then
		assertTrue(application.getClasses().size() == 1);
		assertTrue(application.getClasses().contains(PingEndpoint.class));
		
	}
	
	/**
	 * Test method for {@link me.bayes.vertx.vest.jaxrs.deploy.VestVerticle#createApplication(org.vertx.java.core.json.JsonObject)}.
	 */
	@Test
	public void testCreateApplicationWithClasses() {
		//Given
		JsonObject config = new JsonObject(JSON_CONFIG_CLASSES_TO_ADD);
		Application application = null;
		
		try {
		
			//When
			application = vestVerticle.createApplication(config);
		 
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		if(application == null) {
			fail("Application not initialized.");
		}
		
		//Then
		assertTrue(application.getClasses().size() == 1);
		assertTrue(application.getClasses().contains(PingEndpoint.class));
		
	}

	/**
	 * Test method for {@link me.bayes.vertx.vest.jaxrs.deploy.VestVerticle#createBuilder(javax.ws.rs.core.Application)}.
	 */
	@Test
	public void testCreateBuilder() {
	}

}
