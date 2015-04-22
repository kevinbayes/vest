package me.bayes.vertx.vest.sample;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class BaseSampleProvider implements SampleProvider {

	@Override
	public String doSomething(String foo) {
		return "Hello " + foo.toLowerCase();
	}

}
