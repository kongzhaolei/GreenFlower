package org.gradle.needle.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.gradle.needle.mapper.DataDefined;
import org.gradle.needle.mapper.GlobalSettings;

public class VTimer {

	private static int stop_list_n = -1;
	private static int limit_list_n = -1;
	private static int wtid_list_n = -1;
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));
    private static Logger logger = Logger.getLogger(VTimer.class.getName());

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
	
	/*
	 * ���������к�
	 */
	public static int getWtidNum() {
		return wtid_list_n;
	}

	/**
	 * ģʽ�ֶ�ʱ����ͣ��ģʽ��Ϊ��
	 */
	public static void timerStart() {
		final long interval = 60000;
		Timer timer = new Timer();
		final int stop_size = new DataDefined(protocolid).getStopModeWordIecValueList().size();
		final int limit_size = new DataDefined(protocolid).getLimitModeWordIecValueList().size();
		final int wtid_size = new DataDefined(protocolid).getWtidList().size();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				stop_list_n++;
				limit_list_n++;
				wtid_list_n++;
				if (stop_list_n > stop_size) {
					stop_list_n = 0;
				}
				if (limit_list_n > limit_size) {
					limit_list_n = 0;
				}
				if (wtid_list_n > wtid_size) {
					wtid_list_n = 0;
				}
				logger.info("��ǰͣ��ģʽ�����кţ� " + stop_list_n);
                logger.info("��ǰ�޹���ģʽ�����кţ� " + limit_list_n);
                logger.info("��ǰ�����ŵ����кţ�" + wtid_list_n);
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}

}
