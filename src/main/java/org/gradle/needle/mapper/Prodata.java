package org.gradle.needle.mapper;

/**
 * 
 * @author kongzhaolei
 * 
 * prodata表对应的实体类
 */
public class Prodata {

	// 这里设置成员变量名与prodata表字段名保持一致
	private Integer protocolid;
	private Integer ascflg;
	private Integer pathid;
	private String iecpath;
	private String compath;
	private String datapath;
	private String descrcn;
	private String col_1;
	private String col_2;

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
	
	public String getDatapath() {
		return datapath;
	}
	
	public String getDescrcn() {
		return descrcn;
	}

	public String getCol1() {
		return col_1;
	}

	public String getCol2() {
		return col_2;
	}
}
