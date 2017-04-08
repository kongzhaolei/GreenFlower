package org.gradle.needle.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {

	public void run(int port) {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sbs = new ServerBootstrap();
			     sbs.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast("http-codec", new HttpServerCodec());   //HttpServerCodec�������Ӧ����Ϣ��������ΪHTTP��Ϣ
							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));  //���建���С
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());   //��ͻ��˷���HTML5�ļ���֧��������ͷ���������WebSocketͨ��
							ch.pipeline().addLast("handler", new WebSocketServerHandler());  //�Զ���Handler
						}
					});
			     ChannelFuture future= sbs.bind(port).sync();
			     System.out.println("�����������  " + port);
			     future.channel().closeFuture().sync();		     
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new WebSocketServer().run(7777);
	}
}