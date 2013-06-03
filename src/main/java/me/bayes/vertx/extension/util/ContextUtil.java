/**
 * 
 */
package me.bayes.vertx.extension.util;

import java.lang.reflect.Field;

import javax.ws.rs.core.Context;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

import me.bayes.vertx.extension.BuilderContext;
import me.bayes.vertx.extension.jaxrs.JaxrsBuilderContextProperty;

/**
 * A utility class used to work with the {@link Context} annotation.
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public final class ContextUtil {

	private ContextUtil() { }
	
	/**
	 * This method goes through the class and all the super classes searching for fields annotated with {@link Context}.
	 * It then injects the instance available in the context else null.
	 * 
	 * Currently this only supports injecting {@link Vertx} and {@link JsonObject}. 
	 * 
	 * TODO: Make this generic so anything in the context can be injected using the {@link Context} annotation.
	 * 
	 * @param clazz
	 * @param instance
	 * @param context
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void assignContextFields(@SuppressWarnings("rawtypes") Class clazz, Object instance, BuilderContext context) throws IllegalArgumentException, IllegalAccessException {
		
		do {
		
			for(Field field : clazz.getDeclaredFields()) {
				
				if(field.getAnnotation(Context.class) != null) {
					if(Vertx.class.equals(field.getType())) {
						final Vertx vertx = context.getPropertyValue(JaxrsBuilderContextProperty.VERTX_INSTANCE, Vertx.class);
						if(vertx != null) {
							boolean accessible = field.isAccessible();
							field.setAccessible(true);
							field.set(instance, vertx);
							field.setAccessible(accessible);
						}
					} else if(JsonObject.class.equals(field.getType())) {
						final JsonObject jsonObject = context.getPropertyValue(JaxrsBuilderContextProperty.JSON_CONFIG, JsonObject.class);
						if(jsonObject != null) {
							field.setAccessible(true);
							field.set(instance, jsonObject);
						}
					}
				}
			}
			
			clazz = clazz.getSuperclass();
		
		} while(clazz != null && !Object.class.equals(clazz));
		
	}
	
	
	
}
