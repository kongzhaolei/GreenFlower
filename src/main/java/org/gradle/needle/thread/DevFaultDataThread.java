package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevFaultDataThread implements Runnable{
	
	private Logger logger = Logger.getLogger(DevFaultDataThread.class.getName());

	@Override
	public void run() {
		while (UDPDataClient.is_multicast){
			try {
				Thread.sleep(600000);
				UDPDataClient.sendDevFaultData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}	
	}
}
