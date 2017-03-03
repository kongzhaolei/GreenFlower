/**
 * @author kongzhaolei 
 * �������Ҫ�������ṩPOST����get��������Ҫ��Template��
 * ��ExcelDataUtil���ص�requestdataset��װΪ�̶�ģ���ļ���
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


public class HttpReqGen {
	private static Logger logger = Logger.getLogger(HttpReqGen.class
			.getName());

	public HttpReqGen() {

	}

	/**
	 * ����ģ����ʽ������HTTP�����header��body 
	 * url��String���ͣ�header��map���ͣ�body��map����
	 * 
	 * @param requestdatamap
	 * @return Iterator{[String,Map,String],[String,Map,String]]}
	 * @throws Exception
	 */
	public static Iterator<Object[]> preReqGen(
			Iterator<Map<String, String>> datamap) throws Exception {
		List<Object[]> requestfiles = new ArrayList<Object[]>();
		String template = "";

		// ����requestģ��
		try {
			String filePath = System.getProperty("user.dir")
					+ "/src/main/resources/http_request_template.txt";
			InputStream file = new BufferedInputStream(new FileInputStream(
					filePath));
			template = IOUtils.toString(file, Charset.defaultCharset());
		} catch (Exception e) {
			logger.info("ģ�岻���ڻ��޷���ȡ");
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
				// ��װHTTP������url
				String call_type = parmset.get(nodes[0]);
				request.add(call_type);
				String url = parmset.get(nodes[1]);
				request.add(url);
			} catch (Exception e) {
				logger.error("HTTP�����г����쳣������ģ�������Դ");
				throw new RuntimeException(e);
			}

			// ��װHTTPͷ��header
			for (int i = 2; i < nodes.length - 1; i++) {
				parms[i] = parmset.get(nodes[i]);
				// ���header�ڵ��Ƿ��������Դ
				if (parms[i] != null) {
					header.put(nodes[i], parms[i]);
				} else {
					logger.info(parms[i] + "�޷�ƥ�䵽header��������������Դ");
				}
			}
			request.add(header);
			
			// ��װ����post������body
			String sbody = parmset.get(nodes[nodes.length - 1]);
			String[] zbody = sbody.split("&");
			for (int i = 0; i < zbody.length; i++) {
				if (zbody[i] != null){
				String key = zbody[i].substring(0, zbody[i].indexOf("="));
				String value = zbody[i].substring(zbody[i].indexOf("=") + 1);
				body.put(key, value);
				}else{
					logger.info(zbody[i] + "������������" + sbody);
				}
			}
			request.add(body);
			requestfiles.add(request.toArray());
		}
		Iterator<Object[]> s = requestfiles.iterator();
		logger.info("HTTP��������װ���");
		return s;
	}

	/**
	 * ����һ��ģ�壬��ģ���и��ڵ�Ĳ����������顣 ����ģ������нڵ���ʽ������ƥ���ⲿ����Դ��ʵ�ʲ���
	 * 
	 * @param temp
	 * @return nodes[]
	 */
	private static String[] tokenize_template(String temp) {
		String[] tokens = temp.split("(?=[<]{2})|(?<=[>]{2})");
		String[] nodes = new String[tokens.length / 2];
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
