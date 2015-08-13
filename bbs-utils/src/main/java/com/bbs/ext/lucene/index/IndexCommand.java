package com.bbs.ext.lucene.index;

import org.apache.lucene.document.Document;

/**
 * 索引指令
 */
class IndexCommand {
	
	/**
	 * 索引操作指令常量
	 */
	enum Operate{
		//空操作
		NULL,
		//新建（主索引 ， 历史索引）
		BUILD,
		//新增( 内存索引(增)， 主索引(增) , 历史索引(增))
		ADD,
		//修改(复合操作 - 内存索引(删|增) , 主索引(删|增) , 历史索引(删|增))
		MODIFY,
		//删除(内存索引， 主索引 , 历史索引)
		DELETE,
		//索引优化(主索引 , 历史索引)
		OPTIMIZE,
		//清除索引(内存索引，主索引（删） , 
		//清除索引 是指 在 内存索引迁移到主索引/主索引迁移到历史索引 后的 内存索引/主索引 删除)
		CLEAR;	
	}
	
	/**
	 * 指令状态
	 */
	enum Status{
		//未处理 
		TODO,
		//已经删除
		DELETED,
		//处理完成
		DONE;
	}
	
	//操作指令代码
	private Operate operate;
	//Lucene 文档对象	
	private Document document;
	//操作状态
	private Status status; 	
	
	IndexCommand(Operate operate , Document document){
		this.operate = operate;
		this.document = document;
		this.status = Status.TODO;
	}

	public Operate getOperate() {
		return operate;
	}

	public void setOperate(Operate operate) {
		this.operate = operate;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}

