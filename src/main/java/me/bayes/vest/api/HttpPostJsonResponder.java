package refer.api.jaxrs.vest.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import me.bayes.vertx.ext.http.Responder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by kevinbayes on 2015/02/09.
 */
public class HttpPostJsonResponder extends AbstractJsonResponder {

    private String baseUri;

    public HttpPostJsonResponder(HttpServerRequest request) {
        this(request, request.absoluteURI());
    }

    public HttpPostJsonResponder(HttpServerRequest request, String baseUri) {
        super(request);
        this.baseUri = baseUri;
    }

    public void handle(AsyncResult<Message<JsonObject>> event) {

        if(event.succeeded()) {
            new Responder(request.response())
                    .created()
                    .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), "*")
                    .putHeader(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
                    .location(baseUri + "/" + event.result().body().getValue("_id"))
                    .setChunked(true).end(event.result().body().toString());
        } else {
            internalServerError(event.cause());
        }


    }
}
