package org.gradle.needle.modbus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;

public class ModbusTcpMaster {
	private static Logger logger = Logger.getLogger(ModbusTcpMaster.class.getName());

	private static ModbusRequest getRR(String func, int ref, int quantity) {
		if (func.equals("05"))// 功能码：05
			return new WriteCoilRequest(ref, true);
		else if (func.equals("01"))// 功能码：01
			return new ReadCoilsRequest(ref, quantity);
		else if (func.equals("02"))// 功能码：02
			return new ReadInputDiscretesRequest(ref, quantity);
		else if (func.equals("04"))// 功能码：04
			return new ReadInputRegistersRequest(ref, quantity);
		else if (func.equals("03"))// 功能码：03
			return new ReadMultipleRegistersRequest(ref, quantity);
		else if (func.equals("06")) {
			Register r = ModbusCoupler.getReference().getProcessImageFactory().createRegister();
			return new WriteSingleRegisterRequest(0, r);
		} else if (func.equals("16"))// 功能码：16 return new
		{
			Register rr = ModbusCoupler.getReference().getProcessImageFactory().createRegister();
			rr.setValue(420);
			return new WriteMultipleRegistersRequest(0, new Register[] { rr });
		}

		return null;

	}

	private static String getRS(ModbusResponse res) {
		String data = null;
		if (res == null) {
			logger.info("UDP请求无返回值");
			return null;
		}
		 logger.info("\nTransactionID=" + res.getTransactionID()
		 + "\nProtocolID=" + res.getProtocolID() + "\nDataLength="
		 + res.getDataLength() + "\nUnitID=" + res.getUnitID()
		 + "\nFunctionCode=" + res.getFunctionCode());
		 
		if (res instanceof ReadCoilsResponse) {
			ReadCoilsResponse t = (ReadCoilsResponse) res;
			//logger.info("\ndata=" + t.getCoils().toString());
			data = t.getCoils().toString();
		}
		if (res instanceof ReadInputDiscretesResponse) {
			ReadInputDiscretesResponse t = (ReadInputDiscretesResponse) res;
			//logger.info("\ndata=" + t.getDiscretes().toString());
			data = t.getDiscretes().toString();

		}
		if (res instanceof ReadInputRegistersResponse) {
			ReadInputRegistersResponse t = (ReadInputRegistersResponse) res;
			InputRegister[] s = t.getRegisters();
			List<Integer> valList = new ArrayList<Integer>();
			for (InputRegister ss : s) {
				valList.add(ss.getValue());
			}
			data = StringUtils.join(valList, "-");
			//logger.info("\ndata=" + data);
		}
		if (res instanceof ReadMultipleRegistersResponse) {
			ReadMultipleRegistersResponse t = (ReadMultipleRegistersResponse) res;
			Register[] rlist = t.getRegisters();
			List<Integer> valList = new ArrayList<Integer>();
			for (Register r : rlist) {
				valList.add((r.getValue()));
			}
			data = StringUtils.join(valList, "-");
			//logger.info("\ndata=" + data);
		}
		return data;
	}

	public static String readByTCP(String ip, int port, String func, int ref, int quantity, Integer unitId) {
		TCPMasterConnection con = null; // the connection
		ModbusTCPTransaction transaction = null; // the transaction
		String ress = null;
		try {
			ModbusResponse res = null; // the response
			InetAddress addr = InetAddress.getByName(ip);
			con = new TCPTimeutMasterConnection(addr);
			con.setPort(port);
			con.connect();
			transaction = new ModbusTCPTransaction(con);
			ModbusRequest request = getRR(func, ref, quantity);

			// logger.info( "req=" + request );
			if (unitId != null)
				request.setUnitID(unitId);
			transaction.setRequest(request);
			transaction.setRetries(4);
			transaction.execute();
			res = transaction.getResponse();
			ress = getRS(res);
		} catch (Exception e) {
			logger.error("e", e);
			// e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return ress;
	}
}
