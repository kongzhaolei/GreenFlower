package org.gradle.needle.config;

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
			//����·����ȡJAT����������ļ�����
			String filePath = System.getProperty("user.dir") + "/src/test/resources/UIMap.properties"; 
			InputStream file = new BufferedInputStream(new FileInputStream(filePath)); 
			
			//��ȡJAR���ڵ������ļ�����,���ļ��������ļ���ͬһ����
			//InputStream file = UIMapSetting.class.getClass().getResourceAsStream("/UIMap.properties");
			UIMap.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UIMap;
	}
}
