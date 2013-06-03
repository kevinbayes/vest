/**
 * 
 */
package me.bayes.vertx.extension.jaxrs;

/**
 * All the properties that the context should contain.
 * 
 * @author Kevin Bayes
 * @version 1.0
 * @since 1.0
 */
public interface JaxrsBuilderContextProperty {
	
	String JAXRS_APPLICATION = "jaxrs.application";
	String VERTX_INSTANCE = "vertx.instance";
	String JSON_CONFIG = "json.config";

}
