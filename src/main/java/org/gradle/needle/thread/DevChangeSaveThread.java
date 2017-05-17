package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;

public class DevChangeSaveThread implements Runnable {
	private Logger logger = Logger.getLogger(DevChangeSaveThread.class.getName());

	@Override
	public void run() {
		while (true) {
			try {
				TCPDataClient.sendDevChangeSave();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
