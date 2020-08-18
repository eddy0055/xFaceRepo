package com.xpand.xface.service;

public interface XFaceServerService{
	public void start();	
	public void restart();
	public void stop();
	public String getTransactionId();
}
