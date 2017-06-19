package org.gradle.needle.modbus;

import org.apache.log4j.Logger;
import org.gradle.needle.engine.DataDefined;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.procimg.SimpleRegister;

public class ModbusTcpSlave {

	ModbusTCPListener listener = null;
	SimpleProcessImage spi = null;
	int port;
	Logger logger = Logger.getLogger(ModbusTcpSlave.class.getName());

	public ModbusTcpSlave(int port) {
		this.port = port;
	}

	/*
	 * ¿ªÆô¼àÌý
	 */
	public void startTcpListenerThreadPool(int size, int unitId) {
		try {
			spi = new SimpleProcessImage();
			ModbusCoupler.getReference().setProcessImage(spi);
			ModbusCoupler.getReference().setMaster(false);
			ModbusCoupler.getReference().setUnitID(unitId);
			listener = new ModbusTCPListener(size);
			listener.setPort(port);
			listener.start();
			logger.info("ModbusTcpSlave¿ªÆô¼àÌý: " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ¹Ø±Õ¼àÌý
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
			spi.addDigitalIn(new SimpleDigitalIn(new DataDefined().ranBoolean()));
			i++;
		}
	}

	/*
	 * send DO
	 */
	public void sendDevDO(int len) {
		int i = 0;
		while (i < len) {
			spi.addDigitalOut(new SimpleDigitalOut(new DataDefined().ranBoolean()));
			i++;
		}
	}

	/*
	 * InputRegister
	 */
	public void sendDevIR(int len) {
		int i = 0;
		while (i < len) {
			spi.addInputRegister(new SimpleInputRegister(new DataDefined().ranInteger(20, 70)));
			i++;
		}
	}

	/*
	 * Register
	 */
	public void sendDevRI(int len) {
		int i = 0;
		while (i < len) {
			spi.addRegister(new SimpleRegister(new DataDefined().ranInteger(20, 70)));
			i++;
		}
	}
}
