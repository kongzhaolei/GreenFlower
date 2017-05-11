package org.gradle.needle.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.needle.model.JsonData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * json序列化与反序列化
 * 
 * @author kongzhaolei
 *
 */
public class JsonUtils {

	/*
	 * 嵌套json反序列化 返回 java对象
	 */
	public static JsonData FromJson(String json) {
		Gson gson = new Gson();
		JsonData md = gson.fromJson(json, JsonData.class);
		return md;
	}

	/*
	 * 解析childobject转化为Map<String,JsonObject>对象
	 */
	public static Map<String, JsonElement> childToMap(String json, String childlabel) {
		Map<String, JsonElement> wtjson = new HashMap<String, JsonElement>();
		Gson gson2 = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type type = new TypeToken<Map<String, JsonElement>>() {
		}.getType();
		try {
			JsonElement childobject = FromJson(json).getData().getAsJsonObject("cli").getAsJsonObject("dps")
					.getAsJsonObject(childlabel);
			wtjson = gson2.fromJson(childobject, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wtjson;
	}

	// json 关键字出现次数，正则表达式
	public static int KeyFrequency(String res, String keyword) {
		int count = 0;
		Pattern pattern = Pattern.compile(keyword);
		Matcher matcher = pattern.matcher(res);
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	// ModelData StatusWfCount 合计值
	public static int keyValueSum(String res, String keyword) {
		Map<String, JsonElement> modeldata = JsonUtils.childToMap(res, "ModelData");
		int sum = 0;
		for (String key : modeldata.keySet()) {
			JsonObject child2 = modeldata.get(key).getAsJsonObject();
			JsonObject child3 = child2.getAsJsonObject(keyword);
			for (Entry<String, JsonElement> entry : child3.entrySet()) {
				sum = sum + entry.getValue().getAsInt();
			}
		}
		return sum;
	}

	// AsynTask 获取taskStatus-code
	public static String getTaskStatusCode(String res, String keword) {
		Map<String, JsonElement> asyntask = JsonUtils.childToMap(res, "AsynTask");
		JsonObject child2 = asyntask.get("taskStatus").getAsJsonObject();
		String code = child2.getAsJsonPrimitive(keword).getAsString();
		return code;
	}

}
