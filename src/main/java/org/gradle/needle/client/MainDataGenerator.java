package org.gradle.needle.client;

import java.net.InetSocketAddress;

import org.gradle.needle.Multicast.Multicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.channel.socket.DatagramPacket;

/** author
 * kongzhaolei
 * 
 * --�鲥���ݵķ�����
 * ����ͨѶ����
 *   1. multicast: ��̬ģ��ǰ�õ��鲥
 *   2. UDP: ģ�����ݴ�����������ת��
 * ����ά�ȵĶ�̬�仯
 *   1. ��̬ģ�ⲻͬЭ�������ѯ����
 *   2. ��̬ģ�ⲻͬ���������ѯ����
 * ��ͬҵ���������ݵĶ�̬�仯
 *   1. ����ѯ���� MainData    wman
 *   2. ��������  FaultData    falutdata
 *   3. ͨ��״̬  ComState   
 *   4. ������ PackData
 *   
 */  
public class MainDataGenerator {
	private static Multicast multicast;
	private static String multicastIP;
	private static int multicastPort;
	private static String localIP;
	private static String singleIP;
	private static int singlePort;
	private String message;
	private static boolean is_multicast;
	

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
		return this.message;
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
			if (!channel.closeFuture().await(6000)) {
				System.out.println("���ͳ�ʱ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}

	}

}
