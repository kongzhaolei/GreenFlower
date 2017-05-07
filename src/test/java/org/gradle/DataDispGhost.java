package org.gradle;

import org.gradle.needle.client.TCPDataClient;
import org.gradle.needle.util.VTimer;

public class DataDispGhost {

	private static String host = "10.68.100.61";
	private static int port = 8080;

	public static void main(String[] args) {
		VTimer.timerStart();
		new TCPDataClient(host, port).GeneratorStart();
	}
}
