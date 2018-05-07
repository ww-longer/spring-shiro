package com.sys.commons.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class FileUtils {

	static Logger log = Logger.getLogger(ExcelUtils.class);

	/**
	 * Java读取文件
	 * 
	 * @param file
	 * @param charset
	 * @return
	 */
	public static String readFile(File file, String charset) {
		// 设置默认编码
		if (charset == null) {
			charset = "UTF-8";
		}
		if (file.isFile() && file.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(
						fileInputStream, charset);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				StringBuffer sb = new StringBuffer();
				String text = null;
				while ((text = bufferedReader.readLine()) != null) {
					sb.append(text);
				}
				return sb.toString();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	/**
	 * 以FileWriter方式写入文件。
	 * 
	 * @param filePath
	 *            要写入的文件路径
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件内容
	 */
	public static void writeToFile(String filePath, String fileName,
			String content) {
		try {
			File path = new File(filePath);
			File file = new File(filePath + fileName);
			// 如不存在,先创建路劲
			if (!path.exists()) {
				path.mkdirs();
			}
			// 文件不存在时候，主动穿件文件。
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.exists()) {
				// 在原有文件后追加 == true
				FileWriter fw = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
				fw.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 创建文件或路劲
	 * 
	 * @param path
	 */
	public static void creatFileOrPath(String path, String name) {
		try {
			File file;
			if (name == null || "".equals(name)) {
				file = new File(path);
				// 目录不存在时候，主动创建
				if (!file.exists()) {
					file.mkdirs();
				}
			} else {
				file = new File(path + name);
				// 文件不存在时候，主动创建
				if (!file.exists()) {
					file.createNewFile();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载指定文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static File loadFile(String fileName, String filePath) {
		File file = new File(filePath + fileName);
		if (!file.exists()) {
			return null;
		}
		return file;
	}
	/**
	 * 判断文件或者路劲是否存在
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	public static boolean isFileOrPath(String fileName, String filePath) {
		File file = new File(filePath + fileName);
		return file.exists();
	}

	/**
	 * 删除指定文件
	 *
	 */
	public static void deleteFile(String filePath, String fileName) {
		File file = new File(filePath + fileName);
		if (file.exists()) {
			Boolean result = file.delete();
			if (result) {
				log.info("文件: " + fileName + " 删除成功!");
			} else {
				log.info("文件: " + fileName + " 删除异常!");
			}
		} else {
			log.info("文件: " + fileName + " 不存在!");
		}
	}

	/**
	 * 重命名文件名(文件夹路径)
	 * 
	 * @param filePath
	 *            需要重命名的文件路径
	 * @param fileName
	 *            需要重名的文件(文件夹)
	 * @param reFileNameOrPath
	 *            新文件名(文件夹路径)
	 */
	public static void renameFile(String filePath, String fileName,
			String reFileNameOrPath) {
		File file;
		if (fileName == null || "".equals(fileName)) {
			file = new File(filePath);
			// 目录不存在时候，主动创建
			if (file.exists()) {
				file.renameTo(new File(reFileNameOrPath));
				System.out.println("文件夹重名名完成" + reFileNameOrPath);
			}
		} else {
			file = new File(filePath + fileName);
			// 文件不存在时候，主动创建
			if (file.exists()) {
				file.renameTo(new File(filePath + reFileNameOrPath));
				System.out.println("文件重命名完成:" + filePath + reFileNameOrPath);
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			double bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				double fileSize = inStream.available();
				DecimalFormat df = new DecimalFormat("######0.00");
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
					// System.out.println(df.format((bytesum / fileSize) * 100)
					// + "%");
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}

}
