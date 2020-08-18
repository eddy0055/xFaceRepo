package com.xpand.xface.service.hwapi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.service.GlobalVarService;


public interface HWAPIServiceBackup {	
	public void initialClass(String transactionId, HashMap<String, ApplicationCfg> appCfgList, HWVCM hwVCM, GlobalVarService globalVarService);
	public HWWSField logOn();
	public HWWSField keepAlive();
	public HWWSField logOut();
	public HWWSField changePwd(String newPwd);
	public HWWSField addFaceList(HWCheckPointLibrary hwCheckPointLibrary);	
	public HWWSField removeFaceList(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField queryFaceList(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField addCheckPoint(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField removeCheckPoint(HWCheckPointLibrary hwCheckPointLibrary);
	public HWWSField queryCheckPoint(HWCheckPointLibrary hwCheckPointLibrary, HWIPC hwIPC);
	public HWWSField addCameraToCheckPoint(HWIPC hwIPC);
//	public HWWSField queryCameraFromCheckPoint(HWIPC hwIPC, int pageNo, int pageSize);
	public HWWSField removeCameraFromCheckPoint(HWIPC hwIPC);	
	public HWWSField addSuspectTask(HWTaskList hwTaskList);
	public HWWSField removeSuspectTask(HWTaskList hwTaskList);
	public HWWSField querySuspectTask(HWTaskList hwTaskList);
	public HWWSField queryCamera(HWIPC hwIPC);
	public ResultStatus getCameraTask();
	public HWWSField subscribeAlarm(HWTaskList hwTaskList);
	public HWWSField unSubscribeAlarm(HWCheckPointLibrary hwCheckPointLibrary );	
	//use for person image query
	public String getLivePhoto(String fileId);
	//use for query image on alarm notification
	public String getLivePhoto(String imageUrl, String thumImageUrl, String imageName, String vcmName);	
	public HWWSField getUploadFileURL(String fileName, String imageSize);
	public HWWSField uploadFileToServer(String fileName, String fileSize, String uploadFileId, String uploadFileType, String endPoint, InputStream intputStream);
	public HWWSField publishUploadFile(String fileName, String imageSize, String uploadFieldId);
	public HWWSField addFaceToList1(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwFileId);	
	public HWWSField modifyFaceToList1(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwPeopleId);
	public HWWSField removeFaceFromList1(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo);
	public HWWSField queryFaceFromList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo);
	public ResultStatus getGroupId();	
	public boolean isInitiate();
	public HWWSField queryPerson(String blackListId, String nameListId);
	public HWWSField addIntelligentAnalysisTasks(HWIPC hwIPC);
	public HWWSField queryIntelligentAnalysisTasks(HWIPC hwIPC);
	public HWWSField queryPersonByPhoto1(ArrayList<String> hwIPCList, String base64Photo, int pageNo, Date startDate, Date endDate, int confidenceThreshold);
	public HWWSField queryFaceAlgorithm();
}
