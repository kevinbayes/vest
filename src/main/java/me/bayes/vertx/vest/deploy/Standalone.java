/**
 * Copyright 2013 Bayes Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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