package org.gradle.needle.util;

import org.gradle.needle.model.ModelData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * json���л��뷴���л�
 * @author kongzhaolei
 *
 */
public class JsonUtils {
	
	public JsonUtils() {
		
	}
	
	/*
	 * Ƕ��json�����л�
	 */
	public void FromJson(String res) {
		Gson gson = new Gson();
		ModelData md = gson.fromJson(res,ModelData.class);
		
	}

}
