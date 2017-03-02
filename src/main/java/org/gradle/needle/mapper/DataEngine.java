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
	 * ���췽��,��ʼ��protocolid,cmdname
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
		df = new DataDefined(protocolid, cmdname);
	}

	/**
	 * ���췽��,��ʼ��protocolid
	 */
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
		df = new DataDefined(protocolid);
	}

	/**
	 * �չ��췽��
	 */
	public DataEngine() {

	}

	/**
	 * ģ��ǰ��-˲̬�������� ָ��protocolid ���� DevMainData
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
				sReturn = "(wman|" + df.getWtidList().get(getWtidNum()) + "|" + sReturn + ")";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}

	/*
	 * ���������к�
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
	 * ģ��������-���������� ָ��protocolid, GWSOCKET����cmdname ���� DevPackData
	 */
	public String genDevPackData() {
		String sReturn = null;
		int n = 0;
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("�ѷ��͹��Ϻ�" + sFaultString);
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
	 * ��ʱ��ˢ�µ�ͣ��ģʽ��
	 */
	public String getStopModeWordIecValue() {
		String stopmodeword = null;
		List<String> lists = df.getStopModeWordIecValueList();
		int n = VTimer.getStopNum(); // ʱ�Ӽ�����
		try {
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
	 * ��ʱ��ˢ�µ��޹���ģʽ��
	 * 
	 */
	public String getLimitModeWordIecValue() {
		String limitmodeword = null;
		List<String> lists = df.getLimitModeWordIecValueList();
		int n = VTimer.getLimitNum(); // ʱ�Ӽ�����
		try {
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
	 * ��ʱˢ�µ�ͣ��ģʽ�ֶ�Ӧ�����Ľ���
	 */
	public String getStopModeExplaincn() {
		return df.getStopModeWordMap().get(getStopModeWordIecValue());
	}

	/**
	 * ��ʱˢ�µ��޹���ģʽ�ֶ�Ӧ�����Ľ���
	 */
	public String getLimitModeExplaincn() {
		return df.getLimitModeWordMap().get(getLimitModeWordIecValue());
	}

	/**
	 * ���������
	 */
	public String getMainFault() {

		return "0";
	}

	/**
	 * �������
	 */
	public String getAlarm() {
		return "0";
	}

	/**
	 * ���״̬
	 */
	public String getStatus() {

		return "5";
	}

}
