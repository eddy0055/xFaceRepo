package com.xpand.xface.service;

import java.io.InputStream;
import java.util.HashMap;

import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWIPCAnalyzeList;
import com.xpand.xface.entity.HWVCM;


public interface HWAPIService {	
	public void initialClass(String transactionId, HashMap<String, ApplicationCfg> appCfgList, HWVCM hwVCM, GlobalVarService globalVarService);
	public HWWSField logOn();
	public HWWSField keepAlive();
	public HWWSField logOut();
	public HWWSField changePwd(String newPwd);
	public HWWSField addFaceList(HWIPCAnalyzeList hwIPCAnalyzeList);	
	public HWWSField removeFaceList(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField queryFaceList(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField addCheckPoint(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField removeCheckPoint(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField queryCheckPoint(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField addCameraToCheckPoint(HWIPC hwIPC);
	public HWWSField queryCameraFromCheckPoint(HWIPC hwIPC, int pageNo, int pageSize);
	public HWWSField removeCameraFromCheckPoint(HWIPC hwIPC);	
	public HWWSField addSuspectTask(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField removeSuspectTask(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField querySuspectTask(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField queryCamera(HWIPC hwIPC);
	public ResultStatus getCameraTask();
	public HWWSField subscribeAlarm(HWIPCAnalyzeList hwIPCAnalyzeList);
	public HWWSField unSubscribeAlarm(HWIPCAnalyzeList hwIPCAnalyzeList );	
	public String getLivePhoto(String imageUrl, String thumImageUrl, String imageName, String vcmName);
	public ResultStatus searchPeopleLocation();
	public HWWSField getUploadFileURL(String fileName, String imageSize);
	public HWWSField uploadFileToServer(String fileName, String fileSize, String uploadFileId, String uploadFileType, String endPoint, InputStream intputStream);
	public HWWSField publishUploadFile(String fileName, String imageSize, String uploadFieldId);
	public HWWSField addFaceToList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo, String base64Image, String hwFileId);	
	public HWWSField modifyFaceToList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo, String base64Image, String hwPeopleId);
	public HWWSField removeFaceFromList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo);
	public HWWSField queryFaceFromList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo);
	public ResultStatus getGroupId();	
	public boolean isInitiate();
	public HWWSField queryPerson(String blackListId, String nameListId);
}
