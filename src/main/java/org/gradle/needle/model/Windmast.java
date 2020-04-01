package org.gradle.needle.model;

public class Windmast {

	private String wpid;
	private String id;
	private String windMast_code;
	private float longitude;
	private float latitude;

	
	public String getWpid() {
		return wpid;
	}
	
	public void setWpid(String wpid) {
		this.wpid = wpid;
	}

	public String getWindmastid() {
		return id;
	}
	
	public String getWindcode() {
		return windMast_code;
	}
	
	public float getWindlongitude() {
		return longitude;
	}
	
	public float getWindlatitude() {
		return latitude;
	}
}
