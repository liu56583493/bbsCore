package com.bbs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;


public class FileOperate {
	/**
	 * Logger for this class
	 */
	
	public FileOperate() {
	}

	/**
	 * 新建目录
	 * @param folderPath String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 新建文件
	 * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {

		}

	}

	/**
	 * 删除文件
	 * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {

		}

	}

	/**
	 * 删除文件夹
	 * @param filePathAndName String 文件夹路径及名称 如c:/fqf
	 * @param fileContent String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * @param path String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}
	
	/**
	 * 得到文件夹下文件数量
	 * @param path
	 * @return
	 */
	public static int getFileContent(String path){
		File file = new File(path);
		if(file.isDirectory()){
			return file.list().length;
		}else{
			return 0;
		}
	}

	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	@SuppressWarnings("unused")
	public static boolean copyFile(String oldPath, String newPath) {
		boolean result = false;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
		}
		return result;

	}

	/**
	 * 复制整个文件夹内容
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
		}

	}

	/**
	 * 移动文件到指定目录
	 * @param oldPath String 如：c:/fqf.txt
	 * @param newPath String 如：d:/fqf.txt
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);

	}

	/**
	 * 移动文件夹到指定目录
	 * @param oldPath String 如：c:/fqf.txt
	 * @param newPath String 如：d:/fqf.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);

	}

	/**
	 * 获取指定文件中符合模板的内容 逐行查看指定文件中的内容，将所有包含指定字符串（beginStr）的行中 从beginStr到行尾的内容返回
	 * 
	 * @param filepath
	 * @param format
	 * @return
	 */
	public static String getFileLines(String filepath, String beginStr) {
		StringBuffer result = new StringBuffer();
		try {
			FileReader reader = new FileReader(filepath);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				int index = line.indexOf(beginStr);
				if (index >= 0) {
					result.append(line.substring(index + beginStr.length()));
					result.append(System.getProperty("line.separator"));
				}
			}
			br.close();
			reader.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return result.toString();
	}
	
    /**  
     * 写文件到本地  
     * @param in  
     * @param fileName  
     * @throws IOException  
     */  
    @SuppressWarnings("unused")
	public static void copyFile(InputStream in,String savefilepath) throws IOException{   
        FileOutputStream fs = new FileOutputStream(savefilepath);
          byte[] buffer = new byte[1024 * 1024];   
          int bytesum = 0;   
          int byteread = 0;   
          while ((byteread = in.read(buffer)) != -1) {   
              bytesum += byteread;   
              fs.write(buffer, 0, byteread);   
              fs.flush();   
          }   
          fs.close();   
          in.close();   
    }
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		FileOperate fileOperate = new FileOperate();
		fileOperate.newFolder("F://zhangjiajie"); //新建文件夹
		fileOperate.newFile("F://zhangjiajie/zjj.jsp", "111111");//创建文件
//		fileOperate.delFile("F://zhangjiajie/zjj.jsp");//删除文件
//		fileOperate.moveFolder("F://zhangjiajie","E://zhangjiajie");
//		fileOperate.moveFile("E://zhangjiajie/zjj.jsp", "F://zjj.jsp");
		
		fileOperate.copyFolder("F://zhangjiajie","E://zhangjiajie");//复制文件夹
	}
	
}
