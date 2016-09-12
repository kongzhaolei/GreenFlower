package org.gradle.needle.dbo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.gradle.needle.util.DBUtils;

public class DataDefined {
	int protocolid;
	String cmdname;
	// String datapath = System.getProperty("user.dir")+
	// "/src/main/resources/data.mdb";
	// String configpath = System.getProperty("user.dir")+
	// "/src/main/resources/config.mdb";

	String datapath = "d:/Data.mdb";
	String configpath = "d:/config.mdb";
	DBUtils dataDb = new DBUtils("access", datapath);
	DBUtils configDb = new DBUtils("access", configpath);
	private static Logger logger = Logger
			.getLogger(DataDefined.class.getName());

	/*
	 * ���췽��,��ʼ��Э���
	 */
	public DataDefined(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;

	}

	/*
	 * ��ȡconfig��propaths�����ά���ݼ�
	 */
	public ResultSet getConfigSetOnCmdname() {
		String sql1 = "SELECT * FROM propaths Where protocolid = " + protocolid
				+ " AND compath = " + "'" + getCompathOnCmdname() + "'"
				+ " ORDER BY pathid ASC";

		ResultSet configSet = null;

		try {
			configSet = configDb.Query(sql1);
	//		configDb.ConnClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configSet;
	}

	/*
	 * ��ȡdata��data�����ά���ݼ�
	 */
	protected ResultSet getDataSetOnCmdname() {
		String sql2 = "SELECT * FROM DATA WHERE compath = " + "'"
				+ getCompathOnCmdname() + "'";

		ResultSet dataSet = null;

		try {
			dataSet = dataDb.Query(sql2);
	//		dataDb.ConnClose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSet;
	}

	/*
	 * ����ǰ�õ�GWSOCKET�����ȡ��Ӧ��compath
	 */
	protected String getCompathOnCmdname() {
		String compath = null;
		String sql = "select * from tocmd";

		Map<String, String> cmdMap = new HashMap<String, String>();

		try {
			ResultSet rs = dataDb.Query(sql);
			while (rs.next()) {
				cmdMap.put(rs.getString("cmdname").trim(),
						rs.getString("compath'").trim());
			}
			if (cmdMap.containsKey(cmdname)) {
				compath = cmdMap.get(cmdname);
			} else {
				logger.info("�޷���Ӧ��ָ� " + cmdname);
			}
	//		dataDb.ConnClose();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return compath;

	}

	/*
	 * ����dttype����CacheValue 
	 * 1 FIXED �̶�ֵ��initvalue
	 * 2 FIXBOOL �������   ranBoolean()
	 * 3 DYNAMIC  ��̬����
	 * 4 FAULTMAIN  ������
	 * 5 STATUS   ���״̬
	 * 6 YEAR �� 
	 * 7 MONTH �� 
	 * 8 DAY �� 
	 * 9 HOUR ʱ
	 *10 MINUTE �� 
	 *11 SECOND �� 
	 *12 RANDOM ����� ranDouble() 
	 *13 TOTAL   ң����
	 *14 STOPMODE ͣ��ģʽ��/״̬ģʽ��
	 *15 LIMITMODE �޹���ģʽ��
	 */
	public String getDynamicValue(ResultSet dataSet) throws SQLException {
		String rString = "null";
		String dttype = dataSet.getString("dttype");
		
				switch (dttype.trim()) {
				case "FIXED":
					rString = dataSet.getString("initvalue");
					break;

				case "YEAR":
					rString = Integer.toString(Calendar.YEAR);
					break;

				case "MONTH":
					rString = Integer.toString(Calendar.MONTH);
					break;

				case "DAY":
					rString = Integer.toString(Calendar.DATE);
					break;

				case "HOUR":
					rString = Integer.toString(Calendar.HOUR);
					break;

				case "MINUTE":
					rString = Integer.toString(Calendar.MINUTE);
					break;

				case "SECOND":
					rString = Integer.toString(Calendar.SECOND);
					break;

				case "RANDOM":
					rString = ranDouble(
							dataSet.getString("initvalue").split(",")[0],
							dataSet.getString("initvalue").split(",")[1]);
					break;
					
				case "FIXBOOL":
					rString = Boolean.toString(ranBoolean());
					break;
				
					//��ʱ��ֵinitvalue
				case "DYNAMIC":
					rString = dataSet.getString("initvalue");
					break;
					
				case "FAULTMAIN":
					rString = mainFaultRefresh();
					break;
					
				case "STATUS":
					rString = getStatus();
					break;
					
				case "TOTAL":
					rString = TotalRefresh();
					break;
					
				case "STOPMODE":
					rString = stopModeRefresh();
					break;
					
				case "LIMITMODE":
					rString = limitModeRefresh();
					break;
					
				default:
					rString = dataSet.getString("initvalue");
					break;
				}
				
		return rString;
	}
	
	

	/*
	 * ң��������
	 */
	private String TotalRefresh() {
		// TODO �Զ����ɵķ������
		return null;
	}

	/*
	 * �����϶�̬ˢ��
	 */
	private String mainFaultRefresh() {
		
		return null;
	}

	/*
	 * ���״̬�Ķ�̬ˢ��
	 */
	private String getStatus() {
		
		return null;
	}

	/*
	 * ͣ��ģʽ�ֵĶ�̬ˢ��
	 */
	public String stopModeRefresh() {
		
		return null;

	}

	/*
	 * �޹���ģʽ�ֵĶ�̬ˢ��
	 */
	public String limitModeRefresh() {
		
		return null;

	}

	/*
	 * ��������ַ����ķ���
	 */
	public static String ranString(int length) {
		String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/*
	 * ���������λ��max��min֮��ķ���
	 */
	public static String ranDouble(String max, String min) {
		BigDecimal bd = new BigDecimal(Math.random() * Integer.parseInt(max)
				+ Integer.parseInt(min));
		 DecimalFormat df = new DecimalFormat("#.00");
		return df.format(bd).toString();
	}

	/*
	 * ����һ������Ĳ���ֵ�ķ���
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}
}
