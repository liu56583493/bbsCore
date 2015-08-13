package com.siweifu.utils;


/**
 * 容器环境参数信息
 * @title EnvUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年6月1日上午8:58:15
 */
public final class EnvUtils {

	public static final String RDS_DB_NAME     = "mysql_db_name";
	public static final String RDS_DB_USERNAME = "mysql_db_username";
	public static final String RDS_DB_PASSWORD = "mysql_db_password";
	public static final String RDS_DB_URL      = "mysql_db_url";

	public static final String IN_DOCKER       = "in_docker";
	public static final String JAVA_OPTS       = "JAVA_OPTS";

	// 数据库url模版
	public static final String DB_URL_TEMP     = "jdbc:mysql://{0}/{1}?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";

	// java 环境
	public static final String JAVA_OPTS_TEMP  = "-server -Xms{0}m -Xmx{1}m -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true";

	public static String get(String name) {
		return System.getenv(name); 
	}

	/**
	 * 判断是否在docekr环境中
	 * @return 是否在docekr环境中
	 */
	public static boolean inDocker() {
		String inDocker = System.getenv(IN_DOCKER);
		if (null == inDocker) {
			return false;
		}
		return "true".equalsIgnoreCase(inDocker);
	}
}
