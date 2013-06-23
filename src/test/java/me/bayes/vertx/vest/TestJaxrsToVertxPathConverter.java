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
package me.bayes.vertx.vest;

import static org.junit.Assert.*;

import me.bayes.vertx.vest.util.UriPathUtil;

import org.junit.Test;

public class TestJaxrsToVertxPathConverter {

	@Test
	public void testConvertPathWithVariable() {
		//given
		String path = "/username/{username : [a-zA-Z][a-zA-Z_0-9]}";
		String expected = "/username/:username";
		
		//when
		String result = UriPathUtil.convertPath(path);
		
		//then
		assertEquals(expected, result);
	}
	
	@Test
	public void testConvertPathWithVariables() {
		//given
		String path = "/username/{username : [a-zA-Z][a-zA-Z_0-9]}/{lastname}";
		String expected = "/username/:username/:lastname";
		
		//when
		String result = UriPathUtil.convertPath(path);
		
		//then
		assertEquals(expected, result);
	}
	
	@Test
	public void testConvertVertxPathVariables() {
		//given
		String path = "/username/:username/:lastname";
		String expected = "/username/:username/:lastname";
		
		//when
		String result = UriPathUtil.convertPath(path);
		
		//then
		assertEquals(expected, result);
	}

}
