/**
 * 
 */
package me.bayes.vertx.vest.binding;

import java.util.List;

import me.bayes.vertx.vest.binding.RouteBindingHolder.MethodBinding;

/**
 * @author Kevin Bayes
 *
 */
public interface Function {

	void apply(String method, String key, List<MethodBinding> value) throws Exception;

}
