package org.gradle;

import org.apache.log4j.Logger;
import org.gradle.needle.engine.DataHub;
import org.gradle.needle.modbus.ModbusTcpSlave;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.thread.ModbusOneDataThread;

public class ModbusGhost {
	private static int wfid = 654125;
	private static String time = "2017-11-12";
	private static int count = 20;
	private static int ref = 0;
	private static Logger logger = Logger.getLogger(ModbusGhost.class.getName());

	public static void main(String[] args) {
		DataHub dh = new DataHub();
		try {
			ModbusTcpSlave fjslave = new ModbusTcpSlave("10.200.50.136", 502);
			fjslave.startTcpListenerThreadPool(10, 1);
			fjslave.initRegister(dh.getWtinfo(wfid).size() * count);
			for (Wtinfo wtinfo : dh.getWtinfo(wfid)) {
				new Thread(new ModbusOneDataThread(fjslave,wtinfo,time,ref)).start();
				ref = ref + count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}