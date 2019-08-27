package com.net.client.handler;

import org.msgpack.MessagePack;

import com.net.client.pojo.CmdResult;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;

public class CmdResultHandler extends SimpleChannelInboundHandler<Object>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

		if(msg instanceof ByteBuf) {
			ByteBuf msgBuf=(ByteBuf)msg;
			byte[] msgByte=ByteBufUtil.getBytes(msgBuf);
			
			MessagePack messagePack=new MessagePack();
			CmdResult cmdResult=messagePack.read(msgByte,CmdResult.class);
			
			DefaultSocks5CommandResponse defaultSocks5CommandResponse;
			if(cmdResult.getConnectStatus()) {
				defaultSocks5CommandResponse=new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
			}else{
				defaultSocks5CommandResponse=new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
			}
			ctx.pipeline().remove(CmdResultHandler.this);
			ctx.fireChannelRead(defaultSocks5CommandResponse);
			
		}
		
		
		
	}

	
}
