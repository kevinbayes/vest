Vest Framework
==============

Taking inspiration from the reference implementation of Jaxrs "Jersey", this implementation of Jaxrs for vertx is named "Vest".

A library that can be used to easily create routes with minimal code in vertx. This initial implementation will support creating REST services in vertx using jax-rs 2.0 (JSR339). The idea around this implementation is to create a framework on vertx, meaning that you would not be required to embed an implemenation such as Jersey, but rather leverage off the existing vertx platform.

Current Support
===============
- @Path (without regular expressions)
- @POST, @PUT, @GET, @DELETE, @OPTION and @HEAD
- @Context limited support for vertx and jsonobject
- Package scanning Application
- Multisource scanning Application
- @PathParam
- @HeaderParam Extracts the value of a header.
- @QueryParam Extracts the value of a URI query parameter.

TODO
====
Check issues

Introduction
============
The vestframework is an implementation of the JSR339 specification. It allows you to use the specification to develop Http REST services using vertx.
The framework has the following 3 main components:
- BuilderContext
- RouteMatcherBuilder
- VestApplication

The BuilderContext provides the RouteMatcherBuilder context for building a RouteMatcher from classes using the jaxrs annotations. VestApplication is the abstract implementation of the jaxrs Application that is used by the builder to initialize the rest services.

Quick start
===========
TODO

Example
=======
There are 3 steps to get up and running in the simplest case:
1. Create an implementation of the VestApplication and annotate it with @ApplicationPath to set the context.
```java
@ApplicationPath("/sample")
public class VertxApplication extends me.bayes.vertx.vest.jaxrs.VestApplication {
}

```

2. Annotate your classes that handle http requests with the jaxrs annotations. Note the first parameter must always be the HttpServerRequest.
```java
@Path("/entity")
public class EntityHandler extends VertxAwareHandler {
		
	@GET
	public void get(HttpServerRequest serverRequest) {
		serverRequest.response.headers().put("Content-Type", "text/html; charset=UTF-8");
		serverRequest.response.end("<html><body><h1>Hello from vest!</h1></body></html>");
	}

}
```

3. Use the JaxrsRoute
```java
Vertx vertx = Vertx.newVertx();
VestApplication application = new VertxApplication();
application.addPackagesToScan("<package to scan>");

HttpServer server = vertx.createHttpServer();

application.addSingleton(vertx);

RouteMatcherBuilder builder = new JaxrsRouteMatcherBuilder(application);
server.requestHandler(builder.build());

server.listen(18080);
```
4. Now navigate to http://localhost:18080/sample/entity

Notes
=====
1. Section 3.7 (Matching Requests to Resource Methods) will be ignored and matching requests will be delegated to the RouteMatcher.

