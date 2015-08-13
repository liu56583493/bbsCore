package com.siweifu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfinal.kit.StrKit;

/**
 * 正则表达式工具
 * @title RegexUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦  
 * @version 1.0
 * @created 2015年4月10日上午11:40:45
 */
public class RegexUtils {

	/**
	 * 用户名
	 */
	public static final String USER_NAME = "^[a-zA-Z\\u4E00-\\u9FA5][a-zA-Z0-9_\\u4E00-\\u9FA5]{1,11}$";
	/**
	 * 密码
	 */
	public static final String USER_PASSWORD = "^.{6,32}$";
	/**
	 * 邮箱
	 */
	public static final String EMAIL = "^\\w+([-+.]*\\w+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+$";	
	/**
	 * 手机号
	 */
	public static final String PHONE = "^1[34578]\\d{9}$";
	/**
	 * 手机号或者邮箱
	 */
	public static final String EMAIL_OR_PHONE = EMAIL + "|" + PHONE;
	
	/**
	 * URL路径
	 */
	public static final String URL = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})(:[\\d]+)?([\\/\\w\\.-]*)*\\/?$";

	/**
	 * 匹配话题
	 */
	public static final String TOPIC = ".*(#.*#).*";

	/**
	 * 身份证校验，初级校验，具体规则有一套算法
	 */
	public static final String ID_CARD = "^\\d{15}$|^\\d{17}([0-9]|X)$";

	/**
	 *
	 * 编译传入正则表达式和字符串去匹配,忽略大小写
	 * @param regex
	 * @param beTestString
	 * @return
	 */
	public static boolean match(String regex, String beTestString) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(beTestString);
		return matcher.matches();
	}

	/**
	 *
	 * 编译传入正则表达式在字符串中寻找，如果匹配到则为true
	 * @param regex
	 * @param beTestString
	 * @return
	 */
	public static boolean find(String regex, String beTestString) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(beTestString);
		return matcher.find();
	}

	/**
	 * 编译传入正则表达式在字符串中寻找，如果找到返回第一个结果<br/>
	 * 找不到返回null
	 * @param regex
	 * @param beFoundString
	 * @return
	 */
	public static String findResult(String regex, String beFoundString) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(beFoundString);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * 隐藏手机号中间4位
	 * @param phone
	 * @return String
	 */
	public static String ecodingPhone(String phone) {
		if (StrKit.isBlank(phone)) {
			return "";
		}
		if (match(PHONE, phone)) {
			String begin = phone.substring(0, 3);
			String end   = phone.substring(7, phone.length());
			return begin + "****" + end;
		}
		return phone;
	}

	/**
	 * 取出话题#xxxx#
	 * @param matchTopic
	 * @return String
	 */
	public static String findTopic(String content) {
		return content.replaceAll(TOPIC, "$1");
	}
	
	private static String topicRegex = "#([^\\s|^\\#|.*]+)#"; //不能有空格的 ##之间的内容
	
	/**
	 * 取出话题 #xxx# 的内容
	 * @param content
	 * @return
	 */
	public static List<String> findTopicList(String content){
		Pattern pattern = Pattern.compile(topicRegex);
		Matcher m = pattern.matcher(content);
		List<String> list = null;
		if(m.matches()){
			list = new ArrayList<String>();
		}
		while (m.find()) {
	            String topic = m.group();
	            list.add(topic);
	    }
		return list;
	}

	public static void main(String[] args) {
		System.out.println(match("^\\d{15}$|^\\d{17}([0-9]|X)$", "42108719891030475"));
	}

}