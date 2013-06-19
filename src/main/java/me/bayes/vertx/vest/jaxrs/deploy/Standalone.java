package me.bayes.vertx.vest.jaxrs.deploy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import me.bayes.vertx.vest.util.JsonUtil;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

public class Standalone {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws URISyntaxException, IOException {
		
		if(args.length != 1) {
			System.err.println("Must specify a json config file.");
			return;
		}
		
		final Vertx vertx = Vertx.newVertx();
		
		URI configUri = new URI(args[0]);
		
		JsonObject config = JsonUtil.readConfig(configUri);
		
		
		
		
	}
	
	
	

}
