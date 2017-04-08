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
	
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
	static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
	
	public static void main(String[] args) throws Exception {
		
		//���ÿͻ���
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					ChannelPipeline p = ch.pipeline();
					p.addLast("decoder", new StringDecoder());
					p.addLast("encoder", new StringEncoder());
					p.addLast(new TCPDataClientHandler());
				}
			});
			ChannelFuture future = b.connect(HOST, PORT).sync();
			for(int i = 0; i < 8; i++){
				Thread.sleep(900);
			    future.channel().writeAndFlush("һ��netty�ͻ��˵��԰�");
			}
			future.channel().close().sync();
		} finally {
			group.shutdownGracefully();
		}	
	}

}
