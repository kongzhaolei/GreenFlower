package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevStateDataThread implements Runnable {
	private Logger logger = Logger.getLogger(DevStateDataThread.class.getName());

	@Override
	public void run() {
		try {
			while (UDPDataClient.is_multicast) {
				Thread.sleep(600000);
				UDPDataClient.sendDevStateData();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}

	}

}
