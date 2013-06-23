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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import me.bayes.vertx.vest.VestApplication;
import me.bayes.vertx.vest.sample.PingEndpoint;
import me.bayes.vertx.vest.sample.other.OtherEndpoint;

import org.junit.Test;

public class TestVertxApplication {

	@Test
	public void testGivenPackageToScanOneClassFound() {
		//Given
		VestApplication application = new VestApplication() {
		};
		Set<String> packages = new HashSet<String>(1);
		packages.add("me.bayes.vertx.vest.sample");
		
		//When
		application.addPackagesToScan(packages);
		
		//Then
		assertTrue(application.getClasses().size() == 1);
		assertTrue(application.getClasses().contains(PingEndpoint.class));
	}
	
	@Test
	public void testGivenOneClassOneClassFound() {
		//Given
		VestApplication application = new VestApplication() {
		};
		
		//When
		application.addEndpointClasses(me.bayes.vertx.vest.sample.PingEndpoint.class);
		
		//Then
		assertTrue(application.getClasses().size() == 1);
		assertTrue(application.getClasses().contains(PingEndpoint.class));
	}
	
	
	@Test
	public void testGivenOnceClassAndOnePackageTwoClassFound() {
		//Given
		VestApplication application = new VestApplication() {
		};
		Set<String> packages = new HashSet<String>(1);
		packages.add("me.bayes.vertx.vest.sample.other");
		
		//When
		application.addPackagesToScan(packages);
		application.addEndpointClasses(me.bayes.vertx.vest.sample.PingEndpoint.class);
		
		//Then
		assertTrue(application.getClasses().size() == 2);
		assertTrue(application.getClasses().contains(PingEndpoint.class));
		assertTrue(application.getClasses().contains(OtherEndpoint.class));
	}

}
