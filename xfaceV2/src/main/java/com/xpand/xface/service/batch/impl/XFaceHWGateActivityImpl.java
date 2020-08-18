package com.xpand.xface.service.batch.impl;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.PassengerBoatActivity;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.queue.QueueGateActivity;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;


/*
 * class to check alarm in queue and process
 * 1. send notification to UI
 * 2. execute action code
 * 3. move alarm from queue to db
 */
public class XFaceHWGateActivityImpl extends Thread implements BaseXFaceThreadService{
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private String transactionId;	
	private int timeThreadSleep;			
	private GlobalVarService globalVarService;			
	private XFaceBatchService xFaceBatchService;
	public XFaceHWGateActivityImpl(String transactionId, int threadNo, GlobalVarService globalVarService
				, XFaceBatchService xFaceBatchService, int timeThreadSleep) {
		super(transactionId+"_XFaceHWGateActivityImpl_"+threadNo);		
		this.transactionId = super.getName();
		this.globalVarService = globalVarService;		
		this.timeThreadSleep = timeThreadSleep;		
		this.xFaceBatchService = xFaceBatchService;		
	}
	@Override
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start XFaceHWGateActivityImpl thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		QueueGateActivity queueContent;
		PassengerBoatActivity gateData = null;
		ApplicationCfg appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_DEMONTASK_LIMIT_PRINT_LOG);
		int limitPrintLog = StringUtil.stringToInteger(appTmp.getAppValue1(), 3000); 
		int cntPrintLog = 0;
		ResultStatus resultStatus = null;
		////////////////////		
		while (this.isLoop || (this.globalVarService.getSizeOfGateActivityQueue()>0)) {
			cntPrintLog++;
			if (cntPrintLog>limitPrintLog) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "get gate activity from queue"));
			}			
			queueContent = this.globalVarService.popGateActivity();
			if (queueContent==null) {
				if (cntPrintLog>limitPrintLog) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no gate activity in queue"));
				}
				OtherUtil.waitMilliSecond(this, this.timeThreadSleep);
			}else {				
				gateData = queueContent.getPassengerBoatActivity();	
				if (PassengerBoatActivity.ACTIVITY_BOAT_CHECK_IN_OUT==gateData.getActivityType()) {
					//boat
					resultStatus = this.xFaceBatchService.updateBoatSchedule(this.transactionId, gateData);
				}else if (PassengerBoatActivity.ACTIVITY_PASSENGER_PASS_GATE==gateData.getActivityType()) {
					//person
					resultStatus = this.xFaceBatchService.updateHWGateAccessInfo(this.transactionId, gateData);
				}										
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, "insert data:"+gateData.toString()+", rseult is:"+resultStatus.toString()));				
			}		
			if (cntPrintLog>limitPrintLog) {				
				cntPrintLog = 0;
			}
		}
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop XFaceHWGateActivityImpl thread ["+super.getName()+"]"));
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
