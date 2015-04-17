package me.bayes.vertx.vest.handler.context;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServerRequest;

public class VertxContainerRequestContext implements ContainerRequestContext {

	private HttpServerRequest originalRequest;

	private Map<String, Object> properties = new HashMap<>();

	private URI requestUri;

	private URI baseUri;

	public VertxContainerRequestContext(HttpServerRequest request) {
		this.originalRequest = request;
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
		throw new NotImplementedException("TODO");
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
		throw new NotImplementedException("TODO");
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
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>(originalRequest.headers().size());
		MultiMap originalHeaders = originalRequest.headers();
		for (Entry<String, String> entry : originalHeaders) {
			headers.add(entry.getKey(), entry.getValue());
		}
		//TODO - do it in constructor
		return headers;
	}

	@Override
	public String getHeaderString(String name) {
		return originalRequest.headers().get(name);
	}

	@Override
	public Date getDate() {
		//TODO
		return null;
	}

	@Override
	public Locale getLanguage() {
		//TODO
		return null;
	}

	@Override
	public int getLength() {
		//TODO
		return -1;
	}

	@Override
	public MediaType getMediaType() {
		String mediaType = originalRequest.headers().get("Content-Type");
		return toMediaType(mediaType);
	}

	private MediaType toMediaType(String mediaType) {
		return MediaType.valueOf(mediaType);//TODO wont work
	}
	
	@Override
	public List<MediaType> getAcceptableMediaTypes() {
		String acceptable = originalRequest.headers().get("Accept");
		String[] acceptables = acceptable.split(",");
		List<MediaType> mediaTypes = new ArrayList<>();
		for (String mediaType : acceptables) {
			mediaTypes.add(toMediaType(StringUtils.substringBefore(mediaType, ";"))); //TODO q support
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
		throw new NotImplementedException("TODO");
	}

	@Override
	public void setEntityStream(InputStream input) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public SecurityContext getSecurityContext() {
		return null; //TODO
	}

	@Override
	public void setSecurityContext(SecurityContext context) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void abortWith(Response response) {
		originalRequest.response().setStatusCode(response.getStatus()).end();//TODO
	}

}
