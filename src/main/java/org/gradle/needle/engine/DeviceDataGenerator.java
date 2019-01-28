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
 * @author kongzhaolei ����ģ�������� 1. ֧��ģ��˲̬���ݣ��������ݣ��������ݣ�ͨ��״̬�����״̬����,�澯��־ 2.
 *         ֧��ģ����ʷ˲̬���ݣ���������(10,5,1)�������������ݣ���ʷ�������ݣ���λ����
 */
public class DeviceDataGenerator {

	private int protocolid;
	private String cmdname;
	private DataHub dataHub;
	private static Logger logger = Logger.getLogger(DeviceDataGenerator.class.getName());
	Date date = new Date();

	/*
	 * ���췽�� 1 PLC����ʼ��protocolid,cmdname
	 */
	public DeviceDataGenerator(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		dataHub = new DataHub(protocolid, cmdname);
	}

	/*
	 * ���췽�� 2 normal����ʼ��protocolid
	 */
	public DeviceDataGenerator(int protocolid) {
		this.protocolid = protocolid;
		dataHub = new DataHub(protocolid);
	}

	/**
	 * ͳһ��������
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
			logger.info("��ǰ���ݴ���" + sReturn);
		}
		return sReturn;
	}

	/**
	 * ��ȡ������list<wtid>
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
		return "(realtimedata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("realtimedata") + ")";
	}

	/*
	 * ʮ�������� tendata
	 */
	public String genDevTenData(Integer wtid) {
		return "(tendata|" + wtid + "|" + this.gevDevDataEngine("tendata") + ")";
	}

	/*
	 * ��������� fivedata
	 */
	public String genDevFiveData(Integer wtid) {
		return "(fivedata|" + wtid + "|" + this.gevDevDataEngine("fivedata") + ")";
	}

	/*
	 * һ�������� one
	 */
	public String genDevOne() {
		return "(one|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + this.gevDevDataEngine("one")
				+ ")";
	}

	// һ������������ onedata
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
	 * ��λ���� changesave
	 */
	public String genDevChangeSave() {
		return "(changesave|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("changesave") + ")";
	}

	/*
	 * �������� powercurve
	 */
	public String genDevPowerCurve() {
		return "(powercurve|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("powercurve") + ")";
	}

	/*
	 * ���,���������ѯ���� wman
	 */
	public String genDevWmanData() {
		return "(wman|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|"
				+ this.gevDevDataEngine("wman") + ")";
	}

	/*
	 * �澯��־
	 */
	public StringBuilder genDevWarnLog() {
		StringBuilder warnlog = new StringBuilder();
		int systemid = 3; // ��ʱֻģ�⹦�ʿ���
		String levelid = Integer.toString(dataHub.ranInteger(0, 3)); // 0 ��ʾ��1 ���桢2//
																// ����
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
							logger.info("data��ȱ�ٴ�IEC���� " + pps.getIecpath() + " / " + pps.getDescrcn());
						}
					}
				} else {
					logger.info(protocolid + "Э�鲻���ڸ�compath�� " + dataHub.getCompathOnCmdname());
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4, sReturn.length() - 1);
				logger.info(dataHub.getCompathOnCmdname() + " �����ѷ���" + sReturn.split(";").length + "��IEC��");
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
			if ("0" != genFaultTree()) { // ���ϣ����ͣ��
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
	 * ���״̬
	 */
	public String genDevStateData() {
		return "(statedata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + genStateData() + ")";
	}

	/*
	 * ǰ�ú��豸ͨ��״̬����ʱ����Ϊͨ������
	 */
	public String genDevComState() {
		return "(comstate|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + "0" + ")";
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
		return "(falutdata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + genFaultTree() + ")";
	}

	/*
	 * �������������ʼ�޹��� ��ʱ��Ƶ��ģ��5�����ϣ�֮��ָ� faultsign true �޹��ϣ�false������
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
	 * �������
	 */
	public String genDevAlarmData() {
		return "(alarmdata|" + getWtidList().get(dataHub.ranInteger(0, getWtidList().size())) + "|" + genAlarmTree() + ")";
	}

	/*
	 * �������������ʼ���޾��� ��ʱ��Ƶ��ģ��3�����棬֮��ָ� alarmsign
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
