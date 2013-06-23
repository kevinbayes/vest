/**
 * 
 */
package me.bayes.vertx.vest.deploy;

import me.bayes.vertx.vest.AbstractVestVerticle;
import me.bayes.vertx.vest.JaxrsRouteMatcherBuilder;
import me.bayes.vertx.vest.RouteMatcherBuilder;
import me.bayes.vertx.vest.VestApplication;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;


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
		
		final JsonArray vestPackagesToScan = config.getArray("vestPackagesToScan");
		final JsonArray vestClasses = config.getArray("vestClasses");
		final RootContextVestApplication application = new RootContextVestApplication();
		
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
		
		application.addSingleton(container == null ? null : container.getConfig());
		application.addSingleton(vertx);
		application.addSingleton(container);
		
		return application;
	}


	public RouteMatcherBuilder createBuilder(VestApplication application)
			throws Exception {
		return new JaxrsRouteMatcherBuilder(application);
	}

}
