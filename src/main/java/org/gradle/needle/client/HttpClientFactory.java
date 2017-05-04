/**
 * HTTPClient 4.3
 * 
 */
package org.gradle.needle.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClientFactory {
	private static CloseableHttpClient httpclient;
	private static PoolingHttpClientConnectionManager connManager;
	private static RequestConfig timeconfig;
	private static Logger logger = Logger.getLogger(HttpClientFactory.class.getName());
	private static String response = "";
	private static int statuscode;
	private static int timeout = 60000;

	/**
	 * 初始化配置
	 */
	static {
		// 设置连接池
		connManager = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connManager.setMaxTotal(100);
		connManager.setDefaultMaxPerRoute(connManager.getMaxTotal());
		// connection和socket超时配置项
		timeconfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
	}

	/**
	 * SSL CloseableHttpClient
	 * 
	 * @return CloseableHttpClient
	 */
	private static CloseableHttpClient getSSLHttpClient() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);

		// 指定信任密钥存储对象和连接套接字工厂
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			// 信任任何链接
			TrustStrategy anyTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			};
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy)
					.build();
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			registryBuilder.register("https", sslSF);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		// 设置连接管理器
		connManager = new PoolingHttpClientConnectionManager(registry);
		// 构建客户端
		return HttpClientBuilder.create().setConnectionManager(connManager).build();
	}

	/**
	 * http get and post
	 */
	public static String invokeServiceMethod(String call_type, String url, Map<String, String> header,
			Map<String, String> body) {

		if (call_type.equalsIgnoreCase("get")) {
			response = httpGetWay(url, header);
		} else {
			if (call_type.equalsIgnoreCase("post")) {
				response = httpPostWay(url, header, body);
			} else {
				logger.info(call_type + "请求不在本框架处理范围内");
			}
		}
		return response;
	}

	private static String httpGetWay(String url, Map<String, String> header) {
		// TODO 自动生成的方法存根
		return null;
	}

	/**
	 * 发送POST请求，获取response HttpPost
	 * 
	 * @param url
	 * @param header
	 * @param body
	 * @return
	 */
	private static String httpPostWay(String url, Map<String, String> header, Map<String, String> body) {

		// httpclient = HttpClients.createDefault();
		httpclient = getSSLHttpClient();
		HttpPost post = new HttpPost(url);
		post.setConfig(timeconfig);

		// 设置header
		if (header != null) {
			for (Map.Entry<String, String> entry : header.entrySet()) {
				post.addHeader(entry.getKey().trim(), entry.getValue().trim());
			}
		}

		// 设置body
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		if (body != null) {
			for (Map.Entry<String, String> entry : body.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey().trim(), entry.getValue().trim()));
			}
		}

		// 发送请求
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(post);
			statuscode = httpResponse.getStatusLine().getStatusCode();
			if (statuscode == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				response = EntityUtils.toString(entity, "UTF-8");
				logger.info("接口请求成功");
			} else {
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

	/*
	 * 获取响应码
	 */
	public static int getStatusCode() {
		return statuscode;
	}
}
