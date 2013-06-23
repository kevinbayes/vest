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

import static org.junit.Assert.*;

import javax.ws.rs.core.Application;

import me.bayes.vertx.vest.deploy.VestVerticle;
import me.bayes.vertx.vest.sample.PingEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonObject;

/**
 * @author kevinbayes
 *
 */
public class VestVerticleTest {

	private static final String JSON_CONFIG_PACKAGES_TO_SCAN = "{\"vestPackagesToScan\":[\"me.bayes.vertx.vest.sample\"]}";
	
	private static final String JSON_CONFIG_CLASSES_TO_ADD = "{\"vestClasses\":[\"me.bayes.vertx.vest.sample.PingEndpoint\"]}";
	
	private VestVerticle vestVerticle;
	
	@Before
	public void setUp() {
		vestVerticle = new VestVerticle();
	}
	
	
	/**
	 * Test method for {@link me.bayes.vertx.vest.deploy.VestVerticle#createApplication(org.vertx.java.core.json.JsonObject)}.
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
	 * Test method for {@link me.bayes.vertx.vest.deploy.VestVerticle#createApplication(org.vertx.java.core.json.JsonObject)}.
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
	 * Test method for {@link me.bayes.vertx.vest.deploy.VestVerticle#createBuilder(javax.ws.rs.core.Application)}.
	 */
	@Test
	public void testCreateBuilder() {
	}

}
