package refer.api.jaxrs.vest.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

/**
 * Created by kevinbayes on 2015/03/04.
 */
public abstract class AbstractHttpPostJsonResponder extends AbstractJsonResponder {

    public AbstractHttpPostJsonResponder(HttpServerRequest request) {
        super(request);
    }


    public final void handle(AsyncResult<Message<JsonObject>> event) {

        if(event.succeeded()) {
            onSuccess(event);
        } else {
            internalServerError(event.cause());
        }

    }

    protected abstract void onSuccess(AsyncResult<Message<JsonObject>> event);

}
