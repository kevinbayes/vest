vertx-route-ext
===============

A library that can be used to easily create routes with minimal code in vertx. This initial implementation will support creating REST services in vertx using jax-rs annotations.

Current Support
===============
- @Path (without regular expressions)
- @POST, @PUT, @GET, @DELETE, @OPTION and @HEAD
- @Context limited support for vertx and jsonobject
- Package scanning Application
- Multisource scanning Application

TODO
====
- Application singletons
- Application shared property map
- @PathParam
- @Consumes
- @Produces
- @MatrixParam Extracts the value of a URI matrix parameter.
- @QueryParam Extracts the value of a URI query parameter.
- @PathParam Extracts the value of a URI template parameter.
- @CookieParam Extracts the value of a cookie.
- @HeaderParam Extracts the value of a header.
- @Context Injects an instance of a supported resource.
