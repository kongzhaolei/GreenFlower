package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class DqNewDataSNThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewDqDataSN();
				Thread.sleep(3600000);;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
