package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CgtNewDataSNThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewCgtDataSN();
				Thread.sleep(300000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
