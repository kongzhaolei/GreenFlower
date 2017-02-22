package org.gradle.needle.server;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.gradle.needle.mapper.DataDefined;
import org.gradle.needle.mapper.GlobalSettings;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/***
 * 
 * @author kongzhaolei
 * 
 */
public class WindFarmSimulator {

	private static int list_n = -1;
	private static int protocolid = Integer.parseInt(GlobalSettings
			.getProperty("protocolid"));
	private int port;
	private String host;
	
	public WindFarmSimulator(String host, int port) {
		this.port = port;
		this.host = host;
	}

	/***
	 * ���ݷ��IP���ã���չ��̨���ģ�����
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String host = GlobalSettings.getProperty("host");
		timerStart();
		//Ĭ�϶˿�1120
		new WindFarmSimulator(host, 1120).serverStart();
	}

	public static int getNum() {
		return list_n;
	}

	public static int getProcolid() {
		return protocolid;
	}

	/**
	 * ���������
	 */
	public void serverStart() {
		// EventLoopGroup����������IO�����Ķ��߳��¼�ѭ����
		// bossGroup �������տͻ��˵����ӣ�workerGroup ���������Ѿ������յ�����
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// NIO ����ĸ���������
			ServerBootstrap sbs = new ServerBootstrap()
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						protected void initChannel(SocketChannel ch) {
							ch.pipeline().addLast("decoder",
									new StringDecoder());
							ch.pipeline().addLast("encoder",
									new StringEncoder());
							ch.pipeline().addLast(
									new WindFarmSimulatorHandler(protocolid));

						};
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			// �󶨶˿ڣ���ʼ���ս���������
			ChannelFuture future = sbs.bind(host, port).sync();
			System.out.println("�����������ڣ� " + host + ":" + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * ģʽ�ֶ�ʱ������ͣ��ģʽ��Ϊ��
	 */
	public static void timerStart() {
		final long interval = 60000;
		Timer timer = new Timer();
		final int size = new DataDefined(protocolid).getStopModeWordIecValueList()
				.size();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				list_n++;
				if (list_n > size) {
					list_n = 0;
				}
				System.out.println("fuck everything " + list_n);
				
				
			}
		};
		timer.scheduleAtFixedRate(task, new Date(), interval);
	}
}
