package org.gradle.test;

import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Update {

	static CloseableHttpClient sclient;
	private static String url = "http://10.1.39.14:9080/offline/task/update";

	public Update() {
		sclient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);

		JSONObject jsonParam = new JSONObject();
		try {
			jsonParam.put("zflttools", "");
			jsonParam.put("Urgency", "4");
			jsonParam.put("AffectedItem", "1500-2009044-27#");
			jsonParam.put("ZTurbineModel", "82\\/1500");
			jsonParam.put("TaskID", "FA201508130154-001");
			jsonParam.put("start", "");
			jsonParam.put("OpenedBy", "6729");
			jsonParam.put("ZFltSecurity", "防止触电，防止磕碰，防止挤伤");
			jsonParam.put("ParentIncident", "FA201508130154");
			jsonParam.put("Phase", "Active");
			jsonParam.put("OfflineStatus", "0");
			jsonParam.put("zfltfailurefrom2", "");
			jsonParam.put("Category", "Trouble Shooting Task");
			jsonParam.put("ZTurbineCapacity", "1500");
			jsonParam.put("zfltfailurefrom3", "");
			jsonParam.put("ZFltMethod", "");
			jsonParam.put("end", "");
			jsonParam.put("zfltfailureid", "39");
			jsonParam.put("ProjectName", "山东沾化国华二期风电场");
			jsonParam.put("ZFltTools", "万用表、绝缘手套、一字螺丝刀、十字螺丝刀、端子起、扳手");
			jsonParam.put("ZFltFailurefrom2", "");
			jsonParam.put("zfltsecurity", "");
			jsonParam.put("ZFltTaskType", "electric");
			jsonParam.put("Status", "Work In Progress");
			jsonParam.put("ZFltFailurefrom3", "");
			jsonParam.put("troubleShootingTaskModelType", "");
			jsonParam.put("ZFltFailurefrom", "");
			jsonParam.put("instance", "");
			jsonParam.put("zfltfailurefrom", "");
			jsonParam.put("method", "");
			jsonParam.put("AREA", "华中服务事业部");
			jsonParam.put("Title", "山东沾化国华二期风电场27#电流不平衡");
			jsonParam.put("ZFltFailureID", "39");
			jsonParam.put("Description", "功率：950KV\\/H");
			jsonParam.put("ZTurbineConverter", "The Switch变流器");
			jsonParam.put("ZTurbinePitch", "金风1.5MW变桨驱动器I型");
			jsonParam.put("Assignee", "6729");
			jsonParam.put("UpdatedBy", "offline offline");
			jsonParam.put("OpenTime", "2015-08-13T06:05:27Z");
			jsonParam.put("ZFltFailurename", "电流不平衡");
			jsonParam.put("ZAssigneeFullname", "代卫强");
			jsonParam.put("person1", "");
			jsonParam.put("AssignmentGroup", "山东东营河口项目部");
			jsonParam.put("UpdatedTime", "2015-08-21T06:18:51Z");
			jsonParam.put("ProjectCode", "GOLDWIND-1500-2009044");
			jsonParam.put("keys", "");

			post.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			post.addHeader("User-Agent",
					"GoldWind/4.0.4A CFNetwork/711.1.16 Darwin/14.0.0");

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("taskid", "FA201508130154-001"));
			params.add(new BasicNameValuePair("steps", jsonParam.toString()));
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse httpResponse = sclient.execute(post);
			HttpEntity entity = httpResponse.getEntity();
			String response = EntityUtils.toString(entity, "UTF-8");
			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
