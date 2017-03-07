package org.gradle.needle.server;

import org.apache.log4j.Logger;
import org.gradle.needle.mapper.DataEngine;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class WindFarmSimulatorServerHandler extends ChannelInboundHandlerAdapter {

	int protocolid;
	private static Logger logger = Logger.getLogger(WindFarmSimulatorServerHandler.class
			.getName());

	public WindFarmSimulatorServerHandler(int protocolid) {
		this.protocolid = protocolid;
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		DataEngine de = new DataEngine(protocolid, msg.toString().trim());
		logger.info(ctx.channel().remoteAddress() + "\n" + protocolid + "\n"
				+ msg.toString());
		String sReturnString = de.genCmdData();
		ctx.writeAndFlush(sReturnString);
		logger.info(sReturnString);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (this == ctx.pipeline().last()) {
			logger.warn(cause.getCause());
			ctx.close();
		}
		ctx.writeAndFlush(cause);
	}
}
