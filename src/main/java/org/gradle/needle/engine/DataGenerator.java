package org.gradle.needle.engine;

import java.util.ArrayList;
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
 * @author kongzhaolei
 * 数据模拟引擎类
 * 1. 支持模拟瞬态数据，故障数据，警告数据，通信状态，包数据
 * 2. 支持模拟历史瞬态数据，分钟数据(10,5,1)，风机状态数据，功率曲线数据，历史沉积数据，变位数据
 */
public class DataGenerator {

	private int protocolid;
	private String cmdname;
	private DataDefined df;
	private static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataGenerator.class.getName());

	/*
	 * 构造方法 1 风机数据模拟器，初始化protocolid,cmdname
	 */
	public DataGenerator(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		df = new DataDefined(protocolid, cmdname);
	}

	/*
	 * 构造方法 2 实时数据发生器，初始化protocolid
	 */
	public DataGenerator(int protocolid) {
		this.protocolid = protocolid;
		df = new DataDefined(protocolid);
	}
	
	/**
	 * 组播瞬态数据引擎
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
				sReturn = "(wman|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + sReturn
						+ ")";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}
	
	/**
	 * 风机元数据
	 * @return
	 */
	public String genCmdData() {
		String sReturn = null;
		int n = 0;
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("已发送故障号" + sFaultString);
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
	
	/**
	 * 定时器刷新停机模式字
	 */
	public String genStopModeWord() {
		String stopmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStopNum(); // 时钟计数器
		try {
			for(Pathdescr pathdescr : df.getStopModeWordList()) {
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

	/**
	 * 定时器刷新限功率模式字
	 * 
	 */
	public String genLimitModeWord() {
		String limitmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getLimitNum(); // 时钟计数器
		try {
			for(Pathdescr pathdescr : df.getLimitModeWordList()) {
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
	
	/**
	 * 组播前置和设备通信状态引擎
	 * 暂时设置为通信正常
	 */
	public String genDevComState() {		
		return "(comstate|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + "0"
		+ ")";
	}
	
	/**
	 * 风机主故障
	 */
	public String genMainFault() {
		return genFaultTree().split(";")[0];
	}
	
	/**
	 * 组播故障数据引擎
	 */
	public String genDevFaultData() {
		return "(falutdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genFaultTree()
				+ ")";
	}
	
	/**
	 * 风机故障树,模拟5个故障
	 */
	public String genFaultTree() {
		String faulttree = "";
		List<String> lists = new ArrayList<String>();
		try {
			for(Pathdescr pathdescr : df.getFaultList()){
				lists.add(pathdescr.getIecvalue());
			}
			for(int i = 0; i < 5; i++){
				faulttree += lists.remove((int) (Math.random() * lists.size())) + ";";
			}
			faulttree = faulttree.substring(0, faulttree.length() - 1);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return faulttree;
	}

	/**
	 * 组播风机警告引擎
	 */
	public String genDevAlarmData() {
		return "(alarmdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genAlarmTree()
		+ ")";
	}
	
	/**
	 * 风机警告树，模拟3个警告
	 */
	public String genAlarmTree() {
		String alarmtree = "";
		List<String> lists = new ArrayList<String>();
		try {
			for(Pathdescr pathdescr : df.getAlarmList()){
				lists.add(pathdescr.getIecvalue());
			}
			for(int i = 0; i < 3; i++ ){
				alarmtree += lists.remove((int)(Math.random()* lists.size())) + ";";
			}
			alarmtree = alarmtree.substring(0, alarmtree.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alarmtree;
	}

	/**
	 * 风机状态
	 */
	public String genStatusData() {
		return "5";
	}
}
