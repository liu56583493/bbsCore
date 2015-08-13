package com.siweifu.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

/**
 * 文件上传工具类
 * @title UploadFileUtils.java
 * @description 
 * @company 北京思维夫网络科技有限公司
 * @author 卢春梦  
 * @version 1.0
 * @created 2015年4月15日上午11:15:11
 */
public class UploadFileUtils {

	// 考虑
	// 1. 后台上传的文件处理
	// 2. 用户上传的文件处理
	// 3. 对于size为0的文件不进行图片resize
	// 类型：头像、论坛、新闻
	public enum FileType {
		FAVICON(100),
		BBS_IMG(640),
		BBS_FILE(0),
		NEWS_IMG(640),
		NEWS_FILE(0);

		// 图片的大小
		private int size;

		FileType(int size) {
			this.size = size;
		}

		public int getSzie() {
			return this.size;
		}
	}

	/**
	 * 生成保存的路径
	 * @param getFilePath
	 * @return void
	 */
	public static String getSavePath(FileType fileType, String fileName) {
		StringBuilder filePath = new StringBuilder();
		// 默认路径 uploadFile/
		filePath.append(File.separator).append("uploadFile").append(File.separator);
		// 添加文件类型
		filePath.append(fileType.name().toLowerCase()).append(File.separator);
		// 添加时间
		filePath.append(DateFormatUtils.format(new Date(), "yyyyMMdd")).append(File.separator);
		// 判断路径是否存在，不存在时直接创建整个路径
		String webRootPath = PathKit.getWebRootPath();
		// 拼接出全路径
		String allPath = webRootPath + File.separator + filePath.toString();
		File dir = new File(allPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 生成新的文件名,UUId的形式生成
		filePath.append(StringUtils.getUUID()).append(getFileExt(fileName));
		return filePath.toString();
	}

	/**
	 * 获取文件的扩展名
	 * @param fileName 文件路径或者文件名
	 * @return String
	 */
	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'), fileName.length());
	}

	/**
	 * Jfinal文件上传，返回图片的全路径，本地的会加上basePath<br/>
	 * 云服务器上的会补全域名
	 * @param UploadFile 
	 * @param FileType 文件类型
	 * @return String
	 */
	public static String uploadFile(UploadFile upFile, FileType fileType) {
		// 文件不存在时
		if (null == upFile ) return null;
		File file = upFile.getFile();
		return upFile(file, fileType);
	}

	/**
	 * 普通文件上传，返回图片的全路径，本地的会加上basePath<br/>
	 * 云服务器上的会补全域名
	 * @param file
	 * @param fileType
	 * @return String
	 */
	public static String upFile(File file, FileType fileType) {
		if (null == file) return null;
		// 首先上传到本地服务器，先copy一份到本地
		String webRootPath = PathKit.getWebRootPath();
		String fileName = file.getName();
		// 新的文件路径，用来返回保存
		String newPath = getSavePath(fileType, fileName);
		String allPath = webRootPath + File.separator + newPath;
		File newFile = new File(allPath);
		// 将图片压缩成指定大小
		if (fileType.getSzie() > 0) {
			try {
				ImageUtils.resize(file, newFile, fileType.getSzie());
				FileUtils.deleteQuietly(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			file.renameTo(newFile);
		}
		// 将文件路径全处理成`/`
		newPath = newPath.replace(File.separator, "/");
		return JFinal.me().getContextPath().concat(newPath);
	}

}
