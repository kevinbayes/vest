package me.bayes.vertx.vest.deploy;

import java.net.URI;
import java.util.Scanner;

import me.bayes.vertx.vest.util.JsonUtil;

import org.vertx.java.core.json.JsonObject;

public class Standalone {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length != 1) {
			System.err.println("Must specify a json config file.");
			return;
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down.");
            }
        });
		
		final URI configUri = new URI(args[0]);
		final JsonObject config = JsonUtil.readConfig(configUri);
		new Embedded().start(config);
		
		startConsole();
	}
	
	private static void startConsole() {
		String input ="";
        Scanner in = new Scanner(System.in);
		
		do {
			System.out.print("> ");
            input = in.nextLine();

            if (input.equalsIgnoreCase("e")) {
                break;
            }
            
		} while(true);
		
		in.close();
	}

}