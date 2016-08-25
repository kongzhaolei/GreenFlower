package org.gradle.needle.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipInputStream;

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
//		System.out.println("�յ���   " + req);
	
		ByteArrayInputStream bis = new ByteArrayInputStream(req.getBytes());
		ZipInputStream zis = new ZipInputStream(bis);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int defBlockSize = 4096;
		byte[] buffer = new byte[defBlockSize];
	    int n = zis.read(buffer,0,buffer.length);
		while (n != -1) {
			out.write(buffer, 0, n);
			n = zis.read(buffer,0,buffer.length);
		}
		String s  =  new String(out.toByteArray());
		System.out.println(s);
		
		zis.close();
	    	
		// �ظ����ݸ��ͻ���
//		ctx.writeAndFlush(
//				new DatagramPacket(Unpooled.copiedBuffer("fine��������",
//						CharsetUtil.UTF_8), msg.sender())).sync();
	}
}
