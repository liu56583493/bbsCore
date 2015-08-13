package com.bbs.ext.lucene.client;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bbs.ext.lucene.annotation.FieldIndex;
import com.bbs.ext.lucene.annotation.FieldStore;


/**
 * 索引数据格式转换
 */
public class BasicDataFormatter {		

	/**
	 * 读取dataBean指定属性的值
	 */
	static String readDocFieldValue(Field beanField , Object dataBean){
		//取属性的实际值（对象）
		Object fieldValue = null;
		try {
			fieldValue = beanField.get(dataBean);
			if(fieldValue == null){
				return null;
			}	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if(fieldValue instanceof Date){
			//如果是date型， 转8位日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			return sdf.format((Date)fieldValue);
		}
		return fieldValue.toString();
	}

	/**
	 * 读取Bean的指定属性的FieldStore注释 
	 */
	static String readDocFieldStore(Field beanField){
		FieldStore storeAnno = beanField.getAnnotation(FieldStore.class);
		if(storeAnno == null){
			return FieldStore.NO;
		}
		return storeAnno.value();
	}

	/**
	 * 读取Bean的指定属性的FieldIndex注释
	 */
	static String readDocFieldIndex(Field beanField){
		FieldIndex indexAnno = beanField.getAnnotation(FieldIndex.class);
		if(indexAnno == null){
			return "NO";
		}
		return indexAnno.value();
	}
}

