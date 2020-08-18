package com.xpand.xface.service.batch.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.query.QueryCameraResp;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.service.hwapi.HWAPIBatchService;
import com.xpand.xface.util.BooleanUtil;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.NetworkUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;

@ApplicationScope
@Component
public class XFaceServerServiceImpl extends Thread implements XFaceServerService, BaseXFaceThreadService {
	
	@Autowired
	private GlobalVarService globalVarService;			
	@Autowired
	private SimpMessagingTemplate msgNotification;
	@Autowired
	private SimpMessagingTemplate msgLandingPage;	
	
	@Autowired
	private XFaceBatchService xFaceBatchService;
	//for hw api call
	@Autowired
	private HWAPIBatchService hwAPIService;
		
	private String transactionId = null;	
	//for sub/unsub alarm
	private XFaceVCMServiceImpl xFaceVCMServiceImpl;
	
	//for notification to UI
	private ArrayList<XFaceAlarmNotificationImpl> xFaceAlarmNotificationList;
	//for insert gate activity to db
	private ArrayList<XFaceHWGateActivityImpl> xFaceGateActivityList;
	//for insert/update customer register to db
	private ArrayList<XFaceCustomerRegisterImpl> xFaceCustomerRegisterList;
	
	//for add/remove person on vcm
	private XFaceVCMPersonManageImpl xFaceVCMPersonManageImpl;
	
	//for landing page info
	private XFaceLandingPageImpl xFaceLandingPageImpl;
	
	private List<HWVCM> hwVCMList = null;	
	private ArrayList<XFaceKeepAliveImpl> xFaceKeepAliveList;
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private boolean isStart = false;
	public XFaceServerServiceImpl() {				
		super("XFaceServer_"+new Date().getTime());
		this.transactionId = super.getName();	
	}
	@Override
	public void startService() {
		this.start();
	}
	@Override
	public void run() {					
		this.isTerminate = false;		
		this.isLoop = true;		
		this.isStart = false;
		this.hwAPIService.initialClass(this.transactionId);
		while (this.isLoop) {
			if (this.isStart) {
				//already start sleep forever
				OtherUtil.waitSecondXFaceServer(this, 10);
			}else {
				if (NetworkUtil.checkVIP()) {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "XFace server start"));
					//this.checkExtract();
					//1. read alarm hist table
					//this.getMaxAlarmDateTimeGroupByPerson();
					//3. prepare vcm environment
					//this.prepareVCM();
					//4. subscribe alarm
					//this.subscribeAlarm();
					//5. start process to notification 
					this.startAlarmNotification();
					//6. start keep alive service
					//this.startKeepAlive();
					//7. start add/remove vcm task
					 // ----------->>>>>
					this.startAddRemovePersonOnVCM();
					//8. start gate activity (passenger pass,..)
					//this.startGateActivity();
					//9. start customer register (put customer register in queue to db)
					//this.startCustomerRegister();
					//10. start landing page info
					this.startLandingPageInfo();
					this.isStart = true;
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "xface server start success"));
				}else {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "not a VIP server then sleep"));
					OtherUtil.waitSecond(1000);
				}
			}			
		}		
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out xface server service"));
	}	
	
	@Override
	public void restart() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to restart xFaceServer"));
		this.xFaceBatchService.purgeACCache();
		this.xFaceBatchService.purgeIPCCache();
		this.stopThread();
		this.isStart = false;		
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "XFace server restart successful"));
	}	
	@Override
	public String getTransactionId() {
		return this.transactionId;
	}
	private void stopThread() {
		this.unsubscribeAlarm();
		this.stopAlarmNotification();		
		this.stopKeepAlive();
		this.stopAddRemovePersonOnVCM();
		this.stopGateActivity();
		this.stopCustomerRegister();
		this.logOutVCM();
		this.stopLandingPageInfo();
	}		
	
	/*
	 * start-stop alarm notification
	 */
	private void startAlarmNotification() {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_THREAD_NO_ALARM_NOTIFICATION); 		
		int noOfThread = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue2(), 1);
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start alarm notification with no thread:"+noOfThread+" wait time:"+waitTime));
		this.xFaceAlarmNotificationList = new ArrayList<XFaceAlarmNotificationImpl>(); 
		for (int i=0; i<noOfThread; i++) {
			this.xFaceAlarmNotificationList.add(new XFaceAlarmNotificationImpl(transactionId, i+1, this.globalVarService
						, this.xFaceBatchService, waitTime, this.msgNotification, this.hwAPIService));
			this.xFaceAlarmNotificationList.get(i).start();
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start alarm notification with no thread:"+noOfThread+" wait time:"+waitTime+" success"));
	}
	private void stopAlarmNotification() {
		if (this.xFaceAlarmNotificationList==null || this.xFaceAlarmNotificationList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no alarm notification thread need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop alarm notification thread with no of thread is "+this.xFaceAlarmNotificationList.size()));
			for (XFaceAlarmNotificationImpl alarmThread: this.xFaceAlarmNotificationList) {
				alarmThread.stopServiceThread();
			}
			for (XFaceAlarmNotificationImpl alarmThread: this.xFaceAlarmNotificationList) {
				try {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for alarm notification thread name:"+alarmThread.getName()+" to stop"));
					alarmThread.join();					
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "alarm notification thread name:"+alarmThread.getName()+" stop successful"));
				}catch (Exception ex) {
					//Logger.error(this,LogUtil.getLogError(this.transactionId, "fail to stop vcm thread name:"+vcmThread.getName(), ex));
				}				
			}
			this.xFaceAlarmNotificationList.clear();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "all alarm notification thread are stop successful"));
		}	
	}
	//////////////////////////////////////////
	/*
	 * sub-unsub alarm
	 */
	private void subscribeAlarm() {
		//new on prepareVCM function		
		if (this.hwVCMList==null||this.hwVCMList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no VCM server configure"));
		}else {
			//refresh again
			this.hwVCMList = this.xFaceBatchService.getVCMAll();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+this.hwVCMList.size()+" VCM server need to subscribe alarm"));					
			this.xFaceVCMServiceImpl = new XFaceVCMServiceImpl(this.transactionId, this.hwVCMList, this.hwAPIService);				
			this.xFaceVCMServiceImpl.subscribeAlarm();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "subscribe alarm for "+this.hwVCMList.size()+" VCM with result:"+this.xFaceVCMServiceImpl.getResultStart().toString()));
		}
		
	}
	private void unsubscribeAlarm() {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in unsubscribeAlarm"));				
		if (this.hwVCMList!=null && this.hwVCMList.size()>0 && this.xFaceVCMServiceImpl!=null) {
			this.xFaceVCMServiceImpl.unSubscribeAlarm();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "unsubscribe alarm for "+this.hwVCMList.size()+" VCM server success"));
		}		
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out unsubscribeAlarm"));
	}
	//////////////////////////////////////////////////////
	/*
	 * get person id and last alram which system send to UI
	 * for make sure that system will not send alarm within time configure
	 */
	private void getMaxAlarmDateTimeGroupByPerson() {
		//86400000 = 24*60*60*1000
		ApplicationCfg appNoOfDateFilter = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ALARM_HIST_NOOFDAY_FILTER);
		Date startDate = new Date(new Date().getTime() - (StringUtil.stringToInteger(appNoOfDateFilter.getAppValue1(), 5)*86400000));
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load previouse alarm history"));
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "load previouse alarm history no of date is "+appNoOfDateFilter.getAppValue1()+" start date is "+startDate));
		List<LastAlarmPersonDateTime> lastAlarmPersonList = this.xFaceBatchService.findMaxAlarmHistoryGroupByPerson(startDate);
		this.globalVarService.clearLastAlarmPersonDateTime();
		this.globalVarService.clearLastSendAction();
		if (lastAlarmPersonList!=null) {
			for (LastAlarmPersonDateTime lapt:lastAlarmPersonList) {
				if (lapt.getPersonId()!=null) {
					this.globalVarService.addLastAlarmPersonDateTime(lapt.getPersonId(), lapt.getLastAlarmTime());
				}				
			}
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load previouse alarm history done with "+this.globalVarService.getSizeOfLastAlarmPersonDateTime()));
	}	
	/*
	 * start-stop gate activity
	 */
	private void startGateActivity() {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_THREAD_NO_GATE_ACTIVITY); 		
		int noOfThread = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue2(), 1);
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start gate activity with no thread:"+noOfThread+" wait time:"+waitTime));
		this.xFaceGateActivityList = new ArrayList<XFaceHWGateActivityImpl>(); 
		for (int i=0; i<noOfThread; i++) {
			//String transactionId, int threadNo, GlobalVarService globalVarService
			//, XFaceBatchService xFaceBatchService, int timeThreadSleep, HWAPIBatchService hwAPIService
			this.xFaceGateActivityList.add(new XFaceHWGateActivityImpl(this.transactionId, i+1, this.globalVarService, this.xFaceBatchService, noOfThread));
			this.xFaceGateActivityList.get(i).start();
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start gate activity with no thread:"+noOfThread+" wait time:"+waitTime+" success"));
	}
	private void stopGateActivity() {
		if (this.xFaceGateActivityList==null || this.xFaceGateActivityList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no gate activity thread need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop gate activity thread with no of thread is "+this.xFaceGateActivityList.size()));
			for (XFaceHWGateActivityImpl gateThread: this.xFaceGateActivityList) {
				gateThread.stopServiceThread();
			}
			for (XFaceHWGateActivityImpl gateThread: this.xFaceGateActivityList) {
				try {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for gate activity thread name:"+gateThread.getName()+" to stop"));
					gateThread.join();					
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "alarm gate activity thread name:"+gateThread.getName()+" stop successful"));
				}catch (Exception ex) {
					//Logger.error(this,LogUtil.getLogError(this.transactionId, "fail to stop vcm thread name:"+vcmThread.getName(), ex));
				}				
			}
			this.xFaceGateActivityList.clear();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "all gate activity thread are stop successful"));
		}	
	}
	/////////////////////////////////////////////////
	/*
	 * start-stop customer register
	 */
	private void startCustomerRegister() {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_THREAD_NO_CUSTOMER_REGISTER); 		
		int noOfThread = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue2(), 1);
		appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_DEFAULT_PERSON_CERT_TITLE_CATE);
		int defaultCertificateId = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		int defaultTitleId = StringUtil.stringToInteger(appCfg.getAppValue2(), 1);
		int defaultCategoryId = StringUtil.stringToInteger(appCfg.getAppValue3(), 1);
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start customer register with no thread:"+noOfThread+" wait time:"+waitTime));
		this.xFaceCustomerRegisterList = new ArrayList<XFaceCustomerRegisterImpl>(); 
		for (int i=0; i<noOfThread; i++) {
			this.xFaceCustomerRegisterList.add(new XFaceCustomerRegisterImpl(this.transactionId, i+1, this.globalVarService, this.xFaceBatchService, waitTime
					, defaultCertificateId, defaultTitleId, defaultCategoryId));
			this.xFaceCustomerRegisterList.get(i).start();
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start customer register with no thread:"+noOfThread+" wait time:"+waitTime+" success"));
	}
	private void stopCustomerRegister() {
		if (this.xFaceCustomerRegisterList==null || this.xFaceCustomerRegisterList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no customer register thread need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop customer register thread with no of thread is "+this.xFaceGateActivityList.size()));
			for (XFaceCustomerRegisterImpl customerThread: this.xFaceCustomerRegisterList) {
				customerThread.stopServiceThread();
			}
			for (XFaceCustomerRegisterImpl customerThread: this.xFaceCustomerRegisterList) {
				try {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for customer register thread name:"+customerThread.getName()+" to stop"));
					customerThread.join();					
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "customer register thread name:"+customerThread.getName()+" stop successful"));
				}catch (Exception ex) {
					//Logger.error(this,LogUtil.getLogError(this.transactionId, "fail to stop vcm thread name:"+vcmThread.getName(), ex));
				}				
			}
			this.xFaceCustomerRegisterList.clear();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "all customer register thread are stop successful"));
		}	
	}
	//////////////////////////////////
	private void logOutVCM() {		
		if (this.hwVCMList==null||this.hwVCMList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no VCM server configure"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+this.hwVCMList.size()+" VCM server need to log out"));
			for (HWVCM hwVCM:this.hwVCMList) {				
				this.hwAPIService.logOut(hwVCM);
				this.hwAPIService.logOutSDK(hwVCM);
			}
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "log out VCM success"));
		}				
	}
	
	/*
	 * start-stop keep alive service
	 * keep alive only when jsesion was found
	 */
	private void startKeepAlive() {		
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_HW_KEEP_ALIVE_SERVICE); 				
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		this.xFaceKeepAliveList = new ArrayList<>();
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start keep alive service noOfThread:"+this.hwVCMList.size()+" wait time:"+waitTime));		
		for (int i=0; i<this.hwVCMList.size(); i++) {		
			this.xFaceKeepAliveList.add(new XFaceKeepAliveImpl(this.transactionId, this.hwVCMList.get(i), this.globalVarService, waitTime, i, this.hwAPIService));
			this.xFaceKeepAliveList.get(i).start();
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start keep alive service noOfThread:"+this.hwVCMList.size()+" wait time:"+waitTime+" success"));
	}
	private void stopKeepAlive() {
		if (this.xFaceKeepAliveList==null || this.xFaceKeepAliveList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no keep alive thread need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop keep alive service with no of thread is "+this.xFaceKeepAliveList.size()));
			for (XFaceKeepAliveImpl keepAlive: this.xFaceKeepAliveList) {
				keepAlive.stopServiceThread();
			}
			for (XFaceKeepAliveImpl keepAlive: this.xFaceKeepAliveList) {
				try {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for keep alive service thread name:"+keepAlive.getName()+" to stop"));
					keepAlive.join();					
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for keep alive service thread name:"+keepAlive.getName()+" to stop success"));
				}catch (Exception ex) {
					//Logger.error(this,LogUtil.getLogError(this.transactionId, "fail to stop vcm thread name:"+vcmThread.getName(), ex));
				}				
			}
			this.xFaceKeepAliveList.clear();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "all keep alive thread are stop successful"));
		}
	}
	
	/*
	 * start-stop add/remove person on vcm
	 */
	private void startAddRemovePersonOnVCM() {
		////////////////////////////////////// 
		//if open all code no need to run below command coz it will be execute on subscriber alarm
		this.hwVCMList = this.xFaceBatchService.getVCMAll();
		///////////////////////////////////////
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start add remove person on VCM"));
		if (this.hwVCMList!=null || this.hwVCMList.size()>0) {
			this.xFaceVCMPersonManageImpl = new XFaceVCMPersonManageImpl(this.transactionId, this.xFaceBatchService, this.hwAPIService, this.hwVCMList.get(0));
			this.xFaceVCMPersonManageImpl.start();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start add remove person on VCM success"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "cannot start add/remove person on VCM coz no active VCM"));
		}
	}
	private void stopAddRemovePersonOnVCM() {
		if (this.xFaceVCMPersonManageImpl==null ) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no add/remove person on VCM need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop add/remove person on VCM"));
			this.xFaceVCMPersonManageImpl.stopServiceThread();
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "add/remove person on VCM successful"));
		}
	}
	
	/*
	 * start-stop landing page notification
	 */
	private void startLandingPageInfo() {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_LANDING_NOTIFICATION_SLEEP); 		
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue1(), 60);
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start landing page info wait time:"+waitTime));
		this.xFaceLandingPageImpl = new XFaceLandingPageImpl(this.transactionId, this.globalVarService, this.msgLandingPage
				, this.xFaceBatchService, waitTime);
		this.xFaceLandingPageImpl.start();
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start landing page info wait time:"+waitTime+" success"));
	}
	private void stopLandingPageInfo() {
		if (this.xFaceLandingPageImpl==null) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no landing page info need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop landing page info"));
			this.xFaceLandingPageImpl.stopServiceThread();
			try {
				Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for landing page info to stop"));
				this.xFaceLandingPageImpl.join();
			}catch (Exception ex) {
				
			}			
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "landing page info stop successful"));
		}	
	}
	////////////////////////////////////////
	private void prepareVCM() {
//		1. get list of camera from db which task_id is null
//		2. check if any camera no create task yet 
//		    2.1 create task for each camera which never create task
//		    2.2 update task_id to camera
//		    2.2 goto step 3
//		3. check if image library already have id
//		    3.1 If don't have Id, create image library and store id
//		    3.2 goto step 4
//		4. check if checkpoint already have id
//		    4.1 If don't have Id, create image library and store id
//		    4.2 goto step 5
//		5. get all camera from db which check point id is null
//		    5.1 if never add camera to checkpoint then add it
//		    5.2 goto step 6
//		6. check create task1 (hit)
//		    6.1 if never create task1 then create task1
//		    6.2 goto step 7
//		7. check create task2 (non-hit)
//		    7.1 if never create task2 then create task2
//		    7.2 goto step 8
//		8. subscribe task1
//		9. subscribe task2
		//check for camera which never create task on VCM
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in prepare VCM"));
		this.hwVCMList = this.xFaceBatchService.getVCMAll();		
//		loop thought VCM get all VCN
		for (HWVCM hwVCM: this.hwVCMList) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "working with VCM:"+hwVCM.getVcmName()));			
			//import camera
			this.importCamera(hwVCM);
			//check for camera which never create task on VCM
			this.createCameraTask(hwVCM);
			//check for face library which never create library on VCM
			this.createFaceLibrary(hwVCM);
			//check for check point which never create check point on VCM
			this.createCheckPoint(hwVCM);
			//check camera which never create check point on VCM
			this.attachedCameraToCheckPoint(hwVCM);			
			//check task which never create task on VCM
			this.createAnalyzeTask(hwVCM);
		} // end for (HWVCM hwVCM: this.hwVCMList)												
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out prepare VCM"));
	}
	//register missing camera
	private void importCamera(HWVCM hwVCM) {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in importCamera"));		
		HWWSField result = null;
		ResultStatus updateResult = null;				
		result = this.hwAPIService.queryCamera(hwVCM, null);
		HashMap<String, HWIPC> hwIPCCodeList = new HashMap<>();
		HWIPC hwIPC = null;
		int cntImportSuccess = 0;
		int cntImportFail = 0;
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_IMPORT_CAMERA_VCM_DEFAULT_VALUE);
		if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query camera success found:"+result.getQueryCameraRespList().size()+" camera."));
			//get list of existing camera
			Iterator<HWIPC> hwIPCList = null;			
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "get list of IPC under VCM:"+hwVCM.getVcmName()));			
			hwIPCList = hwVCM.getHwIPCList().iterator();
			while (hwIPCList.hasNext()) {
				hwIPC = hwIPCList.next();					
				Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found IPC name:"+hwIPC.getIpcName()+", code:"+hwIPC.getIpcCode()));
				hwIPCCodeList.put(hwIPC.getIpcCode(), hwIPC);
			}
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start import IPC from VCM to xFace"));			
			for (QueryCameraResp camera:result.getQueryCameraRespList()) {
				hwIPC = hwIPCCodeList.get(camera.getCameraSN());
				if (hwIPC==null) {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "camera name:"+camera.getCameraName()+", code:"+camera.getCameraSN()+" not found then import"));
					hwIPC = new HWIPC();
					hwIPC.setIpcCode(camera.getCameraSN());
					hwIPC.setIpcIp(camera.getCameraIP());
					hwIPC.setIpcName(camera.getCameraName());
					hwIPC.setReceiveAlarm(BooleanUtil.TRUE_VALUE);
					//2=face blacklist, 3=face whitelist, 4=face redlist
					hwIPC.setImageLibraryType(StringUtil.stringToInteger(appCfg.getAppValue1(), 3)); //default 3
					hwIPC.setUserCreated("fr_system@xpand.asia");
					hwIPC.setUserUpdated("fr_system@xpand.asia");
					hwIPC.setIgnoreUnknownAlarm(BooleanUtil.FALSE_VALUE);
					hwIPC.setTaskPrefix("task_");
					//from HW we have to put value is 2
					hwIPC.setTaskType(StringUtil.stringToInteger(appCfg.getAppValue2(), 2)); //default 2
					//from HW we have to put value is 0
					hwIPC.setAnalyzeMode(StringUtil.stringToInteger(appCfg.getAppValue3(), 0)); //default 0
					hwIPC.setHwVCM(hwVCM);
					hwIPC.setHwCheckPointLibrary(this.xFaceBatchService.getCPLOneObject());
					//set null to gate and wait for user to associate gate with IPC
					hwIPC.setHwGateInfo(null);
					hwIPC.setIpcStatus(StringUtil.stringToInteger(camera.getCameraState(), HWIPC.IPC_STATUS_OFFLINE));					
					updateResult = this.xFaceBatchService.updateIPC(this.transactionId, hwIPC);
					if (updateResult.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
						cntImportSuccess++;
					}else {
						cntImportFail++;
					}
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "import camera name:"+camera.getCameraName()+", code:"+camera.getCameraSN()+" with result:"+updateResult.toString()));					
				}else {
					//update status
					hwIPC.setIpcStatus(StringUtil.stringToInteger(camera.getCameraState(), HWIPC.IPC_STATUS_OFFLINE));
					updateResult = this.xFaceBatchService.updateIPCStatus(this.transactionId, hwIPC);
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "camera name:"+camera.getCameraName()+", code:"+camera.getCameraSN()+" already exist and status is "+camera.getCameraState()));														
				}
			}
		}else if (ResultStatus.NO_DATA_FOUND_ERROR_CODE.equals(result.getResult().getStatusCode())) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no camera install on VCM:"+hwVCM.getVcmName()));			
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query camera fail "+result.getResult().toString()));
		}		
		if (cntImportSuccess>0) {
			this.xFaceBatchService.purgeIPCCache();
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out importCamera import success:"+cntImportSuccess+", import fail:"+cntImportFail));
	}
	
	//check for camera which never create task on VCM
	private void createCameraTask(HWVCM hwVCM) {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in createCameraTask"));
		List<HWIPC> hwIPCList = null;
		HWWSField result = null;
		ResultStatus updateResult = null;
		hwIPCList = this.xFaceBatchService.getIPCNeverCreateTaskList(hwVCM);						
		if (hwIPCList!=null && hwIPCList.size()>0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+hwIPCList.size()+" camera need to create task"));
			for (HWIPC hwIPC: hwIPCList) {
				result = this.hwAPIService.queryIntelligentAnalysisTasks(hwIPC);
				if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
					//camera already create task then query and update taskId
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query task id:"+result.getQueryIntelligentTaskId()+" for camera:"+hwIPC.getIpcCode()+" success"));
					//update hwIPCList with task name						
					hwIPC.setIpcTaskId(result.getQueryIntelligentTaskId());
					updateResult = this.xFaceBatchService.updateIPCTaskId(this.transactionId, hwIPC);
					if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())){
						hwIPC.setIpcTaskId(null);
					}	
				}else if (ResultStatus.NO_DATA_FOUND_ERROR_CODE.equals(result.getResult().getStatusCode())) {
					//camera never create atsk then create task and update taskId
					result = this.hwAPIService.addIntelligentAnalysisTasks(hwIPC);
					if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "create task id:"+result.getAddIntelligentAnalsisBatchesResp().getTaskId()+" for camera:"+hwIPC.getIpcCode()+" success"));
						//update hwIPCList with task name						
						hwIPC.setIpcTaskId(result.getAddIntelligentAnalsisBatchesResp().getTaskId());
						updateResult = this.xFaceBatchService.updateIPCTaskId(this.transactionId, hwIPC);
						if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())){
							hwIPC.setIpcTaskId(null);
						}														
					}else {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "create camera "+hwIPC.getIpcCode()+" task fail "+result.getResult().toString()));
					}
				}else {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query camera task "+hwIPC.getIpcCode()+" fail "+result.getResult().toString()));
				}
			}							
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "not found camera need to create task"));
		}		
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out createCameraTask"));
	}
	
	//check for face library which never create library on VCM
	private void createFaceLibrary(HWVCM hwVCM) {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in createFaceLibrary"));
		HWWSField result = null;
		ResultStatus updateResult = null;
		List<HWCheckPointLibrary> hwCheckPointLibraryList = null;
		hwCheckPointLibraryList = this.xFaceBatchService.getCPLNeverCreateLibrary(hwVCM);
		if (hwCheckPointLibraryList!=null && hwCheckPointLibraryList.size()>0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+hwCheckPointLibraryList.size()+" face library need to create"));
			for (HWCheckPointLibrary hwLibrary: hwCheckPointLibraryList) {
				result = this.hwAPIService.queryFaceList(hwLibrary);
				if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "face library "+hwLibrary.getLibraryName()+" query with id "+result.getQueryFaceListResp().getFaceListId()));
					hwLibrary.setLibraryId(result.getQueryFaceListResp().getFaceListId());
					//update library
					updateResult = this.xFaceBatchService.updateCPLLibraryId(this.transactionId, hwLibrary);
					if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
						hwLibrary.setLibraryId(null);
					}
				}else if (ResultStatus.NO_DATA_FOUND_ERROR_CODE.equals(result.getResult().getStatusCode())) {
					result = this.hwAPIService.addFaceList(hwLibrary);
					if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "face library "+hwLibrary.getLibraryName()+" create with id "+result.getAddFaceListId()));
						hwLibrary.setLibraryId(result.getAddFaceListId());
						//update library
						updateResult = this.xFaceBatchService.updateCPLLibraryId(this.transactionId,hwLibrary);
						if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
							hwLibrary.setLibraryId(null);
						}
					}else {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "face library "+hwLibrary.getLibraryName()+" create fail "+result.getResult().toString()));
					}
				}else {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "face library "+hwLibrary.getLibraryName()+" query fail "+result.getResult().toString()));
				}
			}			
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "not found face library need to create"));
		}		
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out createFaceLibrary"));
	}
	
	//check for check point which never create check point on VCM
	private void createCheckPoint(HWVCM hwVCM) {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in createCheckPoint"));
		HWWSField result = null;
		ResultStatus updateResult = null;
		List<HWCheckPointLibrary> hwCheckPointLibraryList = null;
		hwCheckPointLibraryList = this.xFaceBatchService.getCPLNeverCreateCheckPoint(hwVCM);
		if (hwCheckPointLibraryList!=null && hwCheckPointLibraryList.size()>0) {
			for (HWCheckPointLibrary hwCheckPoint: hwCheckPointLibraryList) {
				if (StringUtil.checkNull(hwCheckPoint.getLibraryId())) {
					result = new HWWSField();
					result.setResult(new ResultStatus(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE, "face library "+hwCheckPoint.getLibraryName()+" not create yet then no need to create check point :"+hwCheckPoint.getCheckPointName()));
				}else {
					result = this.hwAPIService.queryCheckPoint(hwCheckPoint, null);
					if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query check point "+hwCheckPoint.getCheckPointName()+" with id "+result.getQueryCheckPointResp().getCheckPointSN()));
						hwCheckPoint.setCheckPointId(result.getQueryCheckPointResp().getCheckPointSN());
						updateResult = this.xFaceBatchService.updateCPLCheckPointId(this.transactionId,hwCheckPoint);
						if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
							hwCheckPoint.setCheckPointId(null);
						}
					}else if (ResultStatus.NO_DATA_FOUND_ERROR_CODE.equals(result.getResult().getStatusCode())) {
						result = this.hwAPIService.addCheckPoint(hwCheckPoint);
						if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
							Logger.info(this,LogUtil.getLogInfo(this.transactionId, "check point "+hwCheckPoint.getCheckPointName()+" create with id "+result.getCameraCheckPointSN()));
							hwCheckPoint.setCheckPointId(result.getCameraCheckPointSN());
							updateResult = this.xFaceBatchService.updateCPLCheckPointId(this.transactionId,hwCheckPoint);
							if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
								hwCheckPoint.setCheckPointId(null);
							}
						}else {
							Logger.info(this,LogUtil.getLogInfo(this.transactionId, "check point "+hwCheckPoint.getCheckPointName()+" create fail "+result.getResult().toString()));
						}
					}else {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "check point "+hwCheckPoint.getCheckPointName()+" query fail "+result.getResult().toString()));
					}
				}					
			}			
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "not found check point need to create"));
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out createCheckPoint"));
	}
	
	//check camera which never create check point on VCM
	private void attachedCameraToCheckPoint(HWVCM hwVCM) {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in attachedCameraToCheckPoint"));
		List<HWIPC> hwIPCList = null;
		HWWSField result = null;
		ResultStatus updateResult = null;
		hwIPCList = this.xFaceBatchService.getIPCNeverAddToCheckPoint(hwVCM);		
		if (hwIPCList!=null && hwIPCList.size()>0) {
			for (HWIPC hwIPC: hwIPCList) {
				if (StringUtil.checkNull(hwIPC.getHwCheckPointLibrary().getCheckPointId())){
					result = new HWWSField();
					result.setResult(new ResultStatus(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE, "check point :"+hwIPC.getHwCheckPointLibrary().getCheckPointName()+" never create yet then no need to add camera:"+hwIPC.getIpcName()));
				}else {
					result = this.hwAPIService.queryCheckPoint(hwIPC.getHwCheckPointLibrary(), hwIPC);
					if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query camera "+hwIPC.getIpcCode()+" check point name "+result.getQueryCheckPointResp().getCheckPointName()+" check point Id "+result.getQueryCheckPointResp().getCheckPointSN()));					
						hwIPC.setCheckPointId(result.getQueryCheckPointResp().getCheckPointSN());
						updateResult = this.xFaceBatchService.updateIPCCheckPointId(this.transactionId,hwIPC);
						if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
							hwIPC.setCheckPointId(null);
						}
					}else if (ResultStatus.NO_DATA_FOUND_ERROR_CODE.equals(result.getResult().getStatusCode())) {
						result = this.hwAPIService.addCameraToCheckPoint(hwIPC);
						if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
							Logger.info(this,LogUtil.getLogInfo(this.transactionId, "camera "+hwIPC.getIpcName()+" add to check point "+hwIPC.getHwCheckPointLibrary().getCheckPointName()));					
							hwIPC.setCheckPointId(hwIPC.getHwCheckPointLibrary().getCheckPointId());
							updateResult = this.xFaceBatchService.updateIPCCheckPointId(this.transactionId,hwIPC);
							if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
								hwIPC.setCheckPointId(null);
							}
						}else {
							Logger.info(this,LogUtil.getLogInfo(this.transactionId, "camera "+hwIPC.getIpcName()+" add to check point fail "+result.getResult().toString()));
						}
					}else {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query camera "+hwIPC.getIpcName()+" check point fail "+result.getResult().toString()));
					}						
				}					
			}
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "not found camera need to add to check point"));
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out attachedCameraToCheckPoint"));
	}
	
	//check task which never create task on VCM
	private void createAnalyzeTask(HWVCM hwVCM) {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in createAnalyzeTask"));
		HWWSField result = null;
		ResultStatus updateResult = null;
		List<HWTaskList> hwTaskLists = null;
		hwTaskLists = this.xFaceBatchService.getTLNeverCreateTaskList(hwVCM);
		if (hwTaskLists!=null && hwTaskLists.size()>0) {
			for (HWTaskList hwTaskList: hwTaskLists) {
				if (StringUtil.checkNull(hwTaskList.getHwCheckPointLibrary().getLibraryId()) || StringUtil.checkNull(hwTaskList.getHwCheckPointLibrary().getCheckPointId())) {
					result = new HWWSField();
					result.setResult(new ResultStatus(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE, "library:"+hwTaskList.getHwCheckPointLibrary().getLibraryName()+" id:"+hwTaskList.getHwCheckPointLibrary().getLibraryId()+" check point:"+hwTaskList.getHwCheckPointLibrary().getCheckPointName()+" id:"+hwTaskList.getHwCheckPointLibrary().getCheckPointId()+" then no need to add suspend task:"+hwTaskList.getTaskName()));
				}else {
					result = this.hwAPIService.querySuspectTask(hwTaskList);
					if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query task list "+hwTaskList.getTaskName()+" with id "+result.getQuerySuspectTaskResp().getSuspectId()));
						hwTaskList.setTaskId(result.getQuerySuspectTaskResp().getSuspectId());
						updateResult = this.xFaceBatchService.updateTLTaskId(this.transactionId, hwTaskList);
						if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
							hwTaskList.setTaskId(null);
						}
					}else if (ResultStatus.NO_DATA_FOUND_ERROR_CODE.equals(result.getResult().getStatusCode())) {
						result = this.hwAPIService.addSuspectTask(hwTaskList);
						if (ResultStatus.SUCCESS_CODE.equals(result.getResult().getStatusCode())) {
							Logger.info(this,LogUtil.getLogInfo(this.transactionId, "task list "+hwTaskList.getTaskName()+" create with id "+result.getAddSuspectTaskId()));
							hwTaskList.setTaskId(result.getAddSuspectTaskId());
							updateResult = this.xFaceBatchService.updateTLTaskId(this.transactionId, hwTaskList);
							if (!ResultStatus.SUCCESS_CODE.equals(updateResult.getStatusCode())) {
								hwTaskList.setTaskId(null);
							}
						}else {
							Logger.info(this,LogUtil.getLogInfo(this.transactionId, "create task list "+hwTaskList.getTaskName()+" fail "+result.getResult().toString()));
						}
					}else {
						Logger.info(this,LogUtil.getLogInfo(this.transactionId, "query task list "+hwTaskList.getTaskName()+" task fail "+result.getResult().toString()));
					}						
				}										
			}
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "not found task list need to create"));
		}
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "out createAnalyzeTask"));
	}
	@Override
	public boolean isServiceRunning() {
		return this.isLoop;
	}
	@Override
	public void stopService() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to stop xFaceServer"));
		this.isLoop = false;
		this.isTerminate = false;		
		this.globalVarService.closeAllWebSocket();
		this.stopThread();
		this.isTerminate = true;
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "XFace server stop successful"));		
	}
	@Override
	public boolean isTerminate() {
		return this.isTerminate;
	}
	@Override
	public void stopServiceThread() {
		this.stopService();		
	}
	private void checkExtract() {
		try {
//			String wsResp = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//						+ "<response>"
//						+ "<result>"
//						+ "<errmsg></errmsg>"
//						+ "<code>0</code>"
//						+ "</result>"
//						+ "<algorithmResults>"
//						+ "<algorithmResult>"
//						+ "<algorithmCode>0104000100</algorithmCode>"
//						+ "<peoplefaceinfos>"
//						+ "<peoplefaceinfo>"
//						+ "<cameraName>X1221-F</cameraName>"
//						+ "<cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>"
//						+ "<confidence>96</confidence>"
//						+ "<fileId>258d5f6fc0c549e480a824e1097dbbe0#2403785860353703936@10</fileId>"
//						+ "<pos>"
//						+ "<bottom>1063</bottom>"
//						+ "<left>1565</left>"
//						+ "<right>1774</right>"
//						+ "<top>857</top>"
//						+ "</pos>"
//						+ "<sourceDevice>1</sourceDevice>"
//						+ "<recordTime>1542880971727</recordTime>"
//						+ "</peoplefaceinfo>"
//						+ "<peoplefaceinfo>"
//						+ "<cameraName>X1221-F</cameraName>"
//	                    + "<cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>"
//	                    + "<confidence>98</confidence>"
//	                    + "<fileId>258d5f6fc0c549e480a824e1097dbbe0#4493451658671243264@10</fileId>"
//	                    + "<pos>"
//	                    + "<bottom>1066</bottom>"
//	                    + "<left>1523</left>"
//	                    + "<right>1717</right>"
//	                    + "<top>888</top>"
//	                    + "</pos>"
//	                    + "<sourceDevice>1</sourceDevice>"
//	                    + "<recordTime>1542617002046</recordTime>"
//	                    + "</peoplefaceinfo>"
//	                    + "<peoplefaceinfo>"
//	                    + "<cameraName>X1221-F</cameraName>"
//	                    + "<cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>"
//	                    + "<confidence>98</confidence>"
//	                    + "<fileId>258d5f6fc0c549e480a824e1097dbbe0#4349336470276620288@10</fileId>"
//	                    + "<pos>"
//	                        + "<bottom>1066</bottom>"
//	                        + "<left>1352</left>"
//	                        + "<right>1568</right>"
//	                        + "<top>873</top>"
//	                    + "</pos>"
//	                    + "<sourceDevice>1</sourceDevice>"
//	                    + "<recordTime>1542616983052</recordTime>"
//	                    + "</peoplefaceinfo>"
//						+ "</peoplefaceinfos>"
//						+ "<result>"
//						+ "<errmsg></errmsg>"
//						+ "<code>0</code>"
//						+ "</result>"
//						+ "<total>1</total>"
//						+ "</algorithmResult>"
//						+ "</algorithmResults>"
//						+ "</response>";
//			HWXMLUtil.extractQueryPersonByPhoto("transactionId", wsResp, "0", "");
//			String wsResp = "<response>"
//					+ "<result>"
//					+ "<errmsg>Success.</errmsg>"
//					+ "<code>0</code>"
//					+ "</result>"
//					+ "<algorithms>"
//						+ "<algorithm>"
//							+ "<chlParam></chlParam>"
//							+ "<code>0104000100</code>"
//							+ "<cpu>3</cpu>"
//							+ "<disk>2</disk>"
//							+ "<initParam></initParam>"
//							+ "<installDate>2018-10-09 20:49:58</installDate>"
//							+ "<license></license>"
//							+ "<mem>2</mem>"
//							+ "<name>yitu_face_sdk</name>"
//							+ "<status>enable</status>"
//							+ "<supplier>yitu</supplier>"
//							+ "<type>4</type>"
//							+ "<version>1.0.0</version>"
//						+ "</algorithm>"
//						+ "<algorithm>"
//							+ "<chlParam></chlParam>"
//							+ "<code>4104000101</code>"
//							+ "<cpu>3</cpu>"
//							+ "<disk>2</disk>"
//							+ "<initParam></initParam>"
//							+ "<installDate>2018-10-09 20:49:58</installDate>"
//							+ "<license></license>"
//							+ "<mem>2</mem>"
//							+ "<name>yitu_face_sdk</name>"
//							+ "<status>enable</status>"
//							+ "<supplier>yitu</supplier>"
//							+ "<type>4</type>"
//							+ "<version>1.0.0</version>"
//						+ "</algorithm>"
//					+ "</algorithms>"
//					+ "</response>";					
//			HWXMLUtil.extractQueryAlgorithm("transactionId", wsResp, "0", "");
			String wsResp = "<response>" + 
					"	<peoplefaces>" + 
					"		<peopleface>" + 
					"			<age>3</age>" + 
					"			<cameraName>26500060000000000101</cameraName>" + 
					"			<cameraSn>26500060000000000101#007a6a0499b844638288064faf520891" + 
					"			</cameraSn>" + 
					"			<confidence>0</confidence>" + 
					"			<domainCode>cb08eab831134bdab1b5929ca2d88715</domainCode>" + 
					"			<domainName>121 lower-level domain</domainName>" + 
					"			<gender>2</gender>" + 
					"			<lowerColor>1</lowerColor>" + 
					"			<lowerStyle>0</lowerStyle>" + 
					"			<otype>4</otype>" + 
					"			<picUrl></picUrl>" + 
					"			<downloadId>6a754ec8-254d-4d9f-955d-d94bb7a0e2ff</downloadId>" + 
					"			<historyFileId>5b30e5adb45caf43a5f1fcf9</historyFileId>" + 
					"			<fileId>cb08eab831134bdab1b5929ca2d88715#78763905564451076@12" + 
					"			</fileId>" + 
					"			<pos>" + 
					"				<bottom>1282</bottom>" + 
					"				<left>1742</left>" + 
					"				<right>1994</right>" + 
					"				<top>767</top>" + 
					"			</pos>" + 
					"			<sourceDevice>1</sourceDevice>" + 
					"			<recordTime>1523862041290</recordTime>" + 
					"			<upperColor>1</upperColor>" + 
					"			<upperStyle>1</upperStyle>" + 
					"			<upperTexture>1</upperTexture>" + 
					"		</peopleface>" + 
					"		<peopleface>" + 
					"			<age>3</age>" + 
					"			<cameraName>2650006000000000011111111</cameraName>" + 
					"			<cameraSn>26500060000000000101#54584221234564564</cameraSn>" + 
					"			<confidence>0</confidence>" + 
					"			<domainCode>cb08eab831134bdab1b5929ca2d88715</domainCode>" + 
					"			<domainName>121 lower-level domain</domainName>" + 
					"			<gender>2</gender>" + 
					"			<lowerColor>1</lowerColor>" + 
					"			<lowerStyle>0</lowerStyle>" + 
					"			<otype>4</otype>" + 
					"			<picUrl></picUrl>" + 
					"			<downloadId>6a754ec8-254d-4d9f-955d-d94bb7a0e2ff</downloadId>" + 
					"			<historyFileId>5b30e5adb45caf43a5f1fcf9</historyFileId>" + 
					"			<fileId>cb08eab831134bdab1b5929ca2d88715#7815151613212</fileId>" + 
					"			<pos>" + 
					"				<bottom>1</bottom>" + 
					"				<left>2</left>" + 
					"				<right>3</right>" + 
					"				<top>4</top>" + 
					"			</pos>" + 
					"			<sourceDevice>1</sourceDevice>" + 
					"			<recordTime>1523862041290</recordTime>" + 
					"			<upperColor>1</upperColor>" + 
					"			<upperStyle>1</upperStyle>" + 
					"			<upperTexture>1</upperTexture>" + 
					"		</peopleface>" + 
					"	</peoplefaces>" + 
					"	<result>" + 
					"		<errmsg />" + 
					"		<code>0</code>" + 
					"	</result>" + 
					"	<total>1</total>" + 
					"</response>";
			HWXMLUtil.extractQueryPersonByPhoto("", wsResp, "0", "");
		}catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
//	private void shutdownServer() {
//		try { 
//		    Socket socket = new Socket("localhost", 8005); 
//		    if (socket.isConnected()) { 
//		        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true); 
//		        pw.println("SHUTDOWN");//send shut down command 
//		        pw.close(); 
//		        socket.close(); 
//		    } 
//		} catch (Exception e) { 
//		    e.printStackTrace(); 
//		}
//	}		
	@Override
	public boolean isServiceReStart() {
		return this.isStart;
	}
}
