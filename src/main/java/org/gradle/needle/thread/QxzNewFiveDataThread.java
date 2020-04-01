package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class QxzNewFiveDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewQXZFiveData();
				Thread.sleep(300000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
