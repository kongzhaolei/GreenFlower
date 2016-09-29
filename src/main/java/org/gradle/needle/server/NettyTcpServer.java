package org.gradle.needle.server;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.gradle.needle.dao.DataDefined;
import org.gradle.needle.dao.GlobalSettings;

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

public class NettyTcpServer {

	private int port;
	static int list_n = -1;
	static int protocolid = Integer.parseInt(GlobalSettings
			.getProperty("protocolid"));

	public NettyTcpServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		int port;
		stopTimerStart();
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 1120; // GWSOCKET
		}
		new NettyTcpServer(port).start();
	}

	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap sbs = new ServerBootstrap()
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port))
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

			// 绑定端口，开始接收进来的连接
			ChannelFuture future = sbs.bind(port).sync();
			System.out.println("服务器开始监听于： " + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/*
	 * 定时器实现停机模式字号迭代
	 */
	public static void stopTimerStart() {
		long interval = 120000;
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
	
	public static int getIecvalue() {
		return list_n;
	}
}
