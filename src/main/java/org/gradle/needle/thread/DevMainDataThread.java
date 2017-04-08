package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevMainDataThread implements Runnable{
	
	private static Logger logger = Logger.getLogger(DevMainDataThread.class.getName());

	@Override
	public void run() {
		while(UDPDataClient.is_multicast){
			try {
				UDPDataClient.sendDevMainData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
