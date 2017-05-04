package org.gradle.needle.util;

import org.gradle.needle.model.ModelData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * json序列化与反序列化
 * @author kongzhaolei
 *
 */
public class JsonUtils {
	
	public JsonUtils() {
		
	}
	
	/*
	 * 嵌套json反序列化
	 */
	public void FromJson(String res) {
		Gson gson = new Gson();
		ModelData md = gson.fromJson(res,ModelData.class);
		
	}

}
