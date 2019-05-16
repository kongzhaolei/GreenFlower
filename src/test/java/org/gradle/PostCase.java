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

public class PostCase {

	private static Logger logger = Logger.getLogger(PostCase.class.getName());
	static CloseableHttpClient sclient;

	private static String url1 = "http://10.12.8.29:9999/dataserver/cdqbasedate";
	private static String url2 = "http://10.12.8.36:9999/dataserver/getfactdatajson";
	private static String url3 = "http://10.12.8.36:9999/dataserver/getkjrldata";
	private static String url4 = "http://10.12.8.41:9999/dataserver/getfactjson";
	private static String url5 = "http://10.12.8.36:9999/dataserver/getsuperjson";
	private static String body2 = "{\"wfids\":652243,\"date\":\"201805\",\"vars\":\"wfid, dtime, r_pres\"}";
	private static String body3 = "{\"wfids\":652243,\"datetime\":\"20190416\",\"vars\":\"wfid,dtime,r_running_cap_ratio\"}";
	private static String body4 = "{\"wfids\":652301,\"datetime\":\"201905081755\",\"vars\":\"wfid,dtime,r_wspd\"}";
	private static String body5 = "{\"wfid\": 652243, 	\"mark_time\": \"2019-04-02 12:45:00\", \"pub_data\": { \"nwp\": { \"dtime\": [\"2019-04-02 13:00:00\", \"2019-04-02 13:15:00\"], 	\"value\": [8.144829884594277, 11.56] }, \"theorypower\": { \"dtime\": [\"2019-04-01 13:00:00\", \"2019-04-01 13:15:00\"], 	\"value\": [147589.74, 148228.52] }, \"power\": { \"dtime\": [\"2019-04-01 13:00:00\", \"2019-04-01 13:15:00\"], \"value\": [43555.484397154396, 144992.73] } } }";

	public static void main(String[] args) throws IOException {
		sclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url4);

		try {
			post.addHeader("Content-Type", "application/json;charset=UTF-8");
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
