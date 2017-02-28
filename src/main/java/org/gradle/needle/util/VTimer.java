package org.gradle.needle.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.gradle.needle.mapper.DataDefined;
import org.gradle.needle.mapper.GlobalSettings;

public class VTimer {
	
	private static int list_n = -1;
	private static int protocolid = Integer.parseInt(GlobalSettings
			.getProperty("protocolid"));
	
	
	public static int getNum() {
		return list_n;
	}
	
	/**
	 * 模式字定时器，以停机模式字为例
	 */
	public static void timerStart() {
		final long interval = 60000;
		VTimer timer = new VTimer();
		final int size = new DataDefined(protocolid).getStopModeWordIecValueList()
				.size();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				list_n++;
				if (list_n > size) {
					list_n = 0;
				}
				System.out.println("fuck everything " + list_n);
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}

}
