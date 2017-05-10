package org.gradle.needle.model;

import com.google.gson.JsonObject;

public class JsonData {
	
	private String time;
	private JsonObject data;
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public JsonObject getData() {
		return data;
	}
	
	public void setData(JsonObject data) {
		this.data = data;
	}

}
