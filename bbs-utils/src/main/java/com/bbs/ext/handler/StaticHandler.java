package com.bbs.ext.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

/**
 * 静态文件处理器
 * @title StaticHandler.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年5月18日下午6:02:26
 */
public class StaticHandler extends Handler {

	public final String[] dirs;

	public StaticHandler(String... dirs) {
		this.dirs = dirs;
	}

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {

		// 判定是否要排除
		boolean needExclude = false;
		for (String dir : dirs) {
			if (target.startsWith(dir)) {
				needExclude = true;
				break;
			}
		}
		if (needExclude) {
			return;
		}
		nextHandler.handle(target, request, response, isHandled);
	}

}
