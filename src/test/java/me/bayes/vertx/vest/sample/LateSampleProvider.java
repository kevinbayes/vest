package me.bayes.vertx.vest.sample;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(2001)
public class LateSampleProvider implements SampleProvider {

	@Override
	public String doSomething(String foo) {
		return "Hello " + foo;
	}

}
