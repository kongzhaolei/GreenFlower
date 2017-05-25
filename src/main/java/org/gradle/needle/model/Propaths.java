package org.gradle.needle.model;

public class Propaths {
	private Integer protocolid;
	private Integer ascflg;
	private Integer offsets;
	private Integer pathid;
	private String iecpath;
	private String compath;
	private String datapath;
	private String descrcn;
	private Integer transtype;
	private Integer changesave;
	private Integer bsend;
	private Integer bsave;
	
	
	
	
	public Integer getProtocolid() {
		return protocolid;
	}
	
	public void setProtocolid(int protocolid) {
		this.protocolid = protocolid;
	}

	public Integer getAscflg() {
		return ascflg;
	}
	
	public Integer getOffsets() {
		return offsets;
	}

	public Integer getPathid() {
		return pathid;
	}
	
	public Integer getTranstype(){
		return transtype;
	}
	
	public Integer getChangesave() {
		return changesave;
	}
	
	public Integer getBsend() {
		return bsend;
	}
	
	public Integer getBsave() {
		return bsave;
	}

	public String getIecpath() {
		return iecpath;
	}

	public String getCompath() {
		return compath;
	}
	
	public String getDatapath() {
		return datapath;
	}
	
	public String getDescrcn() {
		return descrcn;
	}

}
