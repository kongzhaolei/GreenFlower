package org.gradle.needle.thread;

import org.gradle.needle.client.TCPDataClient;

public class DqNewDataThread implements Runnable{
	@Override
	public void run() {
		while(true){
			try {
				TCPDataClient.sendNewDqData();
				Thread.sleep(3600000);;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
