package org.gradle.needle.model;

/**
 * 
 * @author kongzhaolei
 * 
 * prodata���Ӧ��ʵ����
 */
public class Prodata {

	// �������ó�Ա��������prodata���ֶ�������һ��
	private Integer protocolid;
	private Integer ascflg;
	private Integer pathid;
	private String iecpath;
	private String compath;
	private String datapath;
	private String descrcn;
	private String col_1;
	private String col_2;
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
}
