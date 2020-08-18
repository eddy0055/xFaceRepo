package com.xpand.xface.service.impl.hwapi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonRegisterResult;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.exception.ConsumeWSException;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.hwapi.HWAPISessionService;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class HWAPISessionServiceImpl extends HWAPIBaseImpl implements HWAPISessionService{	
	

	@Autowired
	private ApplicationCfgService appCfgList;
	
	@Override
	public void initialClass(String transactionId) {
		if (!super.isInitialClass) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "initial class"));
			this.transactionId = transactionId;
			//hw error code
			ApplicationCfg appTmp = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_WS_CODE_SUCCESS);
			this.appCfgHWSuccessCode = appTmp.getAppValue1();		
			appTmp = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_WS_CODE_PLS_LOGIN_FIRST);
			this.appCfgHWPlsLogon = appTmp.getAppValue1();
			appTmp = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_NO_OF_RETRY);
			this.appCfgHWNoOfRetryAPI = StringUtil.stringToInteger(appTmp.getAppValue1(),3);
			appTmp = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_HTTP_CONNECTION_TIMEOUT);
			this.appCfgHttpConnectionTimeout = StringUtil.stringToInteger(appTmp.getAppValue1(),3) * 1000; 
			///////////////////				
			Logger.info(this, LogUtil.getLogInfo(transactionId, "initial class done then ready to use"));
			super.isInitialClass = true;
		}		
	}
	

	/* TEST API ZK */
	//remove person from ZK accept result 0:delete success or -22:The person does not exist
	@Override
	public HWWSField removePersonZK(String transactionId, String zkPin) {
		ApplicationCfg appServer = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_SERVER);
		ApplicationCfg appCfgURL = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_REMOVE_PERSON);
		//status value1=success, value2=person not found
		ApplicationCfg appZKStatus = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_STATUS);
	//	http://serverIP:serverPort/api/person/delete/{pin}?access_token={apitoken}
		//String endPoint = appServer.getAppValue1()+appCfgURL.getAppValue1() + "/"+certificateNo+"?access_token="+appServer.getAppValue2();
		String endPoint = appServer.getAppValue1() + appCfgURL.getAppValue1();
		String wsRequest = "{" + "\"pin\": \"" +zkPin +"\""  + "}";
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, null);											
				result = HWXMLUtil.extractRemovePersonZK(this.transactionId, this, wsResponse, appZKStatus.getAppValue1(), appZKStatus.getAppValue2());
				executeAPISuccess = true;
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	
	
	@Override
	public HWWSField addPersonZK(String transactionId, PersonRegisterResult personRegister) {
		String pin = personRegister.getCertificateNo();
		String cardNo = personRegister.getCardNo();
		String name = personRegister.getFullName();
		ApplicationCfg appServer = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_SERVER);
		ApplicationCfg appCfgURL = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_ADD_PERSON);
		//status value1=success, value2=person not found
		ApplicationCfg appZKStatus = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_STATUS);
		//status value1=dept code, value2 = access level		
		ApplicationCfg appParam = this.appCfgList.findByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_ADD_PERSON_PARAM);
		//	http://serverIP:serverPort/api/person/add?access_token={apitoken}
		//String endPoint = appServer.getAppValue1()+appCfgURL.getAppValue1() + "?access_token="+appServer.getAppValue2();			
		String endPoint = appServer.getAppValue1()+appCfgURL.getAppValue1();
		Date today = new Date();
		Calendar endDate = Calendar.getInstance();
        endDate.setTime(today);
        endDate.add(Calendar.YEAR, 1);
		//String endTime = StringUtil.dateToString(DateTimeUtil.addDate(personRegister.getRegisterDate().getTime()), StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);		
		String wsRequest = "{" + 
				"\"pin\": \""+personRegister.getCertificateNo()+"\"," + 
				"\"deptCode\": \""+appParam.getAppValue1()+"\"," + 
				"\"name\": \""+personRegister.getFullName()+"\"," + 
				"\"lastName\": \"\"," + 
				"\"gender\": \"F\"," + 
				"\"cardNo\": \"\"," + 
				"\"accLevelIds\": \""+appParam.getAppValue2()+"\"," + 
				"\"accStartTime\": \""+today+"\"," + 
				"\"accEndTime\": \""+today+"\"" + 
				"}";
		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this,LogUtil.getLogInfo(transactionId, "wsRequest Result :" + wsRequest));
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, null);											
				result = HWXMLUtil.extractAddPersonZK(this.transactionId, this, wsResponse, appZKStatus.getAppValue1(), appZKStatus.getAppValue2());
				executeAPISuccess = true;
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}	
		
	@Override
	public HWWSField addFaceToFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwFileId) {
//		<request>
//			<peopleList>
//				<people>
//					<credentialNumber>ID0004</credentialNumber>
//					<credentialType>0</credentialType>
//					<name>eddy naja</name>
//					<pictures>
//						<picture>
//							<base64>/9j/4Z
//							</base64>
//						</picture>
//					</pictures>
//				</people>
//			</peopleList>
//		</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <ids>
//		        <id>5be0091ea2ef2e313048b053</id>
//		    </ids>
//		</response>
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_ADD_FACE_TO_LIST);						
		String wsRequest = "<request><peopleList><people>" +
				"<credentialNumber>" + personInfo.getCertificateNo() + "</credentialNumber>" +
				"<credentialType>"+ personInfo.getPersonCertificate().getThirdPartyCode() + "</credentialType>" +
				"<name>" + personInfo.getFullName() +"</name>" + 
//				"<name>" + StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) +"</name>" +
				"<pictures>" + 
				"<picture>" +				
				(base64Image==null ? "<fileId>"+hwFileId+"</fileId>" : "<base64>"+base64Image+"</base64>") + 
				"</picture>" + 
				"</pictures>" + 
				"</people>" + 
				"</peopleList>" + 
				"</request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, StringUtil.checkNull(hwCheckPointLibrary.getLibraryId())?"":hwCheckPointLibrary.getLibraryId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceToList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn(hwVCM);				
				}else {
					executeAPISuccess = true;
					break;
				}
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;	
	}	
	@Override
	public HWWSField modifyFaceToFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwPeopleId) {
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_MODIFY_FACE_TO_LIST);						
		String wsRequest = "<request><people>" +
				"<credentialNumber>" + personInfo.getCertificateNo() + "</credentialNumber>" +
				"<credentialType>"+ personInfo.getPersonCertificate().getThirdPartyCode() + "</credentialType>" +
				"<name>" + personInfo.getFullName() +"</name>" +
//				"<name>" + StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) +"</name>" +
				"<addPictures>" + 
				"<picture>" +				
				"<base64>"+base64Image+"</base64>" + 
				"</picture>" + 
				"</addPictures>" + 
				"</people>" + 				
				"</request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+ (StringUtil.checkNull(hwPeopleId)?"":"/"+hwPeopleId);	
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, hwCheckPointLibrary.getLibraryId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceToList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn(hwVCM);				
				}else {
					executeAPISuccess = true;
					break;
				}
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;	
	}
	@Override
	public HWWSField removeFaceFromFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo) {
		//https://192.168.2.53:8009/sdk_service/rest/facerepositories/5be00725a2ef2e313048b052/peoples?credentialnumbers=ID0004
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <peopleList>
//		        <people>
//		            <result>
//		                <errmsg>Success</errmsg>
//		                <code>0</code>
//		            </result>
//		            <credentialNumber>ID0004</credentialNumber>
//		            <peopleId>5be0091ea2ef2e313048b053</peopleId>
//		        </people>
//		    </peopleList>
//		</response>
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_REMOVE_FACE_FROM_LIST);						
		ApplicationCfg appStatus = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_REMOVE_FACE_STATUS_NOT_EXIST);
		String wsRequest = "ids="+personInfo.getHwPeopleId();
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;	
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, hwCheckPointLibrary.getLibraryId()==null? "": hwCheckPointLibrary.getLibraryId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveFaceFromList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon, appStatus.getAppValue1());
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn(hwVCM);				
				}else {
					executeAPISuccess = true;
					break;
				}				
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;	
	}	
	
	@Override
	public HWWSField queryPersonByPhoto(HWVCM hwVCM, ArrayList<String> hwIPCList, String base64Photo, int pageNo, Date startDate, Date endDate, int confidenceThreshold) {		
//		<request>
//			<cameraSns>
//				<cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>
//			</cameraSns>
//			<pictures>
//				<picture>
//					<fileId>5c00dec0a2ef2e5abf20512d</fileId>
//					<base64>xxxxxx</base64>
//				</picture>
//			</pictures>
//			<algorithmCode>0104000100</algorithmCode>
//			<confidenceThreshold>60</confidenceThreshold>
//			<endTime>1543559381000</endTime>
//			<startTime>1542695381000</startTime>
//			<page>
//				<no>1</no>
//				<orderName>similarity</orderName>
//				<size>2</size>
//				<sort>desc</sort>
//			</page>
//		</request>
		HWWSField result = null;
		if (hwIPCList==null || hwIPCList.size()==0) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Please provide camera code for search"));
			return result;
		}
		ApplicationCfg appCfgURL = this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_QUERY_PERSON_BY_PHOTO);				
		String wsRequest = "<request><cameraSns>";
		for (String ipcCode:hwIPCList) {
			wsRequest += "<cameraSn>"+ipcCode+"</cameraSn>";
		}								
		wsRequest += "</cameraSns><pictures><picture><base64>"+base64Photo+"</base64></picture></pictures>"
				+ "<confidenceThreshold>"+confidenceThreshold+"</confidenceThreshold>"				
				+ "<endTime>"+endDate.getTime()+"</endTime>"
				+ "<startTime>"+startDate.getTime()+"</startTime>"
				+ "<page><no>"+pageNo+"</no><orderName>similarity</orderName><size>10</size><sort>desc</sort></page></request>";		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryPersonByPhoto(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn(hwVCM);				
				}else {
					executeAPISuccess = true;
					break;
				}
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}	
	private HWWSField logOn(HWVCM hwVCM) {
		return super.logOn(this.transactionId, hwVCM, this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_API_LOGIN)
				, this.appCfgList.findByAppKey(transactionId, ApplicationCfg.KEY_WS_CODE_FIRST_LOGIN).getAppValue1());	
	}	
	
}
