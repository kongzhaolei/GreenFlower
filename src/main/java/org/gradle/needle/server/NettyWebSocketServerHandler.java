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

		// ��ͨhttp�����ֽ���
		if (msg instanceof FullHttpRequest) {
			handlehttpRequst(ctx, (FullHttpRequest) msg);
			// websocket��������
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketRequest(ctx, (WebSocketFrame) msg);

		}
	}

	// ����websocket��������
	private void handleWebSocketRequest(ChannelHandlerContext ctx,
			WebSocketFrame frame) {

		// �ж��Ƿ�ر�����ָ��
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			return;
		}

		// �ж��Ƿ�������
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}

		// �ж��Ƿ�ΪTEXT������Ϣ
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(
					String.format("server�޷���������� " + frame.getClass().getName()));
		}

		// ����ͻ������󣬷���Ӧ����Ϣ
		String request = ((TextWebSocketFrame) frame).text();
		System.out.println(request);
		ctx.channel().writeAndFlush(
				new TextWebSocketFrame("��ӭʹ��websocket�����..."));

	}

	// ����http������������
	private void handlehttpRequst(ChannelHandlerContext ctx,
			FullHttpRequest httprequest) {

		// http�쳣������ʧ�ܣ�ͬʱ�ж���Ϣͷ�Ƿ����upgrade����ʾЭ������
		if (!httprequest.decoderResult().isSuccess()
				|| (!"websocket".equals(httprequest.headers().get("Upgrade")))) {
			sendFailedHttpResponse(ctx, httprequest, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		// �ɹ����ֵķ���
		WebSocketServerHandshakerFactory ws = new WebSocketServerHandshakerFactory(
				  "ws://localhost:8080/websocket", null, false);
		handshaker = ws.newHandshaker(httprequest);
		if (handshaker == null) {
			// �汾��֧��
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx
					.channel());
		} else {
			handshaker.handshake(ctx.channel(), httprequest);
		}

	}
	
	// ʧ�����ֵķ���
	private void sendFailedHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest request,
			FullHttpResponse response) {
		// ���ظ��ͻ���
		if(response.status().code() != HttpResponseStatus.OK.code()){
			ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(response, response.content().readableBytes());
		}
		
		//����Ƿ�keep-Alive���ر�����
		ChannelFuture future = ctx.channel().writeAndFlush(response);
		if(!HttpUtil.isKeepAlive(response) || response.status().code() != HttpResponseStatus.OK.code()){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	// �����쳣
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
}
