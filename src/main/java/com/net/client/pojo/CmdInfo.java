package com.net.client.pojo;

import java.io.Serializable;

import org.msgpack.annotation.Message;

@Message
public class CmdInfo implements Serializable{

	private static final long serialVersionUID = 219935254110844535L;
	private String host;
	private int port;
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public String toString() {
		 return "host: "+host+"  port"+port;
	}
	
	
	
}
