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

public class NettyWebSocketServer {

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
							ch.pipeline().addLast("http-codec", new HttpServerCodec());   //HttpServerCodec将请求和应答消息编码或解码为HTTP消息
							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));  //定义缓存大小
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());   //向客户端发送HTML5文件，支持浏览器和服务器进行WebSocket通信
							ch.pipeline().addLast("handler", new NettyWebSocketServerHandler());  //自定义Handler
						}
					});
			     ChannelFuture future= sbs.bind(port).sync();
			     System.out.println("服务端启动：  " + port);
			     future.channel().closeFuture().sync();		     
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new NettyWebSocketServer().run(7777);
	}
}
