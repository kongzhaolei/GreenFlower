package org.gradle.needle.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class NettyWebSocketServerHandler extends
		SimpleChannelInboundHandler<Object> {

	private WebSocketServerHandshaker handshaker;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		// 普通http，握手接入
		if (msg instanceof FullHttpRequest) {
			handlehttpRequst(ctx, (FullHttpRequest) msg);
			// websocket，已连接
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketRequest(ctx, (WebSocketFrame) msg);

		}
	}

	// 处理websocket类型请求
	private void handleWebSocketRequest(ChannelHandlerContext ctx,
			WebSocketFrame frame) {

		// 判断是否关闭链接指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			return;
		}

		// 判断是否心跳包
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}

		// 判断是否为TEXT类型消息
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(
					String.format("server无法处理该类型 " + frame.getClass().getName()));
		}

		// 处理客户端请求，返回应答消息
		String request = ((TextWebSocketFrame) frame).text();
		System.out.println(request);
		ctx.channel().writeAndFlush(
				new TextWebSocketFrame("欢迎使用websocket服务端..."));

	}

	// 处理http类型握手请求
	private void handlehttpRequst(ChannelHandlerContext ctx,
			FullHttpRequest httprequest) {

		// http异常，解码失败，同时判断消息头是否包含upgrade来表示协议升级
		if (!httprequest.decoderResult().isSuccess()
				|| (!"websocket".equals(httprequest.headers().get("Upgrade")))) {
			sendFailedHttpResponse(ctx, httprequest, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		// 成功握手的返回
		WebSocketServerHandshakerFactory ws = new WebSocketServerHandshakerFactory(
				  "ws://localhost:8080/websocket", null, false);
		handshaker = ws.newHandshaker(httprequest);
		if (handshaker == null) {
			// 版本不支持
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx
					.channel());
		} else {
			handshaker.handshake(ctx.channel(), httprequest);
		}

	}
	
	// 失败握手的返回
	private void sendFailedHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest request,
			FullHttpResponse response) {
		// 返回给客户端
		if(response.status().code() != HttpResponseStatus.OK.code()){
			ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(response, response.content().readableBytes());
		}
		
		//如果是非keep-Alive，关闭连接
		ChannelFuture future = ctx.channel().writeAndFlush(response);
		if(!HttpUtil.isKeepAlive(response) || response.status().code() != HttpResponseStatus.OK.code()){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	// 处理异常
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
}
