package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CdqNewDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewCdqData();
				Thread.sleep(800000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
