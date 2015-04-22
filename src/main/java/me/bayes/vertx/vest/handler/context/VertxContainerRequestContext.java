package me.bayes.vertx.vest.handler.context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

public class VertxContainerRequestContext implements ContainerRequestContext {

	private HttpServerRequest originalRequest;
	private VestRequest vestRequest;

	private Map<String, Object> properties = new HashMap<>();

	private URI requestUri;
	private URI baseUri;

	private UriInfo uriInfo;
	private InputStream entity;

	public VertxContainerRequestContext(HttpServerRequest request) {
		this.originalRequest = request;
		this.vestRequest = new VestRequest(request);
		this.uriInfo = new VestUriInfo(request);
		originalRequest.bodyHandler(new Handler<Buffer>() {
			@Override
			public void handle(Buffer buffer) {
				entity = new ByteArrayInputStream(buffer.getBytes());
			}
		});
	}

	@Override
	public Object getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	@Override
	public void setProperty(String name, Object object) {
		properties.put(name, object);
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}

	@Override
	public UriInfo getUriInfo() {
		return this.uriInfo;
	}

	@Override
	public void setRequestUri(URI requestUri) {
		this.requestUri = requestUri;
	}

	@Override
	public void setRequestUri(URI baseUri, URI requestUri) {
		this.baseUri = baseUri;
		this.requestUri = requestUri;
	}

	@Override
	public Request getRequest() {
		return vestRequest;
	}

	@Override
	public String getMethod() {
		return originalRequest.method();
	}

	@Override
	public void setMethod(String method) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public MultivaluedMap<String, String> getHeaders() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>(originalRequest.headers()
				.size());
		MultiMap originalHeaders = originalRequest.headers();
		for (Entry<String, String> entry : originalHeaders) {
			headers.add(entry.getKey(), entry.getValue());
		}
		// TODO - do it in constructor
		return headers;
	}

	@Override
	public String getHeaderString(String name) {
		return originalRequest.headers().get(name);
	}

	@Override
	public Date getDate() {
		// TODO
		return null;
	}

	@Override
	public Locale getLanguage() {
		// TODO
		return null;
	}

	@Override
	public int getLength() {
		// TODO
		return -1;
	}

	@Override
	public MediaType getMediaType() {
		String mediaType = originalRequest.headers().get("Content-Type");
		return toMediaType(mediaType);
	}

	private MediaType toMediaType(String mediaType) {
		return MediaType.valueOf(mediaType);// TODO wont work
	}

	@Override
	public List<MediaType> getAcceptableMediaTypes() {
		String acceptable = originalRequest.headers().get("Accept");
		String[] acceptables = acceptable.split(",");
		List<MediaType> mediaTypes = new ArrayList<>();
		for (String mediaType : acceptables) {
			mediaTypes.add(toMediaType(StringUtils.substringBefore(mediaType, ";"))); // TODO q support
		}
		return mediaTypes;
	}

	@Override
	public List<Locale> getAcceptableLanguages() {
		String acceptable = originalRequest.headers().get("Accept-Language");
		String[] acceptables = acceptable.split(",");
		List<Locale> locales = new ArrayList<>();
		for (String locale : acceptables) {
			locales.add(Locale.forLanguageTag(locale));
		}
		return locales;
	}

	@Override
	public Map<String, Cookie> getCookies() {
		throw new NotImplementedException("TODO");
	}

	@Override
	public boolean hasEntity() {
		throw new NotImplementedException("TODO");
	}

	@Override
	public InputStream getEntityStream() {
		return entity;
	}

	@Override
	public void setEntityStream(InputStream input) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public SecurityContext getSecurityContext() {
		return null; // TODO
	}

	@Override
	public void setSecurityContext(SecurityContext context) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void abortWith(Response response) {
		originalRequest.response().setStatusCode(response.getStatus()).end();// TODO
	}

	private class VestUriInfo implements UriInfo {

		private HttpServerRequest rsRequest;
		private String decodedPath;
		private String encodedPath;
		private List<PathSegment> decodedPathSegments;
		private List<PathSegment> encodedPathSegments;
		private URI absolutePathUri;
		private MultivaluedMap<String, String> decodedTemplateValues;
		private MultivaluedMap<String, String> encodedTemplateValues;
		private MultivaluedMap<String, String> decodedQueryParameters;
		private MultivaluedMap<String, String> encodedQueryParameters;
		private final LinkedList<String> paths = new LinkedList<String>();
		private List<Object> resources;

		public VestUriInfo(HttpServerRequest request) {
			this.rsRequest = request;
		}

		@Override
		public String getPath() {
			return getPath(true);
		}

		@Override
		public String getPath(boolean decode) {
			if (decode) {
				if (decodedPath != null)
					return decodedPath;

				return decodedPath = UriComponent.decode(getEncodedPath(), UriComponent.Type.PATH);
			} else {
				return getEncodedPath();
			}
		}

		private String getEncodedPath() {
			if (encodedPath != null)
				return encodedPath;

			return encodedPath = getRequestUri().getRawPath().substring(getBaseUri().getRawPath().length());
		}

		@Override
		public List<PathSegment> getPathSegments() {
			return getPathSegments(true);
		}

		@Override
		public List<PathSegment> getPathSegments(boolean decode) {
			if (decode) {
				if (decodedPathSegments != null)
					return decodedPathSegments;

				return decodedPathSegments = UriComponent.decodePath(getPath(false), true);
			} else {
				if (encodedPathSegments != null)
					return encodedPathSegments;

				return encodedPathSegments = UriComponent.decodePath(getPath(false), false);
			}
		}

		@Override
		public URI getRequestUri() {
			return rsRequest.absoluteURI();
		}

		@Override
		public UriBuilder getRequestUriBuilder() {
			return UriBuilderImpl.fromUri(getRequestUri());
		}

		@Override
		public URI getAbsolutePath() {
			if (absolutePathUri != null)
				return absolutePathUri;

			return absolutePathUri = UriBuilderImpl.fromUri(requestUri).replaceQuery("").fragment("").build();
		}

		@Override
		public UriBuilder getAbsolutePathBuilder() {
			return UriBuilderImpl.fromUri(getAbsolutePath());
		}

		@Override
		public URI getBaseUri() {
			return rsRequest.absoluteURI(); // TODO
		}

		@Override
		public UriBuilder getBaseUriBuilder() {
			return UriBuilderImpl.fromUri(getBaseUri());
		}

		@Override
		public MultivaluedMap<String, String> getPathParameters() {
			return getPathParameters(true);
		}

		@Override
		public MultivaluedMap<String, String> getPathParameters(boolean decode) {
			if (decode) {
				if (decodedTemplateValues != null) {
					return decodedTemplateValues;
				}

				decodedTemplateValues = new MultivaluedHashMap<>();
				for (Map.Entry<String, List<String>> e : encodedTemplateValues.entrySet()) {
					List<String> l = new ArrayList<String>();
					for (String v : e.getValue()) {
						l.add(UriComponent.decode(v, UriComponent.Type.PATH));
					}
					decodedTemplateValues.put(UriComponent.decode(e.getKey(), UriComponent.Type.PATH_SEGMENT), l);
				}

				return decodedTemplateValues;
			} else {
				return encodedTemplateValues;
			}
		}

		@Override
		public MultivaluedMap<String, String> getQueryParameters() {
			return getQueryParameters(true);
		}

		@Override
		public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
			if (decode) {
				if (decodedQueryParameters != null) {
					return decodedQueryParameters;
				}
				decodedQueryParameters = UriComponent.decodeQuery(rsRequest.query(), decode);
				return decodedQueryParameters;
			} else {
				if (encodedQueryParameters != null) {
					return encodedQueryParameters;
				}
				encodedQueryParameters = UriComponent.decodeQuery(rsRequest.query(), decode);
				return encodedQueryParameters;
			}
		}

		@Override
		public List<String> getMatchedURIs() {
			return getMatchedURIs(true);
		}

		@Override
		public List<String> getMatchedURIs(boolean decode) {
			List<String> result;
			if (decode) {
				result = new ArrayList<String>(paths.size());

				for (String path : paths) {
					result.add(UriComponent.decode(path, UriComponent.Type.PATH));
				}
			} else {
				result = paths;
			}
			return Collections.unmodifiableList(result);
		}

		@Override
		public List<Object> getMatchedResources() {
			return resources;
		}

		@Override
		public URI resolve(URI uri) {
			return uri.resolve(baseUri).normalize();
		}

		@Override
		public URI relativize(URI uri) {
			return uri.relativize(baseUri).normalize();
		}

	}

	private class VestRequest implements Request {

		private HttpServerRequest rsRequest;

		public VestRequest(HttpServerRequest request) {
			this.rsRequest = request;
		}

		@Override
		public String getMethod() {
			return rsRequest.method();
		}

		@Override
		public Variant selectVariant(List<Variant> variants) {
			return null;
		}

		@Override
		public ResponseBuilder evaluatePreconditions(EntityTag eTag) {
			return null;
		}

		@Override
		public ResponseBuilder evaluatePreconditions(Date lastModified) {
			return null;
		}

		@Override
		public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
			return null;
		}

		@Override
		public ResponseBuilder evaluatePreconditions() {
			return null;
		}

	}
}
