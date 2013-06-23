/**
 * 
 */
package me.bayes.vertx.vest;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.RouteMatcher;

/**
 * @author Kevin Bayes
 */
public abstract class AbstractRouteMatcherBuilder implements RouteMatcherBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRouteMatcherBuilder.class);

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
		
		final RouteMatcher routeMatcher = buildInternal();
		
		if(routeMatcher == null) {
			LOG.warn("Route matcher not built.");
		} else {
			setNoRouteFound(routeMatcher);
		}
		
		return routeMatcher;
	}
	
	protected abstract RouteMatcher buildInternal() throws Exception; 
	
	/**
	 * This method should be overridden if a better no match is needed besides the 
	 * default of a 404.
	 * 
	 * TODO: Add the implemetation required by the specification.
	 * 
	 * @param routeMatcher
	 * @throws Exception
	 */
	protected void setNoRouteFound(RouteMatcher routeMatcher) throws Exception { }
	
}
