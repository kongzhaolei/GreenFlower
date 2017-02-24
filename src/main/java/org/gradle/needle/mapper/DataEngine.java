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
	 * 构造方法,初始化protocolid,cmdname
	 */
	public DataEngine(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;
	}

	/**
	 * 构造方法,初始化protocolid
	 */
	public DataEngine(int protocolid) {
		this.protocolid = protocolid;
	}

	/**
	 * 空构造方法
	 */
	public DataEngine() {

	}
	
	/**
	 * 指定protocolid 生成 DevMainData
	 */
	public String genDevMainData() {
		String sReturn = null;
		
		
		
		
		
		
		
		
		return sReturn;
	}

	/**
	 * 指定protocolid, GWSOCKET命令cmdname 生成 DevPackData 
	 */
	public String genDevPackData() {
		String sReturn = null;
		int n = 0;
		DataDefined df = new DataDefined(protocolid, cmdname);
		Map<String, String> varpathMap = new HashMap<String, String>();
		try {
			if ("GETCURRENTERROR".equals(cmdname)) {
				sReturn = sFaultString;
				logger.info("已发送故障号" + sFaultString);
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

	/**
	 * 定时器刷新的停机模式字
	 */
	public String getStopModeWordIecValue() {
		String stopmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getStopModeWordIecValueList();
		int n = WindFarmSimulator.getNum(); // 时钟计数器
		try {
			if (!(n > lists.size())) {
				stopmodeword = lists.get(n);
			} else {
				stopmodeword = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("当前停机模式字为： " + stopmodeword);
		return stopmodeword;
	}

	/**
	 * 定时器刷新的限功率模式字
	 * 
	 */
	public String getLimitModeWordIecValue() {
		String limitmodeword = null;
		DataDefined ddf = new DataDefined(protocolid);
		List<String> lists = ddf.getLimitModeWordIecValueList();
		int n = WindFarmSimulator.getNum(); // 时钟计数器
		try {
			if (!(n > lists.size())) {
				limitmodeword = lists.get(n);
			} else {
				limitmodeword = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("当前限功率模式字为： " + limitmodeword);
		return limitmodeword;
	}

	/**
	 * 定时刷新的停机模式字对应的中文解析
	 */
	public String getStopModeExplaincn() {
		return new DataDefined(protocolid).getStopModeWordMap().get(
				getStopModeWordIecValue());
	}

	/**
	 * 定时刷新的限功率模式字对应的中文解析
	 */
	public String getLimitModeExplaincn() {
		return new DataDefined(protocolid).getLimitModeWordMap().get(
				getLimitModeWordIecValue());
	}

	/**
	 * 风机主故障
	 */
	public String getMainFault() {

		return "0";
	}

	/**
	 * 风机警告
	 */
	public String getAlarm() {
		return "0";
	}

	/**
	 * 风机状态
	 */
	public String getStatus() {

		return "5";
	}

}
