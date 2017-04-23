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

import sun.awt.windows.WFontConfiguration;

/***
 * 
 * @author kongzhaolei ����ģ�������� 1. ֧��ģ��˲̬���ݣ��������ݣ��������ݣ�ͨ��״̬�����״̬����,�澯��־ 2.
 *         ֧��ģ����ʷ˲̬���ݣ���������(10,5,1)�������������ݣ���ʷ�������ݣ���λ����
 */
public class DataGenerator {

	private int protocolid;
	private String cmdname;
	private DataDefined df;
	private static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataGenerator.class.getName());

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
	 * wman ��������
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
					if (maindatamap.containsKey(propaths.getIecpath()) & propaths.getTranstype().intValue() == 1) { // initValue()����int���ͷ���integer��ֵ
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
	 * �鲥�澯��־
	 */
	public StringBuilder genDevWarnLog() {
		StringBuilder warnlog = new StringBuilder();
		String systemid = "03"; // ��ʱֻģ�⹦�ʿ���
		String levelid = Integer.toString(df.ranInteger(0, 3));  // 0 ��ʾ��1 ���桢2 ����
		String rectime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date());
		int wfid = df.getWfid();
		int objectid = df.getWtidList().get(df.ranInteger(0, df.getWtidList().size()));
		String logcode = "@DisplayAllRealTimeData3377";
		String warnid = df.ranString(16) + "-" + systemid + "003645";
		int flag = df.ranCoin();
		warnlog = warnlog.append("(warnlog|").append(systemid).append("|").append(levelid).append("|").append(rectime)
				.append("|").append(wfid).append("|").append(objectid).append("|").append(logcode).append("|")
				.append(warnid).append("|").append(flag).append(")");
		return warnlog;
	}

	/**
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
				sReturn = sFaultString;
				logger.info("�ѷ��͹��Ϻ�" + sFaultString);
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

	/**
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

	/**
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

	/**
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

	/**
	 * �鲥���״̬
	 */
	public String genDevStateData() {
		return "(statedata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genStateData()
				+ ")";
	}

	/**
	 * �鲥ǰ�ú��豸ͨ��״̬���� ��ʱ����Ϊͨ������
	 */
	public String genDevComState() {
		return "(comstate|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + "0" + ")";
	}

	/**
	 * ���������
	 */
	public String genMainFault() {
		return genFaultTree().split(";")[0];
	}

	/**
	 * �鲥������������
	 */
	public String genDevFaultData() {
		return "(falutdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genFaultTree()
				+ ")";
	}

	/**
	 * �������������ʼ�޹��� ��ʱ��Ƶ��ģ��5�����ϣ�֮��ָ� faultsign true �޹��ϣ�false������
	 */
	public String genFaultTree() {
		String faulttree = "";
		List<String> lists = new ArrayList<String>();
		boolean faultsign = VTimer.getFaultSign();
		try {
			if (faultsign == true) {
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

	/**
	 * �鲥�����������
	 */
	public String genDevAlarmData() {
		return "(alarmdata|" + df.getWtidList().get(df.ranInteger(0, df.getWtidList().size())) + "|" + genAlarmTree()
				+ ")";
	}

	/**
	 * �������������ʼ���޾��� ��ʱ��Ƶ��ģ��3�����棬֮��ָ� alarmsign
	 */
	public String genAlarmTree() {
		String alarmtree = "";
		List<String> lists = new ArrayList<String>();
		boolean alarmsign = VTimer.getAlarmSign();

		try {
			if (alarmsign == true) {
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
