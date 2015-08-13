package com.bbs.ext.lucene.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

import com.bbs.ext.lucene.annotation.FieldIndex;
import com.bbs.ext.lucene.annotation.FieldStore;
import com.bbs.ext.lucene.annotation.PKey;
import com.bbs.ext.lucene.index.IndexConfig;


/**
 * 索引数据格式转换
 * Bean 转 lucene document
 */
public class DocumentDataFormatter extends BasicDataFormatter{
	
	/**
	 * 从javabean列表生成List
	 */
	public static List<Document> createDocumentfromBeans(List<?> dataBeans , IndexConfig indexConfig){
		List<Document> documents = new ArrayList<Document>();
		if(dataBeans != null){
			for(Object dataBean : dataBeans){
				Document document = createDocumentfromBean(dataBean , indexConfig);
				documents.add(document);
			}			
		}
		return documents;
	}

	/**
	 * 解析单个Bean对象
	 */
	public static Document createDocumentfromBean(Object dataBean , IndexConfig indexConfig){
		 Document doc = new Document(); 
		//获取data bean的类对象
		Class<?> dataBeanClass = dataBean.getClass();
		//获取当前Bean中声明的所有属性（不包括继承的类）
		Field[] fields = dataBeanClass.getDeclaredFields();
		//将Bean的属性转成lucene document的Field
		boolean foundPkey = false;
		for(Field beanField : fields){			
			beanField.setAccessible(true);
			//忽略serialVersionUID属性
			if (beanField.getName().equals("serialVersionUID")){
				continue;
			}
			//忽略没有索引相关注释的属性
			if(beanField.getAnnotation(PKey.class) == null && beanField.getAnnotation(FieldStore.class) == null && beanField.getAnnotation(FieldIndex.class) == null){
				continue;
			}
			String docFieldValue = readDocFieldValue(beanField , dataBean);
			//对非空值属性，添加到索引中,忽略null值属性
			if(docFieldValue != null){
				String docFieldName = beanField.getName();;	
				Store docFieldStore = null;
				Index docFieldIndex = null;
				//处理PKey属性
				PKey pKeyAnno = beanField.getAnnotation(PKey.class);
				if(!foundPkey && pKeyAnno != null){
					if(indexConfig.getKeyFieldName() == null || !docFieldName.equals(indexConfig.getKeyFieldName())){
						throw new IllegalArgumentException("数据对象PKey属性校验失败，名称为空或不匹配!");
					}					
					foundPkey = true;
					//PKey必须存储
					docFieldStore = Store.YES;
					//PKey索引，不切分
					docFieldIndex = Index.NOT_ANALYZED_NO_NORMS;
				}else{
					String store = readDocFieldStore(beanField);
					if(FieldStore.YES.equals(store)){
						docFieldStore = Store.YES;
					}else{
						docFieldStore = Store.NO;
					}
					String index = readDocFieldIndex(beanField);
					if(FieldIndex.NO_INDEX.equals(index)){
						docFieldIndex = Index.NO;
					}else if(FieldIndex.NOT_ANALYZED.equals(index) || FieldIndex.NO_ANALYZED.equals(index)){
						docFieldIndex = Index.NOT_ANALYZED_NO_NORMS;
					}else if(FieldIndex.ANALYZED.equals(index)){
						docFieldIndex = Index.ANALYZED_NO_NORMS;
					}
				}			
				org.apache.lucene.document.Field docField = new org.apache.lucene.document.Field(docFieldName , docFieldValue,docFieldStore,docFieldIndex);
				doc.add(docField);
			}
		}
		//没有找到主键，抛异常
		if(!foundPkey){
			throw new IllegalArgumentException("数据对象缺少PKey属性!");
		}
		return doc;
	}	
}