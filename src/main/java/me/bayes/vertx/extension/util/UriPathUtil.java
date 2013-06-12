/**
 * 
 */
package me.bayes.vertx.extension.util;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author kevinbayes
 *
 */
public final class UriPathUtil {
	
	private UriPathUtil() { }
	
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

}
