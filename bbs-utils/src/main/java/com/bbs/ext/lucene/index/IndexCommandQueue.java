package com.bbs.ext.lucene.index;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.lucene.index.Term;

import com.bbs.ext.lucene.index.IndexCommand.Operate;


/**
 * 索引指令队列
 * 1.按索引指令放入的顺序排列，实现先进先出原则
 * 2.对同一Document的指令操作进行合并处理
 */
class IndexCommandQueue {
	
	/**
	 * 对ADD ， MOD ，DEL 指令的映射表
	 * Key ：  Document 的主键（可能没有）
	 * Value ： 包含有对应Document的IndexTask
	 */
	private Map<Term , IndexCommand> innerMap;

	/**
	 * 索引变更任务列表
	 */
	private LinkedList<IndexCommand> innerQueue;
	
	/**
	 * 索引上下文
	 */
	private IndexContext context;
	
	/**
	 * 构造函数
	 * 初始化内部数据结构
	 */
	IndexCommandQueue(IndexContext context){
		this.context = context;
		this.innerMap = new HashMap<Term , IndexCommand>();
		this.innerQueue = new LinkedList<IndexCommand>();
	}	
	
	/**
	 * 添加索引指令
	 * 线程同步操作
	 * @return true ： 接收新任务 ； false 拒绝新任务
	 */
	@SuppressWarnings("incomplete-switch")
	public synchronized boolean addCommand(IndexCommand indexCommand){
		boolean result = false;
		if(indexCommand == null){
			return result;
		}
		if(IndexCommand.Operate.BUILD == indexCommand.getOperate() || IndexCommand.Operate.CLEAR == indexCommand.getOperate() || IndexCommand.Operate.OPTIMIZE == indexCommand.getOperate()){
			//直接向队列添加指令
			this.innerQueue.add(indexCommand);
		}else if(indexCommand.getDocument() != null){
			//一下是对ADD ，MOD ，DEL指令的状态机处理
			//0.从Docment中获取主键值
			Term keyTerm = this.context.keyTerm(indexCommand.getDocument());
			//1.判断队列中是否已存在对同一Document的变更任务，
			IndexCommand taskInMap = this.innerMap.get(keyTerm);			
			if(taskInMap == null){
				//1-1如果不存在，则将任务插入队列
				this.innerMap.put(keyTerm, indexCommand);
				this.innerQueue.add(indexCommand);
			}else{
				//1-2如果存在需要进行任务状态变更
				//状态机矩阵请参见下列文字
				/*
				newIst的状态
					   				ADD		MOD		DEL	
							————————————————————————————————————
					   		ADD	|	未知		ADD		清空
				oldIst状态	MOD	|	未知		MOD		DEL
					   		DEL	|	MOD		未知		未知
				*/
				Operate oldIst = taskInMap.getOperate();
				Operate newIst = indexCommand.getOperate();
				if(Operate.ADD == oldIst){
					switch(newIst){
					case ADD :
						//忽略后来的任务
						break;
					case MODIFY :
						//更新任务中DOC内容
						taskInMap.setDocument(indexCommand.getDocument());
						result = true;
						break;
					case DELETE :
						//更改指令为无效操作指令，等效于清空当前Task，这样处理的目的是避免LinkList的Remove操作耗时
						taskInMap.setOperate(Operate.NULL);
						result = true;
						break;
					}
				}else if(Operate.MODIFY == oldIst){					
					switch(newIst){
					case ADD :
						//忽略后来的任务
						break;
					case MODIFY :
						//更新任务中DOC内容
						taskInMap.setDocument(indexCommand.getDocument());
						result = true;
						break;
					case DELETE :
						//更改指令为删除指令
						taskInMap.setOperate(Operate.DELETE);
						//更新任务中DOC内容
						//taskInMap.setDocument(indexTask.getDocument());
						result = true;
						break;					
					}
				}else if(Operate.DELETE == oldIst){
					switch(newIst){
					case ADD :
						//更改指令为修改指令
						taskInMap.setOperate(Operate.MODIFY);
						//更新任务中DOC内容
						taskInMap.setDocument(indexCommand.getDocument());
						result = true;
						break;
					case MODIFY :
						//忽略后来的任务
						break;
					case DELETE :
						//忽略后来的任务
						break;					
					}					
				}				
			}		
		}
		return result;
	}

	/**
	 * 取出队列中的第一条指令
	 * 线程同步操作
	 * @return 队列中的第一条指令
	 */
	public synchronized IndexCommand pollFirst(){
		IndexCommand first = this.innerQueue.pollFirst();
		if(first != null){
			if(Operate.ADD == first.getOperate() || Operate.MODIFY == first.getOperate() || Operate.DELETE == first.getOperate() ){
				Term firstKeyTerm = this.context.keyTerm(first.getDocument());
				this.innerMap.remove(firstKeyTerm);
			}
		}
		return first;
	}

	/**
	 * 取出队列中的最后一条指令
	 * 线程同步操作
	 * @return 队列中的最后一条指令
	 */
	public synchronized IndexCommand pollLast(){
		IndexCommand last = this.innerQueue.pollLast();
		if(last != null){
			if(Operate.ADD == last.getOperate() || Operate.MODIFY == last.getOperate() || Operate.DELETE == last.getOperate()){
				Term lastKeyTerm = this.context.keyTerm(last.getDocument());
				this.innerMap.remove(lastKeyTerm);
			}
		}
		return last;
	}	
	
	/**
	 * 取出队列中的所有指令,并清空队列
	 * 线程同步操作
	 */
	public synchronized IndexCommand[] pollALL(){
		IndexCommand[] tasks = new IndexCommand[size()];
		tasks = this.innerQueue.toArray(tasks);
		this.clear();
		return tasks; 
	}
	
	/**
	 * 清空队列
	 */
	public synchronized void clear(){
		this.innerQueue.clear();
		this.innerMap.clear();
	}
	
	/**
	 * 返回任务队列大小
	 * @return
	 */
	public int size(){
		return this.innerQueue.size();
	}
	
	/**
	 * 判断队列是否为空
	 * @return
	 */
	public boolean isEmpty(){
		return this.innerQueue.isEmpty();
	}
	
}