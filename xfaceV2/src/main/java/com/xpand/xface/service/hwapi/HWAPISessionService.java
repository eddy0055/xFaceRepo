package com.xpand.xface.service.hwapi;

import java.util.ArrayList;
import java.util.Date;

import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonRegisterResult;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonInfo;


public interface HWAPISessionService {	
	public void initialClass(String transactionId);	
	//individual call
	public HWWSField addFaceToFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwFileId);	
	////////////////
	public HWWSField modifyFaceToFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwPeopleId);
	//individual call
	public HWWSField removeFaceFromFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo);	
	/////////////////////////
	public HWWSField queryPersonByPhoto(HWVCM hwVCM, ArrayList<String> hwIPCList, String base64Photo, int pageNo, Date startDate, Date endDate, int confidenceThreshold);
	
	/// Add for Test ZK API
	public HWWSField removePersonZK(String transactionId, String certificateNo);
	public HWWSField addPersonZK(String transactionId, PersonRegisterResult personRegister);
	
	
}
