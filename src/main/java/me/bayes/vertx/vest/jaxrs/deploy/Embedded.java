/**
 * 
 */
package me.bayes.vertx.vest.jaxrs.deploy;

import me.bayes.vertx.vest.RouteMatcherBuilder;
import me.bayes.vertx.vest.jaxrs.JaxrsRouteMatcherBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * @author kevinbayes
 *
 */
public class Embedded {
	
	private static final String LISTEN_HOST = "host";
	private static final String LISTEN_PORT = "port";
	
	//Default values
	private static final int DEFAULT_PORT = 8080;
	
	private static final Logger LOG = LoggerFactory.getLogger(Embedded.class);
	
	public Vertx start(final JsonObject config) throws Exception {
		
		final Vertx vertx = Vertx.newVertx();
		final JsonArray vestPackagesToScan = config.getArray("vestPackagesToScan");
		final JsonArray vestClasses = config.getArray("vestClasses");
		final RootContextVestApplication application = new RootContextVestApplication();
		final HttpServer server = vertx.createHttpServer();
		final RouteMatcherBuilder builder = new JaxrsRouteMatcherBuilder(application);
		final String listenHost = config.getString(LISTEN_HOST);
		final int listenPort = 
				(config.getInteger(LISTEN_PORT) == null) ? 
						DEFAULT_PORT : config.getInteger(LISTEN_PORT);

		//Add packages to scan
		if(vestPackagesToScan != null) {
			for(Object obj : vestPackagesToScan) {
				application.addPackagesToScan(String.valueOf(obj));
			}
		}
				
		//Add classes
		if(vestClasses != null) {
			for(Object obj : vestClasses) {
				try {
					Class<?> clazz = Class.forName((String)obj);
					application.addEndpointClasses(clazz);
				} catch (ClassNotFoundException e) {
					LOG.error("Could not load class " + obj + ".", e);
				}
			}
		}
		
		application.addSingleton(config);
		application.addSingleton(vertx);
		
		server.requestHandler(builder.build());
		
		//Set listen information
		if(listenHost == null) {
			server.listen(listenPort);
		} else {
			server.listen(listenPort, listenHost);
		}
		
		return vertx;
	}
	
	

}
