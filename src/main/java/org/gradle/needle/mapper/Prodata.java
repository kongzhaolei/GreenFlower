package org.gradle.needle.mapper;

/**
 * 
 * @author kongzhaolei
 * 
 * prodata����Ӧ��ʵ����
 */
public class Prodata {

	// �������ó�Ա��������prodata���ֶ�������һ��
	private Integer protocolid;
	private Integer ascflg;
	private Integer pathid;
	private String iecpath;
	private String compath;
	private String datapath;
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
	
	public void setcompath(String compath) {
		this.compath = compath;
	}

	public String getDatapath() {
		return datapath;
	}

	public String getCol1() {
		return col_1;
	}

	public String getCol2() {
		return col_2;
	}

	@Override
	public String toString() {
		return "Prodata [protocolid = " + protocolid + ", ascflg = " + ascflg
				+ ", pathid = " + pathid + ", iecpath = " + iecpath
				+ ", compath = " + compath + ", datapath = " + datapath
				+ ", col_1 = " + col_1 + ", col_2 = " + col_2 + "]";

	}

}