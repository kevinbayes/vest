/**
 * 
 */
package me.bayes.vertx.extension.jaxrs.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author kevinbayes
 *
 */
@Path("/ping")
public class PingEndpoint {

	@GET
	public void get() {
		System.out.println("Hello");
	}
	
}
