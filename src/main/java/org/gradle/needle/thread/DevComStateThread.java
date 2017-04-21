package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevComStateThread implements Runnable {
	
	private Logger logger  = Logger.getLogger(DevComStateThread.class.getName());

	@Override
	public void run() {
		try {
			while (UDPDataClient.is_multicast) {
				Thread.sleep(60000);
				UDPDataClient.sendDevComState();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		
	}

}
