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
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.util.VTimer;

/***
 * 
 * @author kongzhaolei 锟斤拷锟斤拷模锟斤拷锟斤拷锟斤拷锟斤拷 1. 支锟斤拷模锟斤拷瞬态锟斤拷锟捷ｏ拷锟斤拷锟斤拷锟斤拷锟捷ｏ拷锟斤拷锟斤拷锟斤拷锟捷ｏ拷通锟斤拷状态锟斤拷锟斤拷锟阶刺拷锟斤拷锟�,锟芥警锟斤拷志 2.
 *         支锟斤拷模锟斤拷锟斤拷史瞬态锟斤拷锟捷ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷(10,5,1)锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟捷ｏ拷锟斤拷史锟斤拷锟斤拷锟斤拷锟捷ｏ拷锟斤拷位锟斤拷锟斤拷
 */
public class DeviceDataGenerator {

	private int protocolid;
	private String cmdname;
	private DataHub dataHub;
	private static Logger logger = Logger.getLogger(DeviceDataGenerator.class.getName());
	Date date = new Date();

	/*
	 * 锟斤拷锟届方锟斤拷 1 PLC锟斤拷锟斤拷始锟斤拷protocolid,cmdname
	 */
	public DeviceDataGenerator(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		dataHub = new DataHub(protocolid, cmdname);
	}

	/*
	 * 锟斤拷锟届方锟斤拷 2 normal锟斤拷锟斤拷始锟斤拷protocolid
	 */
	public DeviceDataGenerator(int protocolid) {
		this.protocolid = protocolid;
		dataHub = new DataHub(protocolid);
	}

	/**
	 * 统一锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public String gevDevDataEngine(String type) {
		String sReturn = null;
		Map<String, String> alldatamap = new HashMap<String, String>();
		try {
			for (Prodata prodata : dataHub.getAllProData()) {
				alldatamap.put(prodata.getIecpath().trim(), dataHub.getDynamicValue(prodata));
			}
			if (!dataHub.getAllPropaths().isEmpty()) {
				for (Propaths propaths : dataHub.getTypicalPropaths(type)) {
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
			logger.info("锟斤拷前锟斤拷锟捷达拷锟斤拷" + sReturn);
		}
		return sReturn;
	}

	/**
	 * 锟斤拷取锟斤拷锟斤拷锟斤拷list<wtid>
	 */
	public List<Integer> getWtidList() {
		List<Integer> lists = new ArrayList<Integer>();
		try {
			for (Wtinfo wtinfo : dataHub.getWtinfo()) {
				lists.add(wtinfo.getWtid());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷 sediment,一锟斤拷锟斤拷
	 */
	public String genDevSedimentOne() {
		date.setTime(date.getTime() - 60 * 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevOne() + ")";
	}

	// 锟斤拷锟斤拷锟斤拷锟斤拷 sediment 一锟斤拷锟斤拷锟斤拷锟斤拷
	public String genDevSedimentOneData() {
		date.setTime(date.getTime() - 60 * 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevOneData()
				+ ")";
	}

	// 锟斤拷锟斤拷锟斤拷锟斤拷 sediment 锟斤拷史瞬态
	public String genDevSedimentRealData() {
		date.setTime(date.getTime() - 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevRealTimeData()
				+ ")";
	}

	/*
	 * 锟斤拷史瞬态锟斤拷锟斤拷 realtimedata
	 */
	public String genDevRealTimeData() {
		return "(realtimedata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("realtimedata") + ")";
	}

	/*
	 * 十锟斤拷锟斤拷锟斤拷锟斤拷 tendata
	 */
	public String genDevTenData(Integer wtid) {
		return "(tendata|" + wtid + "|" + this.gevDevDataEngine("tendata") + ")";
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟� fivedata
	 */
	public String genDevFiveData(Integer wtid) {
		return "(fivedata|" + wtid + "|" + this.gevDevDataEngine("fivedata") + ")";
	}
	
	// new fivedata
	public String genNewDevFiveData(Integer wtid) {
		return "(newfivedata|" + wtid + "|" + "1" + "|" + this.gevDevDataEngine("fivedata") + "|" + "0" + "|" + "0" + "|" + "1" + "|" + protocolid + ")";
	}

	/*
	 * 一锟斤拷锟斤拷锟斤拷锟斤拷 one
	 */
	public String genDevOne() {
		return "(one|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + this.gevDevDataEngine("one")
				+ ")";
	}

	// 一锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷 onedata
	public String genDevOneData() {
		StringBuilder onedata = new StringBuilder();
		Integer wtid = getWtidList().get(dataHub.ranInteger(0, getWtidList().size()));
		String wind_power = this.gevDevDataEngine("onedata");
		String statdata = this.genStateData();
		String otherstat = Integer.toString(dataHub.ranInteger(0, 3));
		String mainfault = this.genMainFault();
		String stopword = this.genStopModeWord();
		String ambient_temp = dataHub.ranFloat(20, 60);
		String first_yield = dataHub.ranFloat(0, 8687);
		String last_yield = dataHub.ranFloat(6984, 113256);
		String theoretical = dataHub.ranFloat(24,46);
		onedata = onedata.append("(onedata|").append(wtid).append("|").append(wind_power).append(",")
				.append(theoretical).append(",").append(statdata).append(",").append(otherstat).append(",")
				.append(mainfault).append(",").append(stopword).append(",").append(ambient_temp).append(",")
				.append(first_yield).append(",").append(last_yield).append(")");
		return onedata.toString();
	}

	/*
	 * 锟斤拷位锟斤拷锟斤拷 changesave
	 */
	public String genDevChangeSave() {
		return "(changesave|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("changesave") + ")";
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷 powercurve
	 */
	public String genDevPowerCurve() {
		return "(powercurve|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("powercurve") + ")";
	}

	/*
	 * 锟斤拷锟�,锟斤拷锟斤拷锟斤拷锟斤拷锟窖拷锟斤拷锟� wman
	 */
	public String genDevWmanData() {
		return "(wman|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("wman") + ")";
	}

	/*
	 * 锟芥警锟斤拷志
	 */
	public StringBuilder genDevWarnLog() {
		StringBuilder warnlog = new StringBuilder();
		int systemid = 3; // 锟斤拷时只模锟解功锟绞匡拷锟斤拷
		String levelid = Integer.toString(dataHub.ranInteger(0, 3)); // 0 锟斤拷示锟斤拷1 锟斤拷锟芥、2//
																// 锟斤拷锟斤拷
		String rectime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date());
		int wfid = dataHub.getWfid();
		int objectid = getWtidList().get(dataHub.ranInteger(0, getWtidList().size()));
		String logcode = dataHub.getLogCodeList(systemid).get(dataHub.ranInteger(0, dataHub.getRunLogCode(systemid).size()));
		String warnid = dataHub.ranString(16) + "-0" + systemid + "003645";
		int flag = dataHub.ranCoin();
		warnlog = warnlog.append("(warnlog|").append(systemid).append("|").append(levelid).append("|").append(rectime)
				.append("|").append(wfid).append("|").append(objectid).append("|").append(logcode).append("|")
				.append(warnid).append("|").append(flag).append(")");
		return warnlog;
	}

	/*
	 * 系统锟芥警锟斤拷锟斤拷
	 */
	public String genDevWarnEnd() {
		return "";
	}

	/*
	 * 锟斤拷锟皆拷锟斤拷锟�
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
				for (Prodata pda : dataHub.getCmdProData()) {
					varpathMap.put(pda.getIecpath().trim(), dataHub.getDynamicValue(pda));
				}

				if (!dataHub.getCmdPropaths().isEmpty()) {
					for (Propaths pps : dataHub.getCmdPropaths()) {
						while (n < pps.getAscflg()) {
							sReturn = sReturn + ";";
							n++;
						}

						if (varpathMap.containsKey(pps.getIecpath().trim())) {
							sReturn += varpathMap.get(pps.getIecpath()) + ";";
							n++;
						} else {
							logger.info("data锟斤拷缺锟劫达拷IEC锟斤拷锟斤拷 " + pps.getIecpath() + " / " + pps.getDescrcn());
						}
					}
				} else {
					logger.info(protocolid + "协锟介不锟斤拷锟节革拷compath锟斤拷 " + dataHub.getCompathOnCmdname());
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4, sReturn.length() - 1);
				logger.info(dataHub.getCompathOnCmdname() + " 锟斤拷锟斤拷锟窖凤拷锟斤拷" + sReturn.split(";").length + "锟斤拷IEC锟斤拷");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}

	/*
	 * 锟斤拷时锟斤拷刷锟斤拷停锟斤拷模式锟斤拷
	 */
	public String genStopModeWord() {
		String stopmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStopNum(); // 停锟斤拷模式锟斤拷时锟接硷拷锟斤拷锟斤拷
		try {
			for (Pathdescr pathdescr : dataHub.getStopModeWordList()) {
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
		// logger.info("锟斤拷前停锟斤拷模式锟斤拷为锟斤拷 " + stopmodeword);
		return stopmodeword;
	}

	/*
	 * 锟斤拷时锟斤拷刷锟斤拷锟睫癸拷锟斤拷模式锟斤拷
	 * 
	 */
	public String genLimitModeWord() {
		String limitmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getLimitNum(); // 锟睫癸拷锟斤拷模式锟斤拷时锟接硷拷锟斤拷锟斤拷
		try {
			for (Pathdescr pathdescr : dataHub.getLimitModeWordList()) {
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
		// logger.info("锟斤拷前锟睫癸拷锟斤拷模式锟斤拷为锟斤拷 " + limitmodeword);
		return limitmodeword;
	}

	/*
	 * 锟斤拷时锟斤拷刷锟铰凤拷锟阶刺� 支锟街癸拷锟斤拷锟竭硷拷
	 */
	public String genStateData() {
		String status = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStatusNum(); // 锟斤拷锟阶刺憋拷蛹锟斤拷锟斤拷锟�
		try {
			if ("0" != genFaultTree()) { // 锟斤拷锟较ｏ拷锟斤拷锟酵ｏ拷锟�
				status = "2";
			} else {
				for (Pathdescr pathdescr : dataHub.getStatusList()) {
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
	 * 锟斤拷锟阶刺�
	 */
	public String genDevStateData() {
		return "(statedata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + genStateData() + ")";
	}

	/*
	 * 前锟矫猴拷锟借备通锟斤拷状态锟斤拷锟斤拷时锟斤拷锟斤拷为通锟斤拷锟斤拷锟斤拷
	 */
	public String genDevComState() {
		return "(comstate|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + "0" + ")";
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟�
	 */
	public String genMainFault() {
		return genFaultTree().split(";")[0];
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public String genDevFaultData() {
		return "(falutdata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + genFaultTree() + ")";
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟绞硷拷薰锟斤拷锟� 锟斤拷时锟斤拷频锟斤拷模锟斤拷5锟斤拷锟斤拷锟较ｏ拷之锟斤拷指锟� faultsign true 锟睫癸拷锟较ｏ拷false锟斤拷锟斤拷锟斤拷
	 */
	public String genFaultTree() {
		String faulttree = "";
		List<String> lists = new ArrayList<String>();
		boolean faultsign = dataHub.ranBoolean();
		try {
			if (faultsign == false) {
				faulttree = "0";
			} else {
				for (Pathdescr pathdescr : dataHub.getFaultList()) {
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
	 * 锟斤拷锟斤拷锟斤拷锟�
	 */
	public String genDevAlarmData() {
		return "(alarmdata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + genAlarmTree() + ")";
	}

	/*
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟绞硷拷锟斤拷蘧锟斤拷锟� 锟斤拷时锟斤拷频锟斤拷模锟斤拷3锟斤拷锟斤拷锟芥，之锟斤拷指锟� alarmsign
	 */
	public String genAlarmTree() {
		String alarmtree = "";
		List<String> lists = new ArrayList<String>();
		boolean alarmsign = dataHub.ranBoolean();

		try {
			if (alarmsign == false) {
				alarmtree = "0";
			} else {
				for (Pathdescr pathdescr : dataHub.getAlarmList()) {
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
