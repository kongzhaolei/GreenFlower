package org.gradle.needle.mapper;

public class Pathdescr {
	private Integer protocolid;
	private String iecvalue;
	private String iecpath;

	
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

}
