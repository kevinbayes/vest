package refer.api.jaxrs.vest.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import me.bayes.vertx.ext.http.Responder;

import javax.ws.rs.core.MediaType;

/**
 * Created by kevinbayes on 2015/02/09.
 */
public class DefaultJsonResponder extends AbstractJsonResponder {

    public DefaultJsonResponder(HttpServerRequest request) {
        super(request);
    }

    public void handle(AsyncResult<Message<JsonObject>> event) {

        if(event.succeeded()) {
            new Responder(request.response())
                    .ok()
                    .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), "*")
                    .putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
                    .setChunked(true).end(event.result().body().toString());
        } else {
           internalServerError(event.cause());
        }


    }
}
