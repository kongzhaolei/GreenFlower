package org.gradle.needle.model;

public class Pathdescr {
	private Integer protocolid;
	private String iecvalue;
	private String iecpath;
	private String explaincn;	

	
	public Integer getProtocolid() {
		return protocolid;
	}
	
	public void setProtocolid(int protocolid) {
		this.protocolid = protocolid;
	}
	
	public String getIecpath() {
		return iecpath;
	}
	
	public void setIecpath(String iecpath) {
		this.iecpath = iecpath;
	}
	
	public String getIecvalue() {
		return iecvalue;
	}
	
	public String getexplaincn() {
		return explaincn;
		
	}

}
