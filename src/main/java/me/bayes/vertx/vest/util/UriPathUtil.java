/**
 * Copyright 2013 Bayes Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.bayes.vertx.vest.util;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author kevinbayes
 *
 */
public final class UriPathUtil {
	
	private UriPathUtil() { }
	
	
	/**
	 * Retrieve the context from the provided {@link Application}'s {@link ApplicationPath}.
	 *
	 * @param application with a {@link ApplicationPath} annotation.
	 * @return String of the path.
	 */
	public static String getApplicationContext(Application application) {

		final Class<?> clazz = application.getClass();
		final ApplicationPath applicationPath = clazz.getAnnotation(ApplicationPath.class);

		String path = (applicationPath == null) ? "/" :
			applicationPath.value();

		if(path.length() < 1) {
			return "/";
		} else {
			return path.charAt(0) == '/' ? path : "/" + path;
		}
	}


	/**
	 * Concat paths to a single one in the form of parent/child.
	 *
	 * @param parent
	 * @param child
	 * @return a url in {@link String} form.
	 */
	public static String concatPaths(String parent, String child) {

		if(parent.endsWith("/") ^ child.startsWith("/")) {
			return parent + child;
		} else if(!parent.endsWith("/") && !child.startsWith("/")) {
			return parent + "/" + child;
		} else if(parent.endsWith("/") && child.startsWith("/")) {
			return parent + child.substring(1);
		} else {
			return (parent + child).replaceAll("//", "/");
		}

	}


	/**
	 * Taken path declared in a {@link javax.ws.rs.Path} annotation and replaces it with
	 * a vertx compliant path. 
	 * 
	 * http://vertx.io/core_manual_java.html#extracting-parameters-from-the-path
	 * 
	 * WARNING: This current approach removes the regular expression validations of 
	 * 			the path parameters.
	 * 
	 * @param path - URI of the service
	 * @return vertx path as string
	 */
	public static String convertPath(String path) {
		
		final StringBuilder newPath = new StringBuilder();
		
		boolean foundPathVariable = false;
		boolean foundRegularExpression = false;
		
		for(int i = 0; i < path.length(); i++) {
			char currentCharacter = path.charAt(i);
			
			if(currentCharacter == '{') {
				foundPathVariable = true;
				newPath.append(":");
			} else if(currentCharacter == '}') {
				foundPathVariable = false;
				foundRegularExpression = false;
			} else if(foundPathVariable && currentCharacter == ':') {
				foundRegularExpression = true;
			} else if(foundPathVariable && (foundRegularExpression ||
					currentCharacter == ' ')) {
				continue;
			} else if(i == path.length() - 1 && currentCharacter == '/') {
				continue;
			} else {
				newPath.append(currentCharacter);
			}
		}
		
		return newPath.toString();
		
	}

}
