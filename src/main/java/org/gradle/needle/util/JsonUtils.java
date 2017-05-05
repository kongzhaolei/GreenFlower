package org.gradle.needle.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.gradle.needle.model.ModelData;
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
	public static ModelData FromJson(String json) {
		Gson gson = new Gson();
		ModelData md = gson.fromJson(json, ModelData.class);
		return md;
	}

	/*
	 * 解析JsonObject ModelData{}
	 * 转化为Map<String,JsonObject>对象
	 */
	public static Map<String, JsonObject> phareData(String json) {
		Map<String, JsonObject> wtjson = new HashMap<String, JsonObject>();
		Gson gson2 = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type type = new TypeToken<Map<String, JsonObject>>() {}.getType();
		try {
			JsonElement modeldata = FromJson(json).getData().getAsJsonObject("cli").getAsJsonObject("dps")
					.getAsJsonObject("ModelData");

			wtjson = gson2.fromJson(modeldata, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wtjson;
	}
}
