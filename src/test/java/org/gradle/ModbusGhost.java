package org.gradle;

import org.gradle.needle.modbus.ModbusTcpSlave;

public class ModbusGhost {

	public static void main(String[] args) {
		
		ModbusTcpSlave slave =  new ModbusTcpSlave(502);
		slave.startTcpListenerThreadPool(80, 1);
		try {
			while(true){
				slave.sendDevRI03(56);
				slave.sendDevIR04(26);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
