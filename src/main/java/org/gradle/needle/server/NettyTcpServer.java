package org.gradle.needle.server;

import java.net.InetAddress;
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

/**
 * 
 * @author kongzhaolei
 * 
 */
public class NettyTcpServer {

	private int port;
	private String inetHost;
	static int list_n = -1;
	static int protocolid = Integer.parseInt(GlobalSettings
			.getProperty("protocolid"));

	public NettyTcpServer(String inetHost, int port) {
		this.port = port;
		this.inetHost = inetHost;
	}

	/**
	 * ���ݷ��IP���ã��Զ���չ��̨���ģ�����
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// ��ȡ����IP
		String host = InetAddress.getLocalHost().getHostAddress();
		int port = 1120;
		stopTimerStart();
		new NettyTcpServer(host, port).start();
	}

	public static int getIecvalue() {
		return list_n;
	}

	public static int getProcolid() {
		return protocolid;
	}

	/*
	 * ���������
	 */
	public void start() {
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
									new NettyTcpServerHandler(protocolid));

						};
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			// �󶨶˿ڣ���ʼ���ս���������
			ChannelFuture future = sbs.bind(inetHost, port).sync();
			System.out.println("�����������ڣ� " + inetHost + ":" + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/*
	 * ��ʱ��ʵ��ͣ��ģʽ�ֺŵ���
	 */
	public static void stopTimerStart() {
		final long interval = 60000;
		Timer timer = new Timer();
		final int size = new DataDefined(protocolid).getStopModeWordList()
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
