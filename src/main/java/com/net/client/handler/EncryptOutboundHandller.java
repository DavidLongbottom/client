package com.net.client.handler;



import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EncryptOutboundHandller extends ChannelOutboundHandlerAdapter{

	private String base64KeyString;
	private SecretKeySpec secretKeySpec;
	
	public EncryptOutboundHandller(String base64KeyString) {
		this.base64KeyString=base64KeyString;
    	secretKeySpec= new SecretKeySpec(Base64.decode(base64KeyString), "AES");

	}
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf buf=(ByteBuf)msg;
			byte[] msgByte=ByteBufUtil.getBytes(buf);
			
			Cipher cipher=Cipher.getInstance("AES");
	    	cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] encryptMsgByte=cipher.doFinal(msgByte);
		
			int length=encryptMsgByte.length;
			ByteBuf buf2=Unpooled.buffer(length+4);
			buf2.writeInt(length);
			buf2.writeBytes(encryptMsgByte);
			
			ctx.writeAndFlush(buf2);
		}
		
		
		
		
	}

	
	
	
}
