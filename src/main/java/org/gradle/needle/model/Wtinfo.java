package org.gradle.needle.model;

public class Wtinfo {

	private Integer protocolid;
	private Integer wfid;
	private String wtid;
	private String wtname;
	private float wtlongitude;
	private float wtlatitude;

	public Integer getProtocolid() {
		return protocolid;
	}

	public void setProtocolid(int protocolid) {
		this.protocolid = protocolid;
	}
	
	public Integer getWfid() {
		return wfid;
	}
	
	public void setWfid(int wfid) {
		this.wfid = wfid;
	}

	public String getWtid() {
		return wtid;
	}
	
	public String getWtname() {
		return wtname;
	}
	
	public float getWtlongitude() {
		return wtlongitude;
	}
	
	public float getWtlatitude() {
		return wtlatitude;
	}
}
