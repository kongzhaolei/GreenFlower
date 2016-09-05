package org.gradle.needle.dbo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.gradle.needle.util.DBUtils;

public class DataDefined {
	int protocolid;
	String datapath = System.getProperty("user.dir")
			+ "/src/main/resources/data.mdb";
	DBUtils dataDb = new DBUtils("access", datapath, "admin", "greenflower");
	String configpath = System.getProperty("user.dir")
			+ "/src/main/resources/config.mdb";
	DBUtils configDb = new DBUtils("access", configpath, "", "");

	/*
	 * 构造方法,初始化协议号
	 */
	public DataDefined(int protocolid) {
		this.protocolid = protocolid;

	}

	// 获取config库propaths表典型维数据集
	public ResultSet getConfigSet(String cmdname) {
		String sql1 = "SELECT * FROM propaths Where protocolid = " + protocolid
				+ "AND compath = " + getCompath(cmdname)
				+ "ORDER BY pathid ASC";
		return configDb.Query(sql1);
	}

	// 获取data库data表典型维数据集
	public ResultSet getDataSet(String cmdname) {
		String sql2 = "SELECT * FROM DATA WHERE compath = "
				+ getCompath(cmdname);
		return dataDb.Query(sql2);
	}

	/*
	 * 根据前置的GWSOCKET命令获取对应的compath
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
					System.err.println("无法响应该指令： " + cmdname);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return compath;

	}

	/*
	 * stopmode的随机刷新
	 */
	public void stopModeRefresh() {

	}

	/*
	 * baseData的实时刷新
	 */
	public void cacheValueRefresh() {
		

	}

	/*
	 * 生成随机字符串的方法
	 */
	public static String ranString(int length) {
		String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/*
	 * 生成随机数位于max和min之间的方法
	 */
	public static String ranDouble(int max, int min) {
		BigDecimal db = new BigDecimal(Math.random() * max + min);
		return db.toString();
	}

	/*
	 * 生成一个随机的布尔值的方法
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

}
