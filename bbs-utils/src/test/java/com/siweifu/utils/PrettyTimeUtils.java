package com.siweifu.utils;

import java.util.Date;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Millisecond;
import org.ocpsoft.prettytime.units.Second;

/**
 * 美化时间
 * 来源<url>http://www.oschina.net/question/12_173407</url>
 * @title PrettyTimeUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦  
 * @version 1.0
 * @created 2015年4月15日下午2:10:59
 */
public class PrettyTimeUtils {

	private final static PrettyTime PRETTY_TIME = new PrettyTime(Locale.CHINESE);

	static {
		//刚刚
//		PRETTY_TIME.removeUnit(JustNow.class);
		//片刻之前
		PRETTY_TIME.removeUnit(Second.class);
		PRETTY_TIME.removeUnit(Millisecond.class);
	}

	/**
	 * 美化时间
	 * @param date
	 * @return void
	 */
	public static String prettyTime(Date date) {
		return PRETTY_TIME.format(date);
	}

}
