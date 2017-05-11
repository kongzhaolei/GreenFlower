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
 * json���л��뷴���л�
 * 
 * @author kongzhaolei
 *
 */
public class JsonUtils {

	/*
	 * Ƕ��json�����л� ���� java����
	 */
	public static JsonData FromJson(String json) {
		Gson gson = new Gson();
		JsonData md = gson.fromJson(json, JsonData.class);
		return md;
	}

	/*
	 * ����childobjectת��ΪMap<String,JsonObject>����
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

	// json �ؼ��ֳ��ִ�����������ʽ
	public static int KeyFrequency(String res, String keyword) {
		int count = 0;
		Pattern pattern = Pattern.compile(keyword);
		Matcher matcher = pattern.matcher(res);
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	// ModelData StatusWfCount �ϼ�ֵ
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

	// AsynTask ��ȡtaskStatus-code
	public static String getTaskStatusCode(String res, String keword) {
		Map<String, JsonElement> asyntask = JsonUtils.childToMap(res, "AsynTask");
		JsonObject child2 = asyntask.get("taskStatus").getAsJsonObject();
		String code = child2.getAsJsonPrimitive(keword).getAsString();
		return code;
	}

}
