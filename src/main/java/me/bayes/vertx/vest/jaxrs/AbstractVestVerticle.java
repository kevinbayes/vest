/**
 * 
 */
package me.bayes.vertx.vest.jaxrs;

import me.bayes.vertx.vest.RouteMatcherBuilder;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * @author kevinbayes
 *
 */
public abstract class AbstractVestVerticle extends Verticle implements VestService {
	
	/* (non-Javadoc)
	 * @see org.vertx.java.deploy.Verticle#start()
	 */
	@Override
	public void start() throws Exception {
		
		final JsonObject config = container.getConfig();
		final HttpServer server = vertx.createHttpServer();
		final String listenHost = config.getString(LISTEN_HOST);
		final int listenPort = 
				(config.getInteger(LISTEN_PORT) == null) ? 
						DEFAULT_PORT : config.getInteger(LISTEN_PORT);
		
		VestApplication application = createApplication(config);
		application.addSingleton(container, vertx);
		
		//A hook to add to the VestApplication once it has been created.
		postApplicationCreationProcess(application);
		
		final RouteMatcherBuilder routeBuilder = createBuilder(
				application);
		
		postRouteMatcherBuilderCreationProcess(routeBuilder);
		
		server.requestHandler(
				routeBuilder.build());
		
		//Set listen information
		if(listenHost == null) {
			server.listen(listenPort);
		} else {
			server.listen(listenPort, listenHost);
		}
	}
	
	public void postRouteMatcherBuilderCreationProcess(RouteMatcherBuilder routeBuilder) { }

	/**
	 * Override this method to add post processing once the {@link VestApplication} has been created.
	 * 
	 * @param application that has just been created.
	 */
	public void postApplicationCreationProcess(final VestApplication application) { }
	
}
