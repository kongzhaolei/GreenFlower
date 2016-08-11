package org.gradle.needle.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class NettyUdpServerHandler extends
		SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
			throws Exception {
		// 读取收到的数据
		String req = msg.content().toString(CharsetUtil.UTF_8);
		System.out.println("收到客户端数据：   " + req);

		// 回复数据给客户端
		ctx.writeAndFlush(
				new DatagramPacket(Unpooled.copiedBuffer("fine，三克油",
						CharsetUtil.UTF_8), msg.sender())).sync();
	}
}
