package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.RealDataGeneratorClient;

public class DevMainDataThread implements Runnable{
	
	private static Logger logger = Logger.getLogger(DevMainDataThread.class.getName());

	@Override
	public void run() {
		while(RealDataGeneratorClient.is_multicast){
			try {
				RealDataGeneratorClient.sendDevMainData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
