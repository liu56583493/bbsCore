package com.bbs.ext.lucene.client;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.Sort;

/**
 * 索引服务接口
 */
public interface IndexService {

	/**
	 * 构建索引（与ADD不同，用于索引第一次的初始化，数据批量导入）
	 * dataBean 带有annotation的Bean
	 */
	public void build(Object dataBean)throws IOException;

	/**
	 * 批量构建索引
	 * dataBeanList 带有annotation的Bean List
	 */
	public void build(List<?> dataBeanList)throws IOException;

	/**
	 * 新增索引
	 * dataBean 带有annotation的Bean
	 */
	public void add(Object dataBean)throws IOException;
	
	/**
	 * 批量新增索引
	 * dataBeanList 带有annotation的Bean List
	 */
	public void add(List<?> dataBeanList)throws IOException;	

	/**
	 * 修改索引
	 * dataBean 带有annotation的Bean
	 */
	public void update(Object dataBean)throws IOException;
	
	/**
	 * 批量修改索引
	 * dataBeanList 带有annotation的Bean List
	 */
	public void update(List<?> dataBeanList)throws IOException;
	
	/**
	 * 删除索引
	 * dataBean 带有annotation的Bean
	 */
	public void delete(Object dataBean)throws IOException;
	
	/**
	 * 批量删除索引
	 * dataBeanList 带有annotation的Bean List
	 */
	public void delete(List<?> dataBeanList)throws IOException;			

	/**
	 * 优化索引
	 * immediately 是否立刻执行优化
	 */
	public void optimize(boolean immediately)throws IOException;

	/**
	 * 优化备份索引
	 * immediately 是否立刻执行优化
	 */
	public void optimizeBackup(boolean immediately)throws IOException;	

	/**
	 * 查询主索引
	 * queryString 使用IKExp
	 * reverse 默认使用DOC ID排序
	 */
	public QueryResults query(String queryString , int pageNo , int pageSize , boolean reverse) throws IOException;

	/**
	 * 查询主索引
	 * queryString 使用IKExp
	 * 使用自定义sort
	 */
	public QueryResults query(String queryString, int pageNo, int pageSize, Sort sort) throws IOException;

	/**
	 * 查询主索引
	 */
	public QueryResults query(String queryString , int pageNo , int pageSize , boolean reverse , String sortFieldName , String sortFieldType) throws IOException;

	/**
	 * 查询备份索引
	 * queryString 使用IKExp
	 * 默认使用DOC ID排序
	 */
	public QueryResults queryBackup(String queryString , int pageNo , int pageSize , boolean reverse)throws IOException;

	/**
	 * 查询备份索引
	 */
	public QueryResults queryBackup(String queryString , int pageNo , int pageSize , boolean reverse , String sortFieldName , String sortFieldType)throws IOException;
}

