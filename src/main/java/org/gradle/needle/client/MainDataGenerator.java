package org.gradle.needle.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.gradle.needle.Multicast.Multicast;
import org.gradle.needle.mapper.DataEngine;

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
 *   2. UDP: ģ�����ݴ�����������ת��
 * ����ά�ȵĶ�̬�仯
 *   1. ��̬ģ�ⲻͬЭ�������ѯ����
 *   2. ��̬ģ�ⲻͬ���������ѯ����
 * ��ͬҵ���������ݵĶ�̬�仯
 *   1. ����ѯ���� DevMainData    
 *      ��ʽ˵����(wman|650101001|����)
 *      ����˵����(transtype=1  offsets��С��������)
 *      ����ʵ����(wman|422804646|2016-07-08 10:49:03.431,1092.81,1087.53,1085.78,391.97,390.55,390.55,1259.52,-218.24,50,0.99,False,True,False,False,True,True,False,True,)
 *   
 *   2. ��������  DevFaultData
 *      ��ʽ˵����(falutdata|wtid|���Ϻ�|����iecpath��|�豸״̬|Ψһ��)
 *      ����ʵ����(falutdata|650101001|106;90;91;92;95|
 *               (WTUR.Bool.Rd.b0.QSBut)=FALSE;
 *               (WNAC.Bool.Rd.b0.Qstart)=FALSE;
 *               (WTPS.Bool.Rd.b0.Psafe1)=FALSE;
 *               (WTPS.Bool.Rd.b0.Psafe2)=FALSE;
 *               (WTPS.Bool.Rd.b0.Psafe3)=FALSE|2
 *               |443c860a-9fe6-4916-9a30-0ecad3821ea2)
 *   3. �������� DevAlarmdata
 *      ��ʽ˵����(alarmdata|wtid|�����|����iecpath��|�豸״̬)
                ����ʵ����(alarmdata|650101001|106;90 |(WTUR.Bool.Rd.b0.QSBut)=FALSE;(WNAC.Bool.Rd.b0.Qstart)=FALSE |2)
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
public class MainDataGenerator {
	private static Multicast multicast;
	private static String multicastIP;
	private static int multicastPort;
	private static String localIP;
	private static String singleIP;
	private static int singlePort;
	private static boolean is_multicast;
	private static Logger logger = Logger.getLogger(MainDataGenerator.class.getName());
	

	public MainDataGenerator(String ip, int port, String localIP){
		multicastIP = ip;
		multicastPort = port;
	    MainDataGenerator.localIP = localIP;
	    is_multicast = true;
	}
	
	public MainDataGenerator(String ip, int port){
		singleIP = ip;
		singlePort = port;
		is_multicast = false;
	}
    
	/*
	 * 
	 */
	public String getMessage() {
		return new DataEngine(158112).genDevMainData();
	}
	
	/*
	 * ���ݷ�����
	 * �ж��鲥�򵥲���ʽ
	 */
	public void generatorStart() {
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
	
/*
 * udp�鲥��ʽ��������
 * ģ��ǰ���鲥
 */
	private void multicastGen() {
		try {
			multicast =  new Multicast(multicastIP, multicastPort, localIP);
			multicast.send(getMessage());
			logger.info(multicastIP + ":" + multicastPort + "�鲥����������...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*
 * UDP������ʽ��������
 * ģ�����ݴ�����������ת��ҵ��
 */
	private void singleGen() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new MainDataGeneratorHandler());

			Channel channel = bs.bind(0).sync().channel();

			// �����˴���UDP��Ϣ
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer(getMessage(),
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
}
