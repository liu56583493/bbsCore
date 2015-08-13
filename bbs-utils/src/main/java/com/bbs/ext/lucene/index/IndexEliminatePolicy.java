package com.bbs.ext.lucene.index;

import org.apache.lucene.search.Query;

/**
 * 索引淘汰策略接口
 */
public interface IndexEliminatePolicy {

	/**
	 * 获取索引淘汰条件
	 */
	public Query getEliminateCondition(IndexConfig indexConfig);
}

