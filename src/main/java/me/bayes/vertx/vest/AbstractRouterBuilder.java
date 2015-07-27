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
package me.bayes.vertx.vest;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.impl.RouterImpl;
import me.bayes.vertx.vest.binding.RouteBindingHolder;
import me.bayes.vertx.vest.binding.RouteBindingHolderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kevin Bayes
 */
public abstract class AbstractRouterBuilder implements RouterBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRouterBuilder.class);

	protected VestApplication application;
	protected Router router;
	protected RouteBindingHolderFactory bindingHolderFactory;
	protected RouteBindingHolder bindingHolder;
	protected Handler<HttpServerRequest> exceptionHandler;

	public AbstractRouterBuilder(VestApplication application,
                                 RouteBindingHolderFactory bindingHolderFactory) {
		super();
		this.application = application;

        for(Object singleton : application.getSingletons()) {
            if(singleton instanceof Vertx) {
                this.router = new RouterImpl((Vertx)singleton);
            }
        }

        if(this.router == null) throw new RuntimeException("Vertx must be registered with the application.");

		this.bindingHolderFactory = bindingHolderFactory;
		this.application.addSingleton(this.router, this.bindingHolder);
	}

	public VestApplication getApplication() {
		return application;
	}

	
	/*
	 * (non-Javadoc)
	 * @see me.bayes.vertx.vest.RouteMatcherBuilder#setApplication(me.bayes.vertx.vest.VestApplication)
	 */
	public RouterBuilder setApplication(VestApplication application) {
		this.application = application;
        return this;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see me.bayes.vertx.vest.RouteMatcherBuilder#build()
	 */
	public Router build() throws Exception {
		
		if(application == null) {
			LOG.error("No application was set.");
			throw new Exception("No application available.");
		}
		
		buildBindings();
		
		return buildInternal();
	}
	
	/**
	 * This builds a holder of the bindings that will be used to create the {@link io.vertx.ext.web.Router}.
	 * 
	 * @throws Exception
	 */
	protected void buildBindings() throws Exception {
		this.bindingHolder = this.bindingHolderFactory.build();
	}
	
	/**
	 * Implement this method to add your logic.
	 * 
	 * @return a {@link io.vertx.ext.web.Router}
	 * @throws Exception
	 */
	protected abstract Router buildInternal() throws Exception;
	
	/*
	 *  TODO: Add the implemetation required by the specification.
	 *  
	 * (non-Javadoc)
	 * @see me.bayes.vertx.vest.RouteMatcherBuilder#setExceptionHandler(org.vertx.java.core.Handler)
	 */
	public RouterBuilder setExceptionHandler(Handler<HttpServerRequest> handler) {
		this.exceptionHandler = handler;
        return this;
	}
	
}
