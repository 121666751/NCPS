package com.union.connect;

/**
 * 指定服务器类
 * @author jiazg
 * @date 2017-04-12
 * @version 1.0
 *
 */
public class CustomServer {
	
	
	private String servercode;
	
	private String servergroup;
	
	
	public CustomServer() {
		super();
	}


	public CustomServer(String servercode, String servergroup) {
		super();
		this.servercode = servercode;
		this.servergroup = servergroup;
	}

	
	public String getServercode() {
		return servercode;
	}


	public void setServercode(String servercode) {
		this.servercode = servercode;
	}


	public String getServergroup() {
		return servergroup;
	}


	public void setServergroup(String servergroup) {
		this.servergroup = servergroup;
	}
	
	

}
