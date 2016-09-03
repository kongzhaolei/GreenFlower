package org.gradle.needle.dbo;

import java.math.BigDecimal;
import java.util.Random;

public class DataDefined {
	String cmdString;
	DataEngine de = new DataEngine(cmdString);

	/*
	 * 构造方法
	 */
	public DataDefined() {

	}
	
	/*
	 * stopmode的随机刷新
	 */
	public void stopModeRefresh() {
		
	}

	/*
	 * baseData的实时刷新
	 */
	public void cacheValueRefresh() {

	}

	/*
	 * 生成随机字符串的方法
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
	 * 生成随机数位于max和min之间的方法
	 */
	public static String ranDouble(int max, int min) {
		BigDecimal db = new BigDecimal(Math.random() * max + min);
		return db.toString();
	}

	/*
	 * 生成一个随机的布尔值的方法
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

}
