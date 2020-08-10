package org.gradle;

import org.apache.log4j.Logger;
import org.gradle.needle.config.GlobalSettings;
import org.gradle.needle.engine.DataHub;
import org.gradle.needle.modbus.ModbusTcpSlave;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.thread.ModbusOneDataThread;

public class ModbusGhost {
	private static String wpid = GlobalSettings.getProperty("wpid");
	private static String host = GlobalSettings.getProperty("host");
	private static int port = Integer.parseInt(GlobalSettings.getProperty("port"));
	private static String sdate = GlobalSettings.getProperty("sdate");
	private static int count = 20;
	private static int ref = 0;
	private static Logger logger = Logger.getLogger(ModbusGhost.class.getName());

	public static void main(String[] args) {
		DataHub dh = new DataHub();
		try {
			ModbusTcpSlave fjslave = new ModbusTcpSlave(host, port);
			fjslave.startTcpListenerThreadPool(12, 1);
			fjslave.initRegister(dh.getWtinfo(wpid).size() * count);
			for (Wtinfo wtinfo : dh.getWtinfo(wpid)) {
				new Thread(new ModbusOneDataThread(fjslave,wtinfo,sdate,ref)).start();
				Thread.sleep(3000);
				ref = ref + count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}