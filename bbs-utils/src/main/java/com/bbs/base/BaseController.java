package com.bbs.base;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.upload.UploadFile;

/**
 * 通用控制器，用于添加通用方法
 * @title BaseController.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年5月18日上午11:20:17
 */
public abstract class BaseController extends Controller {

	protected final Logger logger = Logger.getLogger(getClass());

	/**
	 * 新加文件上传，请优先使用这个
	 * @param getFile
	 * @return UploadFile
	 */
	public UploadFile getFile(String parameterName, int maxPostSize) {
		// 获取临时目录，默认tomcat下的temp目录
		String tempDir = System.getProperty("java.io.tmpdir");
		return super.getFile(parameterName, tempDir, maxPostSize);
	}

}
