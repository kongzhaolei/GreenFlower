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
	 * 锟斤拷锟届方锟斤拷,锟斤拷始锟斤拷protocolid,cmdname
	 */
	public DataHub(int protocolid, String cmdname) {
		this.protocolid = protocolid;
		this.cmdname = cmdname;

	}

	/*
	 * 锟斤拷锟届方锟斤拷,锟斤拷始锟斤拷protocolid
	 */
	public DataHub(int protocolid) {
		this.protocolid = protocolid;
	}

	/*
	 * 锟秸癸拷锟届方锟斤拷
	 */
	public DataHub() {

	}

	/**
	 * Get onedata from sqlserver
	 * 
	 * @return
	 */
	public List<WtOneData> getWtonedata(String wtid, String time) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.mysql).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		WtOneData oneData = new WtOneData();
		oneData.setWtid(wtid);
		oneData.setRectime(time);
		List<WtOneData> list = mapper.selectWtOneData(oneData);
		return list;
	}

	/**
	 * 锟斤拷取锟斤拷锟斤拷
	 */
	public List<Pathdescr> getFaultList() {
		return getPathdescr("WTUR.Flt.Rs.S");
	}

	/**
	 * 锟斤拷取锟斤拷锟斤拷
	 */
	public List<Pathdescr> getAlarmList() {
		return getPathdescr("WTUR.Alam.Rs.S");
	}

	/**
	 * 锟斤拷取锟斤拷锟阶刺�
	 */
	public List<Pathdescr> getStatusList() {
		return getPathdescr("WTUR.TurSt.Rs.S");
	}

	/**
	 * 锟斤拷取停锟斤拷模式锟斤拷
	 */
	public List<Pathdescr> getStopModeWordList() {
		return getPathdescr("WTUR.Other.Wn.I16.StopModeWord");
	}

	/**
	 * 锟斤拷取锟睫癸拷锟斤拷模式锟斤拷
	 */
	public List<Pathdescr> getLimitModeWordList() {
		return getPathdescr("WTUR.Other.Rs.S.LitPowByPLC");
	}

	/*
	 * 锟斤拷取localdb pathdescr锟斤拷锟斤拷锟轿拷锟斤拷菁锟�(protocolid, iecpath)
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
	 * 锟斤拷取propaths锟斤拷cmd锟斤拷锟捷硷拷(protocolid, cmdname)
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
			logger.info(" 锟斤拷锟斤拷compath锟斤拷值锟角凤拷为null ");
		}
		return pack_list;
	}

	/**
	 * 锟斤拷取propaths锟斤拷锟截讹拷锟斤拷锟捷硷拷
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
			case "newfivedata":
				String[] foreiec = { "WTUR.WSpd.Ra.F32[AVG]", "WTUR.WSpd.Ra.F32[MIN]", "WTUR.WSpd.Ra.F32[MAX]",
						"WTUR.Wdir.Ra.F32", "WTUR.Wdir.Ra.F32.25s", "WTUR.Wdir.Ra.F32.abs",
						"WTUR.Temp.Ra.F32[AVG]", "WGEN.Spd.Ra.F32[AVG]", "WTPS.Ang.Ra.F32.blade1[AVG]",
						"WTUR.PwrAt.Ra.F32[AVG]", "WTUR.PwrAt.Ra.F32.Theory[AVG]", "WTUR.PwrReact.Ra.F32[AVG]",
						"WTUR.TotEgyAt.Wt.F32[max]", "WTUR.TotEgyAt.Wt.F32[min]", "WTUR.Other.Rn.I16.HaveFault[MAX]",
						"WTUR.Other.Ri.I16.bLitPow", "WTUR.Other.Ri.I16.LitPowByPLC", "WTUR.TurSt.Rs.S",
						"WTUR.Flt.Rs.S" };
				for (Propaths pps : getAllPropaths()) {
					if (Arrays.asList(foreiec).contains(pps.getIecpath().trim())) {
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
	 * 锟斤拷取localdb propaths锟斤拷锟斤拷锟轿拷锟斤拷菁锟�(protocolid)
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
	 * 锟斤拷取prodata锟斤拷锟斤拷锟轿拷锟斤拷菁锟�(protocolid, cmdname)
	 */
	public List<Prodata> getCmdProData() {
		List<Prodata> pack_list = new ArrayList<>();
		try {
			for (Prodata pda : getAllProData()) {
				if (pda.getCompath() != null) { // compath锟斤拷null锟斤拷锟斤拷锟斤拷锟斤拷卸锟�
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
	 * 锟斤拷取datadb prodata锟斤拷锟斤拷锟轿拷锟斤拷菁锟�(protocolid)
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
	 * 锟斤拷取锟斤拷锟界场锟斤拷锟�
	 */
	public Integer getWfid() {
		return this.getWtinfo().get(0).getWfid();
	}

	/*
	 * 锟斤拷取锟斤拷锟斤拷锟斤拷锟轿筹拷锟� float[]
	 */
	public float[] getLongitudeAndLatitude() {
		float[] aFloats = { this.getWtinfo().get(0).getWtlongitude(), getWtinfo().get(0).getWtlatitude() };
		return aFloats;
	}

	/*
	 * 锟斤拷取local wtinfo锟斤拷锟斤拷锟轿拷锟斤拷菁锟�(protocolid)
	 */
	public List<Wtinfo> getWtinfo() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Wtinfo wtinfo = new Wtinfo();
		wtinfo.setProtocolid(protocolid);
		List<Wtinfo> list = mapper.selectWtinfo(wtinfo);
		return list;
	}

	public List<Wtinfo> getWtinfo(int wfid) {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.mysql).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Wtinfo wtinfo = new Wtinfo();
		wtinfo.setWfid(wfid);
		List<Wtinfo> list = mapper.selectWtinfo(wtinfo);
		return list;
	}

	/*
	 * 锟斤拷取runlog code list<code>
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
	 * 锟斤拷取localdb runlogcode锟斤拷锟斤拷锟轿拷锟斤拷菁锟�
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
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟�
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
	 * 锟斤拷取localdb Towerweatherheightmap锟斤拷锟斤拷维锟斤拷锟捷硷拷
	 */
	private List<Towerweatherheightmap> getTowerweatherheightmap() {
		SqlSession sqlSession = DBFactory.getSqlSessionFactory(DBEnvironment.localdb).openSession();
		SuperMapper mapper = sqlSession.getMapper(SuperMapper.class);
		Towerweatherheightmap towerweatherheightmap = new Towerweatherheightmap();
		List<Towerweatherheightmap> list = mapper.selectTowerweatherheightmap(towerweatherheightmap);
		return list;
	}

	/*
	 * 锟斤拷锟斤拷前锟矫碉拷GWSOCKET锟斤拷锟斤拷锟饺★拷锟接︼拷锟絚ompath
	 */
	protected String getCompathOnCmdname() {
		String compath = GlobalSettings.getProperty(cmdname);
		String compathString = null;
		if (null != compath) {
			compathString = compath;
		} else {
			logger.info("锟睫凤拷锟斤拷应锟斤拷指锟筋： " + cmdname);
		}
		return compathString;
	}

	/**
	 * 
	 * 锟斤拷锟斤拷col_1锟斤拷锟斤拷锟斤拷锟斤拷DynamicValue 1 FIXED 锟教讹拷值锟斤拷col_2 2 FIXBOOL
	 * 锟斤拷锟斤拷锟斤拷锟� ranBoolean() 3 DYNAMIC 锟斤拷态锟斤拷锟斤拷 4 FAULTMAIN 锟斤拷锟斤拷锟斤拷 5
	 * STATUS 锟斤拷锟阶刺� 6 YEAR 锟斤拷 7 MONTH 锟斤拷 8 DAY 锟斤拷 9 HOUR 时 10 MINUTE 锟斤拷 11
	 * SECOND 锟斤拷 12 RANDOM 锟斤拷锟斤拷锟� ranDouble() 13 TOTAL 遥锟斤拷锟斤拷 14 STOPMODE
	 * 停锟斤拷模式锟斤拷/状态模式锟斤拷 15 LIMITMODE 锟睫癸拷锟斤拷模式锟斤拷 16 ALARM 锟斤拷锟斤拷 17 FAULT
	 * 锟斤拷锟斤拷锟斤拷
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

		// 锟斤拷时锟斤拷值 ranCoin()
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
	 * 锟斤拷锟斤拷锟斤拷锟斤拷址锟斤拷锟斤拷姆锟斤拷锟�
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
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷停锟轿伙拷锟絤ax锟斤拷min之锟斤拷姆锟斤拷锟�
	 * 锟斤拷锟斤拷(min,max)锟斤拷锟斤拷锟叫碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷max
	 */
	public Integer ranInteger(int min, int max) {
		Random random = new Random();
		int bd = random.nextInt(max - min) + min;
		return bd;
	}

	/**
	 * 锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷牟锟斤拷锟街碉拷姆锟斤拷锟�
	 */
	public boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷锟�0锟斤拷1
	 */
	public int ranCoin() {
		Random rand = new Random();
		return rand.nextInt(2);
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷龋锟轿伙拷锟絤ax锟斤拷min之锟斤拷姆锟斤拷锟�
	 */
	public String ranFloat(int min, int max) {
		double bt = min + ((max - min) * new Random().nextFloat());
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(bt).toString();
	}

}
