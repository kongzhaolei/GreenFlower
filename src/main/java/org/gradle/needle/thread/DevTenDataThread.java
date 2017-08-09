package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;

public class DevTenDataThread implements Runnable {
	private Logger logger = Logger.getLogger(DevTenDataThread.class.getName());

	@Override
	public void run() {
		while (true) {
			try {
				TCPDataClient.sendDevTenData();
				Thread.sleep(600000);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
