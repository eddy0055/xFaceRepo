package com.xpand.xface.service.batch.impl;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.bean.landing.LandingPageInfo;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;


/*
 * class to check alarm in queue and process
 * 1. send notification to UI
 * 2. execute action code
 * 3. move alarm from queue to db
 */
public class XFaceLandingPageImpl extends Thread implements BaseXFaceThreadService{
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private String transactionId;	
	private int timeThreadSleep;			
	private GlobalVarService globalVarService;			
	private XFaceBatchService xFaceBatchService;
	private SimpMessagingTemplate msgTemplate;
	public XFaceLandingPageImpl(String transactionId, GlobalVarService globalVarService, SimpMessagingTemplate msgTemplate
				, XFaceBatchService xFaceBatchService, int timeThreadSleep) {
		super(transactionId+"_XFaceLandingPageImpl");		
		this.transactionId = super.getName();
		this.globalVarService = globalVarService;		
		this.timeThreadSleep = timeThreadSleep;		
		this.xFaceBatchService = xFaceBatchService;	
		this.msgTemplate = msgTemplate;
	}
	@Override
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start XFaceLandingPageImpl thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		ApplicationCfg appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_DEMONTASK_LIMIT_PRINT_LOG);
		int limitPrintLog = StringUtil.stringToInteger(appTmp.getAppValue1(), 3000); 
		int cntPrintLog = 0;
		LandingPageInfo landingPageInfo = null;
		////////////////////		
		while (this.isLoop) {
			OtherUtil.waitSecond(this, this.timeThreadSleep);
			cntPrintLog++;
			if (cntPrintLog>limitPrintLog) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "landing page info thread running"));
			}							
			landingPageInfo = this.xFaceBatchService.getLandingPageInfo(this.transactionId);
			if (landingPageInfo!=null) {
				//send data over web socket
				this.sendNotification(landingPageInfo);				
			}
			if (cntPrintLog>limitPrintLog) {				
				cntPrintLog = 0;
			}			
		}
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop XFaceLandingPageImpl thread ["+super.getName()+"]"));
	}
	private ResultStatus sendNotification(LandingPageInfo landingPageInfo) {						
		WebSocketHolder wsh = null;
		//send alarm to all user under webSocketHolder
		KeySetView<String, WebSocketHolder>  webSocketList = this.globalVarService.getWebSocketHolderList().keySet();
		Iterator<String> keyWebSocket = webSocketList.iterator();
		if (!keyWebSocket.hasNext()) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no websocket found no need to send alarm"));
			return new ResultStatus(ResultStatus.NO_WSH_NEED_TO_SEND_ALARM_CODE, null);
		}		
		String userNameNSessionId = null;		
		while (keyWebSocket.hasNext()) {
			userNameNSessionId = keyWebSocket.next();
			wsh = this.globalVarService.getWebSocketHolder(userNameNSessionId);
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "sending notification to user "+userNameNSessionId));
			if (wsh==null) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "web socket for user "+userNameNSessionId+" not found"));
			}else if (wsh.isMarkDelete()) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "web socket for user "+userNameNSessionId+" mark for delete then send goodby cmd"));
				this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userNameNSessionId+"/"+wsh.getWebSocketModule(), ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
				this.globalVarService.removeWebSocketHolder(userNameNSessionId);
			}else if (wsh.getWebSocketModule()==ConstUtil.WEBSOCKET_MODULE_LANDING_PAGE) {
				//send alarm
				this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userNameNSessionId+"/"+wsh.getWebSocketModule(), landingPageInfo);
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "sent landing page info to "+userNameNSessionId));
			}else if (wsh.getWebSocketModule()==ConstUtil.WEBSOCKET_MODULE_ALARM_MONITOR) {
				//do nothing
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "alarm monitor to user:"+userNameNSessionId+" not support for this module"));
			}else if (wsh.getWebSocketModule()==ConstUtil.WEBSOCKET_MODULE_PERSON_TRACE) {
				//do nothing
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "person trace to user:"+userNameNSessionId+" not support for this module"));
			}else {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not support for web socket module "+wsh.getWebSocketModule()+" for user "+userNameNSessionId));
			}
		}						
		return new ResultStatus();
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
