/**
 * 
 */
package refer.api.jaxrs.vest.binding;


import java.util.List;

/**
 * @author Kevin Bayes
 *
 */
public interface Function {

	void apply(String method, String key, List<RouteBindingHolder.MethodBinding> value) throws Exception;

}
