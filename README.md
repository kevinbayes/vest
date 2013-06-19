vertx-route-ext aka 'vest'
==========================

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
TODO

Notes
=====
1. Section 3.7 (Matching Requests to Resource Methods) will be ignored and matching requests will be delegated to the RouteMatcher.

