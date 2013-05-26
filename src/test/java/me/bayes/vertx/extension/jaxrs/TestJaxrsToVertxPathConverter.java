package me.bayes.vertx.extension.jaxrs;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestJaxrsToVertxPathConverter {

	@Test
	public void testConvertPathWithVariable() {
		//given
		String path = "/username/{username : [a-zA-Z][a-zA-Z_0-9]}";
		String expected = "/username/:username";
		
		//when
		String result = JaxrsToVertxPathConverter.convertPath(path);
		
		//then
		assertEquals(expected, result);
	}
	
	@Test
	public void testConvertPathWithVariables() {
		//given
		String path = "/username/{username : [a-zA-Z][a-zA-Z_0-9]}/{lastname}";
		String expected = "/username/:username/:lastname";
		
		//when
		String result = JaxrsToVertxPathConverter.convertPath(path);
		
		//then
		assertEquals(expected, result);
	}
	
	@Test
	public void testConvertVertxPathVariables() {
		//given
		String path = "/username/:username/:lastname";
		String expected = "/username/:username/:lastname";
		
		//when
		String result = JaxrsToVertxPathConverter.convertPath(path);
		
		//then
		assertEquals(expected, result);
	}

}
