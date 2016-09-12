package org.gradle.needle.dbo;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DataEngine {

	int protocolid;
	String cmdname;
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	// 构造方法,初始化协议号
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/*
	 * 根据GWSOCKET命令获取cachevalue
	 * cachevalue根据varpath对应的dttype动态生成
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
						logger.info("DataSet不存在此IEC量： "
								+ configSet.getString("datapath") + "------" 
								+ configSet.getString("descrcn"));
					}
				}
			} else {
				logger.info(protocolid + " 协议库不匹配，不存在该compath： "
						+ df.getCompathOnCmdname());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn.substring(sReturn.indexOf("null") + 4,
				sReturn.length() - 1);

	}
	
	/*
	 * 根据协议自定义停机模式字
	 */
	public void setStopModeWord() {
		
	}

}
