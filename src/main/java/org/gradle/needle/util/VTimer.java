package org.gradle.needle.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.gradle.needle.mapper.DataDefined;
import org.gradle.needle.mapper.GlobalSettings;

public class VTimer {

	private static int stop_list_n = -1;
	private static int limit_list_n = -1;
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));

	/*
	 * ͣ��ģʽ�����к�
	 */
	public static int getStopNum() {
		return stop_list_n;
	}

	/*
	 * �޹���ģʽ�����к�
	 */
	public static int getLimitNum() {
		return limit_list_n;
	}

	/**
	 * ģʽ�ֶ�ʱ����ͣ��ģʽ��Ϊ��
	 */
	public static void timerStart() {
		final long interval = 60000;
		Timer timer = new Timer();
		final int stop_size = new DataDefined(protocolid).getStopModeWordIecValueList().size();
		final int limit_size = new DataDefined(protocolid).getLimitModeWordIecValueList().size();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				stop_list_n++;
				limit_list_n++;
				if (stop_list_n > stop_size) {
					stop_list_n = 0;
				}
				if (limit_list_n > limit_size) {
					limit_list_n = 0;
				}
				System.out.println("��ǰͣ��ģʽ�����кţ� " + stop_list_n);
                System.out.println("��ǰ�޹���ģʽ�����кţ� " + limit_list_n);
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}

}
