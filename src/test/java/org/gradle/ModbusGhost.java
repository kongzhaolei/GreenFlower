package org.gradle;

import org.gradle.needle.modbus.ModbusTcpSlave;

public class ModbusGhost {

	public static void main(String[] args) {
		
		ModbusTcpSlave slave =  new ModbusTcpSlave(502);
		slave.startTcpListenerThreadPool(1, 1);
		slave.sendDevIR(24);
	}

}
