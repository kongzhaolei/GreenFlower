package org.gradle;

import org.apache.log4j.Logger;
import org.gradle.needle.engine.DataHub;
import org.gradle.needle.modbus.ModbusTcpSlave;
import org.gradle.needle.model.WtOneData;

public class ModbusGhost {
	private static int wtid = 654327001;
	private static String time = "2017-07-30";
	private static int count = 20;
	private static Logger logger = Logger.getLogger(ModbusGhost.class.getName());

	public static void main(String[] args) {
		DataHub dh = new DataHub();
		try {
			ModbusTcpSlave fjslave = new ModbusTcpSlave("10.68.12.40", 502);
			// ModbusTcpSlave cftslave = new ModbusTcpSlave("10.68.12.40", 503);
			fjslave.startTcpListenerThreadPool(10, 1);
			// cftslave.startTcpListenerThreadPool(10, 1);
			fjslave.initRegister(count);
			for (WtOneData onedata : dh.getWtonedata(wtid, time)) {
					fjslave.updateDevRI03(0, onedata.getWindSpeed().intValue());
					fjslave.updateDevRI03(2, dh.ranInteger(-80, 80));
					fjslave.updateDevRI03(4, dh.ranInteger(-20, 40));
					fjslave.updateDevRI03(6, onedata.getRealPower().intValue());
					fjslave.updateDevRI03(8, dh.ranInteger(5000, 8000));
					fjslave.updateDevRI03(10, dh.ranInteger(80000, 90000));
					fjslave.updateDevRI03(12, dh.ranInteger(-80, 80));
					fjslave.updateDevRI03(14, dh.ranInteger(-100, 100));
					fjslave.updateDevRI03(16, onedata.getWtStatus());	
					fjslave.updateDevRI03(18, onedata.getlimitStatus());				
					Thread.sleep(60000);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
