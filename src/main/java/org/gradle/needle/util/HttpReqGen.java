/**
 * @author kongzhaolei 
 * 这个类主要功能是提供POST或者get过程中需要的Template。
 * 将ExcelDataUtil返回的requestdataset组装为固定模板文件组
 */
package org.gradle.needle.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gradle.HttpServiceTest;

public class HttpReqGen {
	private static Logger logger = Logger.getLogger(HttpServiceTest.class
			.getName());

	public HttpReqGen() {

	}

	/**
	 * 根据模板样式，生成HTTP请求的header和body 
	 * url是String类型，header是map类型，body是map类型
	 * 
	 * @param requestdatamap
	 * @return Iterator{[String,Map,String],[String,Map,String]]}
	 * @throws Exception
	 */
	public static Iterator<Object[]> prerequest(
			Iterator<Map<String, String>> datamap) throws Exception {
		List<Object[]> requestfiles = new ArrayList<Object[]>();
		String template = "";

		// 加载request模板
		try {
			String filePath = System.getProperty("user.dir")
					+ "/src/test/resources/http_request_template.txt";
			InputStream file = new BufferedInputStream(new FileInputStream(
					filePath));
			template = IOUtils.toString(file, Charset.defaultCharset());
		} catch (Exception e) {
			logger.info("模板不存在或无法读取");
			e.printStackTrace();
		}

		String[] nodes = HttpReqGen.tokenize_template(template);
		while (datamap.hasNext()) {
			Map<String, String> parmset = datamap.next();
			String[] parms = new String[nodes.length];
			Map<String, String> header = new HashMap<String, String>();
			Map<String, String> body = new HashMap<String, String>();
			List<Object> request = new ArrayList<Object>();
			    
			try {
				String testid = parmset.get("TestId");
				request.add(testid.trim());
				// 组装HTTP请求行url
				String call_type = parmset.get(nodes[0]);
				request.add(call_type);
				String url = parmset.get(nodes[1]);
				request.add(url);
			} catch (Exception e) {
				logger.error("HTTP请求行出现异常，请检查模板和数据源");
				throw new RuntimeException(e);
			}

			// 组装HTTP头部header
			for (int i = 2; i < nodes.length - 1; i++) {
				parms[i] = parmset.get(nodes[i]);
				// 检查header节点是否存在数据源
				if (parms[i] != null) {
					header.put(nodes[i], parms[i]);
				} else {
					logger.info(parms[i] + "无法匹配到header参数，请检查数据源");
				}
			}
			request.add(header);
			
			// 组装用于post方法的body
			String sbody = parmset.get(nodes[nodes.length - 1]);
			String[] zbody = sbody.split("&");
			for (int i = 0; i < zbody.length; i++) {
				if (zbody[i] != null){
				String key = zbody[i].substring(0, zbody[i].indexOf("="));
				String value = zbody[i].substring(zbody[i].indexOf("=") + 1);
				body.put(key, value);
				}else{
					logger.info(zbody[i] + "解析错误，请检查" + sbody);
				}
			}
			request.add(body);
			requestfiles.add(request.toArray());
		}
		Iterator<Object[]> s = requestfiles.iterator();
		return s;
	}

	/**
	 * 引入一个模板，将模板中各节点的参数放入数组。 利用模板的所有节点形式参数来匹配外部数据源的实际参数
	 * 
	 * @param temp
	 * @return nodes[]
	 */
	private static String[] tokenize_template(String temp) {
		String[] tokens = temp.split("(?=[<]{2})|(?<=[>]{2})");
		String[] nodes = new String[tokens.length / 2 + 1];
		int i = 0;
		for (String item : tokens) {
			if (item.startsWith("<<") && item.endsWith(">>")) {
				nodes[i] = item.substring(2, item.length() - 2);
				i++;
			}
		}
		return nodes;
	}
}
