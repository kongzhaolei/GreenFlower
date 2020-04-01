package org.gradle;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.gradle.needle.client.HttpClientFactory;

public class PostCase {

	private static Logger logger = Logger.getLogger(PostCase.class.getName());
	static CloseableHttpClient sclient;

	private static String url1 = "http://10.12.8.29:12306/dataserver/cdqbasedate";
	private static String url2 = "http://10.12.8.29:12306/dataserver/getfactdatajson";
	private static String url3 = "http://10.12.8.29:12306/dataserver/getkjrldata";
	private static String url4 = "http://10.12.8.29:12306/dataserver/getfactjson";	
	private static String url5 = "http://10.12.8.29:12306/dataserver/getsuperjson";
	
	private static String body2 = "{\"wfids\":610826,\"date\":\"201911\",\"vars\":\"wfid, dtime, r_wspd, r_pres, r_running_cap\"}";
	private static String body3 = "{\"wfids\":610826,\"datetime\":\"20191107\",\"vars\":\"wfid,dtime,r_running_cap_ratio\"}";
	private static String body4 = "{\"wfids\":610826,\"datetime\":\"201911061310\",\"vars\":\"\"}";
	private static String body5 = "{\"wfid\":610826, \"mark_time\": \"2019-08-02 12:45:00\", \"pub_data\": { \"nwp\": { \"dtime\": [\"2019-08-02 13:00:00\", \"2019-08-02 13:15:00\"], 	\"value\": [8.144829884594277, 11.56] }, \"theorypower\": { \"dtime\": [\"2019-08-02 13:00:00\", \"2019-08-02 13:15:00\"], 	\"value\": [null, null] }, \"power\": { \"dtime\": [\"2019-08-02 13:00:00\", \"2019-08-02 13:15:00\"], \"value\": [43555.484397154396, 144992.73] } } }";
  
	
	private static String url6 = "http://10.12.8.32:9999/dataserver/app/totalDeviationStatistics";
	private static String url8 = "http://10.10.247.124:9999/dataserver/app/foreCurve";
	private static String url7 = "http://10.64.12.33:9999/dataserver/soam/foreEnergyFourHourTotal";
	
	private static String url = "https://10.200.50.136:9200/ws/NEMWholesale/selfForecast/v1/SubmitDispatchForecast";

	private static String body6 = "{\"date\":\"2019-06-28 00:30\",\"wfIds\":\"[450323,450327,450329,450338]\"}";
    private static String body8 = "{\"date\":\"2019-06-28 12:15\",\"wfId\":\"152523\"}";
    private static String body7 = "data={\"date\":\"2019-06-28 12:15\",\"wfIds\":\"[632500]\"}";
	
	public static void main(String[] args) throws IOException {
		//sclient = HttpClients.createDefault();
		sclient = HttpClientFactory.getSSLHttpClient();
		HttpPost post = new HttpPost(url4);

		try {
			post.addHeader("Content-Type", "application/json;c	harset=UTF-8");
			post.setEntity(new StringEntity(body4));
			HttpResponse httpResponse = sclient.execute(post);
			HttpEntity entity = httpResponse.getEntity();
			String response = EntityUtils.toString(entity, "UTF-8");
			logger.info(response);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			sclient.close();
		}
	}
}
