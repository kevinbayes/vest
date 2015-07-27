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
package me.bayes.vertx.vest.util;

import me.bayes.vertx.vest.VestApplication;

import javax.ws.rs.core.Context;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * A utility class used to work with the {@link Context} annotation.
 *
 * @author Kevin Bayes
 * @since 1.0
 * @version 1.0
 */
public final class ContextUtil {

	private ContextUtil() { }

	/**
	 * This method goes through the class and all the super classes searching for fields annotated with {@link Context}.
	 * It then injects the instance available in the context else null. 
	 * 
	 * @param clazz
	 * @param instance
	 * @param application
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void assignContextFields(@SuppressWarnings("rawtypes") Class clazz, Object instance, VestApplication application) throws IllegalArgumentException, IllegalAccessException {
		
		do {
		
			for(Field field : clazz.getDeclaredFields()) {
				
				if(field.getAnnotation(Context.class) != null) {
					
					final Object injectableObject = getInjectableObject(field.getType(), application.getSingletons());
					if(injectableObject != null) {
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						field.set(instance, injectableObject);
						field.setAccessible(accessible);
					}
				}
			}
			
			clazz = clazz.getSuperclass();
		
		} while(clazz != null && !Object.class.equals(clazz));
		
	}
	
	private static Object getInjectableObject(Class<?> type, Set<Object> objects) {

		for(Object object : objects) {
			if(type.isInstance(object)) {
				return object;
			}
		}
		
		return null;
	}
	
	
	
}
