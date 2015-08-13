package com.bbs.base;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.bbs.ext.render.JCaptchaRender;

/**
 * 通用校验器
 * @title BaseValidator.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年5月18日下午5:11:23
 */
public abstract class BaseValidator extends Validator {

	/**
	 * Validate JCaptcha
	 */
	protected void validateJCaptcha(Controller c, String field, String errorKey, String errorMessage) {
		if (StrKit.isBlank(field)) {
			addError(errorKey, errorMessage);
		}
		String value = c.getPara(field);
		boolean result = JCaptchaRender.validate(c, value);
		if (!result) {
			addError(errorKey, errorMessage);
		}
	}
}
