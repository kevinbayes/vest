package me.bayes.vertx.extension;

import java.util.HashMap;
import java.util.Map;

public class BuilderContext {
	
	public static final String JAXRS_APPLICATION = "jaxrs.application";
	
	public Map<String, Object> properties = new HashMap<String, Object>(0);
	
	public BuilderContext() {
	}

	public void addProperty(String key, Object value) {
		properties.put(key, value);
	}
	
	public <T> T getPropertyValue(String key, Class<T> type) {
		return type.cast(properties.get(key));
	}
	
	public Object getPropertyValue(String key) {
		return properties.get(key);
	}

}
