package org.gradle;

import org.gradle.needle.client.RealDataGeneratorClient;
import org.gradle.needle.util.VTimer;

public class SoftAdapterGhost {
	
	private static String multicastip = "224.1.1.15";
	private static int multicastPort = 8769;

	public static void main(String[] args) {
		VTimer.timerStart();
		new RealDataGeneratorClient(multicastip, multicastPort).GeneratorStart();
	}
}
