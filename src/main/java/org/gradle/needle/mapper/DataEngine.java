package org.gradle.needle.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.util.VTimer;

/***
 * 
 * @author kongzhaolei
 * 
 */
public class DataEngine {

	int protocolid;
	String cmdname;
	DataDefined df;
	public static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	/**
	 * 构造方法,初始化protocolid,cmdname
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		df = new DataDefined(protocolid, cmdname);
	}

	/**
	 * 构造方法,初始化protocolid
	 */
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
		df = new DataDefined(protocolid);
	}

	/**
	 * 空构造方法
	 */
	public DataEngine() {

	}

	/**
	 * 模拟前置-瞬态数据引擎 指定protocolid 生成 DevMainData
	 */
	public String genDevMainData() {
		String sReturn = null;
		Map<String, String> maindatamap = new HashMap<String, String>();
		try {
			for (Prodata prodata : df.getAllProData()) {
				maindatamap.put(prodata.getIecpath().trim(), df.getDynamicValue(prodata));
			}
			if (!df.getAllPropaths().isEmpty()) {
				for (Propaths propaths : df.getAllPropaths()) {
					if (maindatamap.containsKey(propaths.getIecpath()) & propaths.getTranstype().intValue() == 1) { // initValue()：以int类型返回integer的值
						sReturn += maindatamap.get(propaths.getIecpath()) + ",";
					} else {
						continue;
					}
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4, sReturn.length() - 1);
				sReturn = "(wman|" + df.getWtidList().get(getWtidNum()) + "|" + sReturn + ")";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}

	/*
	 * 风机编号序列号
	 */
	private int getWtidNum() {
		int wtid_list_n = -1;
		final int wtid_size = new DataDefined(protocolid).getWtidList().size();
		wtid_list_n++;
		if (wtid_list_n > wtid_size - 1) {
			wtid_list_n = 0;
		}
		return wtid_list_n;
	}

	/**
	 * 模拟风机服务-包数据引擎 指定protocolid, GWSOCKET命令cmdname 生成 DevPackData
	 */
	public String genDevPackData() {
		String sReturn = null;
		int n = 0;
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("已发送故障号" + sFaultString);
			} else {
				for (Prodata pda : df.getPackProData()) {
					varpathMap.put(pda.getIecpath().trim(), df.getDynamicValue(pda));
				}

				if (!df.getPackPropaths().isEmpty()) {
					for (Propaths pps : df.getPackPropaths()) {
						while (n < pps.getAscflg()) {
							sReturn = sReturn + ";";
							n++;
						}

						if (varpathMap.containsKey(pps.getIecpath().trim())) {
							sReturn += varpathMap.get(pps.getIecpath()) + ";";
							n++;
						} else {
							logger.info("data库不存在此IEC量： " + pps.getIecpath() + " / " + pps.getDescrcn());
						}
					}
				} else {
					logger.info(protocolid + "协议不存在该compath： " + df.getCompathOnCmdname());
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4, sReturn.length() - 1);
				logger.info(df.getCompathOnCmdname() + " 数组已发送" + sReturn.split(";").length + "个IEC量");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}

	/**
	 * 定时器刷新的停机模式字
	 */
	public String getStopModeWordIecValue() {
		String stopmodeword = null;
		List<String> lists = df.getStopModeWordIecValueList();
		int n = VTimer.getStopNum(); // 时钟计数器
		try {
			if (!(n > lists.size())) {
				stopmodeword = lists.get(n);
			} else {
				stopmodeword = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// logger.info("当前停机模式字为： " + stopmodeword);
		return stopmodeword;
	}

	/**
	 * 定时器刷新的限功率模式字
	 * 
	 */
	public String getLimitModeWordIecValue() {
		String limitmodeword = null;
		List<String> lists = df.getLimitModeWordIecValueList();
		int n = VTimer.getLimitNum(); // 时钟计数器
		try {
			if (!(n > lists.size())) {
				limitmodeword = lists.get(n);
			} else {
				limitmodeword = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// logger.info("当前限功率模式字为： " + limitmodeword);
		return limitmodeword;
	}

	/**
	 * 定时刷新的停机模式字对应的中文解析
	 */
	public String getStopModeExplaincn() {
		return df.getStopModeWordMap().get(getStopModeWordIecValue());
	}

	/**
	 * 定时刷新的限功率模式字对应的中文解析
	 */
	public String getLimitModeExplaincn() {
		return df.getLimitModeWordMap().get(getLimitModeWordIecValue());
	}

	/**
	 * 风机主故障
	 */
	public String getMainFault() {

		return "0";
	}

	/**
	 * 风机警告
	 */
	public String getAlarm() {
		return "0";
	}

	/**
	 * 风机状态
	 */
	public String getStatus() {

		return "5";
	}

}
