/**
 * 
 */
package me.bayes.vertx.vest.jaxrs.deploy;

import javax.ws.rs.core.Application;

import me.bayes.vertx.vest.BuilderContext;
import me.bayes.vertx.vest.RouteMatcherBuilder;
import me.bayes.vertx.vest.jaxrs.AbstractVestVerticle;
import me.bayes.vertx.vest.jaxrs.BuilderContextProperty;
import me.bayes.vertx.vest.jaxrs.JaxrsRouteMatcherBuilder;

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

	protected Application createApplication(final JsonObject config) throws Exception {
		
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
		
		return application;
	}


	@Override
	protected RouteMatcherBuilder createBuilder(Application application)
			throws Exception {
		
		final BuilderContext context = new BuilderContext();
		context.addProperty(BuilderContextProperty.JAXRS_APPLICATION, application);
		context.addProperty(BuilderContextProperty.JSON_CONFIG, container.getConfig());
		context.addProperty(BuilderContextProperty.VERTX_INSTANCE, vertx);
		context.addProperty(BuilderContextProperty.CONTAINER_INSTANCE, container);
		
		return new JaxrsRouteMatcherBuilder(context);
	}

}
