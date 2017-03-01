package org.gradle.needle.client;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class RealDataGeneratorClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private static Logger logger = Logger.getLogger(RealDataGeneratorClientHandler.class.getName());

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

		String resp = msg.content().toString(CharsetUtil.UTF_8);
		logger.info("收到服务端回应：     " + resp);
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();

	}

}
