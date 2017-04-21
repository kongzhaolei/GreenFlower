/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.gradle.needle.util;

import java.io.InputStream;
import java.util.Properties;

public class GlobalSettings {
	
	public static Properties prop = getFileProperties();
	
	// �������������
	public static int browserCoreType = Integer.parseInt(prop.getProperty(
			"BrowserCoreType", "2"));
	public static String chromeDriverPath = prop.getProperty(
			"ChromeDriverPath", "res/chromedriver_for_win.exe");
	public static String ieDriverPath = prop.getProperty("IEDriverPath",
			"otherpath");
	public static String stepInterval = prop.getProperty("StepInterval", "500");
	public static String timeout = prop.getProperty("Timeout", "30000");

	// webapi���������ļ�·������
	public static String ExcelDataFile = prop.getProperty("ExcelDataFile", "");
	// key

	// XML�����ļ�����
	public static String XmlDataFile1 = prop.getProperty("XmlDataFile1", "");
	
	public static String getProperty(String property) {
		return prop.getProperty(property,"");
	}

	public static Properties getFileProperties() {
		Properties prop = new Properties();
		try {
			//����·����ȡJAT����������ļ�����
//			String filePath = System.getProperty("user.dir") + "/src/main/resources/Globalsetting.properties"; 
//			InputStream file = new BufferedInputStream(new FileInputStream(filePath)); 
			
			//��ȡJAR���ڵ������ļ�����,���ļ��������ļ���ͬһ����
			InputStream file = GlobalSettings.class.getClass().getResourceAsStream("/Globalsetting.properties");
			
			prop.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
}