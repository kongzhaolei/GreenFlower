/**
 * HTTPClient 3.1
 */
package org.gradle.needle.client;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class HttpClientFactory {
	// 设置超时 ms
	private static int timeout = 360000;
	private static Logger logger = Logger.getLogger(HttpClientFactory.class
			.getName());
	static HttpClient sclient;
	static String response = "";

	/*
	 * 必须在本方法中new出一个新的httpclient，否则每次调用该方法，接口会重复使用httpclient的状态
	 * 导致接口请求后不能正常返回结果
	 */
	public static String invokeServiceMethod(String call_type, String url,
			Map<String, String> header, Map<String, String> body) {
		
		sclient = new HttpClient();
		// 设置两个超时时间：1. url的连接等待时间；2. 获取response的返回等待时间
		sclient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(timeout);
		sclient.getHttpConnectionManager().getParams().setSoTimeout(timeout);

		if (call_type.equalsIgnoreCase("get")) {
			response = requestGetMethod(url, header);
		} else {
			if (call_type.equalsIgnoreCase("post")) {
				response = requestPostMethod(url, header, body);
			} else {
				logger.info(call_type + "请求不在本框架处理范围内");
			}
		}
		return response;
	}

	private static String requestPostMethod(String url,
			Map<String, String> header, Map<String, String> body) {

		String res = "";
		PostMethod postMethod = new PostMethod(url.trim());
		// postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
		Header[] headerparm = new Header[header.size()];
		NameValuePair[] bodyparm = new NameValuePair[body.size()];

		int i = 0;
		for (Map.Entry<String, String> entry : header.entrySet()) {
			headerparm[i] = new Header(entry.getKey(), entry.getValue());
			postMethod.setRequestHeader(headerparm[i]);
			i++;
		}

		int j = 0;
		for (Map.Entry<String, String> entry : body.entrySet()) {
			bodyparm[j] = new NameValuePair(entry.getKey(), entry.getValue());
			j++;
		}
		postMethod.setRequestBody(bodyparm);
		
		try {
			int statuscode = sclient.executeMethod(postMethod);
			logger.info("*************************** HTTP 请求返回代码为：" + statuscode + "\r\n");
			
			// HTTP post对于跳转页面不能自动转发的情况，需要做重新定位url
			if (statuscode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statuscode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationheader = postMethod
						.getResponseHeader("location");
				if (locationheader != null) {
					String newurl = locationheader.getValue();
					logger.info("页面已跳转到:" + newurl);
				} else {
					logger.info("页面没有重新定位");
				}
			}
            
			//对于返回代码200的请求，读取返回内容
			if (statuscode == HttpStatus.SC_OK) {
				logger.info("HTTP 请求成功" + "\r\n");
				byte[] responsebody = postMethod.getResponseBody();
				res = new String(responsebody, "UTF-8");
			}
		} catch (HttpException e) {
			logger.info("HTTP 请求地址不正确" + e.getMessage());
		} catch (IOException e) {
			logger.info("网络异常: " + e.getMessage());
		} catch (Exception e) {
			logger.info("HTTP 请求失败", e);
		} finally {
			// 释放连接
			postMethod.releaseConnection();
			((SimpleHttpConnectionManager) sclient.getHttpConnectionManager())
					.shutdown();
		}
		return res;
	}

	private static String requestGetMethod(String url,
			Map<String, String> header) {
		// TODO 自动生成的方法存根
		return null;
	}
}
