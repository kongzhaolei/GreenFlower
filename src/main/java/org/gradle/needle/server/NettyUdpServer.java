package org.gradle.needle.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class NettyUdpServer {

	public void run(int port) throws Exception {
		Bootstrap bs = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bs.group(group)
			.channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(new NettyUdpServerHandler());
			
			//����˼����� port �˿�
			bs.bind(port).sync().channel().closeFuture().await();
			System.out.println("��������ʼ�����ڣ� " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}	
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8805;
		new NettyUdpServer().run(port);
	}
}
