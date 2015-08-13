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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.bbs.ext.lucene.index.IndexCommand.Operate;
import com.bbs.ext.lucene.index.IndexCommand.Status;


/**
 * 主索引控制器
 * 
 * ---主磁盘索引
 * 主磁盘索引保存近期的索引记录
 * 支持三种触发条件
 * 1.队列中，任务数达到触发点（例如：500条）
 * 2.索引间隔时间到达触发点（例如：60秒）
 * 3.管理员手动触发
 */
class MainIndexController implements Runnable {
	/*8
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
	
	/**
	 * 线程停止标识 
	 */
	private boolean stopFlag;
	
	/**
	 * 索引优化标志
	 */
	private boolean optimization;
	
	MainIndexController(IndexContext context){
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
	void sendCommand(IndexCommand command , boolean immediately){
		//System.out.println("Send command = " + command.getDocument());
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
			/*
			 * 队列中的指令数量达到触发临界，
			 * 或者immediately = true
			 * 则唤醒消费者（处理）线程
			 */
			if(immediately ||
					this.commandQueue.size() >= this.context.getIndexConfig().getQueueTriggerCritical()){
				//立即唤醒消费者（处理）线程
				this.commandQueue.notifyAll();
			}
		}		
	}
	
	/**
	 * 停止线程服务
	 */
	void stopService(){
		this.stopFlag = true;
		this.optimization = false;
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
						this.commandQueue.wait(this.context.getIndexConfig().getQueuePollPeriod());
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
		 * 对于MainIndexController, 要负责处理BLD , ADD , MOD , DEL , CLR , OPT
		 */	
		for(IndexCommand command : commands){
			switch(command.getOperate()){
			case BUILD :
				this.toBeAdded.add(command);
				break;
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
			case OPTIMIZE :	
				this.optimization = true;
				command.setStatus(Status.DONE);
				break;				
			}
		}
		//变更索引		
		Directory dir = null;
		try {
			//获取索引目录
			dir = FSDirectory.open(this.context.getIndexConfig().getMainDirectory());
			//判断索引是否已经存在
			boolean exists = IndexReader.indexExists(dir);
			//执行删除索引任务
			if(exists && !toBeDeleted.isEmpty()){
				this.removeIndex(toBeDeleted , dir);
			}
			//执行新增索引任务
			if(!toBeAdded.isEmpty()){
				this.addIndex(toBeAdded, dir, !exists);
			}
			/*
			 * 将指令发送到备份索引
			 * 主索引的指令除CLEAR，OPTIMIZE外，都必须传递到历史索引
			 * 在OPTIMIZE执行前完成向备份索引的指令传递
			 */
			if(this.context.getIndexConfig().isEnableBackup()){
				for(IndexCommand command : commands){
					if(Operate.CLEAR == command.getOperate() 
							|| Operate.OPTIMIZE == command.getOperate()){
						continue;
					}
					this.context.getBackupIndexController().sendCommand(command, false);
				}
			}
			//如果存在索引优化指令，且指令队列中没有其他指令，则执行优化
			if(exists && this.optimization && this.commandQueue.isEmpty()){
				this.optimizeIndex(dir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			//关闭当前索引目录
			if(dir != null){
				try {
					dir.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
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
			this.context.notifyMainIndexOpt(true);
			indexWriter.optimize();
			System.out.println(this.name + " optimization end at " + new Date(begin) + " cost " + (System.currentTimeMillis() - begin) + " ms.");
		}catch (IOException e) {
			e.printStackTrace();	
		}finally{
			this.optimization = false;
			//通知context，主索引优化结束
			this.context.notifyMainIndexOpt(false);
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
				case BUILD :					
					indexWriter.addDocument(doc);
					//变更command操作状态
					command.setStatus(Status.DONE);
					break;					
				case ADD :
					indexWriter.addDocument(doc);
					//变更command操作状态
					command.setStatus(Status.DONE);					
					//删除内存索引中的文档
					IndexCommand clearCommand = new IndexCommand(Operate.CLEAR , doc);
					this.context.getMemoryIndexController().sendClear(clearCommand);
					break;					
				case MODIFY :
					/*
					 * 如果修改文档，在新增文档前，需要判断
					 * IndexCommand.OPSTATUS_DELETED == command.getOpStatus()
					 * 是否在实时索引中且已经被删除
					 * 如果IndexCommand.OPSTATUS_DELETED != command.getOpStatus()
					 * 不在索引中，则不能新增
					 */
					if(Status.DELETED == command.getStatus()){
						indexWriter.addDocument(doc);
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
		//设置索引时，内存的最大缓冲文档数目
		iwConfig.setMaxBufferedDocs(this.context.getIndexConfig().getBufferedDocs());
		//设置索引时，内存的最大缓冲(关闭内存容量Buffer参数)
		iwConfig.setRAMBufferSizeMB(IndexWriterConfig.DISABLE_AUTO_FLUSH);
		//设置索引合并策略
		LogByteSizeMergePolicy mergePolicy = new LogByteSizeMergePolicy();
		//是否将多个segment合并
		mergePolicy.setUseCompoundFile(false);
		//设置合并参数,History库是Main的两倍
		mergePolicy.setMergeFactor(this.context.getIndexConfig().getMergeFactor());
		//设置每个index segment的最大文档数目
		mergePolicy.setMaxMergeDocs(this.context.getIndexConfig().getMaxMergeDocs());
		iwConfig.setMergePolicy(mergePolicy);
		//根据配置生成IndexWriter
		IndexWriter indexWriter = new IndexWriter(dir , iwConfig);
		return indexWriter;
	}	
}