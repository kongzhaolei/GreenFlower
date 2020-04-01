package org.gradle.needle.client;

import org.apache.log4j.Logger;
import org.gradle.needle.config.GlobalSettings;
import org.gradle.needle.engine.DataHub;
import org.gradle.needle.engine.DeviceDataGenerator;
import org.gradle.needle.model.Wtinfo;
import org.gradle.needle.thread.CftFiveDataThread;
import org.gradle.needle.thread.CftNewFiveDataThread;
import org.gradle.needle.thread.CftTenDataThread;
import org.gradle.needle.thread.CgtNewDataSNThread;
import org.gradle.needle.thread.DevChangeSaveThread;
import org.gradle.needle.thread.DevFiveDataThread;
import org.gradle.needle.thread.DevNewFiveDataThread;
import org.gradle.needle.thread.DevPowerCurveThread;
import org.gradle.needle.thread.DevRealTimeDataThread;
import org.gradle.needle.thread.DevTenDataThread;
import org.gradle.needle.thread.DqNewDataSNThread;
import org.gradle.needle.thread.NbqNewFiveDataThread;
import org.gradle.needle.thread.QxzNewFiveDataThread;
import org.gradle.needle.thread.SyzNewFiveDataThread;
import org.gradle.needle.thread.CdqNewDataSNThread;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TCPDataClient implements DataClient {

	private static String HOST;
	private static int PORT;
	private static int protocolid_wt = Integer.parseInt(GlobalSettings.getProperty("protocolid_wt"));
	private static int protocolid_cft = Integer.parseInt(GlobalSettings.getProperty("protocolid_cft"));
	private static int protocolid_syz = Integer.parseInt(GlobalSettings.getProperty("protocolid_syz"));
	private static Logger logger = Logger.getLogger(TCPDataClient.class.getName());
	private static DeviceDataGenerator dgen_wt = new DeviceDataGenerator(protocolid_wt);
	private static DeviceDataGenerator dgen_cft = new DeviceDataGenerator(protocolid_cft);
	private static DeviceDataGenerator dgen_syz = new DeviceDataGenerator(protocolid_syz);
	private static ChannelFuture future;

	public TCPDataClient(String host, int port) {
		TCPDataClient.HOST = host;
		TCPDataClient.PORT = port;
	}

	public TCPDataClient() {

	}

	/**
	 * TCP 閿熺绛规嫹閿熸枻鎷�
	 */
	public void GeneratorStart() {
		try {
			TcpConnect();
			// new Thread(new DevTenDataThread()).start();
			// new Thread(new DevFiveDataThread()).start();
			// new Thread(new CftTenDataThread()).start();
			// new Thread(new CftFiveDataThread()).start();
			// new Thread(new DevRealTimeDataThread()).start();
			// new Thread(new DevChangeSaveThread()).start();
			// new Thread(new DevPowerCurveThread()).start();
			
			// new Thread(new DevNewFiveDataThread()).start();
			// new Thread(new CftNewFiveDataThread()).start();
			new Thread(new NbqNewFiveDataThread()).start();
			new Thread(new QxzNewFiveDataThread()).start();
			new Thread(new SyzNewFiveDataThread()).start();
			
			// new Thread(new CdqNewDataSNThread()).start();
			// new Thread(new DqNewDataSNThread()).start();
			// new Thread(new CgtNewDataSNThread()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹tcp client
	 * 
	 * @param data
	 */
	public void TcpConnect() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast("decoder", new StringDecoder());
							p.addLast("encoder", new StringEncoder());
							p.addLast(new TCPDataClientHandler());
						}
					});
			future = bs.connect(HOST, PORT).sync();
			logger.info("閿熸枻鎷烽敓鏂ゆ嫹閿熸帴纰夋嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷 " + HOST + ":" + PORT);
			// future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 閫氶敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹tcp閿熸枻鎷烽敓鏂ゆ嫹
	 */
	public static void channelSend(String data) {
		try {
			future.channel().writeAndFlush(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send CftTenData
	public static void sendCftTenData() {
		try {
			for (String wtid : dgen_cft.getWtidList()) {
				channelSend(dgen_cft.genDevTenData(wtid));
				// logger.info(dgen_cft.genDevTenData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevTenData
	public static void sendDevTenData() {
		try {
			for (String wtid : dgen_wt.getWtidList()) {
				channelSend(dgen_wt.genDevTenData(wtid));
				// logger.info(dgen_wt.genDevTenData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send CftFiveData
	public static void sendCftFiveData() {
		try {
			for (String wtid : dgen_cft.getWtidList()) {
				channelSend(dgen_cft.genDevFiveData(wtid));
				// logger.info(dgen_cft.genDevFiveData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevFiveData
	public static void sendDevFiveData() {
		try {
			for (String wtid : dgen_wt.getWtidList()) {
				channelSend(dgen_wt.genDevFiveData(wtid));
				// logger.info(dgen_wt.genDevFiveData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send newDevFiveData
	public static void sendNewDevFiveData() {
		try {
			for (String wtid : dgen_wt.getWtidList()) {
				channelSend(dgen_wt.genNewDevFiveData(wtid));
				//logger.info(dgen_wt.genNewDevFiveData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// send newCftFiveData
	public static void sendNewCftFiveData() {
		try {
			for (String wtid : dgen_cft.getWindmastList()) {
				channelSend(dgen_cft.genNewCftFiveData(wtid));
				//logger.info(dgen_cft.genNewCftFiveData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// send newNBQFiveData
	public static void sendNewNBQFiveData() {
		try {
			for (String wtid : dgen_wt.getWtidList()) {
				channelSend(dgen_wt.genNewNBQFiveData(wtid));
				//logger.info(dgen_wt.genNewNBQFiveData(wtid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// send newQXZFiveData
	public static void sendNewQXZFiveData() {
		try {
			for (String wtid : dgen_cft.getWindmastList()) {
				channelSend(dgen_cft.genNewQXZFiveData(wtid));
				// logger.info(dgen_cft.genNewQXZFiveData(wtid)); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// send newSYZFiveData
		public static void sendNewSYZFiveData() {
			try {
				for (String wtid : dgen_syz.getSyzList()) {
					channelSend(dgen_syz.genNewSYZFiveData(wtid));
					// logger.info(dgen_cft.genNewSYZFiveData(wtid));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	

	// send shsn newCdqData
	public static void sendNewCdqDataSN() {
		try {
			channelSend(new DeviceDataGenerator().genNewCdqDataSN());
			// logger.info(dgen_wt.genNewCdqData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// send shsn newdqData
	public static void sendNewDqDataSN() {
		try {
			channelSend(new DeviceDataGenerator().genNewDqDataSN());
			// logger.info(dgen_wt.genNewDqData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// send shsn newcgtdata
	public static void sendNewCgtDataSN() {
		try {
			channelSend(new DeviceDataGenerator().genNewCgtDataSN());
			// logger.info(dgen_wt.genNewCgtData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevOne
	public static void sendDevOne() {
		try {
			String s = dgen_wt.genDevOne();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevRealTimeData
	public static void sendDevRealTimeData() {
		try {
			channelSend(dgen_wt.genDevRealTimeData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevChangeSave
	public static void sendDevChangeSave() {
		try {
			channelSend(dgen_wt.genDevChangeSave());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevPowerCurve
	public static void sendDevPowerCurve() {
		try {
			channelSend(dgen_wt.genDevPowerCurve());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevSedimentOne
	public static void sendDevSedimentOne() {
		try {
			String s = dgen_wt.genDevSedimentOne();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevSedimentOnedata
	public static void sendDevSedimentOneData() {
		try {
			String s = dgen_wt.genDevSedimentOneData();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevSedimentRealData
	public static void sendDevSedimentRealData() {
		try {
			String s = dgen_wt.genDevSedimentRealData();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevWarnLog

	public void sendDevWarnLog() {
		try {
			channelSend(dgen_wt.genDevWarnLog().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevFaultData
	public void sendDevFaultData() {
		try {
			channelSend(dgen_wt.genDevFaultData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevAlarmData
	public void sendDevAlarmData() {
		try {
			channelSend(dgen_wt.genDevAlarmData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevStateData
	public void sendDevStateData() {
		try {
			channelSend(dgen_wt.genDevStateData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
