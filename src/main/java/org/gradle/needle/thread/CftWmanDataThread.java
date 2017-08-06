package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.UDPDataClient;

public class CftWmanDataThread implements Runnable{
	
	private static Logger logger = Logger.getLogger(CftWmanDataThread.class.getName());

	@Override
	public void run() {
		while(UDPDataClient.is_multicast){
			try {
				UDPDataClient.sendCftWmanData();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

}
