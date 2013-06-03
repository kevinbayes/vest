vertx-route-ext
===============

A library that can be used to easily create routes with minimal code in vertx. This initial implementation will support creating REST services in vertx using jax-rs annotations.

Current Support
===============
- @Path (without regular expressions)
- @POST, @PUT, @GET, @DELETE, @OPTION and @HEAD
- @Context limited support for vertx and jsonobject

TODO
====
- Package scanning Application
- Multisource scanning Application
- @PathParam
- @Context - Generic support for all objects in the builder context.
- @Consumes
- @Produces
