package org.gradle.needle.client;

import org.apache.log4j.Logger;
import org.gradle.needle.engine.DataGenerator;
import org.gradle.needle.util.GlobalSettings;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TCPDataClient {

	private static String HOST;
	private static int PORT;
	private static int protocolid = Integer.parseInt(GlobalSettings.getProperty("protocolid"));
	private static Logger logger = Logger.getLogger(TCPDataClient.class.getName());
	private static DataGenerator de = new DataGenerator(protocolid);

	public TCPDataClient(String host, int port) {
		TCPDataClient.HOST = host;
		TCPDataClient.PORT = port;
	}

	public void GeneratorStart() {
		// 配置客户端
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast("decoder", new StringDecoder());
							p.addLast("encoder", new StringEncoder());
						}
					});
			ChannelFuture future = bs.connect(HOST, PORT).sync();
			logger.info("已连接到" + HOST + ":" + PORT);
			while (true) {
				future.channel().writeAndFlush(de.genDevTenData());
				future.channel().writeAndFlush(de.genDevRealTimeData());
				future.channel().writeAndFlush(de.genDevChangeSave());
				future.channel().writeAndFlush(de.genDevPowerCurve());
				future.channel().writeAndFlush(de.genDevWarnLog());
				
				future.channel().close().sync();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

}
