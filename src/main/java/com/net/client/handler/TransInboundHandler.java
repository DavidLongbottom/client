package com.net.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;


// 注意这个命名Inbound 是因为pipelineA 来说是数据的流入， 而写入到另一个pipelineB，但对B 是Outbound
public class TransInboundHandler extends SimpleChannelInboundHandler<Object> {

	private NioSocketChannel destChannel;
	
	public TransInboundHandler(NioSocketChannel destChannel) {
		this.destChannel=destChannel;
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if(msg instanceof DefaultSocks5CommandResponse) {
			DefaultSocks5CommandResponse defaultSocks5CommandResponse=(DefaultSocks5CommandResponse)msg;
			destChannel.writeAndFlush(defaultSocks5CommandResponse);

		}
		else {
			if(msg instanceof ByteBuf) {
				ByteBuf msgBuf=(ByteBuf)msg;
				byte[] msgByte=ByteBufUtil.getBytes(msgBuf);
				destChannel.writeAndFlush(Unpooled.copiedBuffer(msgByte));
			}
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
	}
	
	
	

}
