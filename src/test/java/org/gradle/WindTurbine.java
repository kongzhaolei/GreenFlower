package org.gradle;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.gradle.needle.server.TCPDataServer;

public class WindTurbine {
	
	public static void main(String[] args) throws UnknownHostException {
		new TCPDataServer(InetAddress.getLocalHost().getHostAddress(), 1120).Start();
	}
}
