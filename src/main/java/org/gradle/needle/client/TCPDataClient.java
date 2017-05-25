package org.gradle.needle.client;

import org.apache.log4j.Logger;
import org.gradle.needle.dto.GlobalSettings;
import org.gradle.needle.engine.DataGenerator;
import org.gradle.needle.thread.DevChangeSaveThread;
import org.gradle.needle.thread.DevPowerCurveThread;
import org.gradle.needle.thread.DevRealTimeDataThread;
import org.gradle.needle.thread.DevTenDataThread;

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
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));
	private static Logger logger = Logger.getLogger(TCPDataClient.class.getName());
	private static DataGenerator de = new DataGenerator(protocolid);
	private static ChannelFuture future;

	public TCPDataClient(String host, int port) {
		TCPDataClient.HOST = host;
		TCPDataClient.PORT = port;
	}

	public TCPDataClient() {

	}

	/**
	 * TCP 线程组
	 */
	public void GeneratorStart() {
		try {
			TcpConnect();
			new Thread(new DevTenDataThread()).start();
			new Thread(new DevRealTimeDataThread()).start();
			new Thread(new DevChangeSaveThread()).start();
			new Thread(new DevPowerCurveThread()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 配置tcp client
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
			logger.info("已连接到服务端 " + HOST + ":" + PORT);
			// future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通道发送tcp数据
	 */
	public static void channelSend(String data) {
		try {
			future.channel().writeAndFlush(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevTenData
	public static void sendDevTenData() {
		try {
			channelSend(de.genDevTenData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevOne

	public static void sendDevOne() {
		try {
			String s = de.genDevOne();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevRealTimeData

	public static void sendDevRealTimeData() {
		try {
			channelSend(de.genDevRealTimeData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevChangeSave

	public static void sendDevChangeSave() {
		try {
			channelSend(de.genDevChangeSave());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevPowerCurve

	public static void sendDevPowerCurve() {
		try {
			channelSend(de.genDevPowerCurve());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevSedimentOne
	public static void sendDevSedimentOne() {
		try {
			String s = de.genDevSedimentOne();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// send DevSedimentOnedata
	public static void sendDevSedimentOneData() {
		try {
			String s = de.genDevSedimentOneData();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevSedimentRealData
	public static void sendDevSedimentRealData() {
		try {
			String s = de.genDevSedimentRealData();
			channelSend(s);
			logger.info(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevWarnLog

	public void sendDevWarnLog() {
		try {
			channelSend(de.genDevWarnLog().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevFaultData
	public void sendDevFaultData() {
		try {
			channelSend(de.genDevFaultData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevAlarmData
	public void sendDevAlarmData() {
		try {
			channelSend(de.genDevAlarmData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// send DevStateData
	public void sendDevStateData() {
		try {
			channelSend(de.genDevStateData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
