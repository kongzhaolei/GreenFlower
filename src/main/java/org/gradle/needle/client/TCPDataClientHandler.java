package org.gradle.needle.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TCPDataClientHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		System.out.println("NettyClientHandler¼¤»î");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		System.out.println(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
