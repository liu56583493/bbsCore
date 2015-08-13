package com.siweifu.utils;

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Model;

/**
 * Model工具类
 * @title ModelUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年5月3日下午5:19:08
 */
public class ModelUtils {

	/**
	 * copy 老model的属性到新model
	 * @param src 源model
	 * @param dist 新model
	 */
	public static void copy(Model<?> src, Model<?> dist) {
		dist.setAttrs(CPI.getAttrs(src));
	}

}
