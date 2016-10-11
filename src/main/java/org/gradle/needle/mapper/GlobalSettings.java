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
package org.gradle.needle.mapper;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class GlobalSettings {

	// 浏览器基本配置
	public static Properties prop = getProperties();
	public static int browserCoreType = Integer.parseInt(prop.getProperty(
			"BrowserCoreType", "2"));
	public static String chromeDriverPath = prop.getProperty(
			"ChromeDriverPath", "res/chromedriver_for_win.exe");
	public static String ieDriverPath = prop.getProperty("IEDriverPath",
			"otherpath");
	public static String stepInterval = prop.getProperty("StepInterval", "500");
	public static String timeout = prop.getProperty("Timeout", "30000");


	// Excel数据文件路径配置
	public static String ExcelDataFile = prop.getProperty("ExcelDataFile", "");

	// XML数据文件配置
	public static String XmlDataFile1 = prop.getProperty("XmlDataFile1", "");
	
	//定义接口测试中读取或写入的列号，对应于ExcelDataFile的数据列
	public static int expect = Integer.parseInt((prop.getProperty("expect", "")));
	public static int response = Integer.parseInt((prop.getProperty("response", "")));
	public static int E_key = Integer.parseInt((prop.getProperty("E_key", "")));
	public static int E_value = Integer.parseInt((prop.getProperty("E_value", "")));
	public static int A_key = Integer.parseInt((prop.getProperty("A_key", "")));
	public static int A_value = Integer.parseInt((prop.getProperty("A_value", "")));
	public static int Result = Integer.parseInt((prop.getProperty("Result", "")));
	

	public static String getProperty(String property) {
		return prop.getProperty(property);
	}

	public static Properties getProperties() {
		Properties prop = new Properties();
		try {
			//绝对路径读取JAT包外的配置文件方法
//			String filePath = System.getProperty("user.dir") + "/src/main/resources/Globalsetting.properties"; 
//			InputStream file = new BufferedInputStream(new FileInputStream(filePath)); 
			
			//读取JAR包内的配置文件方法,类文件和配置文件在同一包内
			InputStream file = GlobalSettings.class.getClass().getResourceAsStream("/Globalsetting.properties");
			
			prop.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
}