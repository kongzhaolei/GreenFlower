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
 * --�鲥���ݵķ�����
 * ����ͨѶ����
 *   1. multicast: ��̬ģ��ǰ�õ��鲥
 *   2. UDP: ģ�����ݴ������������ת��
 * ����ά�ȵĶ�̬�仯
 *   1. ��̬ģ�ⲻͬЭ�������ѯ����
 *   2. ��̬ģ�ⲻͬ���������ѯ����
 * ��ͬҵ���������ݵĶ�̬�仯
 *   1. ����ѯ���� DevMainData    
 *      ��ʽ˵����(wman|650101001|����)
 *      ����˵����(transtype=1  offsets��С��������)
 *      ����ʵ����(wman|422804646|2016-07-08 10:49:03.431,1092.81,1087.53,1085.78,391.97,390.55,390.55,1259.52,-218.24,50,0.99,False,True)
 *   
 *   2. ��������  DevFaultData
 *      ��ʽ˵����(falutdata|wtid|���Ϻ�|����iecpath��|�豸״̬|Ψһ��)
 *      ����ʵ����(falutdata|650101001|106;90;91;92;95)

 *   3. �������� DevAlarmdata
 *      ��ʽ˵����(alarmdata|wtid|�����|����iecpath��|�豸״̬)
                ����ʵ����(alarmdata|652111802|163;120;82)
 *   
 *   4. ǰ�ú��豸֮���ͨ��״̬  DevComState
 *      ��ʽ˵����(comstate|wtid|ͨѶ״̬)
 *      ����ʵ����(comstate|650101001|0)
 *      
 *   5. �������鲥 DevPackData
 *      ��ʽ˵����(pack|���ݰ���|wtid|����)
 *      ����˵����compath=���ݰ���offsets��С��������
 *      ����ʵ����(pack|WFPR|650101002|6,0,0,2,7,7,1,1,1,FALSE,FALSE,TRUE,FALSE,TRUE)
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
	 * ���ݷ�����
	 * �ж��鲥�򵥲���ʽ
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
 * udp�鲥��ʽ����
 * ģ��ǰ���鲥
 * ÿ��������������һ���߳�
 */
	private void multicastGen() {
		try {
			multicast =  new Multicast(multicastIP, multicastPort);
			logger.info(multicastIP + ":" + multicastPort + " �鲥����������...");
			new Thread(new DevMainDataThread()).start();
			new Thread(new DevFaultDataThread()).start();
			new Thread(new DevAlarmDataThread()).start();
			new Thread(new DevComStateThread()).start();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/**
 * UDP������ʽ��������
 * ģ�����ݴ������������ת��ҵ��
 */
	private void singleGen() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new UDPDataClientHandler());

			Channel channel = bs.bind(0).sync().channel();

			// �����˴���UDP��Ϣ
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer("msg",
							CharsetUtil.UTF_8), new InetSocketAddress(
									singleIP, singlePort))).sync();
			logger.info("��Ϣ�ѷ���...");
			if (!channel.closeFuture().await(6000)) {
				logger.info("���ͳ�ʱ...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
/*
 * ���� DevMainData
 */
	public static void sendDevMainData() {
		try {
			multicast.send(de.genDevMainData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("�ѷ����鲥����ѯ���ݣ�" + de.genDevMainData());
	}
	
/*
 * ���� DevFaultData
 */
	public static void sendDevFaultData() {
		try {
			multicast.send(de.genDevFaultData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("�ѷ����鲥�������ݣ�" + de.genDevFaultData());
	}
	
/*
 * ���� DevAlarmData
 */
	public static void sendDevAlarmData() {
		try {
			multicast.send(de.genDevAlarmData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("�ѷ����鲥�������ݣ� " + de.genDevAlarmData());
	}
	
/*
 * ���� DevComState
 */
	public static void sendDevComState() {
		try {
			multicast.send(de.genDevComState());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//logger.info("�ѷ����鲥ͨ��״̬���ݣ� " + de.genDevComState());
	}
	
}