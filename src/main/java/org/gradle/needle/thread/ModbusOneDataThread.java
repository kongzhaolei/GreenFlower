/**
 * 
 */
package org.gradle.needle.thread;

import java.util.List;

import org.apache.log4j.Logger;
import org.gradle.needle.engine.DataHub;
import org.gradle.needle.modbus.ModbusTcpSlave;
import org.gradle.needle.model.WtOneData;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.util.ByteConvertUtils;

/**
 * @author needle
 *
 */
public class ModbusOneDataThread implements Runnable {
	DataHub dh = new DataHub();
	private Wtinfo wtinfo;
	private ModbusTcpSlave fjslave;
	private String time;
	private int ref;

	private Logger logger = Logger.getLogger(DevFiveDataThread.class.getName());

	public ModbusOneDataThread(ModbusTcpSlave fjslave, Wtinfo wtinfo, String time, int ref) {
		this.fjslave = fjslave;
		this.wtinfo = wtinfo;
		this.time = time;
		this.ref = ref;
	}

	@Override
	public void run() {
		while (true) {
			try {
				List<WtOneData> wDatas = dh.getWtonedata(wtinfo.getWtid(), time);
				if (null != wDatas && wDatas.size() > 0) {
					for (WtOneData onedata : wDatas) {
						fjslave.updateDevRI03(ref + 0, ByteConvertUtils.getShort(onedata.getWindSpeed().floatValue())[0]);
						fjslave.updateDevRI03(ref + 1, ByteConvertUtils.getShort(onedata.getWindSpeed().floatValue())[1]);
						fjslave.updateDevRI03(ref + 2, ByteConvertUtils.getShort(dh.ranInteger(0, 80).floatValue())[0]);
						fjslave.updateDevRI03(ref + 3, ByteConvertUtils.getShort(dh.ranInteger(0, 80).floatValue())[1]);
						fjslave.updateDevRI03(ref + 4, ByteConvertUtils.getShort(onedata.getEnvitemp().floatValue())[0]);
						fjslave.updateDevRI03(ref + 5, ByteConvertUtils.getShort(onedata.getEnvitemp().floatValue())[1]);
						fjslave.updateDevRI03(ref + 6, ByteConvertUtils.getShort(onedata.getRealPower().floatValue())[0]);
						fjslave.updateDevRI03(ref + 7, ByteConvertUtils.getShort(onedata.getRealPower().floatValue())[1]);
						fjslave.updateDevRI03(ref + 8, ByteConvertUtils.getShort(dh.ranInteger(5000, 8000).floatValue())[0]);
						fjslave.updateDevRI03(ref + 9, ByteConvertUtils.getShort(dh.ranInteger(5000, 8000).floatValue())[1]);
						fjslave.updateDevRI03(ref + 10, ByteConvertUtils.getShort(onedata.getEndelec().doubleValue())[0]);
						fjslave.updateDevRI03(ref + 11, ByteConvertUtils.getShort(onedata.getEndelec().doubleValue())[1]);
						fjslave.updateDevRI03(ref + 12, ByteConvertUtils.getShort(dh.ranInteger(0, 80).floatValue())[0]);
						fjslave.updateDevRI03(ref + 13, ByteConvertUtils.getShort(dh.ranInteger(0, 80).floatValue())[1]);
						fjslave.updateDevRI03(ref + 14, ByteConvertUtils.getShort(dh.ranInteger(0, 100).floatValue())[0]);
						fjslave.updateDevRI03(ref + 15, ByteConvertUtils.getShort(dh.ranInteger(0, 100).floatValue())[1]);
						fjslave.updateDevRI03(ref + 16, ByteConvertUtils.getShort(onedata.getWtStatus().floatValue())[0]);
						fjslave.updateDevRI03(ref + 17, ByteConvertUtils.getShort(onedata.getWtStatus().floatValue())[1]);
						fjslave.updateDevRI03(ref + 18, ByteConvertUtils.getShort(onedata.getlimitStatus().floatValue())[0]);
						fjslave.updateDevRI03(ref + 19, ByteConvertUtils.getShort(onedata.getlimitStatus().floatValue())[1]);
						
						Thread.sleep(55000);
					}
				}
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}
}
