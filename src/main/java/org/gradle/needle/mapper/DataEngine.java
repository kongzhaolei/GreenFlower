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
	 * 构造方法,初始化protocolid,cmdname
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/*
	 * 构造方法,初始化protocolid
	 */
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * 空构造方法
	 */
	public DataEngine() {

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
							logger.info("DataSet不存在此IEC量： " + pps.getIecpath()
									+ " ------ " + pps.getDescrcn());
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
			} else {
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
			} else {
				limitmodeword = "5";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("当前限功率模式字为： " + limitmodeword);
		return limitmodeword;

	}

}
