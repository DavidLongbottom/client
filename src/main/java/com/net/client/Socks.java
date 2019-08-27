package com.net.client;


import java.io.IOException;
import java.util.Properties;


import com.net.client.handler.Socks5CommandRequestHandler;
import com.net.client.handler.Socks5InitialRequestHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;



public class Socks {
	NioEventLoopGroup bossGroup=new NioEventLoopGroup(1);
	NioEventLoopGroup workerGroup=new NioEventLoopGroup();
	
	static String base64KeyString;
    private void init(final int localPort,final String serverIP,final int port,final String base64KeyString) 
    {	
    	
    	ServerBootstrap serverBootstrap=new ServerBootstrap();
	    try {
	    	serverBootstrap.group(bossGroup, workerGroup)
	    					.channel(NioServerSocketChannel.class)
	    					.option(ChannelOption.SO_BACKLOG,1024)
	    					.childHandler(new ChannelInitializer<NioSocketChannel>() {
	
								@Override
								protected void initChannel(NioSocketChannel ch) throws Exception {
									ch.pipeline()
									.addLast(Socks5ServerEncoder.DEFAULT)
									.addLast(new Socks5InitialRequestDecoder())
									.addLast(new Socks5InitialRequestHandler())
									// 注意这个项目在ss local上不做密码的验证
									.addLast(new Socks5CommandRequestDecoder())
									.addLast(new Socks5CommandRequestHandler(serverIP,port,base64KeyString));
								}
	    					    
							});
    		ChannelFuture future=serverBootstrap.bind(localPort).sync();
        	future.channel().closeFuture().sync();
		}catch (Exception e) {
			e.printStackTrace();
		} 
    	finally {
    		bossGroup.shutdownGracefully();
    		workerGroup.shutdownGracefully();
    	}
    }
    
    public static void main( String[] args ) throws IOException
    {
    	Socks socks=new Socks();

        Properties props=new Properties();
        props.load(Socks.class.getResourceAsStream("config.properties"));
        int localPort=Integer.parseInt(props.getProperty("localPort").trim());
        String serverIP=props.getProperty("serverIP").trim();
        int serverPort=Integer.parseInt(props.getProperty("serverPort").trim());
        base64KeyString=props.getProperty("key").trim();
        
        
    	socks.init(localPort,serverIP,serverPort,base64KeyString);
 	   	
    }
    
    public NioEventLoopGroup getWorkerGroup() {
    	return workerGroup;
    }
    
    
}
