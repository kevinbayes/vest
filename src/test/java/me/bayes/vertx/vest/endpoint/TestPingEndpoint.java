package me.bayes.vertx.vest.endpoint;

import static org.junit.Assert.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import me.bayes.vertx.vest.deploy.VestVerticle;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

@Ignore
public class TestPingEndpoint extends TestVerticle {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestPingEndpoint.class);
	
	private static final String JSON_CONFIG_CLASSES_TO_ADD = "{\"vestClasses\":[\"me.bayes.vertx.vest.endpoint.PingEndpoint\"]}";

	@Test
	public void testPing() {
		
		JsonObject config = new JsonObject(JSON_CONFIG_CLASSES_TO_ADD);

		container.deployVerticle("me.bayes.vertx.vest.deploy.VestVerticle", config);
		
		HttpClient client = vertx.createHttpClient();
		client.setHost("localhost");
		client.setPort(8080);
		
		HttpClientRequest request = client.get("/ping", new Handler<HttpClientResponse>() {
		    public void handle(HttpClientResponse resp) {
		    	
		    	resp.bodyHandler(new Handler<Buffer>() {
		            public void handle(Buffer body) {
		               
		               final String payload = body.toString();
		               
		               VertxAssert.testComplete();
		               
		               assertTrue(payload.equals("ping"));
		            }
		        }); 
		    	
		    	String contentType = resp.headers().get(HttpHeaders.CONTENT_TYPE);
		    	assertTrue(MediaType.TEXT_PLAIN.equals(contentType));
		    }
		}).putHeader(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN);
		
		request.end();
			
	}

}
