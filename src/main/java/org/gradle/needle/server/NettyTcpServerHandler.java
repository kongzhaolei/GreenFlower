package org.gradle.needle.server;

import org.gradle.needle.dbo.DataEngine;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTcpServerHandler extends ChannelInboundHandlerAdapter {
	
	int protocolid;
	
	public NettyTcpServerHandler(){
		
	}
	
	public NettyTcpServerHandler(int protocolid){
		this.protocolid = protocolid;
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		DataEngine de = new DataEngine(protocolid);
		System.out.println(ctx.channel().remoteAddress() + msg.toString());
		String sReturnString = de.getCacheValue(msg.toString());
		ctx.writeAndFlush("欢迎使用tcp服务端。。。"  + sReturnString);
		
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
