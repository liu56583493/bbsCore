package com.bbs.ext.lucene.index;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 索引配置信息类
 */
public class IndexConfig {
	/**
	 * 索引名称
	 */
	private String indexName;
	/**
	 * 索引主键field名称
	 * 默认
	 */
	private String keyFieldName;	
	/**
	 * 索引根目录
	 */
	private String rootDir;
	/**
	 * 索引分词器
	 */
	private Analyzer luceneAnalyzer = new IKAnalyzer();
	
	/**
	 * 指令队列触发临界值
	 * 当指令队列中指令个数超过该临界值，则触发执行线程
	 */
	private int queueTriggerCritical = 500;
	
	/**
	 * 指令队列装载上限
	 * 当指令队列中的指令个数达到装载上限时，队列阻塞线程，停止指令的新增
	 */
	private int queueHoldLimited = 3000;
	
	/**
	 * 指令队列轮时间间隔
	 */
	private int queuePollPeriod = 60000;
	
	/**
	 * Document每个Field的最大词元Term数目
	 */
	private int maxFieldLength = 100000;
	
	/**
	 * 获取建索引时，内存缓冲的文档数
	 */
	private int bufferedDocs = 3000;
	
	/**
	 * 获取索引优化时，内存缓冲区大小
	 */
	private int RAMBufferSizeMB = 256;
	
	/**
	 * 获取每个段最大并入的文档数
	 */
	private int maxMergeDocs = 1000000;
	
	/**
	 * 获取Lucene索引段合并系数
	 */
	private int mergeFactor = 64;
	
	/**
	 * 文档迁移临界值，单位：毫秒
	 * 文档的提交日期与当前时间比较，超过临界值的，要求迁移
	 */
	private long migrateCritical = 31536000000L;
	
	/**
	 * 单次文档迁移的数量最大值
	 */
	private int maxMigrateDocs = 500000;
	
	/**
	 *维护任务定时器空闲周期（ms）
	 */
	private long maintainTaskIdlePeriod = 3600000;
	
	/**
	 * 启用备份索引 
	 */
	private boolean enableBackup = false;
	
	/**
	 * 索引淘汰策略
	 */
	private IndexEliminatePolicy eliminatePolicy;
	
	public IndexConfig(){
		
	}

	/**
	 * 获取当前索引目录（相对于历史索引目录）
	 */
	public File getMainDirectory(){
		String mainDirPath = this.rootDir + "/main";
		return new File(mainDirPath);
	}
	
	/**
	 * 获取备份索引目录（相对于备份索引目录）
	 */
	public File getBackupDirectory(){
		String backupDirPath = this.rootDir + "/history";
		return new File(backupDirPath);
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getKeyFieldName() {
		return keyFieldName;
	}

	public void setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public Analyzer getLuceneAnalyzer() {
		return luceneAnalyzer;
	}

	public void setLuceneAnalyzer(Analyzer luceneAnalyzer) {
		this.luceneAnalyzer = luceneAnalyzer;
	}

	public int getQueueTriggerCritical() {
		return queueTriggerCritical;
	}

	public void setQueueTriggerCritical(int queueTriggerCritical) {
		this.queueTriggerCritical = queueTriggerCritical;
	}

	public int getQueueHoldLimited() {
		return queueHoldLimited;
	}

	public void setQueueHoldLimited(int queueHoldLimited) {
		this.queueHoldLimited = queueHoldLimited;
	}

	public int getQueuePollPeriod() {
		return queuePollPeriod;
	}

	public void setQueuePollPeriod(int queuePollPeriod) {
		this.queuePollPeriod = queuePollPeriod;
	}

	public int getMaxFieldLength() {
		return maxFieldLength;
	}

	public void setMaxFieldLength(int maxFieldLength) {
		this.maxFieldLength = maxFieldLength;
	}

	public int getBufferedDocs() {
		return bufferedDocs;
	}

	public void setBufferedDocs(int bufferedDocs) {
		this.bufferedDocs = bufferedDocs;
	}

	public int getRAMBufferSizeMB() {
		return RAMBufferSizeMB;
	}

	public void setRAMBufferSizeMB(int bufferSizeMB) {
		RAMBufferSizeMB = bufferSizeMB;
	}

	public int getMaxMergeDocs() {
		return maxMergeDocs;
	}

	public void setMaxMergeDocs(int maxMergeDocs) {
		this.maxMergeDocs = maxMergeDocs;
	}

	public int getMergeFactor() {
		return mergeFactor;
	}

	public void setMergeFactor(int mergeFactor) {
		this.mergeFactor = mergeFactor;
	}

	public long getMigrateCritical() {
		return migrateCritical;
	}

	public void setMigrateCritical(long migrateCritical) {
		this.migrateCritical = migrateCritical;
	}

	public int getMaxMigrateDocs() {
		return maxMigrateDocs;
	}

	public void setMaxMigrateDocs(int maxMigrateDocs) {
		this.maxMigrateDocs = maxMigrateDocs;
	}

	public long getMaintainTaskIdlePeriod() {
		return maintainTaskIdlePeriod;
	}

	public void setMaintainTaskIdlePeriod(long maintainTaskIdlePeriod) {
		this.maintainTaskIdlePeriod = maintainTaskIdlePeriod;
	}

	public boolean isEnableBackup() {
		return enableBackup;
	}

	public void setEnableBackup(boolean enableBackup) {
		this.enableBackup = enableBackup;
	}

	public IndexEliminatePolicy getEliminatePolicy() {
		return eliminatePolicy;
	}

	public void setEliminatePolicy(IndexEliminatePolicy eliminatePolicy) {
		this.eliminatePolicy = eliminatePolicy;
	}
}

