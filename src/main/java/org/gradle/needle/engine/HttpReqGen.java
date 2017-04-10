/**
 * @author kongzhaolei 
 * �������Ҫ�������ṩPOST����get��������Ҫ��Template��
 * ��ExcelDataUtil���ص�requestdataset��װΪ�̶�ģ���ļ���
 */
package org.gradle.needle.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class HttpReqGen {
	private static Logger logger = Logger.getLogger(HttpReqGen.class
			.getName());

	public HttpReqGen() {

	}

	/**
	 * url��String���ͣ�header��map���ͣ�body��map����
	 * @param requestdatamap
	 * @return Iterator{arr1,arr2]}
	 * @throws Exception
	 */
	public static Iterator<Object[]> preReqGen(
			Iterator<Map<String, String>> datamap) throws Exception {
		List<Object[]> Cases = new ArrayList<Object[]>();
		while (datamap.hasNext()) {
			Map<String, String> parmset = datamap.next();
			Map<String, String> header = new HashMap<String, String>();
			Map<String, String> body = new HashMap<String, String>();
			List<Object> Case = new ArrayList<Object>();
			    
			try {
				String testid = parmset.get("TestId");
				Case.add(testid.trim());
				// ��װHTTP������url
				String call_type = parmset.get("call_type");
				Case.add(call_type.trim());
				String url = parmset.get("url");
				Case.add(url.trim());
				
				// ��װHTTPͷ��header
				header.put("User-Agent", parmset.get("User-Agent"));
				header.put("Content-Type", parmset.get("Content-Type"));
				header.put("Cookie", parmset.get("Cookie"));
				Case.add(header);
				
				// ��װ����post������body
				String sbody = parmset.get("Body");
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
				Case.add(body);
				Cases.add(Case.toArray());	
			} catch (Exception e) {
				throw new RuntimeException(e);
			}		
		}
		Iterator<Object[]> s = Cases.iterator();
		logger.info("HTTP��������װ���");
		return s;
	}
}
