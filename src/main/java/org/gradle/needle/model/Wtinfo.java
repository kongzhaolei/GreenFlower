package org.gradle.needle.model;

public class Wtinfo {

	private String wpid;
	private String wtcode;
	private String wtname;
	private float longitude;
	private float latitude;

	
	public String getWpid() {
		return wpid;
	}
	
	public void setWpid(String wpid) {
		this.wpid = wpid;
	}

	public String getWtcode() {
		return wtcode;
	}
	
	public String getWtname() {
		return wtname;
	}
	
	public float getWtlongitude() {
		return longitude;
	}
	
	public float getWtlatitude() {
		return latitude;
	}
}
