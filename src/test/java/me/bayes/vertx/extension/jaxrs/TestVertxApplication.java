package me.bayes.vertx.extension.jaxrs;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import me.bayes.vertx.extension.jaxrs.sample.PingEndpoint;

import org.junit.Test;

public class TestVertxApplication {

	@Test
	public void testGivenPackageToScanOneClassFound() {
		//Given
		VertxApplication application = new VertxApplication() {
		};
		Set<String> packages = new HashSet<String>(1);
		packages.add("me.bayes.vertx.extension.jaxrs.sample");
		
		//When
		application.addPackagesToScan(packages);
		
		//Then
		assertTrue(application.getClasses().size() == 1);
		assertTrue(application.getClasses().contains(PingEndpoint.class));
	}

}
