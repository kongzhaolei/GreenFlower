package org.gradle;

import org.gradle.needle.client.UDPDataClient;
import org.gradle.needle.util.VTimer;

public class Wman {
	
	private static String multicastip = "224.1.1.15";
	private static int multicastPort = 9678;

	public static void main(String[] args) {
		VTimer.keyWorkTimer();
		new UDPDataClient(multicastip, multicastPort).GeneratorStart();
	}
}
