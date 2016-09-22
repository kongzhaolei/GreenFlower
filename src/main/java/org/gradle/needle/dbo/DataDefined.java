package org.gradle.needle.dbo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
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
			// configDb.ConnClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configSet;
	}

	/*
	 * 获取data库prodata表典型维数据集
	 */
	protected ResultSet getDataSetOnCmdname() {
		String sql2 = "SELECT * FROM prodata Where protocolid = " + protocolid
				+ " AND compath = " + "'" + getCompathOnCmdname() + "'"
				+ " ORDER BY pathid ASC";

		ResultSet dataSet = null;

		try {
			dataSet = dataDb.Query(sql2);
			// dataDb.ConnClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSet;
	}

	/*
	 * 根据前置的GWSOCKET命令获取对应的compath
	 */
	protected String getCompathOnCmdname() {
		String compath = GlobalSettings.getProperty(cmdname);
		String compathString = null;
		if (null != compath) {
			compathString = compath;
		} else {
			logger.info("无法响应该指令： " + cmdname);
		}
		return compathString;
	}

	/*
	 * 
	 * 根据col_1生成CacheValue 1 FIXED 固定值，col_2 2 FIXBOOL 随机布尔 ranBoolean() 3
	 * DYNAMIC 动态计算 4 FAULTMAIN 主故障 5 STATUS 风机状态 6 YEAR 年 7 MONTH 月 8 DAY 日 9
	 * HOUR 时10 MINUTE 分11 SECOND 秒12 RANDOM 随机数 ranDouble()13 TOTAL 遥脉量14
	 * STOPMODE 停机模式字/状态模式字15 LIMITMODE 限功率模式字
	 */
	public String getDynamicValue(ResultSet dataSet) throws SQLException {
		String rString = "null";
		String dttype = dataSet.getString("col_1");

		switch (dttype.trim()) {
		case "FIXED":
			rString = dataSet.getString("col_2");
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
			rString = ranDouble(dataSet.getString("col_2").split(",")[0],
					dataSet.getString("col_2").split(",")[1]);
			break;

		case "FIXBOOL":
			rString = Boolean.toString(ranBoolean());
			// rString = Integer.toString(ranCoin());
			break;

		// 暂时赋值col_2
		case "DYNAMIC":
			rString = Integer.toString(ranCoin());
			break;

		case "FAULTMAIN":
			rString = mainFaultRefresh();
			break;

		case "STATUS":
			rString = getStatus();
			break;

		case "TOTAL":
			// rString = TotalRefresh();
			rString = dataSet.getString("col_2");
			break;

		case "STOPMODE":
			rString = stopModeRefresh();
			break;

		case "LIMITMODE":
			rString = limitModeRefresh();
			break;

		default:
			rString = dataSet.getString("col_2");
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

		return "0";
	}

	/*
	 * 风机状态的动态刷新
	 */
	private String getStatus() {

		return "5";
	}

	/*
	 * 停机模式字的动态刷新
	 */
	public String stopModeRefresh() {

		return "4";

	}

	/*
	 * 限功率模式字的动态刷新
	 */
	public String limitModeRefresh() {

		return "0";

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
	public static String ranDouble(String min, String max) {
		Random random = new Random();
		int bd = random.nextInt(Integer.parseInt(max) - Integer.parseInt(min))
				+ Integer.parseInt(min);
		
//		double bt = Integer.parseInt(min)
//				+ ((Integer.parseInt(max) - Integer.parseInt(min)) * new Random()
//						.nextDouble());
		
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

	/*
	 * 生成随机0或1
	 */
	public static int ranCoin() {
		Random rand = new Random();
		return rand.nextInt(2);
	}
}
