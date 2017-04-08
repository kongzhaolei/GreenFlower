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
	// ���ó�ʱ ms
	private static int timeout = 360000;
	private static Logger logger = Logger.getLogger(HttpClientFactory.class
			.getName());
	static HttpClient sclient;
	static String response = "";

	/*
	 * �����ڱ�������new��һ���µ�httpclient������ÿ�ε��ø÷������ӿڻ��ظ�ʹ��httpclient��״̬
	 * ���½ӿ���������������ؽ��
	 */
	public static String invokeServiceMethod(String call_type, String url,
			Map<String, String> header, Map<String, String> body) {
		
		sclient = new HttpClient();
		// ����������ʱʱ�䣺1. url�����ӵȴ�ʱ�䣻2. ��ȡresponse�ķ��صȴ�ʱ��
		sclient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(timeout);
		sclient.getHttpConnectionManager().getParams().setSoTimeout(timeout);

		if (call_type.equalsIgnoreCase("get")) {
			response = requestGetMethod(url, header);
		} else {
			if (call_type.equalsIgnoreCase("post")) {
				response = requestPostMethod(url, header, body);
			} else {
				logger.info(call_type + "�����ڱ���ܴ���Χ��");
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
			logger.info("*************************** HTTP ���󷵻ش���Ϊ��" + statuscode + "\r\n");
			
			// HTTP post������תҳ�治���Զ�ת�����������Ҫ�����¶�λurl
			if (statuscode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statuscode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationheader = postMethod
						.getResponseHeader("location");
				if (locationheader != null) {
					String newurl = locationheader.getValue();
					logger.info("ҳ������ת��:" + newurl);
				} else {
					logger.info("ҳ��û�����¶�λ");
				}
			}
            
			//���ڷ��ش���200�����󣬶�ȡ��������
			if (statuscode == HttpStatus.SC_OK) {
				logger.info("HTTP ����ɹ�" + "\r\n");
				byte[] responsebody = postMethod.getResponseBody();
				res = new String(responsebody, "UTF-8");
			}
		} catch (HttpException e) {
			logger.info("HTTP �����ַ����ȷ" + e.getMessage());
		} catch (IOException e) {
			logger.info("�����쳣: " + e.getMessage());
		} catch (Exception e) {
			logger.info("HTTP ����ʧ��", e);
		} finally {
			// �ͷ�����
			postMethod.releaseConnection();
			((SimpleHttpConnectionManager) sclient.getHttpConnectionManager())
					.shutdown();
		}
		return res;
	}

	private static String requestGetMethod(String url,
			Map<String, String> header) {
		// TODO �Զ����ɵķ������
		return null;
	}
}
