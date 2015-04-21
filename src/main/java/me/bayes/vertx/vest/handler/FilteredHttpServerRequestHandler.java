package me.bayes.vertx.vest.handler;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;

import me.bayes.vertx.vest.binding.RouteBindingHolder.MethodBinding;
import me.bayes.vertx.vest.handler.context.VertxContainerRequestContext;
import me.bayes.vertx.vest.util.ParameterResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.http.HttpServerRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FilteredHttpServerRequestHandler extends HttpServerRequestHandler {

	private static final Logger LOG = LoggerFactory.getLogger(FilteredHttpServerRequestHandler.class);
	private List<ContainerRequestFilter> containerRequestFilters;
	private List<ContainerResponseFilter> containerResponseFilters;

	public FilteredHttpServerRequestHandler(List<MethodBinding> bindings, ParameterResolver parameterResolver,
			ObjectMapper objectMapper, List<ContainerRequestFilter> containerRequestFilters,
			List<ContainerResponseFilter> containerResponseFilters) {
		super(bindings, parameterResolver, objectMapper);
		this.containerRequestFilters = containerRequestFilters;
		this.containerResponseFilters = containerResponseFilters;
	}
	
	@Override
	public void handle(HttpServerRequest request) {
		ContainerRequestContext requestContext = new VertxContainerRequestContext(request);
		for (ContainerRequestFilter containerRequestFilter : containerRequestFilters) {
			try {
				LOG.info("Run filted: " + containerRequestFilter);
				containerRequestFilter.filter(requestContext);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		super.handle(request);
		
		//TODO response filters
	}

}
