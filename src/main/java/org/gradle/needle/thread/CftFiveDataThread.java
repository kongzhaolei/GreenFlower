package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CftFiveDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendCftFiveData();;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
