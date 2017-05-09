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

public class TCPDataClient {

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

	/**
	 * TCP �߳���
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
	 * ����tcp client
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
						}
					});
			future = bs.connect(HOST, PORT).sync();
			logger.info("�����ӵ�" + HOST + ":" + PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ͨ������tcp����
	 */
	public static void channelSend(String data) {
		try {
			future.channel().writeAndFlush(data);
			future.channel().close().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * send DevTenData
	 */
	public static void sendDevTenData() {
		try {
			channelSend(de.genDevTenData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("�ѷ���ʮ�������ݣ� " + de.genDevTenData());
	}

	/*
	 * send DevRealTimeData
	 */
	public static void sendDevRealTimeData() {
		try {
			channelSend(de.genDevRealTimeData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("�ѷ���ʵʱ���ݣ� " + de.genDevRealTimeData());
	}

	/*
	 * send DevChangeSave
	 */
	public static void sendDevChangeSave() {
		try {
			channelSend(de.genDevChangeSave());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("�ѷ��ͱ�λ���ݣ� " + de.genDevChangeSave());
	}

	/*
	 * send DevPowerCurve
	 */
	public static void sendDevPowerCurve() {
		try {
			channelSend(de.genDevPowerCurve());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("�ѷ��͹������ߣ� " + de.genDevPowerCurve());
	}

}
