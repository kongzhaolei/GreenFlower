package org.gradle.needle.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.gradle.needle.util.DBUtils;

/**
 * 
 * @author kongzhaolei
 *
 */
public class DataDefined {
	int protocolid;
	String cmdname;
	// String datapath = System.getProperty("user.dir")+
	// "/src/main/resources/data.mdb";
	// String configpath = System.getProperty("user.dir")+
	// "/src/main/resources/config.mdb";

	String datapath = "d:/GreenFlower/Data.mdb";
	String configpath = "d:/GreenFlower/config.mdb";
	private static Logger logger = Logger
			.getLogger(DataDefined.class.getName());

	/*
	 * 构造方法,初始化protocolid,cmdname
	 */
	public DataDefined(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;

	}

	/*
	 * 构造方法,初始化protocolid
	 */
	public DataDefined(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * 空构造方法
	 */
	public DataDefined() {

	}

	/*
	 * 获取故障iecvalue列表
	 */
	public List<String> getMainFaultList() {
		String fault_sql = "SELECT * FROM pathdescr WHERE protocolid = "
				+ protocolid
				+ " AND iecpath = 'WTUR.Flt.Rs.S'";
		return getkeyList(fault_sql, "iecvalue");
	}
	
	/*
	 * 获取风机状态iecvalue列表
	 */
	public List<String> getStatusList() {
		String fault_sql = "SELECT * FROM pathdescr WHERE protocolid = "
				+ protocolid
				+ " AND iecpath = 'WTUR.TurSt.Rs.S'";
		return getkeyList(fault_sql, "iecvalue");
	}

	/*
	 * 获取停机模式字iecvalue列表
	 */
	public List<String> getStopModeWordList() {
		String stop_sql = "SELECT * FROM pathdescr WHERE protocolid = "
				+ protocolid
				+ " AND iecpath = 'WTUR.Other.Wn.I16.StopModeWord'";
		return getkeyList(stop_sql, "iecvalue");
	}

	/*
	 * 获取限功率模式字iecvalue列表
	 */
	public List<String> getLimitModeWordList() {
		String limit_sql = "SELECT * FROM pathdescr WHERE protocolid = "
				+ protocolid + " AND iecpath = 'WTUR.Other.Ri.I16.LitPowByPLC'";
		return getkeyList(limit_sql, "iecvalue");
	}

	/*
	 * 抽取一个限功率模式字，停机模式字，风机状态，风机故障公共方法 按列表存储
	 */
	public List<String> getkeyList(String sql, String column) {
		List<String> lists = new ArrayList<String>();
		DBUtils configDb = new DBUtils("access", configpath);
		try {
			lists = configDb.Query(sql, column);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/*
	 * 获取config库propaths表典型维数据集
	 */
	public ResultSet getConfigSetOnCmdname() {
		String sql1 = "SELECT * FROM propaths Where protocolid = " + protocolid
				+ " AND compath = " + "'" + getCompathOnCmdname() + "'"
				+ " ORDER BY pathid ASC";

		DBUtils configDb = new DBUtils("access", configpath);
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

		DBUtils dataDb = new DBUtils("access", datapath);
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
			break;

		// 暂时赋值 ranCoin()
		case "DYNAMIC":
			rString = Integer.toString(ranCoin());
			break;

		case "FAULTMAIN":
			rString = new DataEngine(protocolid).getMainFault();
			break;

		case "STATUS":
			rString = new DataEngine(protocolid).getStatus();
			break;

		case "TOTAL":

			rString = dataSet.getString("col_2");
			break;

		case "STOPMODE":
			rString = new DataEngine(protocolid).getStopModeWord();
			break;

		case "LIMITMODE":
			rString = new DataEngine(protocolid).getLimitModeWord();
			break;

		default:
			rString = dataSet.getString("col_2");
			break;
		}

		return rString;
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

		// Random random = new Random();
		// int bd = random.nextInt(Integer.parseInt(max) -
		// Integer.parseInt(min))
		// + Integer.parseInt(min);

		double bt = Integer.parseInt(min)
				+ ((Integer.parseInt(max) - Integer.parseInt(min)) * new Random()
						.nextDouble());

		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(bt).toString();
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
