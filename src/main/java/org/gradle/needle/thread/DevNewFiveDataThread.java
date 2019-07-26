package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;

public class DevNewFiveDataThread implements Runnable{
	private Logger logger = Logger.getLogger(DevNewFiveDataThread.class.getName());
	
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewDevFiveData();
				Thread.sleep(300000);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
