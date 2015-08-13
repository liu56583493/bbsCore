package com.bbs.ext.lucene;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.Sort;

import com.bbs.ext.lucene.client.IndexService;
import com.bbs.ext.lucene.client.QueryResults;


/**
 * Lucene工具类_主要来源于Zjj.Lucene封装http://jcore.cn
 * 
 * @author L.cm
 * email: 596392912@qq.com
 * site:http://www.dreamlu.net
 * @date 2014年11月4日 下午3:51:57
 */
public class LuceneKit {

	private static volatile IndexService indexService;

	static void init(IndexService indexService) {
		LuceneKit.indexService = indexService;
	}

	/**
	 * 构建索引（与ADD不同，用于索引第一次的初始化，数据批量导入）
	 * dataBean 带有annotation的Bean
	 * @throws IOException 
	 */
	public static void build(Object dataBean)throws IOException {
		indexService.build(dataBean);
	}

	/**
	 * 批量构建索引
	 * dataBeanList 带有annotation的Bean List
	 * @throws IOException
	 */
	public static void build(List<?> dataBeanList)throws IOException {
		indexService.build(dataBeanList);
	}

	/**
	 * 查询功能
	 * @throws IOException 
	 */
	public static QueryResults search(String queryString , int pageNo , int pageSize , boolean reverse) throws IOException {
		return indexService.query(queryString, pageNo, pageSize, reverse);
	}

	/**
	 * 查询功能
	 * @throws IOException 
	 */
	public static QueryResults search(String queryString, int pageNo, int pageSize, Sort sort) throws IOException {
		return indexService.query(queryString, pageNo, pageSize, sort);
	}

	/**
	 * 查询功能,带排序功能
	 * @throws IOException 
	 */
	public static QueryResults search(String queryString, int pageNo, int pageSize, boolean reverse, String sortFieldName, String sortFieldType) throws IOException {
		return indexService.query(queryString, pageNo, pageSize, reverse, sortFieldName, sortFieldType);
	}

	/**
	 * 批量新增索引
	 * dataBeanList 带有annotation的Bean List
	 * @throws IOException
	 */
	public static void add(List<?> dataBeanList) throws IOException {
		indexService.add(dataBeanList);
	}

	/**
	 * 新增索引
	 * dataBean 带有annotation的Bean
	 * @throws IOException
	 */
	public static void add(Object dataBean) throws IOException {
		indexService.add(dataBean);
	}

	/**
	 * 优化索引
	 * immediately 是否立刻执行优化
	 * @throws IOException 
	 */
	public static void optimize(boolean immediately) throws IOException {
		indexService.optimize(immediately);
	}

	/**
	 * 更新索引
	 * dataBean 带有annotation的Bean
	 * @throws IOException 
	 */
	public static void update(Object dataBean) throws IOException {
		indexService.update(dataBean);
	}

	/**
	 * 更新索引
	 * dataBeanList 带有annotation的Bean List
	 * @throws IOException 
	 */
	public static void update(List<?> dataBeanList) throws IOException {
		indexService.update(dataBeanList);
	}

	/**
	 * 删除索引
	 * dataBean 带有annotation的Bean
	 * @throws IOException 
	 */
	public static void delete(Object dataBean) throws IOException {
		indexService.delete(dataBean);
	}

	/**
	 * 批量删除索引
	 * dataBeanList 带有annotation的Bean List
	 * @throws IOException 
	 */
	public static void delete(List<?> dataBeanList) throws IOException {
		indexService.delete(dataBeanList);
	}

}
