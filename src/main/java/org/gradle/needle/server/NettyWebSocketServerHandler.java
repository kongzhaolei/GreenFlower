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
		
		//��ͨhttp����websocket
		if(msg instanceof FullHttpRequest){
			handlehttpRequst(ctx, (FullHttpRequest)msg);
			// websocket
		}else if(msg instanceof WebSocketFrame) {
			handleWebSocketRequest(ctx, (WebSocketFrame)msg);
			
		}
	}
        
	// ����websocket��������
	    private void handleWebSocketRequest(ChannelHandlerContext ctx,
			WebSocketFrame fmsg) {
		
	    	//�ж��Ƿ�ر�����ָ��
	    	if(fmsg instanceof CloseWebSocketFrame){
	    		handshaker.close(ctx.channel(), (CloseWebSocketFrame)fmsg.retain());
	    		return;
	    	}
	    	
	    	//�ж��Ƿ�������
	    	if(fmsg instanceof PingWebSocketFrame){
	    		ctx.channel().write(new PongWebSocketFrame(fmsg.content().retain()));
	    		return;
	    	}
	    	
	    	//�ж��Ƿ�ΪTEXT������Ϣ
	    	if(!(fmsg instanceof TextWebSocketFrame)){
	    	    throw new UnsupportedOperationException(
                    String.format("server�޷���������� " + fmsg.getClass().getName()));
	    	    }
	    	
	    	// ����ͻ������󣬷���Ӧ����Ϣ
	    	String request = ((TextWebSocketFrame)fmsg).text();
	    	ctx.channel().writeAndFlush(new TextWebSocketFrame(request + "  ��ӭʹ��websocket�����"));
		
	}


	//�������http��������
	    private void handlehttpRequst(ChannelHandlerContext ctx, FullHttpRequest msg) {
		
		
	}


}
