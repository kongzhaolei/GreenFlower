package org.gradle.needle.dbo;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DataEngine {

	int protocolid;
	String cmdname;
	public static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	// 构造方法,初始化协议号
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/*
	 * 根据GWSOCKET命令获取cachevalue cachevalue根据varpath对应的dttype动态生成
	 */
	public String getCacheValue() {
		String sReturn = null;
		int n = 0;
		DataDefined df = new DataDefined(protocolid, cmdname);
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("已发送故障号" + sFaultString);
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
							logger.info("DataSet不存在此IEC量： "
									+ configSet.getString("iecpath") + " ------ "
									+ configSet.getString("descrcn"));
						}
					}
				} else {
					logger.info(protocolid + " 协议号不匹配，arraylist不存在该compath： "
							+ df.getCompathOnCmdname());
				}
				sReturn  = sReturn.substring(sReturn.indexOf("null") + 4,
						sReturn.length() - 1);
				logger.info(df.getCompathOnCmdname() + " 数组已发送" 
						+  sReturn.split(";").length + "个IEC量");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}
	
	/*
	 * 定时器
	 */
	public void timerStart() {
		
	}

	/*
	 * 根据协议自定义停机模式字
	 */
	public void setStopModeWord() {
		

	}

}
