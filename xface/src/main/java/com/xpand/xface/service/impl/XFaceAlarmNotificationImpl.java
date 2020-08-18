package com.xpand.xface.service.impl;

import java.util.Date;
import java.util.Set;

import org.springframework.web.socket.TextMessage;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.AlarmActionList;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonNotification;
import com.xpand.xface.bean.QueueContent;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.IPCGroupDetail;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.TimeUnit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.HWAlarmHistService;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.util.BooleanUtil;
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
	private GlobalVarService globalVarService;
	private String transactionId;		
	private PersonInfoService personInfoService;
	private HWAlarmHistService hwAlarmHistService;	
	private ApplicationCfgService appCfg;
	private int timeThreadSleep;
	private int thumbnailImgSizeWidth;
	private int thumbnailImgSizeHeight;
	private long notSendAlarmIfOlderThan;
	private HWAPIServiceImpl hwAPIService;
	private AlarmActionList alarmActionList;	
	HWIPCService hwIPCService;
	public XFaceAlarmNotificationImpl(String transactionId, int threadNo, GlobalVarService globalVarService
				, HWAlarmHistService hwAlarmHistService, ApplicationCfgService appCfg
				, PersonInfoService personInfoService, HWIPCService hwIPCService, int timeThreadSleep) {
		super(transactionId+"_XFaceAlarmNotificationImpl_"+threadNo);		
		this.transactionId = super.getName();
		this.globalVarService = globalVarService;
		this.hwAlarmHistService = hwAlarmHistService;		
		this.appCfg = appCfg;
		this.personInfoService = personInfoService;
		this.hwIPCService = hwIPCService;
		this.timeThreadSleep = timeThreadSleep;		
	}
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start XFaceAlarmNotification thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		QueueContent queueContent;
		HWWSField result = null;
		ApplicationCfg appTmp = this.appCfg.findByAppKey(ApplicationCfg.KEY_IMAGE_THUMBNAIL_SIZE);
		this.thumbnailImgSizeWidth = StringUtil.stringToInteger(appTmp.getAppValue1(), 40);
		this.thumbnailImgSizeHeight = StringUtil.stringToInteger(appTmp.getAppValue2(), 40);
		appTmp = this.appCfg.findByAppKey(ApplicationCfg.KEY_NOT_SEND_ALARM_IF_OLDER_THEN);
		this.notSendAlarmIfOlderThan = StringUtil.stringToInteger(appTmp.getAppValue1(), 5)*1000; 
		this.hwAPIService = new HWAPIServiceImpl();
		this.hwAPIService.initialClass(this.transactionId, this.appCfg.getAllInHashMap(), null, this.globalVarService);
		//load alarm action
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load alarm action thread ["+super.getName()+"]"));				//
		this.alarmActionList = new AlarmActionList(this.transactionId, this.appCfg.findByAppKey(ApplicationCfg.KEY_ALARM_ACTION_PLUGIN_DIRECTORY)
				, super.getName());
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load alarm action thread ["+super.getName()+"] done with name:"));
		appTmp = this.appCfg.findByAppKey(ApplicationCfg.KEY_ALARM_LIMIT_PRINT_LOG);
		int limitPrintLog = StringUtil.stringToInteger(appTmp.getAppValue1(), 100); 
		int cntPrintLog = 0;
		ResultStatus resultStatus = null;
		////////////////////		
		while (this.isLoop || (this.globalVarService.getSizeOfAlarmQueue()>0)) {
			cntPrintLog++;
			if (cntPrintLog>limitPrintLog) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "get alarm from queue"));
			}			
			queueContent = this.globalVarService.popContent();
			if (queueContent==null) {
				if (cntPrintLog>limitPrintLog) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no alarm in queue"));
				}
				OtherUtil.waitMilliSecond(this, this.timeThreadSleep);
			}else {				
				result = this.extractAlarm(queueContent.getContent());				
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "found alarm:"+queueContent.getContent()+" with result "+result.getResult().toString()));				
				if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
					result.getHwAlarmHist().setHwIPC(this.hwIPCService.findByIpcCode(result.getHwAlarmHist().getCameraId()));						
					this.identifyPerson(result.getHwAlarmHist());
					resultStatus = this.sendNotification(result.getHwAlarmHist());
					Logger.info(this, LogUtil.getLogInfo(this.transactionId, "send alarm with result "+resultStatus.toString()));
					result.getHwAlarmHist().setResultStatus(resultStatus.getStatusCode());					
					this.transferAlarmToHistory(result.getHwAlarmHist());
					if (cntPrintLog>limitPrintLog) {
						Logger.info(this, LogUtil.getLogInfo(this.transactionId, "insert alarm to db success"));
						cntPrintLog = 0;
					}
				}				
			}		
			if (cntPrintLog>limitPrintLog) {				
				cntPrintLog = 0;
			}
		}
		if (this.alarmActionList!=null) {
			this.alarmActionList.unLoadPlugin();
		}
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop XFaceAlarmToDB thread ["+super.getName()+"]"));
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
	private void transferAlarmToHistory(HWAlarmHist hwAlarm) {				
		try {			
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "move alarm id "+hwAlarm.getAlarmCode()+" from queue to history"));
			this.hwAlarmHistService.updateAlarm(hwAlarm);			
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
		if (hwAlarm.getHwIPC().getHwIPCAnalyzeList().getIgnoreSamePersonTime()>0 && hwAlarm.getPersonInfo()!=null) {
			Long lastAlarm = this.globalVarService.getLastAlarmPersonDateTime(hwAlarm.getPersonInfo().getPersonId());
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "IgnoreSamePersonTime:"+hwAlarm.getHwIPC().getHwIPCAnalyzeList().getIgnoreSamePersonTime()
					+" IgnoreSamePersonTimeUnit:"+hwAlarm.getHwIPC().getHwIPCAnalyzeList().getIgnoreSamePersonTimeUnit().toString()
					+" PersonId:"+hwAlarm.getPersonInfo().getPersonId()
					+" LastAlarm:"+ StringUtil.dateToString(lastAlarm, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)
					+" current date:"+StringUtil.dateToString(nowDate, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)));
			if (lastAlarm!=null) {
				if (hwAlarm.getHwIPC().getHwIPCAnalyzeList().getIgnoreSamePersonTimeUnit().getTuId()==TimeUnit.TIME_MIDNIGHT) {
					//end at midnight
					if (DateTimeUtil.getDayFromDate(lastAlarm, 0)==DateTimeUtil.getDayFromDate(nowDate, 1)) {
						//previous alarm and current alarm is same day then not send
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz prev and current date is the same"));
						return new ResultStatus(ResultStatus.ALARM_NOT_SEND_TIME_CFG_ERROR_CODE, null);
					}
				}else {
					//use divide value to identify difference
					long timeDiff = (nowDate.getTime() - lastAlarm.longValue()) / hwAlarm.getHwIPC().getHwIPCAnalyzeList().getIgnoreSamePersonTimeUnit().getDividedValue();					
					if (timeDiff<=hwAlarm.getHwIPC().getHwIPCAnalyzeList().getIgnoreSamePersonTime()) {
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz diff between prev and current is "+timeDiff+" no more than configure"));
						return new ResultStatus(ResultStatus.ALARM_NOT_SEND_TIME_CFG_ERROR_CODE, null);
					}
				}				
			}
		}				
		long alarmAge = Math.abs(hwAlarm.getAlarmTime().getTime() - nowDate.getTime());
		if (alarmAge > this.notSendAlarmIfOlderThan) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send alarm coz alarm date:"+StringUtil.dateToString(hwAlarm.getAlarmTime(), StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)+"["+hwAlarm.getAlarmTime().getTime()+"]"
			+" now is "+StringUtil.dateToString(nowDate, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)+"["+nowDate.getTime()+"]"
			+" which more than:"+this.notSendAlarmIfOlderThan+" from setting"));
			return new ResultStatus(ResultStatus.ALARM_NOT_SEND_AGE_CFG_ERROR_CODE, null);
		}		
		Set<IPCGroupDetail> ipcGroupDeatilList = hwAlarm.getHwIPC().getIpcGroupDetail();
		if (ipcGroupDeatilList==null) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no IPC group detail configure match with IPC:"+hwAlarm.getHwIPC().getIpcCode()));
			return new ResultStatus(ResultStatus.ALARM_NOT_SEND_NO_IPCG_ERROR_CODE, null);
		}
		IPCGroupDetail ipcGroupDeatil = null;
		for (IPCGroupDetail ipcGD: ipcGroupDeatilList) {
			ipcGroupDeatil = ipcGD;
			break;
		}
		
		Set<UserInfo> userInfoList = ipcGroupDeatil.getIpcGroup().getUserInfos();
		if (userInfoList==null) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no userInfo configure match with IPC group:"+ipcGroupDeatil.getIpcGroup().getGroupName()));
			return new ResultStatus(ResultStatus.ALARM_NOT_SEND_NO_MATCH_USER_ERROR_CODE, null);
		}
		WebSocketHolder wsh = null;	
		String notificationData = null;
		PersonNotification personNotification = null;
		for (UserInfo userInfo: userInfoList) {
			wsh = this.globalVarService.getWebSocketHolder(userInfo.getUserName());
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send notification to user "+userInfo.getUserName()));			
			if (hwAlarm.getPersonInfo()==null) {
				//unknow person
				personNotification = this.getUnknownPersonNotification(hwAlarm);
				try {					
					notificationData = StringUtil.getJson(personNotification, PersonNotification.class);
					if (wsh==null) {
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "web socket for user "+userInfo.getUserName()+" not found"));
					}else {
						wsh.getWebSocket().sendMessage(new TextMessage(notificationData));
					}					
					this.alarmActionList.doAction(notificationData, this.globalVarService);				
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send notification:"+notificationData+" to user "+userInfo.getUserName()+" success"));
				}catch (Exception ex) {
					Logger.error(this, LogUtil.getLogError(transactionId, "error while send alarm notification to user "+userInfo.getUserName()+" message "+StringUtil.getJson(personNotification, PersonNotification.class), ex));
				}	
			}else {			
				personNotification = this.getPersonNotification(hwAlarm, nowDate);
				try {
					notificationData = StringUtil.getJson(personNotification, PersonNotification.class);
					if (wsh==null) {
						Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "web socket for user "+userInfo.getUserName()+" not found"));
					}else {
						wsh.getWebSocket().sendMessage(new TextMessage(notificationData));
					}
					this.alarmActionList.doAction(notificationData, this.globalVarService);
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send notification:"+notificationData+" to user "+userInfo.getUserName()+" success"));
				}catch (Exception ex) {
					Logger.error(this, LogUtil.getLogError(transactionId, "error while send alarm notification to user "+userInfo.getUserName()+" message "+StringUtil.getJson(personNotification, PersonNotification.class), ex));
				}				
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
		this.hwAPIService.setHwVCM(hwAlarm.getHwIPC().getHwVCN().getHwVCM());		
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "black list id is "+hwAlarm.getBlackListId()));
		if (!StringUtil.checkNull(hwAlarm.getBlackListId())) {
			HWWSField result = this.hwAPIService.queryPerson(hwAlarm.getBlackListId(), hwAlarm.getHwIPC().getHwIPCAnalyzeList().getListNameId());			
			if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "hwPersonId is "+result.getHwPersonId()));
				if (!StringUtil.checkNull(result.getHwPersonId())) {
					hwAlarm.setPersonInfo(this.personInfoService.findByHwPeopleId(result.getHwPersonId(), null));
					if (hwAlarm.getPersonInfo()!=null) {
						hwAlarm.getPersonInfo().setPersonPhoto(
								ImageUtil.resizeImage(hwAlarm.getPersonInfo().getPersonPhoto(), 
														this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
					}
				}							
			}
		}
		return hwAlarm;
	}
	
	/*
	 * read alarm xml and create object HWWSField
	 */
	private HWWSField extractAlarm(String content) {
		HWWSField result = new HWWSField();
		try {
			result = HWXMLUtil.extractAlarm(this.transactionId, content);
			if (result.getResult().getStatusCode()==ResultStatus.SUCCESS_CODE) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "extractAlarm OK then get live photo"));
				HWAlarmHist hwAlarm = result.getHwAlarmHist();				
				hwAlarm.setLivePhoto(this.hwAPIService.getLivePhoto(hwAlarm.getPicImageUrl()
						, hwAlarm.getPicThumImageUrl(), hwAlarm.getAlarmPicName(), hwAlarm.getHwIPC().getHwVCN().getHwVCM().getVcmName()));
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "extractAlarm OK then get live photo success"));
			}			
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while extract alarm", ex));
			result.getResult().setStatusCode(ResultStatus.ALARM_PARSER_ERROR_CODE, ex.toString());
		}	
		return result;
	}
	
	/*
	 * get unknown person alarm notification
	 */
	private PersonNotification getUnknownPersonNotification(HWAlarmHist hwAlarm) {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "unknown person detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));
		PersonNotification personNotification = new PersonNotification();								
		personNotification.setFirstName("Unknown");
		personNotification.setLastName("Unknown");
		personNotification.setPersonCategory(new PersonCategory(PersonCategory.UNKNOWN_CATEGORY_ID
																, PersonCategory.UNKNOWN_CATEGORY_NAME
																, PersonCategory.UNKNOWN_CATEGORY_DESC
																, PersonCategory.UNKNOWN_CATEGORY_COLOR_CODE));
		personNotification.setAlarmId(hwAlarm.getAlarmhId());			
		personNotification.setHwPeopleId("");
		personNotification.setVcnUsername(hwAlarm.getHwIPC().getHwVCN().getLoginUserName());
		personNotification.setVcnPassword(hwAlarm.getHwIPC().getHwVCN().getLoginPassword());
		personNotification.setVcnPort(hwAlarm.getHwIPC().getHwVCN().getLoginPort());
		personNotification.setLatestIPCCode(hwAlarm.getCameraId());
		personNotification.setLatestVCNIP(hwAlarm.getHwIPC().getHwVCN().getVcnIp());
		personNotification.setPersonPhoto(ImageUtil.resizeImage(this.appCfg.findByAppKey(ApplicationCfg.KEY_IMAGE_UNKNOWN_PERSON).getAppLobValue()
				, this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));		
		//after get image then resize image
		personNotification.setLivePhoto(ImageUtil.resizeImage(hwAlarm.getLivePhoto(), 
										this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
		if (StringUtil.checkNull(personNotification.getLivePhoto())) {
			//if fail to get live photo then set to unknown person
			personNotification.setLivePhoto(personNotification.getPersonPhoto());
		}
		personNotification.setAlarmDate(hwAlarm.getAlarmTime());
		personNotification.setPercentMatch(0);
		personNotification.setAlarmCode(hwAlarm.getAlarmCode());
		return personNotification;
	}
	
	/*
	 * get person alarm notification
	 */
	private PersonNotification getPersonNotification(HWAlarmHist hwAlarm, Date nowDate) {		
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "know person detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));
		this.globalVarService.addLastAlarmPersonDateTime(hwAlarm.getPersonInfo().getPersonId(), nowDate.getTime());
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "known personId:"+hwAlarm.getPersonInfo().getPersonId()+"detected by camera:"+hwAlarm.getHwIPC().getIpcCode()));
		PersonNotification personNotification = new PersonNotification(hwAlarm.getPersonInfo());
		personNotification.getPersonCategory().setHwIPCAnalyzeList(null);
		personNotification.setAlarmId(hwAlarm.getAlarmhId());				
		personNotification.setVcnUsername(hwAlarm.getHwIPC().getHwVCN().getLoginUserName());
		personNotification.setVcnPassword(hwAlarm.getHwIPC().getHwVCN().getLoginPassword());
		personNotification.setVcnPort(hwAlarm.getHwIPC().getHwVCN().getLoginPort());
		personNotification.setLatestIPCCode(hwAlarm.getCameraId());
		personNotification.setLatestVCNIP(hwAlarm.getHwIPC().getHwVCN().getVcnIp());		
		personNotification.setLivePhoto(ImageUtil.resizeImage(hwAlarm.getLivePhoto(), 
										this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
		if (StringUtil.checkNull(personNotification.getLivePhoto())) {
			//if fail to get live photo then set to unknown person
			personNotification.setLivePhoto(ImageUtil.resizeImage(this.appCfg.findByAppKey(ApplicationCfg.KEY_IMAGE_UNKNOWN_PERSON).getAppLobValue()
					, this.thumbnailImgSizeWidth, this.thumbnailImgSizeHeight, null));
		}
		personNotification.setAlarmDate(hwAlarm.getAlarmTime());
		personNotification.setPercentMatch(hwAlarm.getMetaScr());
		personNotification.setAlarmCode(hwAlarm.getAlarmCode());
		return personNotification;
	}
}
