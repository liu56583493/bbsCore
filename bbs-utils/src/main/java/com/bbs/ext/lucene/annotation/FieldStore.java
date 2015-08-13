package com.bbs.ext.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * lucene索引字段Annotation 
 * 标注Bean的字段是否要被保存 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldStore {

	/**
	 * 不存储
	 */
	public static final String NO = "NO";

	/**
	 * 存储
	 */
	public static final String YES = "YES";

	/**
	 * lucene document field 存储属性
	 * 默认值 ： 保存
	 * @return
	 */
	public String value() default YES;

}

