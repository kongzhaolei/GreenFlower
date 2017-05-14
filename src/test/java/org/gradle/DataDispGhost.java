package org.gradle;

import org.gradle.needle.client.TCPDataClient;
import org.gradle.needle.util.VTimer;

public class DataDispGhost {

	private static String host = "10.1.3.151";
	private static int port = 8804;

	public static void main(String[] args) {
		VTimer.timerStart();
		new TCPDataClient(host, port);
		TCPDataClient.sendDevTenData();
		//new TCPDataClient(host, port).GeneratorStart();
		
	}
}
