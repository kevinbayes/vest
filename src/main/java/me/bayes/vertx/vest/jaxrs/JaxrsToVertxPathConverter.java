/**
 * 
 */
package me.bayes.vertx.vest.jaxrs;

import javax.ws.rs.Path;

/**
 * A converter to allow developer the ability to use jaxrs paths in vertx.
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public final class JaxrsToVertxPathConverter {
	
	/*Ensure this cannot be instantiated*/
	private JaxrsToVertxPathConverter() { }
	
	
	/**
	 * Taken path declared in a {@link Path} annotation and replaces it with
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
			} else {
				newPath.append(currentCharacter);
			}
		}
		
		return newPath.toString();
		
	}
	

}
