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
 * @author kongzhaolei ����ģ�������� 1. ֧��ģ��˲̬���ݣ��������ݣ��������ݣ�ͨ��״̬�����״̬����,�澯��־ 2.
 *         ֧��ģ����ʷ˲̬���ݣ���������(10,5,1)�������������ݣ���ʷ�������ݣ���λ����
 */
public class DataGenerator {

	private int protocolid;
	private String cmdname;
	private DataDefined df;
	private static Logger logger = Logger.getLogger(DataGenerator.class.getName());
	Date date = new Date();

	/*
	 * ���췽�� 1 PLC����ʼ��protocolid,cmdname
	 */
	public DataGenerator(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		df = new DataDefined(protocolid, cmdname);
	}

	/*
	 * ���췽�� 2 normal����ʼ��protocolid
	 */
	public DataGenerator(int protocolid) {
		this.protocolid = protocolid;
		df = new DataDefined(protocolid);
	}

	/**
	 * ͳһ��������
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
	 * �������� sediment,һ����
	 */
	public String genDevSedimentOne() {
		date.setTime(date.getTime() - 60 * 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevOne() + ")";
	}

	// �������� sediment һ��������
	public String genDevSedimentOneData() {
		date.setTime(date.getTime() - 60 * 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevOneData()
				+ ")";
	}

	// �������� sediment ��ʷ˲̬
	public String genDevSedimentRealData() {
		date.setTime(date.getTime() - 1000);
		return "(sediment|" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(date) + "|" + genDevRealTimeData()
				+ ")";
	}

	/*
	 * ��ʷ˲̬���� realtimedata
	 */
	public String genDevRealTimeData() {
		return "(realtimedata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("realtimedata") + ")";
	}

	/*
	 * ʮ�������� tendata
	 */
	public String genDevTenData() {
		return "(tendata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("tendata") + ")";
	}

	/*
	 * һ�������� one
	 */
	public String genDevOne() {
		return "(one|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("one") + ")";
	}

	// һ������������ onedata
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
	 * ��λ���� changesave
	 */
	public String genDevChangeSave() {
		return "(changesave|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("changesave") + ")";
	}

	/*
	 * �������� powercurve
	 */
	public String genDevPowerCurve() {
		return "(powercurve|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("powercurve") + ")";
	}

	/*
	 * ����ѯ���� wman
	 */
	public String genDevWmanData() {
		return "(wman|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|"
				+ this.gevDevDataEngine("wman") + ")";
	}

	/*
	 * �澯��־
	 */
	public StringBuilder genDevWarnLog() {
		StringBuilder warnlog = new StringBuilder();
		int systemid = 3; // ��ʱֻģ�⹦�ʿ���
		String levelid = Integer.toString(df.ranInteger(0, 3)); // 0 ��ʾ��1 ���桢2//
																// ����
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
	 * ϵͳ�澯����
	 */
	public String genDevWarnEnd() {
		return "";
	}

	/*
	 * ���Ԫ����
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
							logger.info("data�ⲻ���ڴ�IEC���� " + pps.getIecpath() + " / " + pps.getDescrcn());
						}
					}
				} else {
					logger.info(protocolid + "Э�鲻���ڸ�compath�� " + df.getCompathOnCmdname());
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4, sReturn.length() - 1);
				logger.info(df.getCompathOnCmdname() + " �����ѷ���" + sReturn.split(";").length + "��IEC��");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}

	/*
	 * ��ʱ��ˢ��ͣ��ģʽ��
	 */
	public String genStopModeWord() {
		String stopmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStopNum(); // ͣ��ģʽ��ʱ�Ӽ�����
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
		// logger.info("��ǰͣ��ģʽ��Ϊ�� " + stopmodeword);
		return stopmodeword;
	}

	/*
	 * ��ʱ��ˢ���޹���ģʽ��
	 * 
	 */
	public String genLimitModeWord() {
		String limitmodeword = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getLimitNum(); // �޹���ģʽ��ʱ�Ӽ�����
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
		// logger.info("��ǰ�޹���ģʽ��Ϊ�� " + limitmodeword);
		return limitmodeword;
	}

	/*
	 * ��ʱ��ˢ�·��״̬ ֧�ֹ����߼�
	 */
	public String genStateData() {
		String status = null;
		List<String> lists = new ArrayList<String>();
		int n = VTimer.getStatusNum(); // ���״̬ʱ�Ӽ�����
		try {
			if (genFaultTree() != "0") { // ���ϣ����ͣ��
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
	 * ���״̬
	 */
	public String genDevStateData() {
		return "(statedata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genStateData()
				+ ")";
	}

	/*
	 * ǰ�ú��豸ͨ��״̬����ʱ����Ϊͨ������
	 */
	public String genDevComState() {
		return "(comstate|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + "0" + ")";
	}

	/*
	 * ���������
	 */
	public String genMainFault() {
		return genFaultTree().split(";")[0];
	}

	/*
	 * ��������
	 */
	public String genDevFaultData() {
		return "(falutdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genFaultTree()
				+ ")";
	}

	/*
	 * �������������ʼ�޹��� ��ʱ��Ƶ��ģ��5�����ϣ�֮��ָ� faultsign true �޹��ϣ�false������
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
	 * �������
	 */
	public String genDevAlarmData() {
		return "(alarmdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genAlarmTree()
				+ ")";
	}

	/*
	 * �������������ʼ���޾��� ��ʱ��Ƶ��ģ��3�����棬֮��ָ� alarmsign
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
