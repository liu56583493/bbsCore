package com.bbs.base;

/**
 * 短路校验，有一个参数不和规法立即跳出匹配
 * @title ShortCircuitValidator.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦  
 * @version 1.0
 * @created 2015年4月10日下午12:02:03
 */
public abstract class ShortCircuitValidator extends BaseValidator {
	{
		this.setShortCircuit(true);
	}

}
