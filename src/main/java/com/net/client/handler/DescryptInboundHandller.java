package com.net.client.handler;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DescryptInboundHandller extends SimpleChannelInboundHandler<Object> {

	private String base64KeyString;
	private SecretKeySpec secretKeySpec;
	
	public DescryptInboundHandller(String base64KeyString) {
		this.base64KeyString=base64KeyString;
    	secretKeySpec= new SecretKeySpec(Base64.decode(base64KeyString), "AES");

	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf buf=(ByteBuf)msg;
			byte[] msgByte=ByteBufUtil.getBytes(buf);
			Cipher cipher=Cipher.getInstance("AES");
	    	cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] originalMsgByte=cipher.doFinal(msgByte);
			
			ctx.fireChannelRead(Unpooled.copiedBuffer(originalMsgByte));
		
		}
		
	}

}
