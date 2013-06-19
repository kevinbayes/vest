/**
 * 
 */
package me.bayes.vertx.vest.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.json.JsonObject;

/**
 * <pre>
 * The {@link JsonUtil} is a helper class with convenient utils for json
 * files.
 * </pre>
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public final class JsonUtil {
	
	/* Ensure this cannot be instantiated */
	private JsonUtil() { }

	/**
	 * Read the JSON configuration from the provided file.
	 * 
	 * @param fileUri to the json config file.
	 * @return {@link JsonObject} 
	 * @throws IOException
	 */
	public static JsonObject readConfig(URI fileUri) throws IOException {
		final Path path = Paths.get(fileUri);
		final Charset charset = Charset.forName("UTF-8");
		final byte[] bytes = Files.readAllBytes(path);		
		
		return new JsonObject(new String(bytes, charset));
	}

}
