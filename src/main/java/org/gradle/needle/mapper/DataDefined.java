package org.gradle.needle.mapper;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.gradle.needle.util.DBMybatis;

/**
 * 
 * @author kongzhaolei �̳� DBmybatis
 */
public class DataDefined extends DBMybatis {
	int protocolid;
	String cmdname;
	// String datapath = System.getProperty("user.dir")+
	// "/src/main/resources/data.mdb";
	// String configpath = System.getProperty("user.dir")+
	// "/src/main/resources/config.mdb";

	String datapath = "d:/GreenFlower/Data.mdb";
	String configpath = "d:/GreenFlower/config.mdb";
	private static Logger logger = Logger
			.getLogger(DataDefined.class.getName());

	/*
	 * ���췽��,��ʼ��protocolid,cmdname
	 */
	public DataDefined(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;

	}

	/*
	 * ���췽��,��ʼ��protocolid
	 */
	public DataDefined(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * �չ��췽��
	 */
	public DataDefined() {

	}

	/*
	 * ��ȡ����iecvalue�б�
	 */
	public List<String> getMainFaultList() {
		return getkeyList("WTUR.Flt.Rs.S");
	}

	/*
	 * ��ȡ���״̬iecvalue�б�
	 */
	public List<String> getStatusList() {
		return getkeyList("WTUR.TurSt.Rs.S");
	}

	/*
	 * ��ȡͣ��ģʽ��iecvalue�б�
	 */
	public List<String> getStopModeWordList() {
		return getkeyList("WTUR.Other.Wn.I16.StopModeWord");
	}

	/*
	 * ��ȡ�޹���ģʽ��iecvalue�б�
	 */
	public List<String> getLimitModeWordList() {
		return getkeyList("WTUR.Other.Ri.I16.LitPowByPLC");
	}

	/*
	 * ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper�������
	 * ��ȡһ���޹���ģʽ�֣�ͣ��ģʽ�֣����״̬��������Ϲ������� ���б�洢
	 */
	public List<String> getkeyList(String iecpath) {
		SqlSession sqlSession = configssf.openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		List<String> iecvalueList = new ArrayList<String>();
		Pathdescr pathdescr = new Pathdescr();
		pathdescr.setProtocolid(protocolid);
		pathdescr.setIecpath(iecpath);
		List<Pathdescr> list = mapper.selectPathdescr(pathdescr);
		for (Pathdescr pdr : list) {
			iecvalueList.add(pdr.getIecvalue());
		}
		return iecvalueList;

	}

	/*
	 * ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper�������
	 * ��ȡconfig��propaths�����ά���ݼ�
	 */
	public List<Propaths> getPropaths() {
		SqlSession sqlSession = configssf.openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Propaths propaths = new Propaths();
		propaths.setcompath(getCompathOnCmdname());
		propaths.setProtocolid(protocolid);
		List<Propaths> list = mapper.selectPropaths(propaths);
		System.out.println(list);
		return list;
	}

	/*
	 * ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper�������
	 * ��ȡdata��prodata�����ά���ݼ�
	 */
	public List<Prodata> getProData() {
		SqlSession sqlSession = datassf.openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Prodata prodata = new Prodata();
		prodata.setcompath(getCompathOnCmdname());
		prodata.setProtocolid(protocolid);
		List<Prodata> list = mapper.selectProdata(prodata);
		System.out.println(list);
		return list;
	}

	/*
	 * ����ǰ�õ�GWSOCKET�����ȡ��Ӧ��compath
	 */
	protected String getCompathOnCmdname() {
		String compath = GlobalSettings.getProperty(cmdname);
		String compathString = null;
		if (null != compath) {
			compathString = compath;
		} else {
			logger.info("�޷���Ӧ��ָ� " + cmdname);
		}
		return compathString;
	}

	/*
	 * 
	 * ����col_1����CacheValue 1 FIXED �̶�ֵ��col_2 2 FIXBOOL ������� ranBoolean() 3
	 * DYNAMIC ��̬���� 4 FAULTMAIN ������ 5 STATUS ���״̬ 6 YEAR �� 7 MONTH �� 8 DAY �� 9
	 * HOUR ʱ10 MINUTE ��11 SECOND ��12 RANDOM ����� ranDouble()13 TOTAL ң����14
	 * STOPMODE ͣ��ģʽ��/״̬ģʽ��15 LIMITMODE �޹���ģʽ��
	 */
	public String getDynamicValue(Prodata pda) throws SQLException {
		String rString = "null";
		String dttype = pda.getCol1();

		switch (dttype.trim()) {
		case "FIXED":
			rString = pda.getCol2();
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
			rString = ranDouble(pda.getCol2().split(",")[0], pda.getCol2()
					.split(",")[1]);
			break;

		case "FIXBOOL":
			rString = Boolean.toString(ranBoolean());
			break;

		// ��ʱ��ֵ ranCoin()
		case "DYNAMIC":
			rString = Integer.toString(ranCoin());
			break;

		case "FAULTMAIN":
			rString = new DataEngine(protocolid).getMainFault();
			break;

		case "STATUS":
			rString = new DataEngine(protocolid).getStatus();
			break;

		case "TOTAL":

			rString = pda.getCol2();
			break;

		case "STOPMODE":
			rString = new DataEngine(protocolid).getStopModeWord();
			break;

		case "LIMITMODE":
			rString = new DataEngine(protocolid).getLimitModeWord();
			break;

		default:
			rString = pda.getCol2();
			break;
		}

		return rString;
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
	public static String ranDouble(String min, String max) {

		// Random random = new Random();
		// int bd = random.nextInt(Integer.parseInt(max) -
		// Integer.parseInt(min))
		// + Integer.parseInt(min);

		double bt = Integer.parseInt(min)
				+ ((Integer.parseInt(max) - Integer.parseInt(min)) * new Random()
						.nextDouble());

		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(bt).toString();
	}

	/*
	 * ����һ������Ĳ���ֵ�ķ���
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

	/*
	 * �������0��1
	 */
	public static int ranCoin() {
		Random rand = new Random();
		return rand.nextInt(2);
	}
}
