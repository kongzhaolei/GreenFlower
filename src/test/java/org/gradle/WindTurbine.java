package org.gradle;

import org.gradle.needle.server.TCPDataServer;

public class WindTurbine {
	
	public static void main(String[] args) {
		new TCPDataServer("127.0.0.1", 1120).Start();
	}
}
