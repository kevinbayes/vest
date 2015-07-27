package me.bayes.vertx.vest;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

/**
 * The {@link RouterBuilder} builds a {@link io.vertx.ext.web.Router} that vertx uses to
 * route messages.
 *
 * The {@link RouterBuilder} is an implementation of the strategy pattern allowing
 * you to extend this to an implementation best suited for your use case.
 *
 * @author Kevin Bayes
 * @since 1.0
 */
public interface RouterBuilder {


    /**
     * Executing this method ensures that a {@link io.vertx.ext.web.impl.RouterImpl} is built.
     *
     * @return
     * @throws Exception
     */
    Router build() throws Exception;


    /**
     * Set the application that the builder must use to build the jaxrs services.
     *
     * @param application
     */
    RouterBuilder setApplication(VestApplication application);


    /**
     * @param handler for exception;
     */
    RouterBuilder setExceptionHandler(Handler<HttpServerRequest> handler);


}
