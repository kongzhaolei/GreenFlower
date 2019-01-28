package org.gradle.needle.engine;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.gradle.needle.config.GlobalSettings;
import org.gradle.needle.model.Pathdescr;
import org.gradle.needle.model.Prodata;
import org.gradle.needle.model.Propaths;
import org.gradle.needle.model.Runlogcode;
import org.gradle.needle.model.Towerweatherheightmap;
import org.gradle.needle.model.WtOneData;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.util.DBFactory;
import org.gradle.needle.util.DBFactory.DBEnvironment;

/***
 * 
 * @author kongzhaolei
 * 
 * 
 */
public class DataHub {
	private int protocolid;
	private String cmdname;
	private static Logger logger = Logger.getLogger(DataHub.class.getName());

	/*
	 * ���췽��,��ʼ��protocolid,cmdname
	 */
	public DataHub(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;

	}

	/*
	 * ���췽��,��ʼ��protocolid
	 */
	public DataHub(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * �չ��췽��
	 */
	public DataHub() {

	}
	
	/**
	 * Get onedata from sqlserver 
	 * @return
	 */
	public List<WtOneData> getWtonedata(int wtid,String time) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.mysql).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		WtOneData oneData = new WtOneData();
		oneData.setWtid(wtid);
		oneData.setRectime(time);
		List<WtOneData> list = mapper.selectWtOneData(oneData);
		return list;
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

	/*
	 * ��ȡlocaldb pathdescr�����ά���ݼ�(protocolid, iecpath)
	 */
	public List<Pathdescr> getPathdescr(String iecpath) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Pathdescr pathdescr = new Pathdescr();
		pathdescr.setProtocolid(protocolid);
		pathdescr.setIecpath(iecpath);
		List<Pathdescr> list = mapper.selectPathdescr(pathdescr);
		return list;
	}

	/**
	 * ��ȡpropaths��cmd���ݼ�(protocolid, cmdname)
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
	 * ��ȡpropaths���ض����ݼ�
	 */
	public List<Propaths> getTypicalPropaths(String type) {
		List<Propaths> pps_list = new ArrayList<>();
		try {
			switch (type) {
			case "wman":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "tendata":
			case "fivedata":
			case "one":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() == 2) {
						pps_list.add(pps);
					}
				}
				break;
			case "onedata":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getBsend().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "changesave":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() < 2 & pps.getChangesave().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "realtimedata":
				for (Propaths pps : getAllPropaths()) {
					if (pps.getTranstype().intValue() == 1 & pps.getBsave().intValue() == 1) {
						pps_list.add(pps);
					}
				}
				break;
			case "powercurve":
				String[] iec = { "WTUR.WSpd.Ra.F32[AVG]", "WTUR.PwrAt.Ra.F32[AVG]", "WTUR.Temp.Ra.F32[AVG]" };
				for (Propaths pps : getAllPropaths()) {
					if (Arrays.asList(iec).contains(pps.getIecpath().trim())) {
						pps_list.add(pps);
					}
				}
				break;
			default:
				getAllPropaths();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pps_list;
	}

	/*
	 * ��ȡlocaldb propaths�����ά���ݼ�(protocolid)
	 */
	public List<Propaths> getAllPropaths() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Propaths propaths = new Propaths();
		propaths.setProtocolid(protocolid);
		List<Propaths> list = mapper.selectPropaths(propaths);
		return list;
	}

	/**
	 * ��ȡprodata�����ά���ݼ�(protocolid, cmdname)
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

	/*
	 * ��ȡdatadb prodata�����ά���ݼ�(protocolid)
	 */
	public List<Prodata> getAllProData() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.datadb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Prodata prodata = new Prodata();
		prodata.setProtocolid(protocolid);
		List<Prodata> list = mapper.selectProdata(prodata);
		return list;
	}

	/*
	 * ��ȡ���糡���
	 */
	public Integer getWfid() {
		return this.getWtinfo().get(0).getWfid();
	}

	/*
	 * ��ȡ�������γ�� float[]
	 */
	public float[] getLongitudeAndLatitude() {
		float[] aFloats = {this.getWtinfo().get(0).getWtlongitude(), getWtinfo().get(0).getWtlatitude()};
		return aFloats;
	}

	/*
	 * ��ȡlocal wtinfo�����ά���ݼ�(protocolid)
	 */
	public List<Wtinfo> getWtinfo() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Wtinfo wtinfo = new Wtinfo();
		wtinfo.setProtocolid(protocolid);
		List<Wtinfo> list = mapper.selectWtinfo(wtinfo);
		return list;
	}

	/*
	 * ��ȡrunlog code list<code>
	 * 
	 * @systemid
	 */
	public List<String> getLogCodeList(int systemid) {
		List<String> lists = new ArrayList<String>();
		try {
			for (Runlogcode runlogcode : getRunLogCode(systemid)) {
				lists.add(runlogcode.getCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/*
	 * ��ȡlocaldb runlogcode�����ά���ݼ�
	 */
	public List<Runlogcode> getRunLogCode(int systemid) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Runlogcode runlogcode = new Runlogcode();
		runlogcode.setSystemid(systemid);
		List<Runlogcode> list = mapper.selectRunlogcode(runlogcode);
		return list;
	}

	/*
	 * ���������
	 */
	public List<Integer> getTowerHeight() {
		List<Integer> lists = new ArrayList<Integer>();
		try {
			for (Towerweatherheightmap towerweatherheightmap : getTowerweatherheightmap()) {
				lists.add(towerweatherheightmap.getTowerheight());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	/*
	 * ��ȡlocaldb Towerweatherheightmap����ά���ݼ�
	 */
	private List<Towerweatherheightmap> getTowerweatherheightmap() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Towerweatherheightmap towerweatherheightmap = new Towerweatherheightmap();
		List<Towerweatherheightmap> list = mapper.selectTowerweatherheightmap(towerweatherheightmap);
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

	/**
	 * 
	 * ����col_1��������DynamicValue 1 FIXED �̶�ֵ��col_2 2 FIXBOOL ������� ranBoolean() 3
	 * DYNAMIC ��̬���� 4 FAULTMAIN ������ 5 STATUS ���״̬ 6 YEAR �� 7 MONTH �� 8 DAY �� 9
	 * HOUR ʱ 10 MINUTE �� 11 SECOND �� 12 RANDOM ����� ranDouble() 13 TOTAL ң���� 14
	 * STOPMODE ͣ��ģʽ��/״̬ģʽ�� 15 LIMITMODE �޹���ģʽ�� 16 ALARM ���� 17 FAULT ������
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
			rString = ranFloat(Integer.parseInt(pda.getCol2().split(",")[0]),
					Integer.parseInt(pda.getCol2().split(",")[1]));
			break;

		case "FIXBOOL":
			rString = Boolean.toString(ranBoolean());
			break;

		// ��ʱ��ֵ ranCoin()
		case "DYNAMIC":
			rString = Integer.toString(ranCoin());
			break;

		case "FAULTMAIN":
			rString = new DeviceDataGenerator(protocolid).genMainFault();
			break;

		case "STATUS":
			rString = new DeviceDataGenerator(protocolid).genStateData();
			break;

		case "TOTAL":
			rString = pda.getCol2();
			break;

		case "STOPMODE":
			rString = new DeviceDataGenerator(protocolid).genStopModeWord();
			break;

		case "LIMITMODE":
			rString = new DeviceDataGenerator(protocolid).genLimitModeWord();
			break;

		case "ALARM":
			rString = new DeviceDataGenerator(protocolid).genAlarmTree();

		case "FAULT":
			rString = new DeviceDataGenerator(protocolid).genFaultTree();

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
	public String ranString(int length) {
		String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * ����������ͣ�λ��max��min֮��ķ��� ����(min,max)�����е�������������max
	 */
	public Integer ranInteger(int min, int max) {
		Random random = new Random();
		int bd = random.nextInt(max - min) + min;
		return bd;
	}


	/**
	 * ����һ������Ĳ���ֵ�ķ���
	 */
	public boolean ranBoolean() {
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

	/**
	 * ������������ȣ�λ��max��min֮��ķ���
	 */
	public String ranFloat(int min, int max) {
		double bt = min + ((max - min) * new Random().nextFloat());
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(bt).toString();
	}

}
