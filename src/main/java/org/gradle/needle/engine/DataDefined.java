package org.gradle.needle.engine;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.gradle.needle.dto.GlobalSettings;
import org.gradle.needle.model.Pathdescr;
import org.gradle.needle.model.Prodata;
import org.gradle.needle.model.Propaths;
import org.gradle.needle.model.Runlogcode;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.util.DBFactory;
import org.gradle.needle.util.DBFactory.DBEnvironment;

/***
 * 
 * @author kongzhaolei
 * 
 * 
 */
public class DataDefined {
	int protocolid;
	String cmdname;
	private static Logger logger = Logger.getLogger(DataDefined.class.getName());

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

	/**
	 * 获取故障
	 */
	public List<Pathdescr> getFaultList() {
		return getPathdescr("WTUR.Flt.Rs.S");
	}

	/**
	 * 获取警告
	 */
	public List<Pathdescr> getAlarmList() {
		return getPathdescr("WTUR.Alam.Rs.S");
	}

	/**
	 * 获取风机状态
	 */
	public List<Pathdescr> getStatusList() {
		return getPathdescr("WTUR.TurSt.Rs.S");
	}

	/**
	 * 获取停机模式字
	 */
	public List<Pathdescr> getStopModeWordList() {
		return getPathdescr("WTUR.Other.Wn.I16.StopModeWord");
	}

	/**
	 * 获取限功率模式字
	 */
	public List<Pathdescr> getLimitModeWordList() {
		return getPathdescr("WTUR.Other.Rs.S.LitPowByPLC");
	}

	/**
	 * 基于mybatis框架 不需要实现SuperMapper接口，mybatis自动生成mapper代理对象
	 * 获取config库pathdescr表典型维数据集(protocolid, iecpath)
	 */
	public List<Pathdescr> getPathdescr(String iecpath) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Pathdescr pathdescr = new Pathdescr();
		pathdescr.setProtocolid(protocolid);
		pathdescr.setIecpath(iecpath);
		List<Pathdescr> list = mapper.selectPathdescr(pathdescr);
		return list;
	}

	/**
	 * 获取propaths的cmd数据集(protocolid, cmdname)
	 */
	public List<Propaths> getCmdPropaths() {
		List<Propaths> pack_list = new ArrayList<>();
		try {
			for (Propaths pps : getAllPropaths()) {
				if (pps.getCompath() != null) {
					if (getCompathOnCmdname().equals(pps.getCompath().trim())) {
						pack_list.add(pps);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" 请检查compath的值是否为null ");
		}
		return pack_list;
	}

	/**
	 * 获取propaths的特定数据集
	 */
	public List<Propaths> getTypicalPropaths(String type) {
		List<Propaths> pps_list = new ArrayList<>();
		try {
			switch (type) {
			case "wman":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "tendata":
			case "fivedata":
			case "one":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() == 2) {
						pps_list.add(pps);
					}
				}
				break;
			case "onedata":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getBsend().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "changesave":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() < 2 & pps.getChangesave().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "realtimedata":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() == 1 & pps.getBsave().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "powercurve":
				String[] iec = { "WTUR.WSpd.Ra.F32[AVG]", "WTUR.PwrAt.Ra.F32[AVG]", "WTUR.Temp.Ra.F32[AVG]" };
				for (Propaths pps : getAllPropaths()) {
					if (Arrays.asList(iec).contains(pps.getIecpath().trim())) {
						pps_list.add(pps);
					}
				}
				break;			
			default:
				getAllPropaths();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pps_list;
	}

	/**
	 * 基于mybatis框架 不需要实现SuperMapper接口，mybatis自动生成mapper代理对象
	 * 获取config库propaths表典型维数据集(protocolid)
	 */
	public List<Propaths> getAllPropaths() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Propaths propaths = new Propaths();
		propaths.setProtocolid(protocolid);
		List<Propaths> list = mapper.selectPropaths(propaths);
		return list;
	}

	/**
	 * 获取prodata表典型维数据集(protocolid, cmdname)
	 */
	public List<Prodata> getCmdProData() {
		List<Prodata> pack_list = new ArrayList<>();
		try {
			for (Prodata pda : getAllProData()) {
				if (pda.getCompath() != null) { // compath有null情况，先判断
					if (getCompathOnCmdname().equals(pda.getCompath().trim())) {
						pack_list.add(pda);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pack_list;
	}

	/**
	 * 基于mybatis框架 不需要实现SuperMapper接口，mybatis自动生成mapper代理对象
	 * 获取data库prodata表典型维数据集(protocolid)
	 */
	public List<Prodata> getAllProData() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.datadb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Prodata prodata = new Prodata();
		prodata.setProtocolid(protocolid);
		List<Prodata> list = mapper.selectProdata(prodata);
		return list;
	}

	/**
	 * 获取单风场编号
	 */
	public Integer getWfid() {
		return getWtinfo().get(0).getWfid();
	}

	/**
	 * 获取风机编号list<wtid>
	 */
	public List<Integer> getWtidList() {
		List<Integer> lists = new ArrayList<Integer>();
		try {
			for (Wtinfo wtinfo : getWtinfo()) {
				lists.add(wtinfo.getWtid());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * 基于mybatis框架 不需要实现SuperMapper接口，mybatis自动生成mapper代理对象
	 * 获取local库wtinfo表典型维数据集(protocolid)
	 */
	public List<Wtinfo> getWtinfo() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Wtinfo wtinfo = new Wtinfo();
		wtinfo.setProtocolid(protocolid);
		List<Wtinfo> list = mapper.selectWtinfo(wtinfo);
		return list;
	}

	/**
	 * 获取code list<code>
	 * 
	 * @systemid
	 */
	public List<String> getLogCodeList(int systemid) {
		List<String> lists = new ArrayList<String>();
		try {
			for (Runlogcode runlogcode : getRunLogCode(systemid)) {
				lists.add(runlogcode.getCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * 基于mybatis框架 不需要实现SuperMapper接口，mybatis自动生成mapper代理对象
	 * 获取config库runlogcode表典型维数据集
	 */
	public List<Runlogcode> getRunLogCode(int systemid) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Runlogcode runlogcode = new Runlogcode();
		runlogcode.setSystemid(systemid);
		List<Runlogcode> list = mapper.selectRunlogcode(runlogcode);
		return list;
	}

	/**
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

	/**
	 * 
	 * 根据col_1类型生成DynamicValue 1 FIXED 固定值，col_2 2 FIXBOOL 随机布尔 ranBoolean() 3
	 * DYNAMIC 动态计算 4 FAULTMAIN 主故障 5 STATUS 风机状态 6 YEAR 年 7 MONTH 月 8 DAY 日 9
	 * HOUR 时 10 MINUTE 分 11 SECOND 秒 12 RANDOM 随机数 ranDouble() 13 TOTAL 遥脉量 14
	 * STOPMODE 停机模式字/状态模式字 15 LIMITMODE 限功率模式字 16 ALARM 警告 17 FAULT 故障树
	 */
	public String getDynamicValue(Prodata pda) throws SQLException {
		String rString = "";
		String dttype = "";
		try {
			dttype = pda.getCol1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (dttype.trim()) {
		case "FIXED":
			rString = pda.getCol2();
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

		case "CURRENTTIME":
			rString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date());
			break;

		case "RANDOM":
			rString = ranDouble(pda.getCol2().split(",")[0], pda.getCol2().split(",")[1]);
			break;

		case "FIXBOOL":
			rString = Boolean.toString(ranBoolean());
			break;

		// 暂时赋值 ranCoin()
		case "DYNAMIC":
			rString = Integer.toString(ranCoin());
			break;

		case "FAULTMAIN":
			rString = new DataGenerator(protocolid).genMainFault();
			break;

		case "STATUS":
			rString = new DataGenerator(protocolid).genStateData();
			break;

		case "TOTAL":
			rString = pda.getCol2();
			break;

		case "STOPMODE":
			rString = new DataGenerator(protocolid).genStopModeWord();
			break;

		case "LIMITMODE":
			rString = new DataGenerator(protocolid).genLimitModeWord();
			break;

		case "ALARM":
			rString = new DataGenerator(protocolid).genAlarmTree();

		case "FAULT":
			rString = new DataGenerator(protocolid).genFaultTree();

		case "NULL":
			rString = pda.getCol2();

		default:
			rString = pda.getCol2();
			break;
		}
		return rString;
	}

	/**
	 * 生成随机字符串的方法
	 */
	public String ranString(int length) {
		String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 生成随机整型，位于max和min之间的方法 返回(min,max)集合中的整数，不包括max
	 */
	public Integer ranInteger(int min, int max) {
		Random random = new Random();
		int bd = random.nextInt(max - min) + min;
		return bd;
	}

	/**
	 * 生成随机双精度，位于max和min之间的方法
	 */
	public  String ranDouble(String min, String max) {
		double bt = Integer.parseInt(min)
				+ ((Integer.parseInt(max) - Integer.parseInt(min)) * new Random().nextDouble());
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(bt).toString();
	}

	/**
	 * 生成一个随机的布尔值的方法
	 */
	public boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

	/**
	 * 生成随机0或1
	 */
	public int ranCoin() {
		Random rand = new Random();
		return rand.nextInt(2);
	}

}
