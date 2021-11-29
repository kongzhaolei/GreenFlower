package org.gradle;

import java.io.IOException;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.gradle.needle.client.HttpClientFactory;

import com.alibaba.fastjson.JSONObject;

public class PostCase {

	private static Logger logger = Logger.getLogger(PostCase.class.getName());
	static CloseableHttpClient sclient;

	private static String url1 = "http://10.12.7.160:12306/dataserver/getforesuperversion";
	private static String url2 = "http://10.12.7.160:12306/dataserver/getforesuperupdate";
	private static String url3 = "http://10.12.8.29:12306/dataserver/getkjrldata";
	private static String url4 = "http://10.12.8.29:12306/dataserver/getfactjson";
	private static String url5 = "http://10.12.7.160:12306/dataserver/getsuperjson";

	private static String body1 = "{'wfid':'451425', 'sys_version':'v3.0.102','packages_version':'v1.0.200.21'}";
	private static String body2 = "{'wfid':'451425', 'dt':'2020-12-01 10:20:00', 'dir_name':'20201201080042288635', 'type':'pro','status':1,'message':'success-too'}";
	private static String body3 = "{\"wfids\":610826,\"datetime\":\"20191107\",\"vars\":\"wfid,dtime,r_running_cap_ratio\"}";
	private static String body4 = "{\"wfids\":610826,\"datetime\":\"201911061310\",\"vars\":\"\"}";
	private static String body5 = "{"
			+ "\"wfid\": \"371428\","
			+ "\"mark_time\": \"2020-05-28 17:00:00\","
			+ "\"pub_time\": \"2020-05-28 15:12:01\","
			+ "\"pub_data\": {"
			     + "\"nwp\": {"
			            + "\"dtime\": [\"2020-05-28 15:30:00\", \"2020-05-28 15:45:00\", \"2020-05-28 16:00:00\", \"2020-05-28 16:15:00\", \"2020-05-28 16:30:00\", \"2020-05-28 16:45:00\", \"2020-05-28 17:00:00\", \"2020-05-28 17:15:00\", \"2020-05-28 17:30:00\", \"2020-05-28 17:45:00\", \"2020-05-28 18:00:00\", \"2020-05-28 18:15:00\", \"2020-05-28 18:30:00\", \"2020-05-28 18:45:00\", \"2020-05-28 19:00:00\", \"2020-05-28 19:15:00\",\"2020-05-28 19:30:00\",\"2020-05-28 19:45:00\",\"2020-05-28 20:00:00\",\"2020-05-28 20:15:00\",\"2020-05-28 20:30:00\",\"2020-05-28 20:45:00\",\"2020-05-28 21:00:00\",\"2020-05-28 21:15:00\",\"2020-05-28 21:30:00\",\"2020-05-28 21:45:00\",\"2020-05-28 22:00:00\",\"2020-05-28 22:30:00\"],"
			            + "\"value\": [19.69, 19.56, 19.51, 19.51, 19.55, 19.63, 19.63, 19.62, 19.87, 20.11, 20.35, 20.55, 20.72, 20.86, 20.84, 20.79, 22.22, 19.69, 19.56, 19.51, 19.51, 19.55, 19.63, 19.63, 19.62, 19.87, 20.11, 20.35]"
			            + "},"
			     + "\"theorypower\": {"
			            + "\"dtime\": [\"2020-05-28 15:30:00\", \"2020-05-28 15:45:00\", \"2020-05-28 16:00:00\", \"2020-05-28 16:15:00\", \"2020-05-28 16:30:00\", \"2020-05-28 16:45:00\", \"2020-05-28 17:00:00\", \"2020-05-28 17:15:00\", \"2020-05-28 17:30:00\", \"2020-05-28 17:45:00\", \"2020-05-28 18:00:00\", \"2020-05-28 18:15:00\", \"2020-05-28 18:30:00\", \"2020-05-28 18:45:00\", \"2020-05-28 19:00:00\", \"2020-05-28 19:15:00\",\"2020-05-28 19:30:00\",\"2020-05-28 19:45:00\",\"2020-05-28 20:00:00\",\"2020-05-28 20:15:00\",\"2020-05-28 20:30:00\",\"2020-05-28 20:45:00\",\"2020-05-28 21:00:00\",\"2020-05-28 21:15:00\",\"2020-05-28 21:30:00\",\"2020-05-28 21:45:00\",\"2020-05-28 22:00:00\",\"2020-05-28 22:30:00\"],"
			            + "\"value\": [68775.69, 68884.27, 68919.84, 68919.04, 68894.16, 68833.09, 68834.75, 68840.43, 68565.06, 68156.92, 67641.33, 67133.88, 66663.41, 66247.14, 66296.36, 66448.57, 66666.66, 68775.69, 68884.27, 68919.84, 68919.04, 68894.16, 68833.09, 68834.75, 68840.43, 68565.06, 68156.92, 67641.33]"
			            + "},"
			     + "\"power\": {"
			            + "\"dtime\": [\"2020-05-28 15:30:00\", \"2020-05-28 15:45:00\", \"2020-05-28 16:00:00\", \"2020-05-28 16:15:00\", \"2020-05-28 16:30:00\", \"2020-05-28 16:45:00\", \"2020-05-28 17:00:00\", \"2020-05-28 17:15:00\", \"2020-05-28 17:30:00\", \"2020-05-28 17:45:00\", \"2020-05-28 18:00:00\", \"2020-05-28 18:15:00\", \"2020-05-28 18:30:00\", \"2020-05-28 18:45:00\", \"2020-05-28 19:00:00\", \"2020-05-28 19:15:00\",\"2020-05-28 19:30:00\",\"2020-05-28 19:45:00\",\"2020-05-28 20:00:00\",\"2020-05-28 20:15:00\",\"2020-05-28 20:30:00\",\"2020-05-28 20:45:00\",\"2020-05-28 21:00:00\",\"2020-05-28 21:15:00\",\"2020-05-28 21:30:00\",\"2020-05-28 21:45:00\",\"2020-05-28 22:00:00\",\"2020-05-28 22:30:00\"],"
			            + "\"value\": [68775.69, 68884.27, 68919.84, 68919.04, 68894.16, 68833.09, 68834.75, 68840.43, 68565.06, 68156.92, 67641.33, 67133.88, 66663.41, 66247.14, 66296.36, 66448.57, 66666.66, 68775.69, 68884.27, 68919.84, 68919.04, 68894.16, 68833.09, 68834.75, 68840.43, 68565.06, 68156.92, 67641.33]"
			            + "}"
		    + "},"
			+ "\"is_use_bak\": true}";
	
	private static String url6 = "http://10.12.8.32:9999/dataserver/app/totalDeviationStatistics";
	private static String url8 = "http://10.10.247.124:9999/dataserver/app/foreCurve";
	private static String url7 = "http://10.64.12.33:9999/dataserver/soam/foreEnergyFourHourTotal";

	private static String url = "https://10.200.50.136:9200/ws/NEMWholesale/selfForecast/v1/SubmitDispatchForecast";
	private static String tokenurl = "https://metapi.goldwind.com.cn:443/api/token";
	private static String durl = "https://metapi.goldwind.com.cn/weather/v1/weather_fcst/optimal2/hourly/records/?lon=116.02&lat=39.5581";

	private static String body6 = "{\"date\":\"2019-06-28 00:30\",\"wfIds\":\"[450323,450327,450329,450338]\"}";
	private static String body8 = "{\"date\":\"2019-06-28 12:15\",\"wfId\":\"152523\"}";
	private static String body7 = "data={\"date\":\"2019-06-28 12:15\",\"wfIds\":\"[632500]\"}";
	
	private static String url9 = "http://10.10.247.82/wpfore/centralize/StaticWpDeviationAction_queryStaticDevReportData.action";
	private static String body9 = "{\"beginDate\":\"2020-06-23\",\"endDate\":\"2020-06-29\",\"wpList\":\"652204\",\"probability\":\"false\"}";
	private static String cookie = "JSESSIONID=7AAB49B8AA4EEC00CC86FF316F2D2784";
	
	private static String tokenkey = "Basic cG93ZXJfZm9yZWNhc3Q6a3RxRy1mQHJyKUV0";
	

	public static void main(String[] args) throws IOException {
		sclient = HttpClients.createDefault();
		//sclient = HttpClientFactory.getSSLHttpClient();
		HttpPost post = new HttpPost(url1);
		// HttpGet gettoken = new HttpGet(tokenurl);
		// HttpGet normalget = new HttpGet(durl);

		try {
			// gettoken.setHeader("Authorization", tokenkey);
			post.addHeader("Content-Type", "application/json;charset=UTF-8");
		    post.addHeader(new BasicHeader("Cookie",cookie));
			post.setEntity(new StringEntity(body1));
			//HttpResponse httpResponse = sclient.execute(gettoken);
			//HttpEntity entity = httpResponse.getEntity();
			//String stoken = EntityUtils.toString(entity, "UTF-8");
			//JSONObject json = JSONObject.parseObject(stoken);
			//String token = json.getString("token");
			//logger.info(token);
			
			//String token2 = "power_forecast:" + token;
			//normalget.setHeader("Authorization","Basic " + Base64.getEncoder().encodeToString(token2.getBytes()));
			HttpResponse dResponse = sclient.execute(post);
			HttpEntity dentity = dResponse.getEntity();
			String data = EntityUtils.toString(dentity, "UTF-8");
			logger.info(data);
			
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			
			sclient.close();
		}
	}
}
