package com.bbs.ext.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.bbs.ext.lucene.client.IndexService;
import com.bbs.ext.lucene.client.IndexServiceFactory;
import com.bbs.ext.lucene.index.IndexConfig;
import com.bbs.ext.lucene.index.IndexContext;
import com.jfinal.plugin.IPlugin;

/**
 * Lucene插件
 * @author L.cm
 * email: 596392912@qq.com
 * site:http://www.dreamlu.net
 * @date 2014年11月3日 下午2:50:45
 */
@SuppressWarnings("static-access")
public class LucenePlugin implements IPlugin {

	private static IndexConfig indexConfig;
	private static IndexContext indexContext;
	private static IndexService indexService;

	// 索引名
	private static  String indexName;
	// 索引主键
	private static String keyFieldName;
	// 索引目录
	private static String rootDir;
	// 是否备份
	private boolean enableBackup = false;

	public LucenePlugin(String indexName, String keyFieldName, String rootDir) {
		this.indexName = indexName;
		this.keyFieldName = keyFieldName;
		this.rootDir = rootDir;
	}

	public LucenePlugin setIndexName(String indexName) {
		this.indexName = indexName;
		return this;
	}

	public LucenePlugin setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
		return this;
	}

	public LucenePlugin setRootDir(String rootDir) {
		this.rootDir = rootDir;
		return this;
	}

	public LucenePlugin setEnableBackup(boolean enableBackup) {
		this.enableBackup = enableBackup;
		return this;
	}

	@Override
	public boolean start() {
		if(null == indexConfig){
			indexConfig = new IndexConfig();
			// 索引实例名称
			indexConfig.setIndexName(indexName);
			// 索引目录主键
			indexConfig.setKeyFieldName(keyFieldName);
			// 索引目录
			indexConfig.setRootDir(rootDir);
			// 是否启用备份
			indexConfig.setEnableBackup(enableBackup);
			Analyzer ika = new IKAnalyzer(true);
			// 设置所用分词器
			indexConfig.setLuceneAnalyzer(ika);

			indexContext = new IndexContext(indexConfig);
			// 索引服务
			indexService = IndexServiceFactory.getLocalIndexService(indexContext);
			LuceneKit.init(indexService);
			return true;
		}
		return false;
	}

	@Override
	public boolean stop() {
		if (indexConfig != null) {
			indexConfig = null;
		}
		if (indexContext != null) {
			indexContext.close();
			indexContext = null;
		}
		if (indexService != null) {
			indexService = null;
		}
		return true;
	}
	
	
	/* 初始化 索引文件 */
	public static final IndexContext INIT(){
		if( null == indexConfig){
			// 搜索索引名
			indexConfig = new IndexConfig();
			// 索引实例名称
			indexConfig.setIndexName(indexName);
			// 索引目录主键
			indexConfig.setKeyFieldName(keyFieldName);
			// 索引目录
			indexConfig.setRootDir(rootDir);
			// 是否启用备份
			indexConfig.setEnableBackup(false);
			Analyzer ika = new IKAnalyzer(true);
			// 设置所用分词器
			indexConfig.setLuceneAnalyzer(ika);
			indexContext = new IndexContext(indexConfig);
		}
		return indexContext;
	}
	

}
