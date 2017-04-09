package org.gradle.needle.modbus;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.procimg.SimpleRegister;

public class ModbusTcpSlave {
	
	public static void main(String[] args) {

		ModbusTCPListener listener = null;
		SimpleProcessImage spi = null;
		int port = Modbus.DEFAULT_PORT;

		try {
			if (args != null && args.length == 1) {
				port = Integer.parseInt(args[0]);
			}
			System.out.println("jModbus Modbus Slave (Server)");

			// 1. prepare a process image
			spi = new SimpleProcessImage();

			spi.addDigitalOut(new SimpleDigitalOut(true));
			spi.addDigitalOut(new SimpleDigitalOut(true));

			spi.addDigitalIn(new SimpleDigitalIn(false));
			spi.addDigitalIn(new SimpleDigitalIn(true));
			spi.addDigitalIn(new SimpleDigitalIn(false));
			spi.addDigitalIn(new SimpleDigitalIn(true));
			// allow checking LSB/MSB order
			spi.addDigitalIn(new SimpleDigitalIn(true));
			spi.addDigitalIn(new SimpleDigitalIn(true));
			spi.addDigitalIn(new SimpleDigitalIn(true));
			spi.addDigitalIn(new SimpleDigitalIn(true));

			spi.addRegister(new SimpleRegister(251));
			spi.addInputRegister(new SimpleInputRegister(45));
			spi.addInputRegister(new SimpleInputRegister(46));
 
			// 2. create the coupler holding the image
			ModbusCoupler.getReference().setProcessImage(spi);
			ModbusCoupler.getReference().setMaster(false);
			ModbusCoupler.getReference().setUnitID(1);

			// 3. create a listener with 3 threads in pool
			if (Modbus.debug)
				System.out.println("Listening...");
			listener = new ModbusTCPListener(3);
			listener.setPort(port);
			
			System.out.println("Listening to " + listener.getAddress() .getCanonicalHostName()+" on port "+port);
			
			listener.start();
			
			System.out.println("Press enter to exit");
			
			System.in.read();
			
			System.out.println("Exiting...");
			
			listener.stop();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
