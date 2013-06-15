package me.bayes.vertx.vest;



import org.vertx.java.core.http.RouteMatcher;

/**
 * The {@link RouteMatcherBuilder} builds a {@link RouteMatcher} that vertx uses to 
 * route messages.
 * 
 * The {@link RouteMatcherBuilder} is an implementation of the strategy pattern allowing
 * you to extend this to an implementation best suited for your use case.
 * 
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public interface RouteMatcherBuilder {

	/**
	 * Executing this method ensures that a {@link RouteMatcher} is built.
	 * 
	 * @return
	 * @throws Exception
	 */
	RouteMatcher build() throws Exception;
	
	/**
	 * Set the context for building the route.
	 * 
	 * @param context
	 */
	void setContext(BuilderContext context);
	
}
