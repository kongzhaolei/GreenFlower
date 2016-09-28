package org.gradle.needle.dao;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class DataEngine {

	int protocolid;
	String cmdname;
	public static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	/*
	 * ���췽��,��ʼ��Э���
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}
	
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * �չ��췽��
	 */
	public DataEngine() {
		// TODO �Զ����ɵĹ��캯�����
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
				ResultSet dataSet = df.getDataSetOnCmdname();
				while (dataSet.next()) {
					varpathMap.put(dataSet.getString("iecpath").trim(),
							df.getDynamicValue(dataSet));
				}
				
				ResultSet configSet = df.getConfigSetOnCmdname();
				if (!configSet.wasNull()) {
					while (configSet.next()) {
						while (n < configSet.getInt("ascflg")) {
							sReturn = sReturn + ";";
							n++;
						}

						if (varpathMap.containsKey(configSet.getString(
								"iecpath").trim())) {
							sReturn += varpathMap.get(configSet
									.getString("iecpath")) + ";";
							n++;
						} else {
							logger.info("DataSet�����ڴ�IEC���� "
									+ configSet.getString("iecpath")
									+ " ------ "
									+ configSet.getString("descrcn"));
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

     /* ��ʱ��ʵ��ͣ��ģʽ�ֺŵ���
      * 
      */
	public int timerStart() {
		long interval = 5000;
		int list_n = 0;
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				list_n++;
				System.out.println("wo cao " + list_n);
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
		if (list_n > new DataDefined(protocolid).getStopModeWordList().size()) {
			timer.cancel();
			list_n = 0;
		}
		return list_n;
	}
	
	/*
	 * ͣ��ģʽ�ֶ�̬ˢ��
	 */
	public String getStopModeWord() {
		String stopmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		try {
			if (!(timerStart() > ddf.getStopModeWordList().size())) {
				stopmodeword = ddf.getStopModeWordList().get(timerStart());
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
	public String getLimitMode() {

		return "0";

	}

}
