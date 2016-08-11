package org.gradle.needle.client;

import java.net.InetSocketAddress;

import org.gradle.needle.server.NettyUdpServerHandler;

import com.sun.org.apache.bcel.internal.generic.NEW;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.channel.socket.DatagramPacket;

public class NettyUdpClient {

	public static void main(String[] args) throws Exception {

		int port = 7777;
		new NettyUdpClient().run(port);

	}

	private void run(int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new NettyUdpClientHandler());

			Channel channel = bs.bind(0).sync().channel();

			// 向网段内的所有机器组播UDP消息
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer("how are you...",
							CharsetUtil.UTF_8), new InetSocketAddress(
							"255.255.255.255", port))).sync();
			if (!channel.closeFuture().await(6000)) {
				System.out.println("查询超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}

	}

}
