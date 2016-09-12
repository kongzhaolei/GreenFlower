package org.gradle.needle.server;

import org.gradle.needle.dbo.DataEngine;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTcpServerHandler extends ChannelInboundHandlerAdapter {
	
	int protocolid = 158111;
	
	public NettyTcpServerHandler(){
		
	}
	
//	public NettyTcpServerHandler(int protocolid){
//		this.protocolid = protocolid;
//	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		DataEngine de = new DataEngine(protocolid, msg.toString().trim());
		System.out.println(ctx.channel().remoteAddress() + "\n" + msg.toString());
		String sReturnString = de.getCacheValue();
		System.out.println(sReturnString);
		ctx.writeAndFlush(sReturnString);
		
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
