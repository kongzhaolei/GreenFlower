package org.gradle.needle.engine;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.gradle.needle.model.Pathdescr;
import org.gradle.needle.model.Prodata;
import org.gradle.needle.model.Propaths;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.util.DBFactory;
import org.gradle.needle.util.GlobalSettings;
import org.gradle.needle.util.DBFactory.DBEnvironment;

/***
 * 
 * @author kongzhaolei
 * 
 * 
 */
public class DataDefined {
	int protocolid;
	String cmdname;
	private static Logger logger = Logger.getLogger(DataDefined.class.getName());

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

	/**
	 * ��ȡ����
	 */
	public List<Pathdescr> getFaultList() {
		return getPathdescr("WTUR.Flt.Rs.S");
	}

	/**
	 * ��ȡ����
	 */
	public List<Pathdescr> getAlarmList() {
		return getPathdescr("WTUR.Alam.Rs.S");
	}

	/**
	 * ��ȡ���״̬
	 */
	public List<Pathdescr> getStatusList() {
		return getPathdescr("WTUR.TurSt.Rs.S");
	}

	/**
	 * ��ȡͣ��ģʽ��
	 */
	public List<Pathdescr> getStopModeWordList() {
		return getPathdescr("WTUR.Other.Wn.I16.StopModeWord");
	}
	
	/**
	 * ��ȡ�޹���ģʽ��
	 */
	public List<Pathdescr> getLimitModeWordList() {
		return getPathdescr("WTUR.Other.Rs.S.LitPowByPLC");
	}

	/**
	 *  ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper��������
	 *  ��ȡconfig��pathdescr������ά���ݼ�(protocolid, iecpath)
	 */
	public List<Pathdescr> getPathdescr(String iecpath) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.configdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Pathdescr pathdescr = new Pathdescr();
		pathdescr.setProtocolid(protocolid);
		pathdescr.setIecpath(iecpath);
		List<Pathdescr> list = mapper.selectPathdescr(pathdescr);
		return list;
	}
	
	/**
	 * ��ȡά�����ݵ�propaths������ά���ݼ�(protocolid, cmdname)
	 */
	public List<Propaths> getCmdPropaths() {
		List<Propaths> pack_list = new ArrayList<>();
		try {
			for (Propaths pps : getAllPropaths()) {
				if (pps.getCompath() != null) {
					if (getCompathOnCmdname().equals(pps.getCompath().trim())) {
						pack_list.add(pps);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" ����compath��ֵ�Ƿ�Ϊnull ");
		}
		return pack_list;
	}

	/**
	 * ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper��������
	 * ��ȡconfig��propaths������ά���ݼ�(protocolid)
	 */
	public List<Propaths> getAllPropaths() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.configdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Propaths propaths = new Propaths();
		propaths.setProtocolid(protocolid);
		List<Propaths> list = mapper.selectPropaths(propaths);
		return list;
	}

	/**
	 * ��ȡά�����ݵ�prodata������ά���ݼ�(protocolid, cmdname)
	 */
	public List<Prodata> getCmdProData() {
		List<Prodata> pack_list = new ArrayList<>();
		try {
			for (Prodata pda : getAllProData()) {
				if (pda.getCompath() != null) { // compath��null��������ж�
					if (getCompathOnCmdname().equals(pda.getCompath().trim())) {
						pack_list.add(pda);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pack_list;
	}

	/**
	 * ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper��������
	 * ��ȡdata��prodata������ά���ݼ�(protocolid)
	 */
	public List<Prodata> getAllProData() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.datadb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Prodata prodata = new Prodata();
		prodata.setProtocolid(protocolid);
		List<Prodata> list = mapper.selectProdata(prodata);
		return list;
	}

	/**
	 * ��ȡ������list<wtid>
	 */
	public List<Integer> getWtidList() {
		List<Integer> lists = new ArrayList<Integer>();
		try {
			for (Wtinfo wtinfo : getWtinfo()) {
				lists.add(wtinfo.getWtid());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * ����mybatis��� ����Ҫʵ��SuperMapper�ӿڣ�mybatis�Զ�����mapper��������
	 * ��ȡconfig��wtinfo������ά���ݼ�(protocolid)
	 */
	public List<Wtinfo> getWtinfo() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.configdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Wtinfo wtinfo = new Wtinfo();
		wtinfo.setProtocolid(protocolid);
		List<Wtinfo> list = mapper.selectWtinfo(wtinfo);
		return list;
	}

	/**
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

	/**
	 * 
	 * ����col_1��������DynamicValue 
	 * 1 FIXED �̶�ֵ��col_2 
	 * 2 FIXBOOL ������� ranBoolean() 
	 * 3 DYNAMIC ��̬����
	 * 4 FAULTMAIN ������ 
	 * 5 STATUS ���״̬ 
	 * 6 YEAR �� 
	 * 7 MONTH �� 
	 * 8 DAY �� 
	 * 9 HOUR ʱ
	 * 10 MINUTE ��
	 * 11 SECOND ��
	 * 12 RANDOM ����� ranDouble()
	 * 13 TOTAL ң����
	 * 14 STOPMODE ͣ��ģʽ��/״̬ģʽ��
	 * 15 LIMITMODE �޹���ģʽ��
	 * 16 ALARM  ����
	 * 17 FAULT ������
	 */
	public String getDynamicValue(Prodata pda) throws SQLException {
		String rString = "";
		String dttype = "";
		try {
			dttype = pda.getCol1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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

		case "CURRENTTIME":
			rString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date());
			break;

		case "RANDOM":
			rString = ranDouble(pda.getCol2().split(",")[0], pda.getCol2().split(",")[1]);
			break;

		case "FIXBOOL":
			rString = Boolean.toString(ranBoolean());
			break;

		// ��ʱ��ֵ ranCoin()
		case "DYNAMIC":
			rString = Integer.toString(ranCoin());
			break;

		case "FAULTMAIN":
			rString = new DataGenerator(protocolid).genMainFault();
			break;

		case "STATUS":
			rString = new DataGenerator(protocolid).genStatusData();
			break;

		case "TOTAL":
			rString = pda.getCol2();
			break;

		case "STOPMODE":
			rString = new DataGenerator(protocolid).genStopModeWord();
			break;

		case "LIMITMODE":
			rString = new DataGenerator(protocolid).genLimitModeWord();
			break;
			
		case "ALARM":
			rString = new DataGenerator(protocolid).genAlarmTree();
			
		case "FAULT":
			rString = new DataGenerator(protocolid).genFaultTree();
			
		case "NULL":
			rString = pda.getCol2();
			
		default:
			rString = pda.getCol2();
			break;
		}
		return rString;
	}

	/**
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

	/**
	 * ����������ͣ�λ��max��min֮��ķ���
	 * ����(min,max)�����е�������������max
	 */
	public Integer ranInteger(int min, int max) {
		Random random = new Random();
		int bd = random.nextInt(max - min) + min;   
		return bd;
	}

	/**
	 * �������˫���ȣ�λ��max��min֮��ķ���
	 */
	public static String ranDouble(String min, String max) {
		double bt = Integer.parseInt(min)
				+ ((Integer.parseInt(max) - Integer.parseInt(min)) * new Random().nextDouble());
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(bt).toString();
	}

	/**
	 * ����һ������Ĳ���ֵ�ķ���
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

	/**
	 * �������0��1
	 */
	public int ranCoin() {
		Random rand = new Random();
		return rand.nextInt(2);
	}

}