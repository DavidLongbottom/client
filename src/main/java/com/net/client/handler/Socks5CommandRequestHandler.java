package com.net.client.handler;

import java.util.concurrent.CountDownLatch;

import org.msgpack.MessagePack;

import com.net.client.pojo.CmdInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;

public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
 
	private String serverIP;
	private int port;
	private String base64KeyString;
	
    public Socks5CommandRequestHandler(String serverIP,int port,String base64KeyString) {
    	this.serverIP=serverIP;
    	this.port=port;
		this.base64KeyString=base64KeyString;
	}
	
	
	@Override
	protected void channelRead0(final ChannelHandlerContext pcChannelCtx, final DefaultSocks5CommandRequest pcCmdRequest) throws Exception {
		final CountDownLatch startSignal=new CountDownLatch(1);
				Bootstrap bootstrap=new Bootstrap();
				bootstrap.group(pcChannelCtx.channel().eventLoop())
						 .channel(NioSocketChannel.class)
						 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
						 .handler(new ChannelInitializer<NioSocketChannel>() {
							 // 注意这个channel 是新创建的channel,这个channel就是下面b.connect获得future对应的channel是同一个。
							@Override
							protected void initChannel(NioSocketChannel ch) throws Exception {
								
								ch.pipeline()
								// Outbound, 在encrypt 中顺便把长度给处理掉
									.addLast(new EncryptOutboundHandller(base64KeyString))
								//Inbound
								  .addLast(new LengthFieldBasedFrameDecoder(10485760,0,4,0,4))
								  .addLast(new DescryptInboundHandller(base64KeyString))
								  .addLast(new CmdResultHandler()) // 这个用一次就丢掉
								  .addLast(new TransInboundHandler((NioSocketChannel)pcChannelCtx.channel()));
								startSignal.countDown();
							}
						  });
				ChannelFuture future=bootstrap.connect(serverIP, port);
				future.addListener(new ChannelFutureListener() {
					
					public void operationComplete(ChannelFuture future) throws Exception {

						if(future.isSuccess()) {
							CmdInfo cmdInfo=new CmdInfo();
							cmdInfo.setHost(pcCmdRequest.dstAddr());
							cmdInfo.setPort(pcCmdRequest.dstPort());
							NioSocketChannel toSSServerChannel=(NioSocketChannel) future.channel();
							MessagePack messagePack=new MessagePack();
							byte[] cmdInfoByte=messagePack.write(cmdInfo);
							
							// 这个是为了在握手Cmd 命令结束之后，让pc发向目的地的消息得到转发
							pcChannelCtx.pipeline().addLast(new TransInboundHandler(toSSServerChannel));							
							
							startSignal.await();
							toSSServerChannel.writeAndFlush(Unpooled.copiedBuffer(cmdInfoByte));
							
							
						}else {
							pcChannelCtx.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4));
							pcChannelCtx.close();
						}
						
					}
				});			
	}



	
}
