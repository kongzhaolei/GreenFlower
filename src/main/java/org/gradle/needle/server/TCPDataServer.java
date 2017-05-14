package org.gradle.needle.server;

import org.apache.log4j.Logger;
import org.gradle.needle.client.TCPDataClient;
import org.gradle.needle.dto.GlobalSettings;
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
public class TCPDataServer {
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));
	private int port;
	private String host;
	private static Logger logger = Logger.getLogger(TCPDataServer.class.getName());

	public TCPDataServer(String host, int port) {
		this.port = port;
		this.host = host;
	}

	/***
	 * 定时器启动 风机TCP启动
	 */
	public void Start() {
		try {
			VTimer.timerStart();
			run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 风机协议
	 * 
	 * @return
	 */
	public static int getProcolid() {
		return protocolid;
	}

	/**
	 * 服务端启动
	 */
	public void run() {
		// EventLoopGroup是用来处理IO操作的多线程事件循环器
		// bossGroup 用来接收客户端的连接，workerGroup 用来处理已经被接收的连接
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// NIO 服务的辅助启动类
			ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

						protected void initChannel(SocketChannel ch) {
							ch.pipeline().addLast("decoder", new StringDecoder());
							ch.pipeline().addLast("encoder", new StringEncoder());
							ch.pipeline().addLast(new TCPDataServerHandler(protocolid));

						};
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			// 绑定端口，开始接收进来的连接
			ChannelFuture future = sbs.bind(host, port).sync();
			logger.info("服务器监听于： " + host + ":" + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
