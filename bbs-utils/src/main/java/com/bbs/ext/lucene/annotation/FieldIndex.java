package com.bbs.ext.lucene.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * lucene索引字段Annotation 
 * 标注Bean的字段是否要被切分 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldIndex {

	/**
	 * 不索引
	 */
	public static final String NO_INDEX = "NO";

	/**
	 * 索引不分词
	 */
	public static final String NOT_ANALYZED = "NOT_ANALYZED";
	public static final String NO_ANALYZED = "NO_ANALYZED";

	/**
	 * 索引且分词
	 */
	public static final String ANALYZED = "ANALYZED";

	/**
	 * lucene document field 索引属性
	 * 默认值 ： "NOT_ANALYZED" ： 索引不分词
	 * 字符型枚举值： 
	 * ”NO" : 不索引
	 * "NOT_ANALYZED" ： 索引不分词
	 * "ANALYZED" ： 索引且分词
	 * @return
	 */
	public String value() default NOT_ANALYZED;

}

