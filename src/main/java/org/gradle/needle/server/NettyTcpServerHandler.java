package org.gradle.needle.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTcpServerHandler extends ChannelInboundHandlerAdapter {
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		System.out.println(ctx.channel().remoteAddress() + msg.toString());
		ctx.writeAndFlush("欢迎使用tcp服务端。。。");
		
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
