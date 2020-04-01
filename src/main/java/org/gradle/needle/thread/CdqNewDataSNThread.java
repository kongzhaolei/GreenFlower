package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CdqNewDataSNThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewCdqDataSN();
				Thread.sleep(800000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
