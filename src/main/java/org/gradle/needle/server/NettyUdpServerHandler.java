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
		// ��ȡ�յ�������
		String req = msg.content().toString(CharsetUtil.UTF_8);
		System.out.println("�յ��ͻ������ݣ�   " + req);

		// �ظ����ݸ��ͻ���
		ctx.writeAndFlush(
				new DatagramPacket(Unpooled.copiedBuffer("fine��������",
						CharsetUtil.UTF_8), msg.sender())).sync();
	}
}
