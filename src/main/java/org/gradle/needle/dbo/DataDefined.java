package org.gradle.needle.dbo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.gradle.needle.util.DBUtils;

public class DataDefined {
	int protocolid;
	String cmdname;
	// String datapath = System.getProperty("user.dir")+
	// "/src/main/resources/data.mdb";
	// String configpath = System.getProperty("user.dir")+
	// "/src/main/resources/config.mdb";

	String datapath = "d:/Data.mdb";
	String configpath = "d:/config.mdb";
	DBUtils dataDb = new DBUtils("access", datapath);
	DBUtils configDb = new DBUtils("access", configpath);
	private static Logger logger = Logger
			.getLogger(DataDefined.class.getName());

	/*
	 * 构造方法,初始化协议号
	 */
	public DataDefined(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;

	}

	/*
	 * 获取config库propaths表典型维数据集
	 */
	public ResultSet getConfigSetOnCmdname() {
		String sql1 = "SELECT * FROM propaths Where protocolid = " + protocolid
				+ " AND compath = " + "'" + getCompathOnCmdname() + "'"
				+ " ORDER BY pathid ASC";

		ResultSet configSet = null;

		try {
			configSet = configDb.Query(sql1);
	//		configDb.ConnClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configSet;
	}

	/*
	 * 获取data库data表典型维数据集
	 */
	protected ResultSet getDataSetOnCmdname() {
		String sql2 = "SELECT * FROM DATA WHERE compath = " + "'"
				+ getCompathOnCmdname() + "'";

		ResultSet dataSet = null;

		try {
			dataSet = dataDb.Query(sql2);
	//		dataDb.ConnClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSet;
	}

	/*
	 * 根据前置的GWSOCKET命令获取对应的compath
	 */
	protected String getCompathOnCmdname() {
		String compath = null;
		String sql = "select * from tocmd";

		Map<String, String> cmdMap = new HashMap<String, String>();

		try {
			ResultSet rs = dataDb.Query(sql);
			while (rs.next()) {
				cmdMap.put(rs.getString("cmdname").trim(),
						rs.getString("compath'").trim());
			}
			if (cmdMap.containsKey(cmdname)) {
				compath = cmdMap.get(cmdname);
			} else {
				logger.info("无法响应该指令： " + cmdname);
			}
	//		dataDb.ConnClose();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return compath;

	}

	/*
	 * 根据dttype生成CacheValue 
	 * 1 FIXED 固定值，initvalue
	 * 2 FIXBOOL 随机布尔   ranBoolean()
	 * 3 DYNAMIC  动态计算
	 * 4 FAULTMAIN  主故障
	 * 5 STATUS   风机状态
	 * 6 YEAR 年 
	 * 7 MONTH 月 
	 * 8 DAY 日 
	 * 9 HOUR 时
	 *10 MINUTE 分 
	 *11 SECOND 秒 
	 *12 RANDOM 随机数 ranDouble() 
	 *13 TOTAL   遥脉量
	 *14 STOPMODE 停机模式字/状态模式字
	 *15 LIMITMODE 限功率模式字
	 */
	public String getDynamicValue(ResultSet dataSet) throws SQLException {
		String rString = "null";
		String dttype = dataSet.getString("dttype");
		
				switch (dttype.trim()) {
				case "FIXED":
					rString = dataSet.getString("initvalue");
					break;

				case "YEAR":
					rString = Integer.toString(Calendar.YEAR);
					break;

				case "MONTH":
					rString = Integer.toString(Calendar.MONTH);
					break;

				case "DAY":
					rString = Integer.toString(Calendar.DATE);
					break;

				case "HOUR":
					rString = Integer.toString(Calendar.HOUR);
					break;

				case "MINUTE":
					rString = Integer.toString(Calendar.MINUTE);
					break;

				case "SECOND":
					rString = Integer.toString(Calendar.SECOND);
					break;

				case "RANDOM":
					rString = ranDouble(
							dataSet.getString("initvalue").split(",")[0],
							dataSet.getString("initvalue").split(",")[1]);
					break;
					
				case "FIXBOOL":
					rString = Boolean.toString(ranBoolean());
					break;
				
					//暂时赋值initvalue
				case "DYNAMIC":
					rString = dataSet.getString("initvalue");
					break;
					
				case "FAULTMAIN":
					rString = mainFaultRefresh();
					break;
					
				case "STATUS":
					rString = getStatus();
					break;
					
				case "TOTAL":
					rString = TotalRefresh();
					break;
					
				case "STOPMODE":
					rString = stopModeRefresh();
					break;
					
				case "LIMITMODE":
					rString = limitModeRefresh();
					break;
					
				default:
					rString = dataSet.getString("initvalue");
					break;
				}
				
		return rString;
	}
	
	

	/*
	 * 遥脉量计算
	 */
	private String TotalRefresh() {
		// TODO 自动生成的方法存根
		return null;
	}

	/*
	 * 主故障动态刷新
	 */
	private String mainFaultRefresh() {
		
		return null;
	}

	/*
	 * 风机状态的动态刷新
	 */
	private String getStatus() {
		
		return null;
	}

	/*
	 * 停机模式字的动态刷新
	 */
	public String stopModeRefresh() {
		
		return null;

	}

	/*
	 * 限功率模式字的动态刷新
	 */
	public String limitModeRefresh() {
		
		return null;

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
	public static String ranDouble(String max, String min) {
		BigDecimal bd = new BigDecimal(Math.random() * Integer.parseInt(max)
				+ Integer.parseInt(min));
		 DecimalFormat df = new DecimalFormat("#.00");
		return df.format(bd).toString();
	}

	/*
	 * 生成一个随机的布尔值的方法
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}
}
