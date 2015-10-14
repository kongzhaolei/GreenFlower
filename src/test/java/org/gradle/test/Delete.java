package org.gradle.test;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Delete {

	static CloseableHttpClient sclient;
	private static String url = "http://10.1.39.14:9080/offline/delete";

	public Delete() {
		sclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);

		try {
			post.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			post.addHeader("User-Agent",
					"GoldWind/4.0.4A CFNetwork/711.1.16 Darwin/14.0.0");

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("taskId", "FA201508130154-001"));
			params.add(new BasicNameValuePair("groupName", "山东东营河口项目部"));
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse httpResponse = sclient.execute(post);
			HttpEntity entity = httpResponse.getEntity();
			String response = EntityUtils.toString(entity, "UTF-8");
			System.out.println(response);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
