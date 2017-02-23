package org.gradle.needle.client;

import java.net.InetSocketAddress;

import org.gradle.needle.Multicast.Multicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.channel.socket.DatagramPacket;

/** author
 * kongzhaolei
 * 
 * --组播数据的发生器
 * 两种通讯类型
 *   1. multicast: 动态模拟前置的组播
 *   2. UDP: 模拟数据处理服务的向上转发
 * 两个维度的动态变化
 *   1. 动态模拟不同协议的主轮询数据
 *   2. 动态模拟不同风机的主轮询数据
 * 不同业务类型数据的动态变化
 *   1. 主轮询数据 MainData    wman
 *   2. 故障数据  FaultData    falutdata
 *   3. 通信状态  ComState   
 *   4. 包数据 PackData
 *   
 */  
public class MainDataGenerator {
	private static Multicast multicast;
	private static String multicastIP;
	private static int multicastPort;
	private static String localIP;
	private static String singleIP;
	private static int singlePort;
	private String message;
	private static boolean is_multicast;
	

	public MainDataGenerator(String ip, int port, String localIP){
		multicastIP = ip;
		multicastPort = port;
	    MainDataGenerator.localIP = localIP;
	    is_multicast = true;
	}
	
	public MainDataGenerator(String ip, int port){
		singleIP = ip;
		singlePort = port;
		is_multicast = false;
	}
    
	/*
	 * 
	 */
	public String getMessage() {
		return this.message;
	}
	
	/*
	 * 数据发生器
	 * 判断组播或单播方式
	 */
	public void generatorStart() {
		try {
			if (is_multicast) {
				multicastGen();
			} else {
				singleGen();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/*
 * udp组播方式发送数据
 * 模拟前置组播
 */
	private void multicastGen() {
		try {
			multicast =  new Multicast(multicastIP, multicastPort, localIP);
			multicast.send(getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*
 * UDP单播方式发送数据
 * 模拟数据处理服务的向上转发业务
 */
	private void singleGen() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new MainDataGeneratorHandler());

			Channel channel = bs.bind(0).sync().channel();

			// 向服务端传递UDP消息
			channel.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer(getMessage(),
							CharsetUtil.UTF_8), new InetSocketAddress(
									singleIP, singlePort))).sync();
			if (!channel.closeFuture().await(6000)) {
				System.out.println("发送超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}

	}

}
