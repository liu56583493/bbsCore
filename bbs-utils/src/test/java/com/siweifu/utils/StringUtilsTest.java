package com.siweifu.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.bbs.utils.StringUtils;

public class StringUtilsTest {

	@Test
	public void testFormat1() {
		String ss = StringUtils.format("my name is {0}, and i like {1}!", "L.cm", "java");

		String s  = "my name is L.cm, and i like java!";

		Assert.assertEquals("ok?", ss, s);
	}

	@Test
	public void testFormat2() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "L.cm");
		map.put("like", "java");

		String ss = StringUtils.format("my name is ${name}, and i like ${like}!", map);
		String s  = "my name is L.cm, and i like java!";

		Assert.assertEquals("ok?", ss, s);
	}
}
