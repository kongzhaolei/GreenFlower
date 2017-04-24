package org.gradle.needle.client;

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
							p.addLast(new TCPDataClientHandler());
						}
					});
			ChannelFuture future = bs.connect(HOST, PORT).sync();
			future.channel().writeAndFlush("一个netty客户端的自白");
			future.channel().close().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

}
