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
package refer.api.jaxrs.vest;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.apex.Route;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import refer.api.jaxrs.vest.binding.DefaultRouteBindingHolderFactory;
import refer.api.jaxrs.vest.binding.RouteBindingHolder;
import refer.api.jaxrs.vest.util.DefaultParameterResolver;
import refer.api.jaxrs.vest.util.ParameterResolver;
import refer.api.jaxrs.vest.util.UriPathUtil;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <pre>
 * The {@link DefaultRouterBuilder} is a basic {@link RouterBuilder} that
 * uses the {@link javax.ws.rs.core.Application} to get classes that are candidates for adding to
 * the {@link Router}.
 *
 * This simple logic searches the given classes for the {@link javax.ws.rs.Path} annotation
 * and uses that with the combination of the {@link javax.ws.rs.Path} anotation on method level
 * to create the route that the method will handle. Once the path is established
 * a {@link Handler} is added to the route matcher for the specific path. The method
 * associated with the {@link Handler} is derived from either one of the {@link javax.ws.rs.GET},
 * {@link javax.ws.rs.POST}, {@link javax.ws.rs.PUT}, {@link javax.ws.rs.DELETE}, {@link javax.ws.rs.OPTIONS} xor {@link javax.ws.rs.HEAD} annotations
 * on the method.
 *
 * Spec: 3.3.5 HEAD and OPTIONS is only supported for explicit request
 *
 * TODO: {@link javax.ws.rs.core.Context} annotation support.
 * TODO: {@link javax.ws.rs.Consumes}
 * TODO: {@link javax.ws.rs.Produces}
 * TODO: Add verticle reference injection through {@link javax.ws.rs.core.Context} annotation.
 * TODO: Add default behaviour for HEAD and OPTIONS according to 3.3.5.
 * TODO: Handle exceptions better. 3.3.4 Exceptions
 * TODO: 3.8 Determining the MediaType of Responses
 * TODO: Allow multiple endpoint implementation to have the same uri but different consumes and produces parameters.
 * </pre>
 *
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 *
 */
public class DefaultRouterBuilder extends AbstractRouterBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultRouterBuilder.class);

	private final ParameterResolver parameterResolver;

	/**
	 * Requires a {@link refer.api.jaxrs.vest.VestApplication}.
	 * @param application
	 */
	public DefaultRouterBuilder(final VestApplication application) {
		super(application, new DefaultRouteBindingHolderFactory(application));
		this.parameterResolver = new DefaultParameterResolver(application);
	}


	/*
	 * (non-Javadoc)
	 * @see me.bayes.vertx.extension.RouteMatcherBuilder#build(me.bayes.vertx.extension.BuilderContext)
	 */
	protected Router buildInternal() throws Exception {

		addRoutes(router);

		return router;
	}
	/**
	 * The simplest case adds routes and delegates the execution to the method annotated with the {@link javax.ws.rs.Path} annotation.
	 * 
	 * @param router
	 * @throws Exception
	 */
	private void addRoutes(final Router router) throws Exception {

        router.route().handler(BodyHandler.create());

        this.bindingHolder.foreach(
                (method, key, bindings) -> {

                    //3.7 Matching Requests to Resource Methods is delegated to vertx route matcher
                    final String finalPath = UriPathUtil.convertPath(key);

                    Route route = router.route(HttpMethod.valueOf(method), finalPath);
                    route
                            .handler(event -> {
                                final HttpServerRequest request = event.request();
                                final HttpServerResponse response = event.response();

                                try {
                                    RouteBindingHolder.MethodBinding binding = null;

                                    String acceptsHeader = request.headers().get(HttpHeaders.ACCEPT);
                                    String contentTypeHeader = request.headers().get(HttpHeaders.CONTENT_TYPE);

                                    if (!request.headers().isEmpty()) {
                                        for (RouteBindingHolder.MethodBinding binding_ : bindings) {
                                            if (binding_.hasConsumes(contentTypeHeader) && binding_.hasProduces(acceptsHeader)) {
                                                binding = binding_;
                                                break;
                                            }
                                            if (binding_.hasConsumes(contentTypeHeader) && acceptsHeader == null) {
                                                binding = binding_;
                                                break;
                                            }
                                            if (contentTypeHeader == null && binding_.hasProduces(acceptsHeader)) {
                                                binding = binding_;
                                                break;
                                            }
                                        }

                                    }

                                    if (binding == null && bindings.size() > 0) {
                                        binding = bindings.get(0);
                                    } else if (binding == null) {
                                        throw new Exception("No route that supports accepts given HTTP parameters");
                                    }

                                    Method bindingMethod = binding.getMethod();

                                    final Class<?>[] parameterTypes = bindingMethod.getParameterTypes();
                                    if (parameterTypes.length == 0 || !parameterTypes[0].equals(HttpServerRequest.class)) {
                                        LOG.warn("Classes marked with a HttpMethod must have at least one parameter. The first parameter should be HttpServerRequest.");
                                        return;
                                    }

                                    final Annotation[][] parameterAnnotations = bindingMethod.getParameterAnnotations();
                                    final String[] produces = binding.getProduces();
                                    final String[] consumes = binding.getConsumes();
                                    String producesMediaType = null;

                                    if (acceptsHeader != null) {
                                        if (produces != null && produces.length > 0) {
                                            for (String type : produces) {
                                                if (acceptsHeader.contains(type)) {
                                                    producesMediaType = type;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (producesMediaType == null) {
                                        producesMediaType =
                                                (produces != null && produces.length == 1) ?
                                                        produces[0] : MediaType.TEXT_PLAIN;
                                    }

                                    if (contentTypeHeader != null) {
                                        if (consumes != null && consumes.length > 0) {
                                            for (String type : consumes) {
                                                if (acceptsHeader.contains(type)) {
                                                    contentTypeHeader = type;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    request.response().headers().set(HttpHeaders.CONTENT_TYPE, producesMediaType);

                                    final Object[] parameters = new Object[parameterTypes.length];
                                    parameters[0] = request;

                                    boolean isBodyResolved = false;
                                    int objectParameterIndex = -1;

                                    if (parameters.length > 1) {
                                        for (int i = 1; i < parameters.length; i++) {
                                            if (parameterAnnotations[i].length > 0 ||
                                                    RoutingContext.class.equals(parameterTypes[i]) ||
                                                    HttpServerResponse.class.equals(parameterTypes[i])) {

                                                parameters[i] = parameterResolver.resolve(bindingMethod, parameterTypes[i], parameterAnnotations[i], event);

                                            } else if (JsonObject.class.equals(parameterTypes[i])) {
                                                objectParameterIndex = i;
                                                isBodyResolved = true;
                                            }
                                        }
                                    }

                                    if (isBodyResolved) {
                                        parameters[objectParameterIndex] = event.getBodyAsJson();
                                        bindingMethod.invoke(binding.getDelegate(), parameters);
                                    } else {
                                        bindingMethod.invoke(binding.getDelegate(), parameters);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LOG.error("Exception occurred.", e);

                                    if (exceptionHandler != null) {
                                        exceptionHandler.handle(request);
                                    } else {
                                        request.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
                                        request.response().setStatusCode(500);
                                        request.response().setStatusMessage("Internal server error");
                                        request.response().end();
                                    }
                                }
                           });
        });
    }


}