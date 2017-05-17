package org.gradle.needle.thread;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;

public class DevPowerCurveThread implements Runnable {
	private Logger logger = Logger.getLogger(DevPowerCurveThread.class.getName());

	@Override
	public void run() {
		while (true) {
			try {
				TCPDataClient.sendDevPowerCurve();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

}
