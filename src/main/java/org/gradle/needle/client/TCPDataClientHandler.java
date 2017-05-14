package org.gradle.needle.client;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TCPDataClientHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = Logger.getLogger(TCPDataClientHandler.class.getName());
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		logger.info("NettyClientHandler¼¤»î");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		logger.info(msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
