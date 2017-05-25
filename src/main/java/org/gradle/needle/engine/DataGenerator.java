package org.gradle.needle.engine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.model.Pathdescr;
import org.gradle.needle.model.Prodata;
import org.gradle.needle.model.Propaths;
import org.gradle.needle.util.VTimer;

/***
 * 
 * @author kongzhaolei 数据模拟引擎类 1. 支持模拟瞬态数据，故障数据，警告数据，通信状态，风机状态数据,告警日志 2.
 *         支持模拟历史瞬态数据，分钟数据(10,5,1)，功率曲线数据，历史沉积数据，变位数据
 */
public class DataGenerator {

	private int protocolid;
	private String cmdname;
	private DataDefined df;
	private static Logger logger = Logger.getLogger(DataGenerator.class.getName());
	Date date = new Date();

	/*
	 * 构造方法 1 PLC，初始化protocolid,cmdname
	 */
	public DataGenerator(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		df = new DataDefined(protocolid, cmdname);
	}

	/*
	 * 构造方法 2 normal，初始化protocolid
	 */
	public DataGenerator(int protocolid) {
		this.protocolid = protocolid;
		df = new DataDefined(protocolid);
	}

	/**
	 * 统一数据引擎
	 */
	public String gevDevDataEngine(String type) {
		String sReturn = null;
		Map<String, String> alldatamap = new HashMap<String, String>();
		try {
			for (Prodata prodata : df.getAllProData()) {
				alldatamap.put(prodata.getIecpath().trim(), df.getDynamicValue(prodata));
			}
			if (!df.getAllPropaths().isEmpty()) {
				for (Propaths propaths : df.getTypicalPropaths(type)) {
					if (alldatamap.containsKey(propaths.getIecpath())) {
						sReturn += alldatamap.get(propaths.getIecpath()) + ",";
					} else {
						continue;
					}
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4, sReturn.length() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}

	/*
	 * 沉积数据 sediment,一分钟
	 */
	public String genDevSedimentOne() {
		date.setTime(date.getTime() - 60 * 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevOne() + ")";
	}

	// 沉积数据 sediment 一分钟理论
	public String genDevSedimentOneData() {
		date.setTime(date.getTime() - 60 * 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevOneData()
				+ ")";
	}

	// 沉积数据 sediment 历史瞬态
	public String genDevSedimentRealData() {
		date.setTime(date.getTime() - 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevRealTimeData()
				+ ")";
	}

	/*
	 * 历史瞬态数据 realtimedata
	 */
	public String genDevRealTimeData() {
		return "(realtimedata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("realtimedata") + ")";
	}

	/*
	 * 十分钟数据 tendata
	 */
	public String genDevTenData() {
		return "(tendata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("tendata") + ")";
	}

	/*
	 * 一分钟数据 one
	 */
	public String genDevOne() {
		return "(one|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("one") + ")";
	}

	// 一分钟理论数据 onedata
	public String genDevOneData() {
		StringBuilder onedata = new StringBuilder();
		Integer wtid = df.getWtidList().get(df.ranInteger(0, df.getWtidList().size()));
		String wind_power = this.gevDevDataEngine("onedata");
		String theoretical = df.ranDouble("25", "87");
		String statdata = this.genStateData();
		String otherstat = Integer.toString(df.ranInteger(0, 3));
		String mainfault = this.genMainFault();
		String stopword = this.genStopModeWord();
		String ambient_temp = df.ranDouble("20", "60");
		String first_yield = df.ranDouble("0", "8687");
		String last_yield = df.ranDouble("90001", "686877");
		onedata = onedata.append("(onedata|").append(wtid).append("|").append(wind_power).append(",").append(theoretical)
				.append(",").append(statdata).append(",").append(otherstat).append(",").append(mainfault).append(",")
				.append(stopword).append(",").append(ambient_temp).append(",").append(first_yield).append(",")
				.append(last_yield).append(")");
		return onedata.toString();
	}

	/*
	 * 变位数据 changesave
	 */
	public String genDevChangeSave() {
		return "(changesave|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("changesave") + ")";
	}

	/*
	 * 功率曲线 powercurve
	 */
	public String genDevPowerCurve() {
		return "(powercurve|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("powercurve") + ")";
	}

	/*
	 * 主轮询数据 wman
	 */
	public String genDevWmanData() {
		return "(wman|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("wman") + ")";
	}

	/*
	 * 告警日志
	 */
	public StringBuilder genDevWarnLog() {
		StringBuilder warnlog = new StringBuilder();
		int systemid = 3; // 暂时只模拟功率控制
		String levelid = Integer.toString(df.ranInteger(0, 3)); // 0 提示、1 警告、2//
																// 故障
		String rectime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date());
		int wfid = df.getWfid();
		int objectid = df.getWtidList().get(df.ranInteger(0, df.getWtidList().size()));
		String logcode = df.getLogCodeList(systemid).get(df.ranInteger(0, df.getRunLogCode(systemid).size()));
		String warnid = df.ranString(16) + "-0" + systemid + "003645";
		int flag = df.ranCoin();
		warnlog = warnlog.append("(warnlog|").append(systemid).append("|").append(levelid).append("|").append(rectime)
				.append("|").append(wfid).append("|").append(objectid).append("|").append(logcode).append("|")
				.append(warnid).append("|").append(flag).append(")");
		return warnlog;
	}

	/*
	 * 系统告警结束
	 */
	public String genDevWarnEnd() {
		return "";
	}

	/*
	 * 风机元数据
	 * 
	 * @return
	 */
	public String genCmdData() {
		String sReturn = null;
		int n = 0;
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = genMainFault();
			} else {
				for (Prodata pda : df.getCmdProData()) {
					varpathMap.put(pda.getIecpath().trim(), df.getDynamicValue(pda));
				}

				if (!df.getCmdPropaths().isEmpty()) {
					for (Propaths pps : df.getCmdPropaths()) {
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

	/*
	 * 定时器刷新停机模式字
	 */
	public String genStopModeWord() {
		String stopmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStopNum(); // 停机模式字时钟计数器
		try {
			for (Pathdescr pathdescr : df.getStopModeWordList()) {
				lists.add(pathdescr.getIecvalue());
			}
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

	/*
	 * 定时器刷新限功率模式字
	 * 
	 */
	public String genLimitModeWord() {
		String limitmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getLimitNum(); // 限功率模式字时钟计数器
		try {
			for (Pathdescr pathdescr : df.getLimitModeWordList()) {
				lists.add(pathdescr.getIecvalue());
			}
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

	/*
	 * 定时器刷新风机状态 支持故障逻辑
	 */
	public String genStateData() {
		String status = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStatusNum(); // 风机状态时钟计数器
		try {
			if (genFaultTree() != "0") { // 故障，风机停机
				status = "2";
			} else {
				for (Pathdescr pathdescr : df.getStatusList()) {
					lists.add(pathdescr.getIecvalue());
				}
				if (!(n > lists.size())) {
					status = lists.get(n);
				} else {
					status = "5";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/*
	 * 风机状态
	 */
	public String genDevStateData() {
		return "(statedata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genStateData()
				+ ")";
	}

	/*
	 * 前置和设备通信状态，暂时设置为通信正常
	 */
	public String genDevComState() {
		return "(comstate|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + "0" + ")";
	}

	/*
	 * 风机主故障
	 */
	public String genMainFault() {
		return genFaultTree().split(";")[0];
	}

	/*
	 * 故障数据
	 */
	public String genDevFaultData() {
		return "(falutdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genFaultTree()
				+ ")";
	}

	/*
	 * 风机故障树，初始无故障 定时器频率模拟5个故障，之后恢复 faultsign true 无故障；false：故障
	 */
	public String genFaultTree() {
		String faulttree = "";
		List<String> lists = new ArrayList<String>();
		boolean faultsign = df.ranBoolean();
		try {
			if (faultsign == false) {
				faulttree = "0";
			} else {
				for (Pathdescr pathdescr : df.getFaultList()) {
					lists.add(pathdescr.getIecvalue());
				}
				for (int i = 0; i < 5; i++) {
					faulttree += lists.remove((int) (Math.random() * lists.size())) + ";";
				}
				faulttree = faulttree.substring(0, faulttree.length() - 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return faulttree;
	}

	/*
	 * 风机警告
	 */
	public String genDevAlarmData() {
		return "(alarmdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genAlarmTree()
				+ ")";
	}

	/*
	 * 风机警告树，初始化无警告 定时器频率模拟3个警告，之后恢复 alarmsign
	 */
	public String genAlarmTree() {
		String alarmtree = "";
		List<String> lists = new ArrayList<String>();
		boolean alarmsign = df.ranBoolean();

		try {
			if (alarmsign == false) {
				alarmtree = "0";
			} else {
				for (Pathdescr pathdescr : df.getAlarmList()) {
					lists.add(pathdescr.getIecvalue());
				}
				for (int i = 0; i < 3; i++) {
					alarmtree += lists.remove((int) (Math.random() * lists.size())) + ";";
				}
				alarmtree = alarmtree.substring(0, alarmtree.length() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alarmtree;
	}
}
