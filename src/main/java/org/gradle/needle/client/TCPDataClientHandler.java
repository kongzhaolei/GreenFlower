package org.gradle.needle.client;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TCPDataClientHandler extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = Logger.getLogger(TCPDataClientHandler.class.getName());
	static String res;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		logger.info("ͨ����ʼ����� ");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		res = msg.toString();
		logger.info("����˷�����Ϣ�� " + msg.toString());
	}
	
	public static String getchannelRead() {
		return res;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}

}
