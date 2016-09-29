package org.gradle.needle.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gradle.needle.server.NettyTcpServer;

public class DataEngine {

	int protocolid;
	String cmdname;
	public static String sFaultString = "0";
	private static Logger logger = Logger.getLogger(DataEngine.class.getName());

	/*
	 * 构造方法,初始化协议号
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}
	
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * 空构造方法
	 */
	public DataEngine() {
		// TODO 自动生成的构造函数存根
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
							logger.info("DataSet不存在此IEC量： "
									+ configSet.getString("iecpath")
									+ " ------ "
									+ configSet.getString("descrcn"));
						}
					}
				} else {
					logger.info(protocolid + " 协议号不匹配，arraylist不存在该compath： "
							+ df.getCompathOnCmdname());
				}
				sReturn = sReturn.substring(sReturn.indexOf("null") + 4,
						sReturn.length() - 1);
				logger.info(df.getCompathOnCmdname() + " 数组已发送"
						+ sReturn.split(";").length + "个IEC量");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sReturn;
	}
	
	/*
	 * 停机模式字动态刷新
	 */
	public String getStopModeWord() {
		String stopmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getStopModeWordList();
		int n = NettyTcpServer.getIecvalue();
		try {
			if (!(n > lists.size())) {
				stopmodeword = lists.get(n);
			}else {
				stopmodeword = "4";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("当前停机模式字为： " + stopmodeword);
		return stopmodeword;

	}
	
	/*
	 * 主故障动态刷新
	 */
	public String getMainFault() {

		return "0";
	}

	/*
	 * 风机状态的动态刷新
	 */
	public String getStatus() {

		return "5";
	}

	/*
	 * 限功率模式字的动态刷新
	 */
	public String getLimitModeWord() {

		String limitmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getLimitModeWordList();
		int n = NettyTcpServer.getIecvalue();
		try {
			if (!(n > lists.size())) {
				limitmodeword = lists.get(n);
			}else {
				limitmodeword = "5";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("当前限功率模式字为： " + limitmodeword);
		return limitmodeword;

	}

}
