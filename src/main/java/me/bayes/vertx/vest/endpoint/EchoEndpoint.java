/**
 * 
 */
package me.bayes.vertx.vest.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;

/**
 * @author kevinbayes
 *
 */
@Path("/echo")
public class EchoEndpoint {
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	public void ping(HttpServerRequest request, HttpServerResponse response, JsonObject body) {
		response.headers().set(HttpHeaders.CONTENT_ENCODING, "UTF-8");
		response.headers().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		response.end(body.toString());
	}

}
