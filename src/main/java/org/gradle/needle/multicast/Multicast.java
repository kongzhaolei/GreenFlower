package org.gradle.needle.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast {
	private MulticastSocket multicastSocket;
	private InetAddress mcastaddr;

	public Multicast(String ip, int port) throws IOException {
		this.multicastSocket = new MulticastSocket(port);
		this.mcastaddr = InetAddress.getByName(ip);
		this.multicastSocket.joinGroup(this.mcastaddr);
	}

	public Multicast(String ip, int port, String inf) throws IOException {
		this.multicastSocket = new MulticastSocket(port);
		this.multicastSocket.setInterface(InetAddress.getByName(inf));
		this.mcastaddr = InetAddress.getByName(ip);
		this.multicastSocket.joinGroup(this.mcastaddr);
	}

	public void send(String message) throws IOException {
		byte[] buffer = message.getBytes();
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, this.mcastaddr,
				this.multicastSocket.getLocalPort());
		this.multicastSocket.send(datagramPacket);
	}

	public String receive() throws IOException {
		String message = null;
		byte[] buffer = new byte[32768];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, 32768);
		this.multicastSocket.receive(datagramPacket);
		message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

		return message;
	}

	public String receive(int length) throws IOException {
		String message = null;
		byte[] buffer = new byte[length];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, length);
		this.multicastSocket.receive(datagramPacket);
		message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

		return message;
	}

	public DatagramPacket getData() throws IOException {
		byte[] buffer = new byte[32768];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, 32768);
		this.multicastSocket.receive(datagramPacket);

		return datagramPacket;
	}

	public void close() throws IOException {
		this.multicastSocket.leaveGroup(this.mcastaddr);
		this.multicastSocket.close();
	}

}
