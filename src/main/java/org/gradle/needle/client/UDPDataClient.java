package org.gradle.needle.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.gradle.needle.Multicast.Multicast;
import org.gradle.needle.engine.DataGenerator;
import org.gradle.needle.thread.DevAlarmDataThread;
import org.gradle.needle.thread.DevComStateThread;
import org.gradle.needle.thread.DevFaultDataThread;
import org.gradle.needle.thread.DevMainDataThread;
import org.gradle.needle.util.GlobalSettings;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.channel.socket.DatagramPacket;

/** 
 * author kongzhaolei
 * 
 * --组播数据的发生器
 * 两种通讯类型
 *   1. multicast: 动态模拟前置的组播
 *   2. UDP: 模拟数据处理服务的向上转发
 * 两个维度的动态变化
 *   1. 动态模拟不同协议的主轮询数据
 *   2. 动态模拟不同风机的主轮询数据
 * 不同业务类型数据的动态变化
 *   1. 主轮询数据 DevMainData    
 *      格式说明：(wman|650101001|数据)
 *      数据说明：(transtype=1  offsets从小到大排序)
 *      数据实例：(wman|422804646|2016-07-08 10:49:03.431,1092.81,1087.53,1085.78,391.97,390.55,390.55,1259.52,-218.24,50,0.99,False,True)
 *   
 *   2. 故障数据  DevFaultData
 *      格式说明：(falutdata|wtid|故障号|关联iecpath量|设备状态|唯一号)
 *      数据实例：(falutdata|650101001|106;90;91;92;95)

 *   3. 警告数据 DevAlarmdata
 *      格式说明：(alarmdata|wtid|警告号|关联iecpath量|设备状态)
                数据实例：(alarmdata|652111802|163;120;82)
 *   
 *   4. 前置和设备之间的通信状态  DevComState
 *      格式说明：(comstate|wtid|通讯状态)
 *      数据实例：(comstate|650101001|0)
 *      
 *   5. 包数据组播 DevPackData
 *      格式说明：(pack|数据包名|wtid|数据)
 *      数据说明：compath=数据包名offsets从小到大排序
 *      数据实例：(pack|WFPR|650101002|6,0,0,2,7,7,1,1,1,FALSE,FALSE,TRUE,FALSE,TRUE)
 *   
 */  
public class UDPDataClient {
	private static Multicast multicast;
	private static String multicastIP;
	private static int multicastPort;
	private static String singleIP;
	private static int singlePort;
	public static boolean is_multicast;
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));
	private static Logger logger = Logger.getLogger(UDPDataClient.class.getName());
	private static DataGenerator de = new DataGenerator(protocolid);
	

	public UDPDataClient(String ip, int port){
		multicastIP = ip;
		multicastPort = port;
	    is_multicast = true;
	}
	
	/**
	 * 数据发生器
	 * 判断组播或单播方式
	 */
	public void GeneratorStart() {
		try {
			if (is_multicast) {
				multicastGen();
			} else {
				singleGen();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/**
 * udp组播方式发送
 * 模拟前置组播
 * 每种类型数据启动一个线程
 */
	private void multicastGen() {
		try {
			multicast =  new Multicast(multicastIP, multicastPort);
			logger.info(multicastIP + ":" + multicastPort + " 组播服务已启动...");
			new Thread(new DevMainDataThread()).start();
			new Thread(new DevFaultDataThread()).start();
			new Thread(new DevAlarmDataThread()).start();
			new Thread(new DevComStateThread()).start();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/**
 * UDP单播方式发送数据
 * 模拟数据处理服务的向上转发业务
 */
	private void singleGen() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new UDPDataClientHandler());

			Channel channel = bs.bind(0).sync().channel();

			// 向服务端传递UDP消息
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer("msg",
							CharsetUtil.UTF_8), new InetSocketAddress(
									singleIP, singlePort))).sync();
			logger.info("消息已发送...");
			if (!channel.closeFuture().await(6000)) {
				logger.info("发送超时...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
/*
 * 发送 DevMainData
 */
	public static void sendDevMainData() {
		try {
			multicast.send(de.genDevMainData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("已发送组播主轮询数据：" + de.genDevMainData());
	}
	
/*
 * 发送 DevFaultData
 */
	public static void sendDevFaultData() {
		try {
			multicast.send(de.genDevFaultData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("已发送组播故障数据：" + de.genDevFaultData());
	}
	
/*
 * 发送 DevAlarmData
 */
	public static void sendDevAlarmData() {
		try {
			multicast.send(de.genDevAlarmData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("已发送组播警告数据： " + de.genDevAlarmData());
	}
	
/*
 * 发送 DevComState
 */
	public static void sendDevComState() {
		try {
			multicast.send(de.genDevComState());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("已发送组播通信状态数据： " + de.genDevComState());
	}
	
}
