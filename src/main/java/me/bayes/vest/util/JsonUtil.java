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
package refer.api.jaxrs.vest.util;


import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	 * @throws java.io.IOException
	 */
	public static JsonObject readConfig(URI fileUri) throws IOException {
		final Path path = Paths.get(fileUri);
		final Charset charset = Charset.forName("UTF-8");
		final byte[] bytes = Files.readAllBytes(path);		
		
		return new JsonObject(new String(bytes, charset));
	}

}
