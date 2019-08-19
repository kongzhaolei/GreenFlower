package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CftNewFiveDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewCftFiveData();
				Thread.sleep(300000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
