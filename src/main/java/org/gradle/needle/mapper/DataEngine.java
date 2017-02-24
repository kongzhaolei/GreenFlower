package org.gradle.needle.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.server.WindFarmSimulator;

/***
 * 
 * @author kongzhaolei
 * 
 */
public class DataEngine {

	int protocolid;
	String cmdname;
	public static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	/**
	 * ���췽��,��ʼ��protocolid,cmdname
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/**
	 * ���췽��,��ʼ��protocolid
	 */
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/**
	 * �չ��췽��
	 */
	public DataEngine() {

	}
	
	/**
	 * ָ��protocolid ���� DevMainData
	 */
	public String genDevMainData() {
		String sReturn = null;
		
		
		
		
		
		
		
		
		return sReturn;
	}

	/**
	 * ָ��protocolid, GWSOCKET����cmdname ���� DevPackData 
	 */
	public String genDevPackData() {
		String sReturn = null;
		int n = 0;
		DataDefined df = new DataDefined(protocolid, cmdname);
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("�ѷ��͹��Ϻ�" + sFaultString);
			} else {
				for (Prodata pda : df.getPackProData()) {
					varpathMap.put(pda.getIecpath().trim(),
							df.getDynamicValue(pda));
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
							logger.info("DataSet�����ڴ�IEC���� " + pps.getIecpath()
									+ " ------ " + pps.getDescrcn());
						}
					}
				} else {
					logger.info(protocolid + " Э��Ų�ƥ�䣬arraylist�����ڸ�compath�� "
							+ df.getCompathOnCmdname());
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4,
						sReturn.length() - 1);
				logger.info(df.getCompathOnCmdname() + " �����ѷ���"
						+ sReturn.split(";").length + "��IEC��");
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
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getStopModeWordIecValueList();
		int n = WindFarmSimulator.getNum(); // ʱ�Ӽ�����
		try {
			if (!(n > lists.size())) {
				stopmodeword = lists.get(n);
			} else {
				stopmodeword = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("��ǰͣ��ģʽ��Ϊ�� " + stopmodeword);
		return stopmodeword;
	}

	/**
	 * ��ʱ��ˢ�µ��޹���ģʽ��
	 * 
	 */
	public String getLimitModeWordIecValue() {
		String limitmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getLimitModeWordIecValueList();
		int n = WindFarmSimulator.getNum(); // ʱ�Ӽ�����
		try {
			if (!(n > lists.size())) {
				limitmodeword = lists.get(n);
			} else {
				limitmodeword = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("��ǰ�޹���ģʽ��Ϊ�� " + limitmodeword);
		return limitmodeword;
	}

	/**
	 * ��ʱˢ�µ�ͣ��ģʽ�ֶ�Ӧ�����Ľ���
	 */
	public String getStopModeExplaincn() {
		return new DataDefined(protocolid).getStopModeWordMap().get(
				getStopModeWordIecValue());
	}

	/**
	 * ��ʱˢ�µ��޹���ģʽ�ֶ�Ӧ�����Ľ���
	 */
	public String getLimitModeExplaincn() {
		return new DataDefined(protocolid).getLimitModeWordMap().get(
				getLimitModeWordIecValue());
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
