/**
 * 
 */
package me.bayes.vertx.extension;


import org.vertx.java.core.http.RouteMatcher;

/**
 * @author Kevin Bayes
 */
public abstract class AbstractRouteMatcherBuilder implements RouteMatcherBuilder {

	protected BuilderContext context;
	protected RouteMatcher routeMatcher;

	public AbstractRouteMatcherBuilder(BuilderContext context) {
		super();
		this.context = context;
		this.routeMatcher = new RouteMatcher();
	}

	public BuilderContext getContext() {
		return context;
	}

	public void setContext(BuilderContext context) {
		this.context = context;
	}
	
	public RouteMatcher build() throws Exception {
		
		if(context == null) {
			throw new Exception("No context available.");
		}
		
		return buildInternal();
	}
	
	protected abstract RouteMatcher buildInternal() throws Exception; 
	
}
