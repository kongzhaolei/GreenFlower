package org.gradle.needle.server;

import org.gradle.needle.mapper.GlobalSettings;
import org.gradle.needle.util.VTimer;

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
public class WindFarmSimulatorServer {
	private static int protocolid = Integer.parseInt(GlobalSettings
			.getProperty("protocolid"));
	private int port;
	private String host;
	
	public WindFarmSimulatorServer(String host, int port) {
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
		VTimer.timerStart();
		//Ĭ�϶˿�1120
		new WindFarmSimulatorServer(host, 1120).serverStart();
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
									new WindFarmSimulatorServerHandler(protocolid));

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
}
