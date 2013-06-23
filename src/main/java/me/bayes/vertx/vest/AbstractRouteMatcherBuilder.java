/**
 * 
 */
package me.bayes.vertx.vest;



import org.vertx.java.core.http.RouteMatcher;

/**
 * @author Kevin Bayes
 */
public abstract class AbstractRouteMatcherBuilder implements RouteMatcherBuilder {

	protected VestApplication application;
	protected RouteMatcher routeMatcher;

	public AbstractRouteMatcherBuilder(VestApplication application) {
		super();
		this.application = application;
		this.routeMatcher = new RouteMatcher();
	}

	public VestApplication getApplication() {
		return application;
	}

	public void setApplication(VestApplication application) {
		this.application = application;
	}
	
	public RouteMatcher build() throws Exception {
		
		if(application == null) {
			throw new Exception("No context available.");
		}
		
		return buildInternal();
	}
	
	protected abstract RouteMatcher buildInternal() throws Exception; 
	
}
