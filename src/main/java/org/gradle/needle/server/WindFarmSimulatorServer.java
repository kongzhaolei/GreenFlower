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
	 * 根据风机IP配置，扩展多台风机模拟服务
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String host = GlobalSettings.getProperty("host");
		VTimer.timerStart();
		//默认端口1120
		new WindFarmSimulatorServer(host, 1120).serverStart();
	}

	public static int getProcolid() {
		return protocolid;
	}

	/**
	 * 服务端启动
	 */
	public void serverStart() {
		// EventLoopGroup是用来处理IO操作的多线程事件循环器
		// bossGroup 用来接收客户端的连接，workerGroup 用来处理已经被接收的连接
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// NIO 服务的辅助启动类
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

			// 绑定端口，开始接收进来的连接
			ChannelFuture future = sbs.bind(host, port).sync();
			System.out.println("服务器监听于： " + host + ":" + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
