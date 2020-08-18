package com.xpand.xface.service.impl;

import java.util.HashMap;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
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
	private HWAPIServiceImpl hwAPIService;
	private int timeThreadSleep; 
	public XFaceKeepAliveImpl(String transactionId, HashMap<String, ApplicationCfg> appCfgList, HWVCM hwVCM, GlobalVarService globalVarService, int timeThreadSleep, int threadNo) {
		super(transactionId+"_XFaceKeepAliveImpl_"+threadNo);		
		this.transactionId = super.getName();
		this.timeThreadSleep = timeThreadSleep;
		this.hwAPIService = new HWAPIServiceImpl();
		this.hwAPIService.initialClass(transactionId, appCfgList, hwVCM, globalVarService);			
	}
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start HWKeepAlive thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		////////////////////		
		while (this.isLoop) {
			this.hwAPIService.keepAlive();
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
	public void stopService() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to stop [ThreadName:"+super.getName())+"]");		
		this.isLoop = false;
		this.isTerminate = false;
	}
	@Override
	public boolean isTerminate() {
		return this.isTerminate;
	}	
}
