/**
 * 
 */
package me.bayes.vertx.vest.jaxrs;

import javax.ws.rs.core.Application;

import me.bayes.vertx.vest.AbstractRouteMatcherBuilder;
import me.bayes.vertx.vest.BuilderContext;
import me.bayes.vertx.vest.RouteMatcherBuilder;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

/**
 * @author kevinbayes
 *
 */
public abstract class AbstractVestVerticle extends Verticle {
	
	//Properties
	private static final String LISTEN_HOST = "host";
	private static final String LISTEN_PORT = "port";
	
	//Default values
	private static final int DEFAULT_PORT = 8080;

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
		
		final RouteMatcherBuilder routeBuilder = createBuilder(
				createApplication(config));
		server.requestHandler(
				routeBuilder.build());
		
		//Set listen information
		if(listenHost == null) {
			server.listen(listenPort);
		} else {
			server.listen(listenPort, listenHost);
		}
	}
	
	/**
	 * <pre>
	 * Create an application for your jaxrs route builder.
	 * </pre>
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	protected abstract Application createApplication(final JsonObject config) throws Exception;

	/**
	 * <pre>
	 * Create a route builder.
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract RouteMatcherBuilder createBuilder(final Application application) throws Exception;
	
}
