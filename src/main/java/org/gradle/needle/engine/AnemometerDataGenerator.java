package org.gradle.needle.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.gradle.needle.dto.GlobalSettings;

import org.apache.log4j.Logger;

public class AnemometerDataGenerator {

	private DataDefined dataDefined;
	private File file;
	private Calendar calendar;
	private String longtitude;
	private String latitude;
	private static Logger logger = Logger.getLogger(AnemometerDataGenerator.class.getName());

	public AnemometerDataGenerator(File file) {
		this.file = file;
		dataDefined = new DataDefined(Integer.parseInt(GlobalSettings.getProperty("protocolid_cft")));
	}
	
	//设置经度
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	
	//设置纬度
	public void setlatitude(String latitude) {
		this.latitude = latitude;
	}

	public String genAnemometerDataEngine() {
		String puwspd = dataDefined.ranFloat(-5, 5);
		String pvwspd = dataDefined.ranFloat(-4, 14);
		String ptemp = dataDefined.ranFloat(20, 33);	
		String phum = dataDefined.ranFloat(8, 100);
		String ppres = dataDefined.ranFloat(840, 855);
		String pradiantflux = dataDefined.ranFloat(0, 1007);
		String psurfacetemperature = dataDefined.ranFloat(10, 47);
		String pcloudamount = dataDefined.ranFloat(0, 1);
		String pwdir = dataDefined.ranFloat(0, 360);
		String pwspd = dataDefined.ranFloat(0, 33);
		String airdensity = dataDefined.ranFloat(0, 2);
		StringBuilder data = new StringBuilder().append(puwspd + "\t").append(pvwspd + "\t").append(ptemp + "\t")
				.append(phum + "\t").append(ppres + "\t").append(pradiantflux + "\t").append(psurfacetemperature + "\t")
				.append(pcloudamount + "\t").append(pwdir + "\t").append(pwspd + "\t").append(airdensity + "\t");
		return data.toString() + "\n";
	}

	/*
	 * 一般每个文件包括当前5天数据
	 * 
	 * @ 00文件：0000-0600
	 * 
	 * @ 12文件：1200-1800
	 * 
	 * @duration 天数
	 */
	public void genAnemometerFiles(int duration) throws IOException {
		file = new File(file.getAbsolutePath() + "/" + anemometerFileNameLogic());
		int timeGranularity = 96; // 时间粒度
		FileWriter fw = new FileWriter(file);
		try {
			for (int ontoday = -1; ontoday < duration; ontoday++) {
				for (String time : anemometerTimeGranularity(timeGranularity)) {
					for (int height : dataDefined.getTowerHeight()) {
						StringBuilder line = new StringBuilder(longtitude + "\t").append(latitude + "\t")
								.append(Integer.toString(height) + "\t").append(formatAnotherday(ontoday) + "\t")
								.append(time + "\t").append(genAnemometerDataEngine());
						fw.write(line.toString());
						fw.flush();
						logger.info(line.toString());
					}
				}
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 气象时间粒度 每日定时触发两次：02:50，14:50 96个时间点
	 */
	private List<String> anemometerTimeGranularity(int timeGranularity) {
		String amtime = "0000";
		String pmtime = "1200";
		List<String> timelist = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		try {
			while (timelist.size() < timeGranularity) {
				if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
					timelist.add(amtime);
					amtime = sdf.format(sdf.parse(amtime).getTime() + 900000);
				} else {
					timelist.add(pmtime);
					pmtime = sdf.format(sdf.parse(pmtime).getTime() + 900000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timelist;
	}

	/*
	 * 生成当前天的气象文件名
	 */
	private String anemometerFileNameLogic() {
		String filename = "";
		calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
			filename = formatAnotherday(-1) + "12.txt";
		} else {
			filename = formatAnotherday(-1) + "00.txt";
		}
		return filename;
	}

	/*
	 * 计算距离当前天第n天的日期，返回格式yyyyMMdd n = -1 返回前一天 n = 1 返回第二天
	 */
	private String formatAnotherday(int n) {
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, n);
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		return dFormat.format(calendar.getTime());
	}
}
