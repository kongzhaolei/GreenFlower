package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CftTenDataThread implements Runnable{
	
	@Override
	public void run() {
		while (true) {
			try {
				TCPDataClient.sendCftTenData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
