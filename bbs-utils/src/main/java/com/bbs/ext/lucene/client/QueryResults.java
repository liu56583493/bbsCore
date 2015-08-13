package com.bbs.ext.lucene.client;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 查询结果对象
 */
public class QueryResults{

	/**
	 * 总的查询命中数量
	 */
	private int totalHit;
	
	/**
	 * 当前页码
	 */
	private int pageNo;
	
	/**
	 * 页面大小 
	 */
	private int pageSize = 1;

	/**
	 * 结果集
	 */
	private List<Map<String, String>> results;
	
	/**
	 * 构造结果对象
	 */
	public QueryResults() {
	}

	public int getTotalHit() {
		return totalHit;
	}

	public void setTotalHit(int totalHit) {
		this.totalHit = totalHit;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<Map<String, String>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}

	/**
	 * 根据pageSize和totalHit计算总页数
	 */
	public int getTotalPage() {
		int totalPage = this.totalHit / this.pageSize; 
		if(this.totalHit % this.pageSize != 0){
			totalPage = totalPage + 1;
		}
		return totalPage;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

