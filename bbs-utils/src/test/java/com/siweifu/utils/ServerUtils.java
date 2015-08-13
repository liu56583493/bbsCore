package com.siweifu.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;

/**
 * 服务器工具，用来读取服务器相关信息
 * @title ServerUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦
 * @version 1.0
 * @created 2015年6月4日下午5:51:47
 */
public final class ServerUtils {

	private static final Logger logger = Logger.getLogger(ServerUtils.class);

	/**
	 * 内存信息封装
	 * 单位为M
	 */
	public static class MemInfo {

		private int total;
		private int used;
		private int free;

		public int getTotal() {
			return total;
		}
		public int getUsed() {
			return used;
		}
		public int getFree() {
			return free;
		}

		public MemInfo(int total, int used, int free) {
			this.total = total;
			this.used = used;
			this.free = free;
		}

		/**
		 * 返回使用百分比
		 * @return String 例子：98.03
		 */
		public double getUsage() {
			return ((double) used / (double) total) * 100;
		}

	}

	// 获取内存信息的脚本
	private final static String MEM_SCRIPT = "free -m | grep Mem | awk '{print $2\"~\"$3\"~\"$4}'";

	/**
	 * 获取服务器内存信息
	 * Mem:   8058084k total,  6444128k used,  1613956k free,   181300k buffers
	 * @return
	 */
	public static MemInfo getMemInfo() {
		String info = runShell(MEM_SCRIPT);
		if (StrKit.isBlank(info)) {
			return null;
		}
		// 返回的信息以k最为单位
		String details[] = info.split("~");
		if (details.length < 3) {
			return null;
		}
		// 转换类型
		int total   = Integer.parseInt(details[0]);
		int used    = Integer.parseInt(details[1]);
		int free    = Integer.parseInt(details[2]);

		return new MemInfo(total, used, free);
	}

	// 获取CPU信息的脚本
	private final static String CPU_SCRIPT = "top -b -n 1 | grep Cpu | awk '{print $2}' | cut -f 1 -d \"u\"";

	/**
	 * 获取CPU的使用率
	 * @return String 例子： 92.4%
	 */
	public static Double getCpuUsage() {
		String usage = runShell(CPU_SCRIPT);
		if (StrKit.isBlank(usage)) {
			return null;
		}
		String used = usage.replace("%", "");
		return Double.parseDouble(used);
	}

	/**
	 * 获取服务器内网ip
	 * 
	 * 先利用java获取，失败则利用shell获取
	 * 
	 * @return String ip
	 */
	public static String getIp() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	/**
	 * 运行shell
	 * @param script
	 * @return 执行结果
	 */
	private static String runShell(String script) {
		InputStream ins = null;
		try {
			String[] cmd = { "/bin/sh", "-c", script };
			//执行liunx命令
			Process process = Runtime.getRuntime().exec(cmd);
			//获取执行完后的结果
			ins = process.getInputStream();
			//转为string类型分析执行结果
			return IOUtils.toString(ins).trim();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(ins);
		}
		return null;
	}

}
