package com.siweifu.utils;

import java.util.Arrays;

/**
 * Sql相关工具类
 * @title SqlUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦  
 * @version 1.0
 * @created 2015年4月8日上午10:30:40
 */
public class SqlUtils {

	/**
	 * 生成sql占位符 ?,?,?
	 * @param size
	 * @return
	 */
	public static String sqlHolder(int size) {
		String[] paras = new String[size];
		Arrays.fill(paras, "?");
		return org.apache.commons.lang3.StringUtils.join(paras, ',');
	}

}
