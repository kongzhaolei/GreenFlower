package org.gradle.needle.modbus;

import java.net.InetAddress;
import org.apache.log4j.Logger;
import org.gradle.needle.engine.DataHub;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.IllegalAddressException;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.procimg.SimpleRegister;

public class ModbusTcpSlave {

	ModbusTCPListener listener = null;
	SimpleProcessImage spi = null;
	String m_address;
	int port;
	Logger logger = Logger.getLogger(ModbusTcpSlave.class.getName());

	public ModbusTcpSlave(String address, int port) {
		this.m_address = address;
		this.port = port;

	}	

	/*
	 * ��������
	 */
	public void startTcpListenerThreadPool(int poolsize, int unitId) {
		try {
			spi = new SimpleProcessImage();
			ModbusCoupler.getReference().setProcessImage(spi);
			ModbusCoupler.getReference().setMaster(false);
			ModbusCoupler.getReference().setUnitID(unitId);

			listener = new ModbusTCPListener(poolsize);
			listener.setAddress(InetAddress.getByName(m_address));
			listener.setPort(port);
			listener.start();
			logger.info("ModbusTcpSlave��������: " + m_address + ":" + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * �رռ���
	 */
	public void stopTcplistener() {
		listener.stop();
	}

	/*
	 * send DI
	 */
	public void sendDevDI(int len) {
		int i = 0;
		while (i < len) {
			spi.addDigitalIn(new SimpleDigitalIn(new DataHub().ranBoolean()));
			i++;
		}
	}

	/*
	 * send DO
	 */
	public void sendDevDO(int len) {
		int i = 0;
		while (i < len) {
			spi.addDigitalOut(new SimpleDigitalOut(new DataHub().ranBoolean()));
			i++;
		}
	}

	/*
	 * InputRegister
	 */
	public void sendDevIR04(int len) {
		int i = 0;
		while (i < len) {
			spi.addInputRegister(new SimpleInputRegister(new DataHub().ranInteger(0, 10)));
			i++;
		}
	}

	/*
	 * update Register
	 */
	public void addDevRI03(int value) {
		spi.addRegister(new SimpleRegister(value));
		logger.info(spi.getRegisterCount());
	}

	public void updateDevRI03(int ref, int value) {
		//logger.info(spi.getRegisterCount());
		if (ref < 0 || ref > spi.getRegisterCount()) {
			throw new IllegalAddressException();
		} else {
			spi.setRegister(ref, new SimpleRegister(value));
		}
	}

	public void initRegister(int count) {
		for (int i = 0; i < count; i++) {
			spi.addRegister(new SimpleRegister(0));
		}
	}
}
