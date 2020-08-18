package com.xpand.xface.service.batch.impl;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.hwapi.HWAPIBatchService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;


/*
 * class to check alarm in queue and process
 * 1. send notification to UI
 * 2. execute action code
 * 3. move alarm from queue to db
 */
public class XFaceKeepAliveImpl extends Thread implements BaseXFaceThreadService{
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private String transactionId;		
	private HWAPIBatchService hwAPIService;
	private int timeThreadSleep; 
	private HWVCM hwVCM;
	public XFaceKeepAliveImpl(String transactionId, HWVCM hwVCM, GlobalVarService globalVarService, int timeThreadSleep, int threadNo
				, HWAPIBatchService hwAPIService) {
		super(transactionId+"_XFaceKeepAliveImpl_"+threadNo);		
		this.transactionId = super.getName();
		this.timeThreadSleep = timeThreadSleep;
		this.hwAPIService = hwAPIService;
		this.hwVCM = hwVCM;
	}
	@Override
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start HWKeepAlive thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		////////////////////		
		while (this.isLoop) {
			this.hwAPIService.keepAlive(this.hwVCM);
			this.hwAPIService.keepAliveSDK(this.hwVCM);
			OtherUtil.waitSecond(this, this.timeThreadSleep);
		}		
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop HWKeepAlive thread ["+super.getName()+"]"));
	}
	@Override
	public boolean isServiceRunning() {
		return this.isLoop;
	}	
	@Override
	public void stopServiceThread() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to stop [ThreadName:"+super.getName())+"]");		
		this.isLoop = false;
		this.isTerminate = false;
	}
	@Override
	public boolean isTerminate() {
		return this.isTerminate;
	}
	@Override
	public boolean isServiceReStart() {
		// TODO Auto-generated method stub
		return false;
	}
}
