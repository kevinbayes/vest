package me.bayes.vertx.vest.sample;

import javax.ws.rs.ext.Provider;

@Provider
public class OtherSampleProvider implements SampleProvider {

	@Override
	public String doSomething(String foo) {
		return "Hello " + foo.toUpperCase();
	}

}
