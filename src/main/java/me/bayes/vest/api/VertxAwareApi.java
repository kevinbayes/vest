package refer.api.jaxrs.vest.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import me.bayes.vertx.ext.http.Responder;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevinbayes on 2015/02/08.
 */
public class VertxAwareApi {

    @Context
    private Vertx vertx;

    @Context
    private JsonObject config;

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public EventBus eb() {
        return vertx.eventBus();
    }

    public JsonObject getConfig() {
        return config;
    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public void ebSend(String address, JsonObject message, HttpServerRequest request) {
        eb().send(address, message, new DeliveryOptions().setSendTimeout(5000L), new DefaultJsonResponder(request));
    }

    public void ebSend(String address, JsonObject message,  Handler<AsyncResult<Message<JsonObject>>> responder) {
        eb().send(address, message, new DeliveryOptions().setSendTimeout(5000L), responder);
    }

    public void ebSend(String address, Map<String, String> headers, JsonObject message,  Handler<AsyncResult<Message<JsonObject>>> responder) {

        DeliveryOptions deliveryOptions = new DeliveryOptions()
                .setSendTimeout(5000L);

        headers.forEach((key, value) -> {
           deliveryOptions.addHeader(key, value);
        });

        eb().send(address, message, deliveryOptions, responder);
    }

}
