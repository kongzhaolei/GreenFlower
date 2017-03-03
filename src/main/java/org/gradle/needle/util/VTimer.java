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
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));
    private static Logger logger = Logger.getLogger(VTimer.class.getName());

	/*
	 * 停机模式字序列号
	 */
	public static int getStopNum() {
		return stop_list_n;
	}

	/*
	 * 限功率模式字序列号
	 */
	public static int getLimitNum() {
		return limit_list_n;
	}
	
	/**
	 * 模式字定时器，停机模式字为例
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
				if (stop_list_n > stop_size-1) {
					stop_list_n = 0;
				}
				if (limit_list_n > limit_size-1) {
					limit_list_n = 0;
				}
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}

}
