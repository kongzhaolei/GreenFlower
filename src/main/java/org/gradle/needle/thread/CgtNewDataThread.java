package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class CgtNewDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewCgtData();
				Thread.sleep(300000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
