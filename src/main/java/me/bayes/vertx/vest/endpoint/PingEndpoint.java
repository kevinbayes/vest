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
package me.bayes.vertx.vest.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import me.bayes.vertx.vest.handler.VertxAwareHandler;


@Path("/ping")
public class PingEndpoint extends VertxAwareHandler {

	@GET
	@Produces({MediaType.TEXT_PLAIN})
	public void ping(HttpServerRequest request, HttpServerResponse response) {
		response.headers().set(HttpHeaders.CONTENT_ENCODING, "UTF-8");
		response.end("ping");
	}

	@GET
	@Path("{string}")
	@Produces({MediaType.TEXT_PLAIN})
	public void echo(HttpServerRequest request, @PathParam("string") final String echoString) {
		request.response().headers().set(HttpHeaders.CONTENT_ENCODING, "UTF-8");
		request.response().end(String.format("echo: %s", echoString));
	}
	
}
