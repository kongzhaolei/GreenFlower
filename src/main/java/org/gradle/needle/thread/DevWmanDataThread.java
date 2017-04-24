package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class DevWmanDataThread implements Runnable{
	
	private static Logger logger = Logger.getLogger(DevWmanDataThread.class.getName());

	@Override
	public void run() {
		while(UDPDataClient.is_multicast){
			try {
				UDPDataClient.sendDevWmanData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
