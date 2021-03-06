package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevAlarmDataThread implements Runnable{
	
	private Logger logger = Logger.getLogger(DevAlarmDataThread.class.getName());

	@Override
	public void run() {
		try {
			while (UDPDataClient.is_multicast) {
				Thread.sleep(70000);
				UDPDataClient.sendDevAlarmData();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}
}
