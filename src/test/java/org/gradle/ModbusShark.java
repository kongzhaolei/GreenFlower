package org.gradle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.gradle.needle.modbus.ModbusTcpMaster;
import org.gradle.needle.util.ByteConvertUtils;

public class ModbusShark {
	private static Logger logger = Logger.getLogger(ModbusShark.class.getName());

	public static void main(String[] args) {
		try {
			while (true) {
				String dd = ModbusTcpMaster.readByTCP("10.68.12.54", 503, "04", 0, 4, 1);
				logger.info(dd);
				String[] dds = StringUtils.split(dd, "-");
				float[] r = ByteConvertUtils.getFloat(dds, true);
				for (float f : r) {
					logger.info(f);
				}
				Thread.sleep(60000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


