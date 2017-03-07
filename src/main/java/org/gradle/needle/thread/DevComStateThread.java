package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.RealDataGeneratorClient;

public class DevComStateThread implements Runnable {
	
	private Logger logger  = Logger.getLogger(DevComStateThread.class.getName());

	@Override
	public void run() {
		try {
			while (RealDataGeneratorClient.is_multicast) {
				Thread.sleep(60000);
				RealDataGeneratorClient.sendDevComState();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		
	}

}
