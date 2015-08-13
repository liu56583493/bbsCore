package com.bbs.vo;

import com.bbs.utils.JsonUtils;

/**
 * 功能描述: 封装ajax返回
 * @title AjaxResult.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦  
 * @version 1.0
 * @created 2015年4月9日下午6:55:53
 */
public class AjaxResult {

	// 标记成功失败，默认0：成功，1：失败、用于alert，2：失败、用于confirm
	private int code = 0;

	// 返回的中文消息
	private String message;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// 校验错误
	public boolean hasError() {
		return this.code == 1;
	}

	// 添加错误，用于alertError
	public AjaxResult addError (String message) {
		this.message = message;
		this.code = 1;
		return this;
	}

	/**
	 * 用于Confirm的错误信息
	 * @param addConfirmError
	 * @return AjaxResult
	 */
	public AjaxResult addConfirmError(String message) {
		this.message = message;
		this.code = 2;
		return this;
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
