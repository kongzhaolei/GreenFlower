package org.gradle.needle.server;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.NEW;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<Object>{

	
	private static final Logger log = Logger.getLogger(NettyWebSocketServerHandler.class.getName());
	private WebSocketServerHandshaker handshaker;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		//普通http，非websocket
		if(msg instanceof FullHttpRequest){
			handlehttpRequst(ctx, (FullHttpRequest)msg);
			// websocket
		}else if(msg instanceof WebSocketFrame) {
			handleWebSocketRequest(ctx, (WebSocketFrame)msg);
			
		}
	}
        
	// 处理websocket类型请求
	    private void handleWebSocketRequest(ChannelHandlerContext ctx,
			WebSocketFrame fmsg) {
		
	    	//判断是否关闭链接指令
	    	if(fmsg instanceof CloseWebSocketFrame){
	    		handshaker.close(ctx.channel(), (CloseWebSocketFrame)fmsg.retain());
	    		return;
	    	}
	    	
	    	//判断是否心跳包
	    	if(fmsg instanceof PingWebSocketFrame){
	    		ctx.channel().write(new PongWebSocketFrame(fmsg.content().retain()));
	    		return;
	    	}
	    	
	    	//判断是否为TEXT类型消息
	    	if(!(fmsg instanceof TextWebSocketFrame)){
	    	    throw new UnsupportedOperationException(
                    String.format("server无法处理该类型 " + fmsg.getClass().getName()));
	    	    }
	    	
	    	// 处理客户端请求，返回应答消息
	    	String request = ((TextWebSocketFrame)fmsg).text();
	    	ctx.channel().writeAndFlush(new TextWebSocketFrame(request + "  欢迎使用websocket服务端"));
		
	}


	//处理常规的http类型请求
	    private void handlehttpRequst(ChannelHandlerContext ctx, FullHttpRequest msg) {
		
		
	}


}
