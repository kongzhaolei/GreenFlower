/**
 * HTTPClient 4.3
 * 
 */
package org.gradle.needle.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientFactory {
	private static CloseableHttpClient sclient;
	private static Logger logger = Logger.getLogger(HttpClientFactory.class
			.getName());
	private static String response = "";
	// connection和socket超时配置项
	private static int timeout = 60000;
	private static RequestConfig timeconfig = RequestConfig.custom()
			.setSocketTimeout(timeout).setConnectTimeout(timeout).build();

	public static String invokeServiceMethod(String call_type, String url,
			Map<String, String> header, Map<String, String> body) {

		if (call_type.equalsIgnoreCase("get")) {
			response = requestGetMethod(url, header);
		} else {
			if (call_type.equalsIgnoreCase("post")) {
				response = httpPostWay(url, header, body);
			} else {
				logger.info(call_type + "请求不在本框架处理范围内");
			}
		}
		return response;
	}

	private static String requestGetMethod(String url,
			Map<String, String> header) {
		// TODO 自动生成的方法存根
		return null;
	}
	
    /**
     * HttpPost
     * @param url
     * @param header
     * @param body
     * @return
     */
	private static String httpPostWay(String url,
			Map<String, String> header, Map<String, String> body) {
		
		sclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url.trim());
		post.setConfig(timeconfig);

		// 设置header
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				post.addHeader(entry.getKey(), entry.getValue());
			}
		}

		// 设置body
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		if (body != null) {
			for (Map.Entry<String, String> entry : body.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}

		// 发送请求
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
			HttpResponse httpResponse = sclient.execute(post);
			int statuscode = httpResponse.getStatusLine().getStatusCode();
			if (statuscode == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				response = EntityUtils.toString(entity, "UTF-8");
				logger.info("接口请求成功");
			}else {
				logger.info("接口请求失败，statuscode： " + statuscode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("网络异常:" + e.getMessage());
		} finally {
			post.releaseConnection();
		}
		return response;
	}

}
