package org.gradle.needle.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class UdpDataServerHandler extends
		SimpleChannelInboundHandler<DatagramPacket> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		ByteBuf buf = msg.content();
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		messageReceived(ctx, req);
	}

	protected void messageReceived(ChannelHandlerContext ctx, byte[] msg)
			throws Exception {
		// 读取收到的数据
		String message = unZipData(msg);
		System.out.println("收到：   " + message);
		
		// 回复数据给客户端
//		ctx.writeAndFlush(
//				new DatagramPacket(Unpooled.copiedBuffer("fine，三克油",
//						CharsetUtil.UTF_8), msg.sender())).sync();
	

	}

	private String unZipData(byte[] data) throws IOException{
		String CNCHARSET = "iso-8859-1";
		ByteArrayInputStream bis = null;
		String sdata = null;
		try {
			bis = new ByteArrayInputStream(data);
			GZIPInputStream zis = new GZIPInputStream(bis);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int defBlockSize = 4096;
			byte[] buffer = new byte[defBlockSize];
		    int n = zis.read(buffer,0,buffer.length);
			while (n != -1) {
				out.write(buffer, 0, n);
				out.flush();
			}
			sdata  =  out.toString(CNCHARSET);
			out.close();
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			bis.close();
		}
		return sdata;
	}
}
