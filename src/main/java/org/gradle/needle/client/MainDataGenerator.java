package org.gradle.needle.client;

import java.net.InetSocketAddress;

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
 *   1. ��̬ģ��ǰ�õ��鲥��
 *   2. ģ�����ݴ�������UDP����ת��
 * ����ά�ȵĶ�̬�仯
 *   1. ��̬ģ�ⲻͬЭ�������ѯ����
 *   2. ��̬ģ�ⲻͬ���������ѯ����
 * ��ͬҵ����������
 *   1. ����ѯ���� MainData    wman
 *   2. ��������  FaultData    falutdata
 *   3. ͨ��״̬  ComState   
 *   4. ������ PackData
 *   
 */  
public class MainDataGenerator {

	public static void main(String[] args) throws Exception {

		int port = 8769;
		new MainDataGenerator().run(port);

	}

	private void run(int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new MainDataGeneratorHandler());

			Channel channel = bs.bind(0).sync().channel();

			// �����˴���UDP��Ϣ
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer("comstate|836610009|0",
							CharsetUtil.UTF_8), new InetSocketAddress(
							"224.1.1.15", port))).sync();
			if (!channel.closeFuture().await(6000)) {
				System.out.println("��ѯ��ʱ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}

	}

}
