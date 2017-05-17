package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;

public class DevRealTimeDataThread implements Runnable {
	private Logger logger = Logger.getLogger(DevRealTimeDataThread.class.getName());

	@Override
	public void run() {
		while (true) {
			try {
				TCPDataClient.sendDevRealTimeData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

}
