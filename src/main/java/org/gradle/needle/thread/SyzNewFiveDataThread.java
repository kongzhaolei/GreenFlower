package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class SyzNewFiveDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewSYZFiveData();
				Thread.sleep(300000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
