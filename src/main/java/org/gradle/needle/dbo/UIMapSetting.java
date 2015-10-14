package org.gradle.needle.dbo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class UIMapSetting {
	private static Properties UIMap;

	private UIMapSetting() {

	}
	
	public static Properties getUIMap() {
		UIMap = new Properties();
		try {
			//绝对路径读取JAT包外的配置文件方法
			String filePath = System.getProperty("user.dir") + "/src/test/resources/UIMap.properties"; 
			InputStream file = new BufferedInputStream(new FileInputStream(filePath)); 
			
			//读取JAR包内的配置文件方法,类文件和配置文件在同一包内
			//InputStream file = UIMapSetting.class.getClass().getResourceAsStream("/UIMap.properties");
			UIMap.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UIMap;
	}
}
