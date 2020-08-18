package com.xpand.xface.service.hwapi;

import java.io.InputStream;

import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonRegisterResult;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonInfo;


public interface HWAPIBatchService {	
	public void initialClass(String transactionId);
	//public HWWSField logOn();
	public HWWSField keepAlive(HWVCM hwVCM);
	public HWWSField keepAliveSDK(HWVCM hwVCM);
	public HWWSField logOut(HWVCM hwVCM);
	public HWWSField changePwd(HWVCM hwVCM, String newPwd);
	public HWWSField addFaceList(HWCheckPointLibrary hwCheckPointLibrary);	
	public HWWSField removeFaceList(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField queryFaceList(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField addCheckPoint(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField removeCheckPoint(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField queryCheckPoint(HWCheckPointLibrary hwCheckPointLibrary, HWIPC hwIPC);
	public HWWSField addCameraToCheckPoint(HWIPC hwIPC);
	public HWWSField removeCameraFromCheckPoint(HWIPC hwIPC);	
	public HWWSField addSuspectTask(HWTaskList hwTaskList);
	public HWWSField removeSuspectTask(HWTaskList hwTaskList);
	public HWWSField querySuspectTask(HWTaskList hwTaskList);
	public HWWSField queryCamera(HWVCM hwVCM, HWIPC hwIPC);
	public ResultStatus getCameraTask();
	public HWWSField subscribeAlarm(HWTaskList hwTaskList);
	public HWWSField unSubscribeAlarm(HWCheckPointLibrary hwCheckPointLibrary );
	//batch call add/remove face from face list
	public HWWSField addFaceToFaceList(String transactionId, PersonRegisterResult personRegister, HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField modifyFaceToFaceList(String transactionId, HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwPeopleId);
	public HWWSField removeFaceFromFaceList(String transactionId, HWCheckPointLibrary hwCheckPointLibrary, String hwPeopleId, String certificateNo);
	////////////////////
	//use for person image query
	public String getLivePhoto(HWVCM hwVCM, String fileId);
	//use for query image on alarm notification
	public String getLivePhoto(String imageUrl, String thumImageUrl, String imageName, String vcmName);	
	public HWWSField getUploadFileURL(HWVCM hwVCM, String fileName, String imageSize);
	public HWWSField uploadFileToServer(HWVCM hwVCM, String fileName, String fileSize, String uploadFileId, String uploadFileType, String endPoint, InputStream intputStream);
	public HWWSField publishUploadFile(HWVCM hwVCM, String fileName, String imageSize, String uploadFieldId);	
	public HWWSField queryFaceFromFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo);
	public HWWSField queryFaceFromFaceList(HWCheckPointLibrary hwCheckPointLibrary, String certificateNo, String certificateType); 
	public ResultStatus getGroupId();	
	//public boolean isInitiate();
	public HWWSField queryPerson(HWVCM hwVCM, String blackListId, String nameListId);
	public HWWSField addIntelligentAnalysisTasks(HWIPC hwIPC);
	public HWWSField queryIntelligentAnalysisTasks(HWIPC hwIPC);
	
	public HWWSField queryFaceAlgorithm(HWVCM hwVCM);
	
	public HWWSField registerCallBackSDK(HWVCM hwVCM);
	public HWWSField subscribeAlarmSDK(HWVCM hwVCM);
	public HWWSField logOutSDK(HWVCM hwVCM);
	public HWWSField unSubscribeAlarmSDK(HWVCM hwVCM);
	
	public HWWSField removePersonZK(String transactionId, String certificateNo);
	public HWWSField addPersonZK(String transactionId, PersonRegisterResult personRegister, int noOfRemove);	
	
}
