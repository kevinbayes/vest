package me.bayes.vertx.vest.jaxrs.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.vertx.java.core.http.HttpServerRequest;

@Path("/ping")
public class PingEndpoint {

	@GET
	@Produces({MediaType.TEXT_PLAIN})
	public void ping(HttpServerRequest request) {
		request.response.headers().put("Content-Type", String.format("%s; charset=%s", MediaType.TEXT_PLAIN, "UTF-8"));
		request.response.end("ping");
	}

	@GET
	@Path("{string}")
	@Produces({MediaType.TEXT_PLAIN})
	public void echo(HttpServerRequest request, @PathParam("string") final String echoString) {
		request.response.headers().put("Content-Type", String.format("%s; charset=%s", MediaType.TEXT_PLAIN, "UTF-8"));
		request.response.end(String.format("echo: %s", echoString));
	}
	
}
