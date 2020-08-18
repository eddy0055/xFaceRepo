package com.xpand.xface.service.batch.impl;

import java.util.Iterator;
import java.util.List;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.hwapi.HWAPIBatchService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

public class XFaceVCMServiceImpl{	
	private List<HWVCM> hwVCMList;
	private String transactionId;
	private boolean isTerminate = true;
	private ResultStatus resultStart = null;
	private HWAPIBatchService hwAPIService;
	public XFaceVCMServiceImpl(String transactionId, List<HWVCM> hwVCMList, HWAPIBatchService hwAPIService) {
		this.transactionId = transactionId+"_VCM";
		this.hwVCMList = hwVCMList;			
		this.hwAPIService = hwAPIService;
	}	
	public boolean getIsTerminate() {
		return this.isTerminate;
	}			
	public void subscribeAlarm() {		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start subscribeAlarm"));			
		int startFail=0;
		int startSuccess=0;
		ResultStatus result = null;
		for (HWVCM hwVCM: this.hwVCMList) {
			//alarm of VCM
			result = this.subscribeAlarm(hwVCM);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {				
				startSuccess++;
			}else {
				startFail++;
			}
			result = this.subscribeAlarmSDK(hwVCM);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {				
				startSuccess++;
			}else {
				startFail++;
			}
		}
		startSuccess = startSuccess / 2;
		startFail = startFail / 2;
		if (startSuccess==this.hwVCMList.size()) {
			//success
			this.resultStart = new ResultStatus();
		}else if (startFail==this.hwVCMList.size()) {
			//totally fail
			this.resultStart = new ResultStatus(ResultStatus.START_XFACE_TOTALLY_FAIL_ERROR_CODE, null);
		}else {
			//partial fail
			this.resultStart = new ResultStatus(ResultStatus.START_XFACE_PARTIAL_FAIL_ERROR_CODE, null);
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm with result:"+this.resultStart.toString()));
	}	
	private ResultStatus subscribeAlarm(HWVCM hwVCM) {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start subscribeAlarm for VCM:"+hwVCM.getVcmName()));
		HWWSField result = null;
		Iterator<HWCheckPointLibrary> hwCheckPointLibraryList = hwVCM.getHwCheckPointLibraryList().iterator();
		HWCheckPointLibrary hwCheckPointLibrary = null; 
		Iterator<HWTaskList> hwTaskLists = null;
		HWTaskList hwTaskList = null;
		while (hwCheckPointLibraryList.hasNext()) {
			hwCheckPointLibrary = hwCheckPointLibraryList.next();
			if (StringUtil.checkNull(hwCheckPointLibrary.getCheckPointId()) || StringUtil.checkNull(hwCheckPointLibrary.getLibraryId())) {
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, "check point "+hwCheckPointLibrary.getCheckPointName()+" id:"+hwCheckPointLibrary.getCheckPointId()+" Library:"+hwCheckPointLibrary.getLibraryName()+" id:"+hwCheckPointLibrary.getLibraryId()+" no need to subscribe alarm"));
//				return new ResultStatus(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE, "check point or library is not create yet");
			}else {
				hwTaskLists = hwCheckPointLibrary.getHwTaskListList().iterator();
				while (hwTaskLists.hasNext()) {			
					hwTaskList = hwTaskLists.next();
					if (StringUtil.checkNull(hwTaskList.getTaskId())) {
						Logger.info(this, LogUtil.getLogInfo(this.transactionId, "task:"+hwTaskList.getTaskName()+" id is null no need to subscribe alarm"));
					}else {
						Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for suspect Id "+hwTaskList.getTaskId()+" name "+hwTaskList.getTaskName()));
						result = this.hwAPIService.subscribeAlarm(hwTaskList);
						if (!result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
							Logger.info(this, LogUtil.getLogInfo(this.transactionId, "fail to subscribeAlarm  result is "+result.getResult().toString()));
							return result.getResult();
						}
						Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for suspect Id "+hwTaskList.getTaskId()+" name "+hwTaskList.getTaskName()+" result is "+result.getResult().toString()));
					}										
				}
			}			
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for VCM:"+hwVCM.getVcmName()+" success"));
		if (result==null) {
			return new ResultStatus(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE, "check point or library is not create yet");
		}else {
			return result.getResult();
		}		
	}
	private ResultStatus subscribeAlarmSDK(HWVCM hwVCM) {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start subscribeAlarmSDK for VCN:"+hwVCM.getVcnName()));
		//register call back first then subscribe alarm
		HWWSField result = this.hwAPIService.registerCallBackSDK(hwVCM);		
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			result = this.hwAPIService.subscribeAlarmSDK(hwVCM);
		}		
		if (!result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "fail to subscribeAlarmSDK result is "+result.getResult().toString()));
			return result.getResult();
		}				
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for VCM:"+hwVCM.getVcmName()+" success"));		
		return result.getResult();			
	}
	
	public void unSubscribeAlarm() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start unSubscribeAlarm"));
		for (HWVCM hwVCM: this.hwVCMList) {			
			this.unsubscribeAlarm(hwVCM);
			this.unsubscribeAlarmSDK(hwVCM);
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "end unSubscribeAlarm"));
	}
	private void unsubscribeAlarm(HWVCM hwVCM) {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start unsubscribeAlarm for VCM:"+hwVCM.getVcmName()));
		HWWSField result = null;
		for (HWCheckPointLibrary hwChkPoint:hwVCM.getHwCheckPointLibraryList()) {
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unSubscribeAlarm for library id "+hwChkPoint.getLibraryId()+" name "+hwChkPoint.getLibraryName()));
			result = this.hwAPIService.unSubscribeAlarm(hwChkPoint);
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unSubscribeAlarm for library id "+hwChkPoint.getLibraryId()+" name "+hwChkPoint.getLibraryName()+" result is "+result.getResult().toString()));
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unSubscribeAlarm for VCM:"+hwVCM.getVcmName()+" success"));
	}
	private void unsubscribeAlarmSDK(HWVCM hwVCM) {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start unsubscribeAlarmSDK for VCN:"+hwVCM.getVcnName()));
		HWWSField result = this.hwAPIService.unSubscribeAlarmSDK(hwVCM);		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unSubscribeAlarm for VCN:"+hwVCM.getVcnName()+" result:"+result.getResult().toString()));
	}
	public ResultStatus getResultStart() {
		return resultStart;
	}
}
