package org.gradle.needle.dbo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.gradle.needle.util.DBUtils;

public class DataEngine {

	int protocolid;
	String cmdname;
	String datapath = System.getProperty("user.dir")
			+ "/src/main/resources/data.mdb";
	DBUtils dataDb = new DBUtils("access", datapath, "admin", "greenflower");
	String configpath = System.getProperty("user.dir")
			+ "/src/main/resources/config.mdb";
	DBUtils configDb = new DBUtils("access", configpath, "", "");
	
	String sql1 = "SELECT * FROM propaths Where protocolid = " + protocolid
			+ "AND compath = " + getCompath(cmdname)
			+ "ORDER BY pathid ASC";

	String sql2 = "SELECT * FROM DATA WHERE compath = "
			+ getCompath(cmdname);
	
	// ���췽��1,��ʼ��GWSOCKET����
	public DataEngine(String cmdname){
		this.cmdname = cmdname;
	}

	// ���췽��2,��ʼ��Э��ţ�GWSOCKET����
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}
	
	// ��ȡconfig��propaths�����ݼ�
	public ResultSet getConfigSet() {
		return configDb.Query(sql1);
	}
	
	// ��ȡdata��data�����ݼ�
	public ResultSet getDataSet() {
		return dataDb.Query(sql2);
	}
	

	/*
	 * ����Э��ź�GWSOCKET�����ȡcachevalue
	 */
	public String getCacheValue() {
		String sReturn = null;
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			ResultSet rSet1 = getConfigSet();
			ResultSet rSet2 = getDataSet();
			while (rSet2.next()) {
				varpathMap.put(rSet2.getString("varpath"), rSet2.getString("cachevalue"));
			}
			
			while (rSet1.next()) {
				if (varpathMap.containsKey(rSet1.getString("datapath"))) {
					sReturn = sReturn + varpathMap.get(rSet1.getString("datapath")) + ";";
				}else {
					System.err.println();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;

	}

	/*
	 * ����ǰ�õ�GWSOCKET�����ȡ��Ӧ��compath
	 */
	protected String getCompath(String cmdname) {
		String compath = null;
		String sql = "select * from tocmd";

		Map<String, String> cmdMap = new HashMap<String, String>();

		try {
			ResultSet rs = dataDb.Query(sql);
			while (rs.next()) {
				cmdMap.put(rs.getString("cmdname"), rs.getString("compath'"));
				if (cmdMap.containsKey(cmdname)) {
					compath = cmdMap.get(cmdname);
				} else {
					System.err.println("�޷���Ӧ��ָ� " + cmdname);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return compath;

	}
}
