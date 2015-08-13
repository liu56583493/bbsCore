package com.siweifu.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * Json转换
 * 默认使用jackson
 * 再次fastJson
 * 最后使用jsonKit
 * 
 * 有报错不用管
 * 
 * @title JsonUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年5月13日下午4:58:33
 */
public class JsonUtils {

	/**
	 * 将model转为json字符串
	 * @param model
	 */
	public static String toJson(Model<? extends Model<?>> model) {
		return toJson(CPI.getAttrs(model));
	}

	/**
	 * 将Collection<Model>转换为json字符串
	 * @param models
	 * @return
	 */
	public static String toJson(Collection<Model<? extends Model<?>>> models) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (Model<? extends Model<?>> model : models) {
			list.add(CPI.getAttrs(model));
		}
		return toJson(list);
	}

	/**
	 * 将 record 转为json字符串
	 * @param record
	 * @return
	 */
	public static String toJson(Record record) {
		return toJson(record.getColumns());
	}

	/**
	 * 将Collection<Model>转换为json字符串
	 * @param models
	 * @return
	 */
	public static String toJson(List<Record> records) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for (Record record : records) {
			list.add(record.getColumns());
		}
		return toJson(list);
	}

	private static final JsonDelegate delegate;

	static {
		JsonDelegate delegateToUse = null;
		// com.fasterxml.jackson.databind.ObjectMapper?
		if (ClassUtil.isPresent("com.fasterxml.jackson.databind.ObjectMapper", JsonUtils.class.getClassLoader())) {
			delegateToUse = new JacksonDelegate();
		}
		// com.alibaba.fastjson.JSONObject?
		else if (ClassUtil.isPresent("com.alibaba.fastjson.JSONObject", JsonUtils.class.getClassLoader())) {
			delegateToUse = new FastJsonDelegate();
		}
		// com.jfinal.kit.JsonKit
		else if (ClassUtil.isPresent("com.jfinal.kit.JsonKit", JsonUtils.class.getClassLoader())) {
			delegateToUse = new JsonKitDelegate();
		}
		delegate = delegateToUse;
	}

	/**
	 * Json 委托，默认使用
	 * 默认使用jackson
	 * 再次fastJson
	 * 最后使用jsonKit
	 */
	private interface JsonDelegate {
		// 对象转json
		String toJson(Object object);
		// json转对象
		<T> T decode(String jsonString, Class<T> valueType);
	}

	/**
	 * jackson委托
	 */
	private static class JacksonDelegate implements JsonDelegate {

		@Override
		public String toJson(Object object) {
			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			try {
				return objectMapper.writeValueAsString(object);
			} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public <T> T decode(String jsonString, Class<T> valueType) {
			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			try {
				return objectMapper.readValue(jsonString, valueType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * fastJson委托
	 */
	private static class FastJsonDelegate implements JsonDelegate {

		@Override
		public String toJson(Object object) {
			return com.alibaba.fastjson.JSONObject.toJSONString(object);
		}

		@Override
		public <T> T decode(String jsonString, Class<T> valueType) {
			return JSON.parseObject(jsonString, valueType);
		} 

	}

	/**
	 * JsonKit委托
	 */
	private static class JsonKitDelegate implements JsonDelegate {

		@Override
		public String toJson(Object object) {
			return com.jfinal.kit.JsonKit.toJson(object);
		}

		@Override
		public <T> T decode(String jsonString, Class<T> valueType) {
			throw new RuntimeException("Jackson, Fastjson are not supported~");
		}

	}

	/**
	 * 将 Object 转为json字符串
	 * @param record
	 * @return
	 */
	public static String toJson(Object object) {
		if (delegate == null) {
			throw new RuntimeException("Jackson, Fastjson and JsonKit are not supported");
		}
		return delegate.toJson(object);
	}

	/**
	 * 将 json字符串 转为Object
	 * @param jsonString
	 * @param valueType
	 * @return
	 */
	public static <T> T decode(String jsonString, Class<T> valueType) {
		if (delegate == null) {
			throw new RuntimeException("Jackson, Fastjson and JsonKit are not supported");
		}
		return delegate.decode(jsonString, valueType);
	}
}
