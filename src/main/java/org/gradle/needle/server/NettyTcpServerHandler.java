package org.gradle.needle.server;

import org.apache.log4j.Logger;
import org.gradle.needle.dbo.DataEngine;



import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTcpServerHandler extends ChannelInboundHandlerAdapter {
	
	int protocolid = 158112;
	private static Logger logger = Logger
			.getLogger(NettyTcpServerHandler.class.getName());
	
	public NettyTcpServerHandler(){
		
	}
	
//	public NettyTcpServerHandler(int protocolid){
//		this.protocolid = protocolid;
//	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		DataEngine de = new DataEngine(protocolid, msg.toString().trim());
		System.out.println(ctx.channel().remoteAddress() + "\n" + msg.toString());
		String sReturnString = de.getCacheValue();
		ctx.writeAndFlush(sReturnString);
		System.out.println(sReturnString);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		if (this == ctx.pipeline().last()) {
			logger.warn(cause.getCause());
			ctx.close();
		}
		ctx.writeAndFlush(cause); 
	}
}
