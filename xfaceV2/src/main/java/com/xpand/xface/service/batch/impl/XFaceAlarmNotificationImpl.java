package com.xpand.xface.service.batch.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonNotification;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.bean.query.photo.ResultDataFace;
import com.xpand.xface.bean.query.photo.ResultDataIPC;
import com.xpand.xface.bean.query.photo.ResultDataMap;
import com.xpand.xface.bean.query.photo.ResultDataPerson;
import com.xpand.xface.bean.queue.QueueHWAlarm;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.TimeUnit;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.service.hwapi.HWAPIBatchService;
import com.xpand.xface.util.BooleanUtil;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.DateTimeUtil;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;


/*
 * class to check alarm in queue and process
 * 1. send notification to UI
 * 2. execute action code
 * 3. move alarm from queue to db
 */
public class XFaceAlarmNotificationImpl extends Thread implements BaseXFaceThreadService{
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private String transactionId;	
	private int timeThreadSleep;
	private int thumbnailImgSizeWidth;
	private int thumbnailImgSizeHeight;
	private long notSendAlarmIfOlderThan;
	private HWAPIBatchService hwAPIService;
	private SimpMessagingTemplate msgTemplate;
	private GlobalVarService globalVarService;			
	private XFaceBatchService xFaceBatchService;
	public XFaceAlarmNotificationImpl(String transactionId, int threadNo, GlobalVarService globalVarService
				, XFaceBatchService xFaceBatchService, int timeThreadSleep, SimpMessagingTemplate msgTemplate
				, HWAPIBatchService hwAPIService) {
		super(transactionId+"_XFaceAlarmNotificationImpl_"+threadNo);		
		this.transactionId = super.getName();
		this.globalVarService = globalVarService;		
		this.timeThreadSleep = timeThreadSleep;
		this.msgTemplate = msgTemplate;
		this.xFaceBatchService = xFaceBatchService;
		this.hwAPIService = hwAPIService;
	}
	@Override
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start XFaceAlarmNotification thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		QueueHWAlarm queueContent;
		HWWSField result = null;
		ApplicationCfg appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_IMAGE_THUMBNAIL_SIZE);
		this.thumbnailImgSizeWidth = StringUtil.stringToInteger(appTmp.getAppValue1(), 40);
		this.thumbnailImgSizeHeight = StringUtil.stringToInteger(appTmp.getAppValue2(), 40);
		appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_NOT_SEND_ALARM_IF_OLDER_THEN);
		this.notSendAlarmIfOlderThan = StringUtil.stringToInteger(appTmp.getAppValue1(), 5)*1000; 
		appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_DEMONTASK_LIMIT_PRINT_LOG);
		int limitPrintLog = StringUtil.stringToInteger(appTmp.getAppValue1(), 100); 
		int cntPrintLog = 0;
		ResultStatus resultStatus = null;
		String alarmType = HWAlarmHist.HW_VCM_ALARM;
		String alarmData = null;
		HWIPC hwIPC = null;
		////////////////////		
		while (this.isLoop || (this.globalVarService.getSizeOfAlarmQueue()>0)) {
			cntPrintLog++;
			if (cntPrintLog>limitPrintLog) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "get alarm from queue"));
			}
			//Queue 
			queueContent = this.globalVarService.popAlarm();
			if (queueContent==null) {
				if (cntPrintLog>limitPrintLog) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no alarm in queue"));
				}
				OtherUtil.waitMilliSecond(this, this.timeThreadSleep);
			}else if (!StringUtil.checkNull(queueContent.getContent())){
				//if queue not null 
				alarmType =  queueContent.getContent().substring(0, 1); //create alarmType getContent M or N 
				alarmData = queueContent.getContent().substring(1, queueContent.getContent().length()); // create alarmData
				
				if (HWAlarmHist.HW_VCM_ALARM.equals(alarmType)) {
					//case alarm type M
					result = this.extractVCMAlarm(alarmData);				
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "found VCM alarm:"+alarmData+" with result "+result.getResult().toString()));				
					if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
						//setHwIPC 
						result.getHwAlarmHist().setHwIPC(this.xFaceBatchService.findIPCByCode(result.getHwAlarmHist().getCameraId()));						
						result.setHwAlarmHist(this.identifyPerson(result.getHwAlarmHist()));
						//keep result = call sendNotification & send value getHwAlarmHist 
						resultStatus = this.sendNotification(result.getHwAlarmHist());
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send alarm with result "+resultStatus.toString()));
						result.getHwAlarmHist().setResultStatus(resultStatus.getStatusCode());					
						this.transferAlarmToHistory(result.getHwAlarmHist());
						if (cntPrintLog>limitPrintLog) {
							Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "insert VCM alarm to db success"));
							cntPrintLog = 0;
						}
					}
				}else if (HWAlarmHist.VCN_ALM_IPC_ONLINE.equals(result.getHwAlarmHist().getAlarmType())||HWAlarmHist.VCN_ALM_IPC_OFFLINE.equals(result.getHwAlarmHist().getAlarmType())
					      ||HWAlarmHist.VCN_ALM_DI.equals(result.getHwAlarmHist().getAlarmType())){
					result = this.extractVCNAlarm(alarmData);
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId,"found VCN alarm:" + alarmData + " with result " + result.getResult().toString()));
					if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
						hwIPC = this.xFaceBatchService.findIPCByCode(result.getHwAlarmHist().getCameraId());
						if (hwIPC != null) {
							// default status is online
							hwIPC.setIpcStatus(HWIPC.IPC_STATUS_ONLINE);
							if (HWAlarmHist.VCN_ALM_IPC_OFFLINE.equals(result.getHwAlarmHist().getAlarmType())) {
								// update IPC status to offline
								hwIPC.setIpcStatus(HWIPC.IPC_STATUS_OFFLINE);
							} else if (HWAlarmHist.VCN_ALM_DI.equals(result.getHwAlarmHist().getAlarmType())) {
								// alarm set or clear
								if (HWAlarmHist.VCN_ALM_EVENT_SET.equals(result.getHwAlarmHist().getEventType())) {
									hwIPC.setIpcStatus(HWIPC.IPC_STATUS_ALARM);
								} else if (HWAlarmHist.VCN_ALM_EVENT_CLEAR.equals(result.getHwAlarmHist().getEventType())) {
									hwIPC.setIpcStatus(HWIPC.IPC_STATUS_ONLINE);
								}
							}
							this.xFaceBatchService.updateIPCStatus(this.transactionId, hwIPC);
						}
						result.getHwAlarmHist().setHwIPC(hwIPC);
						// insert to db
						this.transferAlarmToHistory(result.getHwAlarmHist());
						if (cntPrintLog > limitPrintLog) {
							Logger.debug(this,LogUtil.getLogDebug(this.transactionId, "insert VCN alarm to db success"));
							cntPrintLog = 0;
						}
					}
				}
			}		
			if (cntPrintLog>limitPrintLog) {				
				cntPrintLog = 0;
			}
		}		
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop XFaceAlarmNotification thread ["+super.getName()+"]"));
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
	private void transferAlarmToHistory(HWAlarmHist hwAlarm) {				
		try {			
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "move alarm id "+hwAlarm.getAlarmCode()+" from queue to history"));
			this.xFaceBatchService.updateAlarmHistory(this.transactionId, hwAlarm);			
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "move alarm id "+hwAlarm.getAlarmCode()+" from queue to history success"));
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while transfer alarm : "+hwAlarm.getAlarmCode()+" to history", ex));			
		}		
	}
	
	/*
	 * send alarm notification to UI, call plugin action
	 */
	private ResultStatus sendNotification(HWAlarmHist hwAlarm) {
		if (hwAlarm.getHwIPC()==null){
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz cannot find IPC code:"+hwAlarm.getCameraId()+" in database pls check camera configuration"));			
			return new ResultStatus(ResultStatus.ALARM_NOT_SEND_NO_IPC_CFG_ERROR_CODE, hwAlarm.getCameraId());
		}else if ((hwAlarm.getPersonInfo()==null) && (hwAlarm.getHwIPC().getIgnoreUnknownAlarm()==BooleanUtil.TRUE_VALUE)) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "unkown person not send alarm coz ignore alarm setting by IPC:"+hwAlarm.getHwIPC().getIpcCode()));
			return new ResultStatus(ResultStatus.ALARM_NOT_SEND_UNKNOWN_PERSON_CFG_ERROR_CODE, null);
		}
		Date nowDate = new Date();
		ResultStatus resultStatus= this.checkAlarmTimingRule(hwAlarm, nowDate);
		if (!resultStatus.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			return resultStatus;
		}
				
		WebSocketHolder wsh = null;	
		//send alarm to all user under webSocketHolder
		KeySetView<String, WebSocketHolder>  webSocketList = this.globalVarService.getWebSocketHolderList().keySet();
		if (webSocketList==null) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no websocket found no need to send alarm"));			
			return new ResultStatus(ResultStatus.NO_WSH_NEED_TO_SEND_ALARM_CODE, null);
		}
		Iterator<String> keyWebSocket = webSocketList.iterator();
		String userNameNSessionId = null;
		ResultDataPerson dataPerson = null;
		PersonNotification personNotification = null;
		int isSendAlarm = -1;
		while (keyWebSocket.hasNext()) {
			userNameNSessionId = keyWebSocket.next();
			wsh = this.globalVarService.getWebSocketHolder(userNameNSessionId);
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send notification to user "+userNameNSessionId));
			if (wsh==null) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "web socket for user "+userNameNSessionId+" not found"));
			}else if (wsh.isMarkDelete()) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "web socket for user "+userNameNSessionId+" mark for delete then send goodby cmd"));
				this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userNameNSessionId+"/"+wsh.getWebSocketModule(), ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
				this.globalVarService.removeWebSocketHolder(userNameNSessionId);
			}else if (wsh.getWebSocketModule()==ConstUtil.WEBSOCKET_MODULE_PERSON_TRACE) {
				//open door
				/////////////
				if (dataPerson==null) {
					dataPerson = this.getAlarmPersonTrace(hwAlarm, nowDate, wsh);
				}
				//certificate is mandatory for person trace
				isSendAlarm = wsh.findCertificateNo(dataPerson.getCertificateNo());
				if (isSendAlarm==-1) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm person trace coz certificate no "+dataPerson.getCertificateNo()+" not match for user "+userNameNSessionId));
				}else {
					//send alarm
					this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userNameNSessionId+"/"+wsh.getWebSocketModule(), dataPerson);
				}
			}else if (wsh.getWebSocketModule()==ConstUtil.WEBSOCKET_MODULE_ALARM_MONITOR) {
				//open door
				/////////////
				if (personNotification==null) {
					personNotification = new PersonNotification(hwAlarm);
				}				
				//if user need to filter by certificate
				if (wsh.getCertificateNoList().size()>0) {
					isSendAlarm = wsh.findCertificateNo(personNotification.getCertificateNo());
				}else if (!StringUtil.checkNull(wsh.getFullName())){
					//or user need to filter by full name
					isSendAlarm = personNotification.getFullName().indexOf(wsh.getFullName());
				}else {
					isSendAlarm = 0;
				}				
				if (isSendAlarm>-1 && wsh.getIpcCodeList().size()>0) {
					isSendAlarm = wsh.findIpcCode(personNotification.getIpcCode());
				}else if (isSendAlarm>-1 && wsh.getGateInfoCodeList().size()>0) {
					isSendAlarm = wsh.findGateInfoCode(personNotification.getGateInfoCode());
				}
				if (isSendAlarm==-1) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm person trace coz certificate no "+personNotification.getCertificateNo()+",full name:"+personNotification.getFullName()+",gate:"+personNotification.getGateInfoCode()+",ipc code:"+personNotification.getIpcCode()+" not match for user "+userNameNSessionId));
				}else {
					this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userNameNSessionId+"/"+wsh.getWebSocketModule(), personNotification);
				}
			}else if (wsh.getWebSocketModule()==ConstUtil.WEBSOCKET_MODULE_LANDING_PAGE) {
				//do nothing
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "landing page info to user:"+userNameNSessionId+" not support for this module"));
			}else {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not support for web socket module "+wsh.getWebSocketModule()+" for user "+userNameNSessionId));
			}
		}						
		return new ResultStatus();
	}
	
	/*
	 * base on alarm use black list id to search for person on xface db.
	 */
	private HWAlarmHist identifyPerson(HWAlarmHist hwAlarm) {
		if (hwAlarm.getHwIPC()==null) {			
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "cannot find IPC code "+hwAlarm.getCameraId()+" in database pls check camera configuration"));
			return hwAlarm;
		}
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "black list id is "+hwAlarm.getBlackListId()));
		if (!StringUtil.checkNull(hwAlarm.getBlackListId())) {
			//start test code
			//for test reason, blacklist id is hwpeopleid
			hwAlarm.setPersonInfo(this.xFaceBatchService.findPersonByHwPeopleId(transactionId, hwAlarm.getBlackListId()));
			if (hwAlarm.getPersonInfo()==null) {
				hwAlarm.setRecMatch(ConstUtil.BYTE_ZERO_VALUE);
				hwAlarm.setRecUnMatch(ConstUtil.BYTE_ONE_VALUE);
			}else {
				hwAlarm.setRecMatch(ConstUtil.BYTE_ONE_VALUE);
				hwAlarm.setRecUnMatch(ConstUtil.BYTE_ZERO_VALUE);
				hwAlarm.getPersonInfo().setPersonPhoto(
						ImageUtil.resizeImage(hwAlarm.getPersonInfo().getPersonPhoto(), 
												this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
			}
			////// end of test code////
//			HWWSField result = this.hwAPIService.queryPerson(hwAlarm.getBlackListId(), hwAlarm.getHwIPC().getHwCheckPointLibrary().getLibraryId());			
//			if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
//				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "hwPersonId is "+result.getHwPersonId()));
//				if (!StringUtil.checkNull(result.getHwPersonId())) {
//					hwAlarm.setPersonInfo(this.personInfoService.findByHwPeopleId(transactionId, result.getHwPersonId()));
//					if (hwAlarm.getPersonInfo()==null) {
//						//VCM return not match any personInfo
//						hwAlarm.setRecMatch(ConstUtil.BYTE_ZERO_VALUE);
//						hwAlarm.setRecUnMatch(ConstUtil.BYTE_ONE_VALUE);
//					}else {
//						//VCM return match with personInfo
//						hwAlarm.setRecMatch(ConstUtil.BYTE_ONE_VALUE);
//						hwAlarm.setRecUnMatch(ConstUtil.BYTE_ZERO_VALUE);
//						hwAlarm.getPersonInfo().setPersonPhoto(
//								ImageUtil.resizeImage(hwAlarm.getPersonInfo().getPersonPhoto(), 
//														this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
//					}
//				}							
//			}
		}
		return hwAlarm;
	}
	
	/*
	 * read alarm xml and create object HWWSField
	 */
	private HWWSField extractVCMAlarm(String content) {
		HWWSField result = new HWWSField();
		try {
			result = HWXMLUtil.extractVCMAlarm(this.transactionId, content);
			if (result.getResult().getStatusCode()==ResultStatus.SUCCESS_CODE) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "extractVCMAlarm OK then get live photo"));
				HWAlarmHist hwAlarm = result.getHwAlarmHist();
				hwAlarm.setHwIPC(this.xFaceBatchService.findIPCByCode(hwAlarm.getCameraId()));
//				for test we extract live photo from alarm
//				hwAlarm.setLivePhoto(this.hwAPIService.getLivePhoto(hwAlarm.getPicImageUrl()
//						, hwAlarm.getPicThumImageUrl(), hwAlarm.getAlarmPicName(), hwAlarm.getHwIPC().getHwVCN().getHwVCM().getVcmName()));
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "extractVCMAlarm OK then get live photo success"));
			}			
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while extract VCM alarm", ex));
			result.getResult().setStatusCode(ResultStatus.ALARM_PARSER_ERROR_CODE, ex.toString());
		}	
		return result;
	}
	private HWWSField extractVCNAlarm(String content) {
		//replace &gt; with > and replace &lt; with <		
		HWWSField result = null;
		String alarmType = null;
		String eventType = null;
		try {
			content = content.replace("&gt;", ">").replace("&lt;", "<");
			//get AlarmType
			int index1 = content.indexOf("<AlarmType>");
			int index2 = content.indexOf("</AlarmType>");
			if (index1>=0 && index2 >=0) {
				//alarmType = content.substring(index1, index2+"</AlarmType>".length());
				//11 come from "<AlarmType>".length();
				alarmType = content.substring(index1+11, index2);
			}
			if (HWAlarmHist.VCN_ALM_DI.equals(alarmType)){
				// check event type 10017 = clear, 10013 = alarm
				index1 = content.indexOf("<eventType>");
				index2 = content.indexOf("</eventType>");
				if (index1>=0 && index2 >=0) {
					//11 come from "<eventType>".length();
					eventType = content.substring(index1+11, index2);
				}
				if (HWAlarmHist.VCN_ALM_EVENT_SET.equals(eventType)) {
					result = HWXMLUtil.extractVCNAlarmSOSBotton(this.transactionId, content);
				}else if (HWAlarmHist.VCN_ALM_EVENT_CLEAR.equals(eventType)) {
					result = HWXMLUtil.extractVCNClearSOSBotton(this.transactionId, content);
				}				
			}else if (HWAlarmHist.VCN_ALM_IPC_ONLINE.equals(alarmType)){
				result = HWXMLUtil.extractVCNIPCOnline(this.transactionId, content);
			}else if (HWAlarmHist.VCN_ALM_IPC_OFFLINE.equals(alarmType)){
				result = HWXMLUtil.extractVCNIPCOffline(this.transactionId, content);
			}
			if (result==null) {
				//not support alarm
				result = new HWWSField();
				result.setResult(new ResultStatus(ResultStatus.NOT_SUPPORT_VCN_ALARM_ERROR_CODE, "AlarmType:"+ alarmType+", EventType:"+eventType));
			}				
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while extract VCN alarm", ex));
			result.getResult().setStatusCode(ResultStatus.ALARM_PARSER_ERROR_CODE, ex.toString());
		}	
		return result;
	}
	
	/*
	 * get unknown person alarm notification
	 */
//	private PersonNotification getUnknownPersonNotification(HWAlarmHist hwAlarm) {
//		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "unknown person detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));
//		PersonNotification personNotification = new PersonNotification();								
//		personNotification.setFullName("Unknown");		
//		personNotification.setPersonCategory(new PersonCategory(PersonCategory.UNKNOWN_CATEGORY_ID
//																, PersonCategory.UNKNOWN_CATEGORY_NAME
//																, PersonCategory.UNKNOWN_CATEGORY_DESC
//																, PersonCategory.UNKNOWN_CATEGORY_COLOR_CODE));
//		personNotification.setAlarmId(hwAlarm.getAlarmhId());			
//		personNotification.setHwPeopleId("");
//		personNotification.setVcnUsername(hwAlarm.getHwIPC().getHwVCN().getLoginUserName());
//		personNotification.setVcnPassword(hwAlarm.getHwIPC().getHwVCN().getLoginPassword());
//		personNotification.setVcnPort(hwAlarm.getHwIPC().getHwVCN().getLoginPort());
//		personNotification.setLatestIPCCode(hwAlarm.getCameraId());
//		personNotification.setLatestVCNIP(hwAlarm.getHwIPC().getHwVCN().getVcnIp());
//		personNotification.setPersonPhoto(ImageUtil.resizeImage(this.appCfg.findByAppKey(ApplicationCfg.KEY_IMAGE_UNKNOWN_PERSON).getAppLobValue()
//				, this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));		
//		//after get image then resize image
//		personNotification.setLivePhoto(ImageUtil.resizeImage(hwAlarm.getLivePhoto(), 
//										this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
//		if (StringUtil.checkNull(personNotification.getLivePhoto())) {
//			//if fail to get live photo then set to unknown person
//			personNotification.setLivePhoto(personNotification.getPersonPhoto());
//		}
//		personNotification.setAlarmDate(hwAlarm.getAlarmTime());
//		personNotification.setPercentMatch(0);
//		personNotification.setAlarmCode(hwAlarm.getAlarmCode());
//		return personNotification;
//	}
//	
//	/*
//	 * get person alarm notification
//	 */
//	private PersonNotification getPersonNotification(HWAlarmHist hwAlarm, Date nowDate) {				
//		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "known personId:"+hwAlarm.getPersonInfo().getPersonId()+"detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));
//		this.globalVarService.addLastAlarmPersonDateTime(hwAlarm.getPersonInfo().getPersonId(), nowDate.getTime());
//		PersonNotification personNotification = new PersonNotification(hwAlarm.getPersonInfo());
//		personNotification.getPersonCategory().setHwCheckPointLibrary(null);
//		personNotification.setAlarmId(hwAlarm.getAlarmhId());				
//		personNotification.setVcnUsername(hwAlarm.getHwIPC().getHwVCN().getLoginUserName());
//		personNotification.setVcnPassword(hwAlarm.getHwIPC().getHwVCN().getLoginPassword());
//		personNotification.setVcnPort(hwAlarm.getHwIPC().getHwVCN().getLoginPort());
//		personNotification.setLatestIPCCode(hwAlarm.getCameraId());
//		personNotification.setLatestVCNIP(hwAlarm.getHwIPC().getHwVCN().getVcnIp());		
//		personNotification.setLivePhoto(ImageUtil.resizeImage(hwAlarm.getLivePhoto(), 
//										this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
//		if (StringUtil.checkNull(personNotification.getLivePhoto())) {
//			//if fail to get live photo then set to unknown person
//			personNotification.setLivePhoto(ImageUtil.resizeImage(this.appCfg.findByAppKey(ApplicationCfg.KEY_IMAGE_UNKNOWN_PERSON).getAppLobValue()
//					, this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
//		}
//		personNotification.setAlarmDate(hwAlarm.getAlarmTime());
//		personNotification.setPercentMatch(hwAlarm.getMetaScr());
//		personNotification.setAlarmCode(hwAlarm.getAlarmCode());
//		return personNotification;
//	}
	
	/*
	 * send alarm for person trace
	 */
	private ResultDataPerson getAlarmPersonTrace(HWAlarmHist hwAlarm, Date nowDate, WebSocketHolder wsh) {
		if (hwAlarm.getPersonInfo()==null) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "unknown person detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));			
		}else {			
			this.globalVarService.addLastAlarmPersonDateTime(hwAlarm.getPersonInfo().getPersonId(), nowDate.getTime());
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "known personId:"+hwAlarm.getPersonInfo().getPersonId()+"detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));
		}
		ResultDataFace dataFace = new ResultDataFace();				
		dataFace.setAlarmCode(hwAlarm.getAlarmCode());
		dataFace.setAlarmCodeList(hwAlarm.getAlarmCode());
		dataFace.setFacePhoto(hwAlarm.getLivePhoto());
		dataFace.setNoOfFace(1);
		dataFace.setPhotoDate(hwAlarm.getAlarmTime());
		ResultDataIPC dataIPC = new ResultDataIPC();
		dataIPC.setIpcCode(hwAlarm.getHwIPC().getIpcCode());
		dataIPC.setIpcId(hwAlarm.getHwIPC().getIpcId());
		dataIPC.setIpcName(hwAlarm.getHwIPC().getIpcName());
		dataIPC.setLatest(true);
		dataIPC.setLocationX(hwAlarm.getHwIPC().getMapLocationX());
		dataIPC.setLocationY(hwAlarm.getHwIPC().getMapLocationY());
		dataIPC.getDataFaceList().add(dataFace);
		ResultDataMap dataMap = new ResultDataMap();
		dataMap.setMapCode(hwAlarm.getHwIPC().getLocationMap().getMapCode());
		dataMap.setMapId(hwAlarm.getHwIPC().getLocationMap().getMapId());
		dataMap.setMapName(hwAlarm.getHwIPC().getLocationMap().getMapName());
		dataMap.setMapPhoto(hwAlarm.getHwIPC().getLocationMap().getMapPhoto());
		dataMap.getDataIpcList().add(dataIPC);
		ResultDataPerson dataPerson = new ResultDataPerson();
		if (hwAlarm.getPersonInfo()==null) {
			dataPerson.setCertificateNo(ConstUtil.UNKNOWN_PERSON_CERTIFICATE_NO);
			dataPerson.setCertificateType("");
		}else {
			dataPerson.setCertificateNo(hwAlarm.getPersonInfo().getCertificateNo());
			dataPerson.setCertificateType(hwAlarm.getPersonInfo().getPersonCertificate().getCertificateName());
		}			
		dataPerson.getDataMapList().add(dataMap);
		return dataPerson;
//		this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userName, );
	}	
	
	//check alarm againt timing rule
	//1.alarm for same person
	//2.alarm older than xx sec, should not send
	private ResultStatus checkAlarmTimingRule(HWAlarmHist hwAlarm, Date nowDate) {
		if (hwAlarm.getHwIPC().getHwCheckPointLibrary().getIgnoreSamePersonTime()>0 && hwAlarm.getPersonInfo()!=null) {
			Long lastAlarm = this.globalVarService.getLastAlarmPersonDateTime(hwAlarm.getPersonInfo().getPersonId());
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "IgnoreSamePersonTime:"+hwAlarm.getHwIPC().getHwCheckPointLibrary().getIgnoreSamePersonTime()
					+" IgnoreSamePersonTimeUnit:"+hwAlarm.getHwIPC().getHwCheckPointLibrary().getIgnoreSamePersonTimeUnit().toString()
					+" PersonId:"+hwAlarm.getPersonInfo().getPersonId()
					+" LastAlarm:"+ StringUtil.dateToString(lastAlarm, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)
					+" current date:"+StringUtil.dateToString(nowDate, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)));
			if (lastAlarm!=null) {
				if (hwAlarm.getHwIPC().getHwCheckPointLibrary().getIgnoreSamePersonTimeUnit().getTuId()==TimeUnit.TIME_MIDNIGHT) {
					//end at midnight
					if (DateTimeUtil.getDayFromDate(lastAlarm, 0)==DateTimeUtil.getDayFromDate(nowDate, 1)) {
						//previous alarm and current alarm is same day then not send
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz prev and current date is the same"));
						return new ResultStatus(ResultStatus.ALARM_NOT_SEND_TIME_CFG_ERROR_CODE, null);
					}
				}else {
					//use divide value to identify difference
					long timeDiff = (nowDate.getTime() - lastAlarm.longValue()) / hwAlarm.getHwIPC().getHwCheckPointLibrary().getIgnoreSamePersonTimeUnit().getDividedValue();					
					if (timeDiff<=hwAlarm.getHwIPC().getHwCheckPointLibrary().getIgnoreSamePersonTime()) {
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz diff between prev and current is "+timeDiff+" no more than configure"));
						return new ResultStatus(ResultStatus.ALARM_NOT_SEND_TIME_CFG_ERROR_CODE, null);
					}
				}				
			}
		}				
		long alarmAge = Math.abs(hwAlarm.getAlarmTime().getTime() - nowDate.getTime());
		//if alarm older than X sec, no need to send
		if (alarmAge > this.notSendAlarmIfOlderThan) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz alarm date:"+StringUtil.dateToString(hwAlarm.getAlarmTime(), StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)+"["+hwAlarm.getAlarmTime().getTime()+"]"
			+" now is "+StringUtil.dateToString(nowDate, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)+"["+nowDate.getTime()+"]"
			+" which more than:"+this.notSendAlarmIfOlderThan+" from setting"));
			return new ResultStatus(ResultStatus.ALARM_NOT_SEND_AGE_CFG_ERROR_CODE, null);
		}
		//success
		return new ResultStatus();
	}
	@Override
	public boolean isServiceReStart() {
		// TODO Auto-generated method stub
		return false;
	}
}
