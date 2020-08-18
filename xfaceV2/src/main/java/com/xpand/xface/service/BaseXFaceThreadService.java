package com.xpand.xface.service;

public interface BaseXFaceThreadService {
	public boolean isServiceRunning();
	public boolean isServiceReStart();
	public void stopServiceThread();
	public boolean isTerminate();
}
