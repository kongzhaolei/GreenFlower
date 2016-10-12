package org.gradle.needle.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.server.NettyTcpServer;

/**
 * 
 * @author kongzhaolei
 * 
 */
public class DataEngine {

	int protocolid;
	String cmdname;
	public static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	/*
	 * ���췽��,��ʼ��protocolid,cmdname
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/*
	 * ���췽��,��ʼ��protocolid
	 */
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * �չ��췽��
	 */
	public DataEngine() {

	}

	/*
	 * ����GWSOCKET�����ȡcachevalue cachevalue����varpath��Ӧ��dttype��̬����
	 */
	public String getCacheValue() {
		String sReturn = null;
		int n = 0;
		DataDefined df = new DataDefined(protocolid, cmdname);
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("�ѷ��͹��Ϻ�" + sFaultString);
			} else {
				for (Prodata pda : df.getProData()) {
					varpathMap.put(pda.getIecpath().trim(),
							df.getDynamicValue(pda));
				}

				if (!df.getPropaths().isEmpty()) {
					for (Propaths pps : df.getPropaths()) {
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

	/*
	 * ͣ��ģʽ�ֶ�̬ˢ��
	 */
	public String getStopModeWord() {
		String stopmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getStopModeWordList();
		int n = NettyTcpServer.getIecvalue();
		try {
			if (!(n > lists.size())) {
				stopmodeword = lists.get(n);
			} else {
				stopmodeword = "4";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("��ǰͣ��ģʽ��Ϊ�� " + stopmodeword);
		return stopmodeword;

	}

	/*
	 * �����϶�̬ˢ��
	 */
	public String getMainFault() {

		return "0";
	}

	/*
	 * ���״̬�Ķ�̬ˢ��
	 */
	public String getStatus() {

		return "5";
	}

	/*
	 * �޹���ģʽ�ֵĶ�̬ˢ��
	 */
	public String getLimitModeWord() {

		String limitmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getLimitModeWordList();
		int n = NettyTcpServer.getIecvalue();
		try {
			if (!(n > lists.size())) {
				limitmodeword = lists.get(n);
			} else {
				limitmodeword = "5";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("��ǰ�޹���ģʽ��Ϊ�� " + limitmodeword);
		return limitmodeword;

	}

}
