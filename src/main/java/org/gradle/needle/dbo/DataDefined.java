package org.gradle.needle.dbo;

import java.math.BigDecimal;
import java.util.Random;

public class DataDefined {
	String cmdString;
	DataEngine de = new DataEngine(cmdString);

	/*
	 * ���췽��
	 */
	public DataDefined() {

	}
	
	/*
	 * stopmode�����ˢ��
	 */
	public void stopModeRefresh() {
		
	}

	/*
	 * baseData��ʵʱˢ��
	 */
	public void cacheValueRefresh() {

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
	public static String ranDouble(int max, int min) {
		BigDecimal db = new BigDecimal(Math.random() * max + min);
		return db.toString();
	}

	/*
	 * ����һ������Ĳ���ֵ�ķ���
	 */
	public static boolean ranBoolean() {
		Random x = new Random();
		return x.nextBoolean();
	}

}
