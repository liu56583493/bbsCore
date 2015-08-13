package com.bbs.ext.lucene.client;

import com.bbs.ext.lucene.index.IndexContext;


/**
 * 索引服务工厂类
 */
public class IndexServiceFactory {

	/**
	 * 获取索引服务本地实现
	 */
	public static IndexService getLocalIndexService(IndexContext indexContext){
		return new LocalIndexService(indexContext);
	}
}
