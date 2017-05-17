package org.gradle.needle.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class CustomUtils {
	
	 private static Logger logger = Logger.getLogger(CustomUtils.class);
	
	/**
	 * 中控自定义格式，非标准json或xml格式
	 * @param response
	 * @return
	 */
	public static Map<String, String> parser(String response) {
		Map<String, String> m = new HashMap<String, String>();
		if (response != null) {
			if (response.indexOf("OK") >= 0) {
				String[] tokens = response.split("},");
				for (int i = 0; i < tokens.length; i++) {
					int firstindex = tokens[i].indexOf("\"");
					int lastindex = tokens[i].lastIndexOf("\"");
					String key = tokens[i].substring(firstindex + 1,
							tokens[i].indexOf("\"", firstindex + 1) - 1);
					String value = tokens[i].substring(
							tokens[i].lastIndexOf("\"", lastindex - 1) + 1,
							lastindex - 1);
					m.put(key, value);
				}
			} else {
				logger.info("非正常返回结果，不予以解析");
			}
		} else {
			logger.info("返回结果为空，不予以解析");
		}
		return m;
	}

}
