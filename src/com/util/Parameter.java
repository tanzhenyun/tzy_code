package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Properties文件读取类
 * 
 * @author KF_04
 * 
 */
public class Parameter {
	/**
	 * 读取Properties参数值
	 * 
	 * @param param
	 * @return
	 */
	public static String getParameter(String param) {
		Properties properties = new Properties();
		String path = Parameter.class.getClassLoader().getResource("config.properties").getPath();

		// 使用properties对象加载输入流
		try {

			InputStream is = new FileInputStream(path);
			properties.load(is);

			// properties.load(in);
			// 获取key对应的value值
			return properties.getProperty(param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
