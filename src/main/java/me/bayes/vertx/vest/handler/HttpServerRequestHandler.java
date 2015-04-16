package me.bayes.vertx.vest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.bayes.vertx.vest.binding.RouteBindingHolder;
import me.bayes.vertx.vest.util.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class HttpServerRequestHandler implements Handler<HttpServerRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpServerRequestHandler.class);

    private final List<RouteBindingHolder.MethodBinding> bindings;
    private List<RouteBindingHolder.MethodBinding> delegates;
    private final ParameterResolver parameterResolver;
    private final ObjectMapper objectMapper;

    public HttpServerRequestHandler(List<RouteBindingHolder.MethodBinding> bindings, ParameterResolver parameterResolver,
                                    ObjectMapper objectMapper) {
        this.bindings = bindings;
        this.delegates = bindings;
        this.parameterResolver = parameterResolver;
        this.objectMapper = objectMapper;
    }

    public void handle(HttpServerRequest request) {

        try {
            RouteBindingHolder.MethodBinding binding = null;
            Method method;

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

            if (binding == null && delegates.size() > 0) {
                binding = delegates.get(0);
            } else if (binding == null) {
                throw new Exception("No route that supports accepts given HTTP parameters");
            }

            method = binding.getMethod();

            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0 || !parameterTypes[0].equals(HttpServerRequest.class)) {
                LOG.warn("Classes marked with a HttpMethod must have at least one parameter. The first parameter should be HttpServerRequest.");
                return;
            }

            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
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
            boolean deserializeUsingJackson = false;
            int objectParameterIndex = -1;

            if (parameters.length > 1) {
                for (int i = 1; i < parameters.length; i++) {
                    if (hasJaxRsAnnotations(parameterAnnotations[i]) ||
                            HttpServerResponse.class.equals(parameterTypes[i])) {

                        parameters[i] = parameterResolver.resolve(method, parameterTypes[i], parameterAnnotations[i], request);

                    } else if (JsonObject.class.equals(parameterTypes[i])) {
                        objectParameterIndex = i;
                        isBodyResolved = true;
                        deserializeUsingJackson = false;
                    } else {
                        objectParameterIndex = i;
                        isBodyResolved = true;
                        deserializeUsingJackson = true;
                    }
                }
            }

            if (isBodyResolved) {
                resolvedBodyDelegate(binding.getDelegate(), request, objectParameterIndex, method, parameters, parameterTypes, deserializeUsingJackson);
            } else {
                delegate(binding.getDelegate(), method, parameters);
            }

        } catch (Exception e) {
            handleException(request, e);
        }
    }

    private void delegate(Object delegate, Method method, Object[] parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(delegate, parameters);
    }

    private void resolvedBodyDelegate(final Object delegate, final HttpServerRequest request,
                                      final int objectParameterIndex, final Method method,
                                      final Object[] parameters, final Class<?>[] parameterTypes, final boolean deserializeUsingJackson) {

        final Buffer buffer = new Buffer();

        request.dataHandler(new Handler<Buffer>() {

            @Override
            public void handle(Buffer internalBuffer) {
                buffer.appendBuffer(internalBuffer);
            }

        });

        request.endHandler(new Handler<Void>() {

            @Override
            public void handle(Void event) {
                String jsonString = buffer.toString();
                if (deserializeUsingJackson) {
                    try {
                        parameters[objectParameterIndex] = objectMapper.readValue(jsonString, parameterTypes[objectParameterIndex]);
                    } catch (IOException e) {
                        handleException(request, e);
                    }
                } else {
                    parameters[objectParameterIndex] = new JsonObject(jsonString);
                }

                try {
                    method.invoke(delegate, parameters);
                } catch (Exception e) {
                    handleException(request, e);
                }
            }
        });
    }

    private void handleException(HttpServerRequest request, Exception e) {
        LOG.error("Exception occurred.", e);

        request.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        request.response().setStatusCode(INTERNAL_SERVER_ERROR.code());
        request.response().setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase());
        request.response().end();
    }

    private boolean hasJaxRsAnnotations(Annotation[] parameterAnnotation) {
        for (Annotation annotation : parameterAnnotation) {
            if (annotation.annotationType() == BeanParam.class ||
                    annotation.annotationType() == CookieParam.class ||
                    annotation.annotationType() == FormParam.class ||
                    annotation.annotationType() == HeaderParam.class ||
                    annotation.annotationType() == MatrixParam.class ||
                    annotation.annotationType() == PathParam.class ||
                    annotation.annotationType() == QueryParam.class) {
                return true;
            }
        }
        return false;
    }
}
