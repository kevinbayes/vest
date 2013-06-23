/**
 * 
 */
package me.bayes.vertx.vest;

import javax.ws.rs.core.Application;


import org.vertx.java.core.json.JsonObject;

/**
 * @author kevinbayes
 *
 */
public interface VestService {
	
	//Properties
	final String LISTEN_HOST = "host";
	final String LISTEN_PORT = "port";
		
	//Default values
	final int DEFAULT_PORT = 8080;
	
	/**
	 * <pre>
	 * Create an application for your jaxrs route builder.
	 * </pre>
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	VestApplication createApplication(final JsonObject config) throws Exception;

	/**
	 * <pre>
	 * Create a route builder.
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	RouteMatcherBuilder createBuilder(final VestApplication application) throws Exception;

}
