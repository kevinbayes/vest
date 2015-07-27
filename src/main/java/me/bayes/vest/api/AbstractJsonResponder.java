package refer.api.jaxrs.vest.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import me.bayes.vertx.ext.http.Responder;

import javax.ws.rs.core.MediaType;

/**
 * Created by kevinbayes on 2015/02/09.
 */
public abstract class AbstractJsonResponder implements Handler<AsyncResult<Message<JsonObject>>> {

    protected HttpServerRequest request;

    public AbstractJsonResponder(HttpServerRequest request) {
        this.request = request;
    }

    public void internalServerError(Throwable cause) {
        new Responder(request.response())
                .internalServerError()
                .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), "*")
                .putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
                .setChunked(true).end(new JsonObject().put("error", cause.getMessage()).toString());
    }

}
