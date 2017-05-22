package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.DataClient;
import org.gradle.needle.client.UDPDataClient;

public class DevWarnLogThread implements Runnable {
	private Logger logger = Logger.getLogger(DevWarnLogThread.class.getName());
	DataClient dc = new UDPDataClient();

	@Override
	public void run() {
		try {
			while (UDPDataClient.is_multicast) {
				// Thread.sleep(30000);
				dc.sendDevWarnLog();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}

	}

}
