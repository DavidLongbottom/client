package com.net.client.pojo;

import java.io.Serializable;

import org.msgpack.annotation.Message;

@Message
public class CmdResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8697116490856599063L;

	private boolean connectStatus;

	public boolean getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(boolean connectStatus) {
		this.connectStatus = connectStatus;
	}
	
}
