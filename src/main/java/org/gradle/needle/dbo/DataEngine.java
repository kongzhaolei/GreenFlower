package org.gradle.needle.dbo;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DataEngine {

	int protocolid;
	String cmdname;
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	// ���췽��,��ʼ��Э���
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/*
	 * ����GWSOCKET�����ȡcachevalue
	 * cachevalue����varpath��Ӧ��dttype��̬����
	 */
	public String getCacheValue() {
		String sReturn = null;
		DataDefined df = new DataDefined(protocolid,cmdname);
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			ResultSet configSet = df.getConfigSetOnCmdname();
			ResultSet dataSet = df.getDataSetOnCmdname();
			while (dataSet.next()) {
				varpathMap.put(dataSet.getString("varpath").trim(),
						df.getDynamicValue(dataSet));
//				varpathMap.put(rSet2.getString("varpath").trim(),
//						"1");
			}

			if (!configSet.wasNull()) {
				while (configSet.next()) {
					if (varpathMap.containsKey(configSet.getString("datapath").trim())) {
						sReturn += varpathMap.get(configSet.getString("datapath"))
								+ ";";
					} else {
						logger.info("DataSet�����ڴ�IEC���� "
								+ configSet.getString("datapath") + "------" 
								+ configSet.getString("descrcn"));
					}
				}
			} else {
				logger.info(protocolid + " Э��ⲻƥ�䣬�����ڸ�compath�� "
						+ df.getCompathOnCmdname());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn.substring(sReturn.indexOf("null") + 4,
				sReturn.length() - 1);

	}
	
	/*
	 * ����Э���Զ���ͣ��ģʽ��
	 */
	public void setStopModeWord() {
		
	}

}
