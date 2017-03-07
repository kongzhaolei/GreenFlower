package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.RealDataGeneratorClient;

public class DevFaultDataThread implements Runnable{
	
	private Logger logger = Logger.getLogger(DevFaultDataThread.class.getName());

	@Override
	public void run() {
		while (RealDataGeneratorClient.is_multicast){
			try {
				Thread.sleep(80000);
				RealDataGeneratorClient.sendDevFaultData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}	
	}
}
