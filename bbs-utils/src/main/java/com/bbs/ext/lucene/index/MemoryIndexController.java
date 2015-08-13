package com.bbs.ext.lucene.index;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.bbs.ext.lucene.index.IndexCommand.Operate;
import com.bbs.ext.lucene.index.IndexCommand.Status;


/**
 * 内存索引控制器
 */
class MemoryIndexController implements Runnable{
	
	/**
	 * 控制器名称
	 */
	private String name;
	
	/**
	 * 索引控制器上下文
	 */
	private IndexContext context; 
	
	/**
	 * 等待索引的文档队列 
	 */
	private IndexCommandQueue commandQueue;
	
	/**
	 * 待删除的文档
	 */
	private List<IndexCommand> toBeDeleted;
	
	/**
	 * 待新增的文档
	 */
	private List<IndexCommand> toBeAdded;
	
	/*8
	 * 线程停止标识 
	 */
	private boolean stopFlag;
	
	/**
	 * 索引优化标志
	 */
	private boolean optimization;
	
	/**
	 * 索引变更计数器
	 */
	private int updateCount;
	
	MemoryIndexController(IndexContext context){
		this.context = context;
		this.name = getClass().getSimpleName() + " for " + context.getIndexConfig().getIndexName();
		this.init();
	}
	
	/**
	 * 初始化索引控制器
	 */
	private void init(){
		this.stopFlag = false;
		this.optimization = false;
		this.updateCount = 0;
		//初始化指令队列
		this.commandQueue = new IndexCommandQueue(this.context);
		this.toBeDeleted = new LinkedList<IndexCommand>();
		this.toBeAdded = new LinkedList<IndexCommand>();
		//启动执行线程
		new Thread(this , this.name).start();
		System.out.println(this.name + " start." );
	}
	
	/**
	 * 发送索引变更指令
	 */
	void sendCommand(IndexCommand command){
		//System.out.println(command.getDocument());
		synchronized(this.commandQueue){
			//超过容量上限
			while(this.commandQueue.size() >= this.context.getIndexConfig().getQueueHoldLimited()){
				//要求等待
				try {
					this.commandQueue.wait();
					if(this.stopFlag){
						//服务已停止
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//初始化命令执行状态
			command.setStatus(Status.TODO);
			this.commandQueue.addCommand(command);
			//立即唤醒消费者（处理）线程
			this.commandQueue.notifyAll();
		}		
	}
	
	/**
	 * 发送清除内存索引指令（来至MainIndexController的回送）
	 */
	void sendClear(IndexCommand command){
		if(Operate.CLEAR != command.getOperate()){
			return;
		}
		//初始化命令执行状态
		command.setStatus(Status.TODO);
		synchronized(this.commandQueue){
			this.commandQueue.addCommand(command);
			//立即唤醒消费者（处理）线程
			this.commandQueue.notifyAll();
		}
	}
	
	/**
	 * 停止线程服务
	 */
	void stopService(){
		this.stopFlag = true;
		synchronized(this.commandQueue){
			this.commandQueue.clear();
			this.commandQueue.notifyAll();
		}
		this.toBeAdded.clear();
		this.toBeDeleted.clear();
	}
	
	public void run() {
		while(!this.stopFlag){
			IndexCommand[] commands = null;
			//同步队列
			synchronized(this.commandQueue){
				while(!this.stopFlag && this.commandQueue.isEmpty()){
					//当队列为空，线程等待
					try {
						this.commandQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(this.stopFlag){
					//终止当前线程的处理
					return;
				}
				//线程被唤醒，取出所有的任务
				commands = this.commandQueue.pollALL();
				//唤醒生产者线程
				this.commandQueue.notifyAll();
			}
			if(commands != null){
				try{
					//执行索引变更指令
					this.processIndexCommands(commands);
				}catch(Exception allEx){
					//捕获所以异常，保证线程的run可以继续循环执行
					allEx.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 执行索引变更指令
	 * commands 索引变更指令数组
	 */
	@SuppressWarnings("incomplete-switch")
	private void processIndexCommands(IndexCommand[] commands){
		/*
		 * 分离指令，构造删除、新增两个指令队列	
		 * 对于MemoryIndexController，处理ADD ,MOD ,DEL ,CLR
		 */	
		for(IndexCommand command : commands){
			switch(command.getOperate()){
			case ADD :
				this.toBeAdded.add(command);
				break;
			case MODIFY :
				this.toBeDeleted.add(command);
				this.toBeAdded.add(command);
				break;
			case DELETE :	
				this.toBeDeleted.add(command);
				break;
			case CLEAR :	
				this.toBeDeleted.add(command);
				break;
			}
		}		
		
		/*
		 * 进行索引变更
		 * 1.先执行删除操作
		 * 2.在执行新增操作
		 * 3.在没有更多指令等待的情况下，且有优化指令，则执行优化索引操作
		 */
		Directory dir = null;
		try {
			//获取内存索引目录
			dir = this.context.getMemIndexDir();
			//判断索引是否已经建立
			boolean exists = IndexReader.indexExists(dir);			
			//执行删除索引任务
			if(exists && !toBeDeleted.isEmpty()){
				this.removeIndex(toBeDeleted , dir);
			}			
			//执行新增索引指令
			if(!toBeAdded.isEmpty()){
				this.addIndex(toBeAdded, dir, !exists);
			}
			/*
			 * 将指令发送到主索引
			 * 内存索引的所有指令都必须复制到主索引
			 */
			for(IndexCommand command : commands){
				//OPERATE_CLR指令不发送主索引
				if(Operate.CLEAR == command.getOperate()){
					continue;
				}
				//Operate.DELETE指令立即执行
				if(Operate.DELETE == command.getOperate()){
					this.context.getMainIndexController().sendCommand(command, true);
				}else{
					this.context.getMainIndexController().sendCommand(command, false);
				}
			}			
			//索引变更到一定数量,触发内存索引优化
			if(this.updateCount >= 4096){
				this.optimization = true;
				this.updateCount = 0;
			}
			//如果存在索引优化指令，且指令队列中没有其他指令，则执行优化
			if(exists && this.optimization && this.commandQueue.isEmpty()){
				this.optimizeIndex(dir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			//这里不关闭dir索引目录
			//清空索引任务队列
			this.toBeDeleted.clear();
			this.toBeAdded.clear();
		}
	
	}
	
	/**
	 * 执行删除索引操作
	 */
	@SuppressWarnings("incomplete-switch")
	private void removeIndex(List<IndexCommand> commands , Directory dir){
		//构造IndexReader
		IndexReader indexReader = null;
		try{
			indexReader = IndexReader.open(dir , false);
			//批量删除
			for(IndexCommand command : commands){
				Term keyTerm = this.context.keyTerm(command.getDocument());
				//查找当索引中PKey对应的文档
				TermDocs termDocs = indexReader.termDocs(keyTerm);
				/*
				 * PKey是唯一的，则该termDocs.next()只执行一次
				 */
				if(termDocs.next()){
					//删除PKey对应的文档
					indexReader.deleteDocument(termDocs.doc());
					this.updateCount++;
					//变更command操作状态
					switch(command.getOperate()){
					case MODIFY :
						command.setStatus(Status.DELETED);
						break;
					case DELETE :	
						command.setStatus(Status.DONE);
						break;
					case CLEAR :	
						command.setStatus(Status.DONE);
						break;
					}
				}
			}
			indexReader.flush();
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			//关闭reader 提交删除的文档
			if(indexReader != null){
				try {
					indexReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}	
	
	/**
	 * 新增索引
	 */
	@SuppressWarnings("incomplete-switch")
	private void addIndex(List<IndexCommand> commands , Directory dir , boolean create){
		//构造IndexWriter
		IndexWriter indexWriter = null;
		try{
			indexWriter = this.openWriter(dir , create);
			//批量添加文档
			for(IndexCommand command : commands){
				Document doc = command.getDocument();
				switch(command.getOperate()){
				case ADD :					
					indexWriter.addDocument(doc);
					//变更command操作状态
					command.setStatus(Status.DONE);
					break;
				case MODIFY :
					/*
					 * 如果修改文档，在新增文档前，需要判断
					 * IndexCommand.OPSTATUS_DELETED == command.getOpStatus()
					 * 查看是否在实时索引中且已经被删除
					 * 如果IndexCommand.OPSTATUS_DELETED != command.getOpStatus()
					 * 不在索引中，则不能新增
					 */
					if(Status.DELETED == command.getStatus()){
						indexWriter.addDocument(doc);
						this.updateCount++;
						//变更command操作状态
						command.setStatus(Status.DONE);
					}
					break;
				}
			}
			//提交事务
			indexWriter.commit();
		} catch (IOException e) {
			e.printStackTrace();	
			if(indexWriter != null){
				try {
					indexWriter.rollback();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}finally{
			if(indexWriter != null){
				try {
					indexWriter.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	/**
	 * 优化索引
	 */
	@SuppressWarnings("deprecation")
	private void optimizeIndex(Directory dir){
		IndexWriter indexWriter = null;
		try{
			indexWriter = this.openWriter(dir , false);
			long begin = System.currentTimeMillis();
			System.out.println(this.name + " optimization beign at " + new Date(begin));
			//通知context，主索引开始优化
			this.context.notifyMemoryIndexOpt(true);
			indexWriter.optimize();
			System.out.println(this.name + " optimization cost " + (System.currentTimeMillis() - begin) + " ms.");
		}catch (IOException e) {
			e.printStackTrace();	
		}finally{
			this.optimization = false;
			//通知context，主索引优化结束
			this.context.notifyMemoryIndexOpt(false);
			if(indexWriter != null){
				try {
					indexWriter.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 打开内存索引的写入器
	 * dir 索引目录
	 * create 是否重建索引
	 */
	private IndexWriter openWriter(Directory dir , boolean create) throws CorruptIndexException, LockObtainFailedException, IOException{
		//配置IndexWriterConfig
		IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_34 , context.getIndexConfig().getLuceneAnalyzer());
		if(create){
			iwConfig.setOpenMode(OpenMode.CREATE);
		}else{
			iwConfig.setOpenMode(OpenMode.APPEND);
		}
		//设置索引合并策略
		LogByteSizeMergePolicy mergePolicy = new LogByteSizeMergePolicy();
		//是否将多个segment合并
		mergePolicy.setUseCompoundFile(false);
		//设置合并参数,History库是Main的两倍
		mergePolicy.setMergeFactor(this.context.getIndexConfig().getMergeFactor());
		//设置每个index segment的最大文档数目
		mergePolicy.setMaxMergeDocs(2048);
		iwConfig.setMergePolicy(mergePolicy);
		//根据配置生成IndexWriter
		IndexWriter indexWriter = new IndexWriter(dir , iwConfig);
		return indexWriter;
	}		
}