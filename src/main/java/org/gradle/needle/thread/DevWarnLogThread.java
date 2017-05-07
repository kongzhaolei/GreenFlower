package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevWarnLogThread implements Runnable{
	private Logger logger = Logger.getLogger(DevWarnLogThread.class.getName());

	@Override
	public void run() {
		try {
			while (UDPDataClient.is_multicast) {
				//Thread.sleep(30000);
				UDPDataClient.sendDevWarnLog();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}

	}

}
