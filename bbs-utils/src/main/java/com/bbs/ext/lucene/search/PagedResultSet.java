package com.bbs.ext.lucene.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

/**
 * 带翻页的文档列表结果集
 */
public class PagedResultSet implements Serializable{

	private static final long serialVersionUID = -5534812624903533365L;
	
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
	
	public PagedResultSet(){
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

	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}

	public List<Map<String, String>> getResults() {
		return results;
	}

	public void setResultDocument(Document[] docs) {
		results = new ArrayList<Map<String,String>>();
		if(docs != null){
			for(Document doc : docs){
				results.add(documentToMap(doc));
			}			
		}
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

	/**
	 * Lucene Document对象转Map
	 */
	private Map<String , String> documentToMap(Document doc){
		Map<String , String> result = new HashMap<String , String>();
		if(doc != null){
			List<Fieldable> fields = doc.getFields();
			for(Fieldable f : fields){
				result.put(f.name(), f.stringValue()); 
			}		
		}		
		return result;
	}
}