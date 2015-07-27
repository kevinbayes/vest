/**
 * Copyright 2014 Bayes Technologies
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
package refer.api.jaxrs.vest.binding;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * All the properties that the context should contain.
 * 
 * TODO: Extract this to a concern on its own. This should have a builder so it
 * can be substituted for a custom implementation.
 * 
 * @author Kevin Bayes
 * @version 1.1
 * @since 1.1
 */
public class RouteBindingHolder {
	
	//	private static final String KEY_TEMPLATE = "%s %s";
	
	private final Map<String, List<MethodBinding>> getBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> putBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> postBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> deleteBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> optionsBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> headBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> traceBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> connectBindings = new HashMap<>(0);
	private final Map<String, List<MethodBinding>> patchBindings = new HashMap<>(0);
	
	private final Map<String, List<MethodBinding>> emptyMap = Collections.unmodifiableMap(new HashMap<String, List<MethodBinding>>(0));
	
	public void foreach(Function function) {
		foreach("GET", function, getBindings);
		foreach("PUT", function, putBindings);
		foreach("POST", function, postBindings);
		foreach("DELETE", function, deleteBindings);
		foreach("OPTIONS", function, optionsBindings);
		foreach("HEAD", function, headBindings);
		foreach("TRACE", function, traceBindings);
		foreach("CONNECT", function, connectBindings);
		foreach("PATCH", function, patchBindings);
	}
	
	private void foreach(String method, Function function, Map<String, List<MethodBinding>> bindings) {
		for(Entry<String, List<MethodBinding>> binding : bindings.entrySet()) {
			applyFunction(method, function, binding);
		}
	}
	
	private void applyFunction(String method, Function function, Entry<String, List<MethodBinding>> binding) {
		try {
			function.apply(method, binding.getKey(), binding.getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	
	public void addBinding(HttpMethod httpMethod, String path,
			Consumes consumes, Produces produces, Object instance,
			Class<?> clazz, Method method) {
		addBinding(getMethodBindingMap(httpMethod), path, consumes, produces, instance, clazz, method);
	}
	
	private void addBinding(Map<String, List<MethodBinding>> bindings, String path,
			Consumes consumes, Produces produces, Object instance,
			Class<?> clazz, Method method) {
		
		if(emptyMap == bindings) {
			return;
		}
		
		List<MethodBinding> bindings_ = null;
		
		if(bindings.containsKey(path)) {
			bindings_ = bindings.get(path);
		} else {
			bindings_ = new ArrayList<>(1);
			bindings.put(path, bindings_);
		}

		bindings_.add(new MethodBinding(instance, clazz, method,
				consumes == null ? new String[]{MediaType.TEXT_PLAIN} : consumes.value(),
						produces == null ? new String[]{MediaType.TEXT_PLAIN} : produces.value()));
		
	}
	
	private Map<String, List<MethodBinding>> getMethodBindingMap(HttpMethod method) {
		return getMethodBindingMap(method.value());
	}
	
	private Map<String, List<MethodBinding>> getMethodBindingMap(String verb) {
		switch (verb) {
		case HttpMethod.DELETE:
			return deleteBindings;
		case HttpMethod.GET:
			return getBindings;
		case HttpMethod.HEAD:
			return headBindings;
		case HttpMethod.OPTIONS:
			return optionsBindings;
		case HttpMethod.POST:
			return postBindings;
		case HttpMethod.PUT:
			return putBindings;
		case "TRACE":
			return traceBindings;
		case "CONNECT":
			return connectBindings;
		case "PATCH":
			return patchBindings;
		default:
			return emptyMap;
		}
	}
	
	public MethodBinding getBinding(String method, String path, String contentType, String accepts){
		List<MethodBinding> bindings = getMethodBindingMap(method).get(path);
		if(bindings != null) {
			for(MethodBinding binding : bindings) {
				if(binding.hasConsumes(contentType) && binding.hasProduces(accepts)) {
					return binding;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * This is just a holder for object to method bindings with the produces and consumes available.
	 * This will help decide which object to call when receiving a request.
	 * 
	 * @author Kevin Bayes
	 */
	public static class MethodBinding {
		
		private final String[] consumes;
		private final String[] produces;
		private Object delegate;
		private Class<?> clazz;
		private Method method;
		
		public MethodBinding(Object delegate, Class<?> clazz, Method method, String[] consumes, String[] produces) {
			this.delegate = delegate;
			this.consumes = consumes;
			this.produces = produces;
			this.clazz = clazz;
			this.method = method;
		}

		public Object getDelegate() {
			return delegate;
		}
		
		public Class<?> getClazz() {
			return clazz;
		}

		public Method getMethod() {
			return method;
		}

		public String[] getConsumes() {
			return consumes;
		}

		public String[] getProduces() {
			return produces;
		}
		
		public boolean hasConsumes(String contentType) {
			for(String consume : consumes) {
				if(consume.equals(contentType)) {
					return true;
				}
			}
			return false;
		}
		
		public boolean hasProduces(String accepts) {
			for(String produce : produces) {
				if(produce.equals(accepts)) {
					return true;
				}
			}
			return false;
		}
		
	}



	

}
