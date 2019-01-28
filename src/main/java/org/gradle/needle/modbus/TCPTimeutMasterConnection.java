//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
package org.gradle.needle.modbus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransport;
import net.wimpi.modbus.io.ModbusTransport;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 * Class that implements a TCPMasterConnection.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class TCPTimeutMasterConnection extends TCPMasterConnection {

	public TCPTimeutMasterConnection(InetAddress adr) {
		super(adr);
		this.m_tAddress = adr;
		// TODO Auto-generated constructor stub
	}

	// instance attributes
	private Socket m_tSocket;
	private int m_soTimeout = Modbus.DEFAULT_TIMEOUT;
	private boolean m_tConnected;

	private InetAddress m_tAddress;
	private int m_tPort = Modbus.DEFAULT_PORT;

	// private int m_Retries = Modbus.DEFAULT_RETRIES;
	private ModbusTCPTransport m_tModbusTransport;

	@Override
	public synchronized void connect() throws Exception {
		if (!m_tConnected) {
			if (Modbus.debug)
				System.out.println("connect()");
			InetSocketAddress isa=new InetSocketAddress(m_tAddress, m_tPort);
			m_tSocket = new Socket();
			m_tSocket.connect(isa, 5000);
			setTimeout(10000);
			prepareTransport();
			m_tConnected = true;
		}
	}// connect

	@Override
	public void close() {
		if (m_tConnected) {
			try {
				m_tModbusTransport.close();
			} catch (IOException ex) {
				if (Modbus.debug)
					System.out.println("close()");
			}
			m_tConnected = false;
		}
	}// close

	@Override
	public ModbusTransport getModbusTransport() {
		return m_tModbusTransport;
	}// getModbusTransport

	private void prepareTransport() throws IOException {
		if (m_tModbusTransport == null) {
			m_tModbusTransport = new ModbusTCPTransport(m_tSocket);
		} else {
			m_tModbusTransport.setSocket(m_tSocket);
		}
	}// prepareIO

	@Override
	public int getTimeout() {
		return m_soTimeout;
	}// getTimeout

	@Override
	public void setTimeout(int timeout) {
		m_soTimeout = timeout;
		try {
			m_tSocket.setSoTimeout(m_soTimeout);
		} catch (IOException ex) {
			// handle?
		}
	}// setTimeout

	@Override
	public int getPort() {
		return m_tPort;
	}// getPort

	@Override
	public void setPort(int port) {
		m_tPort = port;
	}// setPort

	@Override
	public InetAddress getAddress() {
		return m_tAddress;
	}// getAddress

	@Override
	public void setAddress(InetAddress adr) {
		m_tAddress = adr;
	}// setAddress

	@Override
	public boolean isConnected() {
		return m_tConnected;
	}// isConnected

}// class TCPMasterConnection
