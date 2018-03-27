package org.gradle.needle.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.gradle.needle.dto.GlobalSettings;
import org.gradle.needle.engine.DataDefined;

public class VTimer {

	private static int stop_list_n = -1;
	private static int limit_list_n = -1;
	private static int status_list_n = -1;
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid_wt"));
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
	
	/*
	 * 风机状态字序列号
	 */
	public static int getStatusNum() {
		return status_list_n;
	}
	
	/**
	 * 定时器
	 */
	public static void keyWorkTimer() {
		final long interval = Long.parseLong(GlobalSettings.getProperty("vtime"));
		Timer timer = new Timer();
		final int stop_size = new DataDefined(protocolid).getStopModeWordList().size();
		final int limit_size = new DataDefined(protocolid).getLimitModeWordList().size();
		final int status_size = new DataDefined(protocolid).getStatusList().size();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				stop_list_n++;
				limit_list_n++;
				status_list_n++;
				if (stop_list_n > stop_size-1) {
					stop_list_n = 0;
				}
				if (limit_list_n > limit_size-1) {
					limit_list_n = 0;
				}
				if (status_list_n > status_size-1) {
					status_list_n = 0;
				}
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}

}
