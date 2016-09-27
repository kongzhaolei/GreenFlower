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
	int list_n;
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
			}else{
				ResultSet configSet = df.getConfigSetOnCmdname();
				ResultSet dataSet = df.getDataSetOnCmdname();
				while (dataSet.next()) {
					varpathMap.put(dataSet.getString("iecpath").trim(),
							df.getDynamicValue(dataSet));
				}

				if (!configSet.wasNull()) {
					while (configSet.next()) {
						while(n < configSet.getInt("ascflg")){
							sReturn = sReturn+";";
							n++;
						}
						
						if (varpathMap.containsKey(configSet.getString("iecpath")
								.trim())) {
							sReturn += varpathMap.get(configSet
									.getString("iecpath")) + ";";
							n++;
						} else {
							logger.info("DataSet�����ڴ�IEC���� "
									+ configSet.getString("iecpath") + " ------ "
									+ configSet.getString("descrcn"));
						}
					}
				} else {
					logger.info(protocolid + " Э��Ų�ƥ�䣬arraylist�����ڸ�compath�� "
							+ df.getCompathOnCmdname());
				}
				sReturn  = sReturn.substring(sReturn.indexOf("null") + 4,
						sReturn.length() - 1);
				logger.info(df.getCompathOnCmdname() + " �����ѷ���" 
						+  sReturn.split(";").length + "��IEC��");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}
	
	/*
	 * ģʽ�ֶ�ʱ��
	 */
	public void timerStart() {
		long interval = 3000;
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {			
			@Override
			public void run() {
				list_n++;
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}

	/*
	 * ����Э���Զ���ͣ��ģʽ��
	 */
	public void setStopModeWord() {
		

	}

}
