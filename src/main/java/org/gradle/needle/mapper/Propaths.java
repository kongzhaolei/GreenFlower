package org.gradle.needle.mapper;

public class Propaths {
	private Integer protocolid;
	private Integer ascflg;
	private Integer pathid;
	private String iecpath;
	private String compath;
	private String datapath;
	private String descrcn;
	
	public Integer getProtocolid() {
		return protocolid;
	}
	
	public void setProtocolid(int protocolid) {
		this.protocolid = protocolid;
	}

	public Integer getAscflg() {
		return ascflg;
	}

	public Integer getPathid() {
		return pathid;
	}

	public String getIecpath() {
		return iecpath;
	}

	public String getCompath() {
		return compath;
	}
	
	public void setcompath(String compath) {
		this.compath = compath;
	}

	public String getDatapath() {
		return datapath;
	}
	
	public String getDescrcn() {
		return descrcn;
	}

}
