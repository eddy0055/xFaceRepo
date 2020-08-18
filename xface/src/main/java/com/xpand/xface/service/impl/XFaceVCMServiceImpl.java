package com.xpand.xface.service.impl;

import java.util.HashMap;
import java.util.List;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWIPCAnalyzeList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.HWAPIService;
import com.xpand.xface.service.HWIPCAnalyzeListService;
import com.xpand.xface.util.LogUtil;

public class XFaceVCMServiceImpl{	
	HWAPIService hwAPIService;
	List<HWVCM> hwVCMList;
	String transactionId;
	boolean isLoop = true;
	boolean isTerminate = true;
	ApplicationCfg appCfg = null;
	HashMap<String, ApplicationCfg> appCfgList = null;
	GlobalVarService globalVarService;
	HWIPCAnalyzeListService hwIPCAnalyzeListService;
	ResultStatus resultStart = null;
	public XFaceVCMServiceImpl(String transactionId, List<HWVCM> hwVCMList, HashMap<String, ApplicationCfg> appCfgList, GlobalVarService globalVarService
			, HWAPIService hwAPIService, HWIPCAnalyzeListService hwIPCAnalyzeListService) {
		this.transactionId = transactionId+"_VCM";
		this.hwVCMList = hwVCMList;		
		this.appCfgList = appCfgList;
		this.globalVarService = globalVarService;
		this.hwAPIService = hwAPIService;
		this.hwIPCAnalyzeListService = hwIPCAnalyzeListService;
	}	
	public boolean getIsTerminate() {
		return this.isTerminate;
	}			
	public void subscribeAlarm() {		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start subscribeAlarm"));
		HWAPIServiceImpl hwAPIService = new HWAPIServiceImpl();		
		int startFail=0;
		int startSuccess=0;
		ResultStatus result = null;
		for (HWVCM hwVCM: this.hwVCMList) {
			hwAPIService.initialClass(this.transactionId, this.appCfgList, hwVCM, this.globalVarService);			
			result = this.subscribeAlarm(hwVCM, hwAPIService);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {				
				startSuccess++;
			}else {
				startFail++;
			}
		}
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
	private ResultStatus subscribeAlarm(HWVCM hwVCM, HWAPIService hwAPIService) {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start subscribeAlarm for VCM:"+hwVCM.getVcmName()));
		HWWSField result = null;		
		for (HWIPCAnalyzeList hwipca:hwVCM.getHwIPCAnalyzeLists()) {
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for suspect Id "+hwipca.getSuspectId()+" name "+hwipca.getSuspectName()));
			//1. get face list id from vcm			
			result = hwAPIService.queryFaceList(hwipca);
			if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				//update id
				hwipca.setListNameId(result.getQueryFaceListResp().getFaceListId());
			}else {
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, "fail to queryFaceList  result is "+result.getResult().toString()));
				return result.getResult();
			}
			//2. search suspect task			
			result = hwAPIService.querySuspectTask(hwipca);
			this.resultStart = result.getResult();
			if (result.getResult().getStatusCode()==ResultStatus.SUCCESS_CODE) {
				//update id
				hwipca.setSuspectId(result.getQuerySuspectTaskResp().getSuspectId());
				hwipca.setSuspectType(result.getQuerySuspectTaskResp().getSuspectType());
			}else {
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, "fail to querySuspectTask  result is "+result.getResult().toString()));
				return result.getResult();
			}
					
			result = hwAPIService.subscribeAlarm(hwipca);
			if (!result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, "fail to subscribeAlarm  result is "+result.getResult().toString()));
				return result.getResult();
			}
			//update data for hwIPCAnaylzeList			
			this.hwIPCAnalyzeListService.updateByService(hwipca);
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for suspect Id "+hwipca.getSuspectId()+" name "+hwipca.getSuspectName()+" result is "+result.getResult().toString()));				
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for VCM:"+hwVCM.getVcmName()+" success"));
		return result.getResult();
	}
	
	public void unSubscribeAlarm() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start unSubscribeAlarm"));
		HWAPIServiceImpl hwAPIService = new HWAPIServiceImpl();
		for (HWVCM hwVCM: this.hwVCMList) {
			hwAPIService.initialClass(this.transactionId, this.appCfgList, hwVCM, this.globalVarService);
			this.unsubscribeAlarm(hwVCM, hwAPIService);					
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "end unSubscribeAlarm"));
	}
	private void unsubscribeAlarm(HWVCM hwVCM, HWAPIService hwAPIService) {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start unsubscribeAlarm for VCM:"+hwVCM.getVcmName()));
		HWWSField result = null;
		for (HWIPCAnalyzeList hwipca:hwVCM.getHwIPCAnalyzeLists()) {
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unSubscribeAlarm for suspect Id "+hwipca.getSuspectId()+" name "+hwipca.getSuspectName()));
			result = hwAPIService.unSubscribeAlarm(hwipca);
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unSubscribeAlarm for suspect Id "+hwipca.getSuspectId()+" name "+hwipca.getSuspectName()+" result is "+result.getResult().toString()));
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "subscribeAlarm for VCM:"+hwVCM.getVcmName()+" success"));
	}
	public ResultStatus getResultStart() {
		return resultStart;
	}
}
