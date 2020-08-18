package com.xpand.xface.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.HWAPIService;
import com.xpand.xface.service.HWAlarmHistService;
import com.xpand.xface.service.HWIPCAnalyzeListService;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.service.HWVCMService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.NetworkUtil;
import com.xpand.xface.util.StringUtil;

//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class XFaceServerServiceImpl implements XFaceServerService {
	
	@Autowired
	HWVCMService hwVCMService;
	@Autowired
	ApplicationCfgService applicationCfgService;
	@Autowired
	GlobalVarService globalVarService;		
	@Autowired
	HWAlarmHistService hwAlarmHistService;
	@Autowired
	PersonInfoService personInfoService;
	@Autowired
	HWIPCService hwIPCService;
	@Autowired
	HWIPCAnalyzeListService hwIPCAnalyzeListService;
	
	//for receive gate information
	@Autowired
	@Qualifier("HWGateServiceImpl")
	private HWGateServiceImpl hwGateInfoServiceImpl;
	
//	@Value("${xfaceserver.wait_for_next_activity}")
//	private int waitForNextActivity;			
	private String transactionId = null;	
	//for sub/unsub alarm
	private XFaceVCMServiceImpl xFaceVCMServiceImpl;
	//for hw api call
	private HWAPIService hwAPIService;
	//for notification to UI
	private ArrayList<XFaceAlarmNotificationImpl> xFaceAlarmNotificationList;	
	private List<HWVCM> hwVCMList = null;	
	private ArrayList<XFaceKeepAliveImpl> xFaceKeepAliveList;
	
	public XFaceServerServiceImpl() {				
		this.transactionId = "XFaceServer_"+new Date().getTime();
	}
	@Override
	public void start() {			
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "XFace server start"));
		//0. read alarm hist table
		this.getMaxAlarmDateTimeGroupByPerson();
		//1. start gate service
		this.startGateService();
		//2. subscribe alarm
		this.subscribeAlarm();
		//3. start process to notification 
		this.startAlarmNotification();
		//4. start keep alive service
		this.startKeepAlive();
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "xface server start success"));
	}	
	
	@Override
	public void restart() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to restart xFaceServer"));
		this.applicationCfgService.purgeCache();
		this.unsubscribeAlarm();		
		this.stopThread();
		this.start();
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "XFace server restart successful"));
	}
	@Override
	public void stop() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to stop xFaceServer"));				
		this.unsubscribeAlarm();
		this.globalVarService.closeAllWebSocket();
		this.stopThread();
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "XFace server stop successful"));
	}
	@Override
	public String getTransactionId() {
		return this.transactionId;
	}
	private void stopThread() {				
		this.stopAlarmNotification();
		this.stopGateService();
		this.stopKeepAlive();
		this.logOutVCM();
	}		
	
	/*
	 * start-stop alarm notification
	 */
	private void startAlarmNotification() {
		ApplicationCfg appCfg = this.applicationCfgService.findByAppKey(ApplicationCfg.KEY_THREAD_NO_ALARM_NOTIFICATION); 		
		int noOfThread = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue2(), 1);
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start alarm notification with no thread:"+noOfThread+" wait time:"+waitTime));
		this.xFaceAlarmNotificationList = new ArrayList<XFaceAlarmNotificationImpl>(); 
		for (int i=0; i<noOfThread; i++) {
			this.xFaceAlarmNotificationList.add(new XFaceAlarmNotificationImpl(transactionId, i+1, this.globalVarService
						, this.hwAlarmHistService, this.applicationCfgService, this.personInfoService, this.hwIPCService, waitTime));
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
				alarmThread.stopService();
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
		if (NetworkUtil.checkVIP()) {
			this.hwVCMList = this.hwVCMService.getAll();
			if (this.hwVCMList==null||this.hwVCMList.size()==0) {
				Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no VCM server configure"));
			}else {
				Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+this.hwVCMList.size()+" VCM server need to subscribe alarm"));
				this.hwAPIService = new HWAPIServiceImpl();
				this.xFaceVCMServiceImpl = new XFaceVCMServiceImpl(this.transactionId, this.hwVCMList
						, this.applicationCfgService.getAllInHashMap()
						, this.globalVarService, this.hwAPIService, this.hwIPCAnalyzeListService);				
				this.xFaceVCMServiceImpl.subscribeAlarm();
				Logger.info(this,LogUtil.getLogInfo(this.transactionId, "subscribe alarm for "+this.hwVCMList.size()+" VCM with result:"+this.xFaceVCMServiceImpl.getResultStart().toString()));
			}			
		}	
	}
	private void unsubscribeAlarm() {
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "in unsubscribeAlarm"));
		if (NetworkUtil.checkVIP()) {					
			if (this.hwAPIService==null) {
				this.hwVCMList = this.hwVCMService.getAll();
				if (this.hwVCMList==null||this.hwVCMList.size()==0) {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no VCM server configure"));
				}else {
					Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+this.hwVCMList.size()+" VCM server need to unsubscribe alarm"));
					this.hwAPIService = new HWAPIServiceImpl();				
					this.xFaceVCMServiceImpl = new XFaceVCMServiceImpl(this.transactionId, this.hwVCMService.getAll()
							, this.applicationCfgService.getAllInHashMap()
							, this.globalVarService, this.hwAPIService, this.hwIPCAnalyzeListService);
				}
			}					
			if (this.hwVCMList!=null && this.hwVCMList.size()>0) {
				this.xFaceVCMServiceImpl.unSubscribeAlarm();
				Logger.info(this,LogUtil.getLogInfo(this.transactionId, "unsubscribe alarm for "+this.hwVCMList.size()+" VCM server success"));
			}
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
		ApplicationCfg appNoOfDateFilter = this.applicationCfgService.findByAppKey(ApplicationCfg.KEY_ALARM_HIST_NOOFDAY_FILTER);
		Date startDate = new Date(new Date().getTime() - (StringUtil.stringToInteger(appNoOfDateFilter.getAppValue1(), 5)*86400000));
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load previouse alarm history"));
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "load previouse alarm history no of date is "+appNoOfDateFilter.getAppValue1()+" start date is "+startDate));
		List<LastAlarmPersonDateTime> lastAlarmPersonList = this.hwAlarmHistService.findMaxAlarmGroupByPerson(startDate);
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
	 * start-stop gate service
	 */
	private void startGateService() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "in start gate service"));
		ApplicationCfg ipService = this.applicationCfgService.findByAppKey(ApplicationCfg.KEY_GATE_IP_SERVICE);
		this.hwGateInfoServiceImpl.initClass(this.transactionId, ipService.getAppValue1(), StringUtil.stringToInteger(ipService.getAppValue2(), 61005));
		ResultStatus result = this.hwGateInfoServiceImpl.performTask();
		if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {
			this.hwGateInfoServiceImpl.start();
		}else {
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start gate service fail with result:"+result.toString()));
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "out start gate service"));
	}
	private void stopGateService() {
		if (this.hwGateInfoServiceImpl==null) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no gate service thread need to stop"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "send reqeust to stop gate service"));
			this.hwGateInfoServiceImpl.stopService();			
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for gate service thread to stop"));
			try {
				this.hwGateInfoServiceImpl.join();				
			}catch (Exception ex) {}										
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "wait for gate service thread stop success"));			
		}	
	}
	/////////////////////////////////////////////////
	private void logOutVCM() {		
		if (this.hwVCMList==null||this.hwVCMList.size()==0) {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "no VCM server configure"));
		}else {
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "found "+this.hwVCMList.size()+" VCM server need to log out"));
			for (HWVCM hwVCM:this.hwVCMList) {
				this.hwAPIService.initialClass(this.transactionId, this.applicationCfgService.getAllInHashMap()
						, hwVCM, this.globalVarService);
				this.hwAPIService.logOut();
			}
			Logger.info(this,LogUtil.getLogInfo(this.transactionId, "log out VCM success"));
		}				
	}
	
	/*
	 * start-stop keep alive service
	 * keep alive only when jsesion was found
	 */
	private void startKeepAlive() {		
		ApplicationCfg appCfg = this.applicationCfgService.findByAppKey(ApplicationCfg.KEY_HW_KEEP_ALIVE_SERVICE); 				
		int waitTime = StringUtil.stringToInteger(appCfg.getAppValue1(), 1);
		this.xFaceKeepAliveList = new ArrayList<>();
		Logger.info(this,LogUtil.getLogInfo(this.transactionId, "start keep alive service noOfThread:"+this.hwVCMList.size()+" wait time:"+waitTime));		
		for (int i=0; i<this.hwVCMList.size(); i++) {
			this.xFaceKeepAliveList.add(new XFaceKeepAliveImpl(this.transactionId, this.applicationCfgService.getAllInHashMap()
					, this.hwVCMList.get(i), this.globalVarService, waitTime, i));
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
				keepAlive.stopService();
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
}
