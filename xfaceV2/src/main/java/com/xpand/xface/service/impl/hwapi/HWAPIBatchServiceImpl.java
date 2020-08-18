package com.xpand.xface.service.impl.hwapi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonRegisterResult;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.exception.ConsumeWSException;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.service.hwapi.HWAPIBatchService;
import com.xpand.xface.util.DateTimeUtil;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@ApplicationScope
@Component
public class HWAPIBatchServiceImpl extends HWAPIBaseImpl implements HWAPIBatchService{	
	@Autowired
	protected XFaceBatchService xFaceBatchService;
	@Override
	public void initialClass(String transactionId) {
		if (!super.isInitialClass) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "initial class"));
			this.transactionId = transactionId;
			//hw error code
			ApplicationCfg appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_WS_CODE_SUCCESS);
			this.appCfgHWSuccessCode = appTmp.getAppValue1();		
			this.appCfgHWSDKSuccessCode = appTmp.getAppValue2();
			appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_WS_CODE_PLS_LOGIN_FIRST);
			this.appCfgHWPlsLogon = appTmp.getAppValue1();
			this.appCfgHWSDKPlsLogon = appTmp.getAppValue2();
			appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_NO_OF_RETRY);
			this.appCfgHWNoOfRetryAPI = StringUtil.stringToInteger(appTmp.getAppValue1(),3);
			appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_HTTP_CONNECTION_TIMEOUT);
			this.appCfgHttpConnectionTimeout = StringUtil.stringToInteger(appTmp.getAppValue1(),3) * 1000;
			Logger.info(this, LogUtil.getLogInfo(transactionId, "initial class done then ready to use"));
			///////////////////
			super.isInitialClass = true;
		}						
	}	
	
	@Override
	public HWWSField keepAlive(HWVCM hwVCM) {
		HWWSField result = null;
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_KEEP_ALIVE);
		//if not j session id for this vcm then return immediately
		if (this.globalVarService.getJSessionId(hwVCM.getVcmName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE, hwVCM.getVcmName()));
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, appCfg.getAppValue2()+" no jsession"));
			return result;
		}		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;
		
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = super.invokeRestfulService(this.transactionId, endPoint, null, appCfg.getAppValue2(), appCfg.getAppValue3(), false, hwVCM.getVcmName());
			result = HWXMLUtil.extractKeepAlive(this.transactionId, wsResponse, this.appCfgHWSuccessCode);						
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;		
	}
	@Override
	public HWWSField keepAliveSDK(HWVCM hwVCM) {
		HWWSField result = null;
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_KEEP_ALIVE_SDK);
		//if not j session id for this vcm then return immediately
		if (this.globalVarService.getJSessionId(hwVCM.getVcnName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE, hwVCM.getVcnName()));
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, appCfg.getAppValue2()+" no jsession"));
			return result;
		}		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcnSDKIp()+":"+hwVCM.getVcnSDKLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;
		String wsRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esdk=\"esdk_ivs_professional_server\"><soapenv:Header/><soapenv:Body><esdk:keepAlive/></soapenv:Body></soapenv:Envelope>";
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = super.invokeSoapService(this.transactionId, endPoint, wsRequest, appCfg.getAppValue2(), false, hwVCM.getVcnName(), appCfg.getAppValue3()); 
			result = HWXMLUtil.extractKeepAliveSDK(this.transactionId, wsResponse, this.appCfgHWSDKSuccessCode);						
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;		
	}
	@Override
	public HWWSField logOut(HWVCM hwVCM) {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_LOGOUT);
		HWWSField result = null;
		//if not j session id for this vcm then return immediately
		if (this.globalVarService.getJSessionId(hwVCM.getVcmName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE, hwVCM.getVcmName()));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" no jsession"));
			return result;
		}
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfg.getAppValue2(), appCfg.getAppValue3(), false, hwVCM.getVcmName());
//			wsResponse =  "<response><result><code>0</code><errmsg>Success.</errmsg></result></response>";
			result = HWXMLUtil.extractLogOut(this.transactionId, wsResponse, this.appCfgHWSuccessCode);						
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;		
	}
	@Override
	public HWWSField changePwd(HWVCM hwVCM, String newPwd) {
		return super.changePwd(this.transactionId, hwVCM, this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_CHANGE_PWD),newPwd);		
	}
	@Override
	public HWWSField addFaceList(HWCheckPointLibrary hwCheckPointLibrary) {
//		<request>
//			<repository>
//				<type>3</type> --> 3 is whitelist
//				<name>eddydemo</name> --> name cannot include _
//			</repository>
//		</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <id>5be00725a2ef2e313048b052</id>
//		</response>
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ADD_FACE_LIST);						
		String wsRequest = "<request><repository><type>"+hwCheckPointLibrary.getLibraryType()+"</type><name>"+hwCheckPointLibrary.getLibraryName()+"</name></repository></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField removeFaceList(HWCheckPointLibrary hwCheckPointLibrary) {
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_REMOVE_FACE_LIST);						
		String wsRequest = hwCheckPointLibrary.getLibraryId();		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, wsRequest);
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveFaceList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField queryFaceList(HWCheckPointLibrary hwCheckPointLibrary) {
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_FACE_LIST);						
		String wsRequest = "size=1&no=1&sort=asc&ordername=time&" + (hwCheckPointLibrary.getLibraryName()==null ? "id="+hwCheckPointLibrary.getLibraryId() : "name="+hwCheckPointLibrary.getLibraryName());
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1() + "?" + wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryFaceList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField addCheckPoint(HWCheckPointLibrary hwCheckPointLibrary) {
//		<request>
//			<name>eddycheckpoint</name>
//			<type>2</type> --> 2 is face image checkpoint
//		</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//			<sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
//		</response>
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ADD_CAMERA_CHECK_POINT);				
		String wsRequest = "<request><name>"+hwCheckPointLibrary.getCheckPointName()+"</name><type>"+hwCheckPointLibrary.getCheckPointType()+"</type></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddCameraCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField removeCheckPoint(HWCheckPointLibrary hwCheckPointLibrary) {
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_REMOVE_CAMERA_CHECK_POINT);				
		String wsRequest = "<request><bayonetList><sn>"+hwCheckPointLibrary.getCheckPointId()+"</sn></bayonetList></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveCameraCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField queryCheckPoint(HWCheckPointLibrary hwCheckPointLibrary, HWIPC hwIPC) {
//		<request>
//			<name>eddycheckpoint</name>
//			<page>
//				<no>1</no>
//				<pageSize>1</pageSize>
//			</page>
//		</request>
//		<response>
//		    <bayonetList>
//		        <bayonet>
//		            <createTime>2018-11-05 17:20:29</createTime>
//		            <name>eddycheckpoint</name>
//		            <sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
//		            <type>2</type>
//		        </bayonet>
//		    </bayonetList>
//		    <result>
//		        <errmsg>Success</errmsg>
//		        <code>0</code>
//		    </result>
//		    <total>1</total>
//		</response>		
		
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_CAMERA_CHECK_POINT);				
		String wsRequest = "<request><name>"+hwCheckPointLibrary.getCheckPointName()+"</name><page><no>1</no><pageSize>1</pageSize></page>";
		if (hwIPC==null) {
			wsRequest+="</request>";
		}else {
			wsRequest+="<cameraList><cameraSn>"+hwIPC.getIpcCode()+"</cameraSn></cameraList></request>";
		}
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryCameraCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField addSuspectTask(HWTaskList hwTaskList) {
//		<request>
//			<type>3</type>
//			<hitType>1</hitType> -> hit, 
//			<name>whitelistxxhit</name>
//			<startDate>2000-01-01</startDate>
//			<endDate>2099-01-01</endDate>
//			<timeList>
//				<time><day>0</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//				<time><day>1</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//				<time><day>2</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//				<time><day>3</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//				<time><day>4</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//				<time><day>5</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//				<time><day>6</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			</timeList>
//			<groupList>
//				<groupId>5be00725a2ef2e313048b052</groupId> --> face list id
//			</groupList>
//			<domains>
//				<domain>
//					<bayonetList>
//						<bayonetSn>aaf86369-727e-46b8-9ff7-c466c769f752</bayonetSn> --> check point sn
//					</bayonetList>
//				</domain>
//			</domains>
//			<confidenceThreshold>85</confidenceThreshold>
//		</request>			
//		<response>
//	    	<result>
//	        	<errmsg>Success.</errmsg>
//	        	<code>0</code>
//	    	</result>
//	    	<suspectId>5be04360a2ef2e313048b06d</suspectId>
//		</response>
//		non hit
//		<request>
//		<type>3</type>
//		<hitType>2</hitType> -> nonhit, 
//		<name>whitelistxxnonhit</name>
//		<startDate>2000-01-01</startDate>
//		<endDate>2099-01-01</endDate>
//		<timeList>
//			<time><day>0</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			<time><day>1</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			<time><day>2</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			<time><day>3</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			<time><day>4</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			<time><day>5</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//			<time><day>6</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>
//		</timeList>
//		<groupList>
//			<groupId>5be00725a2ef2e313048b052</groupId> --> face list id
//		</groupList>
//		<domains>
//			<domain>
//				<bayonetList>
//					<bayonetSn>aaf86369-727e-46b8-9ff7-c466c769f752</bayonetSn> --> check point sn
//				</bayonetList>
//			</domain>
//		</domains>
//		<confidenceThreshold>85</confidenceThreshold>
//	</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <suspectId>5be043d0a2ef2e313048b06e</suspectId>
//		</response>		
		HWVCM hwVCM = hwTaskList.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ADD_SUSPECT_TASK);				
		String wsRequest = "<request><type>"+hwTaskList.getTaskType()+"</type><name>"+hwTaskList.getTaskName()+"</name>"
						+"<startDate>"+hwTaskList.getTaskStartDate()+"</startDate><endDate>"+hwTaskList.getTaskEndDate()+"</endDate>"
						+"<timeList>"
						+"<time><day>0</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>1</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>2</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>3</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>4</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>5</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>6</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"</timeList>"
						+"<groupList><groupId>"+hwTaskList.getHwCheckPointLibrary().getLibraryId()+"</groupId></groupList>"
						+"<domains><domain>"
						+"<bayonetList><bayonetSn>"+hwTaskList.getHwCheckPointLibrary().getCheckPointId()+"</bayonetSn></bayonetList>"
						+"</domain></domains>"
						+"<request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddSuspectTask(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField removeSuspectTask(HWTaskList hwTaskList) {
		HWVCM hwVCM = hwTaskList.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_REMOVE_SUSPECT_TASK);				
		String wsRequest = "<request><suspectList><suspectId>"+hwTaskList.getTaskId()+"</suspectId></suspectList></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveSuspectTask(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField querySuspectTask(HWTaskList hwTaskList) {
		HWVCM hwVCM = hwTaskList.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_SUSPECT_TASK);				
		String wsRequest = "<request><name>"+hwTaskList.getTaskName()+"</name><page><no>1</no><pageSize>10</pageSize></page></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQuerySuspectTask(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField addCameraToCheckPoint(HWIPC hwIPC) {		
//		<request>
//			<cameraList>
//				<cameraSn>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</cameraSn>
//			</cameraList>
//			<sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
//		</request> 
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		</response>
		HWVCM hwVCM = hwIPC.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ADD_CAMERA_TO_CHECK_POINT);				
		String wsRequest = "<request><cameraList><cameraSn>"+hwIPC.getIpcCode()+"</cameraSn></cameraList><sn>"+hwIPC.getHwCheckPointLibrary().getCheckPointId()+"</sn></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddCameraToCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField removeCameraFromCheckPoint(HWIPC hwIPC) {
//		<request>
//		<cameraList>
//			<cameraSn>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</cameraSn>
//			<cameraSn>01174380000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</cameraSn>
//		</cameraList>
//		<sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
//	</request> 
//	<response>
//	    <result>
//	        <errmsg>Success.</errmsg>
//	        <code>0</code>
//	    </result>
//	</response>
		HWVCM hwVCM = hwIPC.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_REMOVE_CAMERA_FROM_CHECK_POINT);				
		String wsRequest = "<request><cameraList><cameraSn>"+hwIPC.getIpcCode()+"</cameraSn></cameraList><sn>"+hwIPC.getHwCheckPointLibrary().getCheckPointId()+"</sn></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveCameraFromCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField queryCamera(HWVCM hwVCM, HWIPC hwIPC) {		
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_CAMERA);				
		String wsRequest = null;
		if (hwIPC==null) {
			wsRequest = "?page=1&limit=999";
		}else {
			wsRequest = "?sn="+hwIPC.getIpcCode()+"&page=1&limit=1";
		}		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryCamera(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public ResultStatus getCameraTask() {
		return null;
	}	
	
	@Override
	public HWWSField subscribeAlarm(HWTaskList hwTaskList) {		
//		<request>
//			<callbackUrl>
//				<master>http://192.168.2.249:8090/rest/testhit</master>
//			</callbackUrl>
//			<suspectId>5be04360a2ef2e313048b06d</suspectId>
//			<dataType>2</dataType>
//		</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		</response>
		HWVCM hwVCM = hwTaskList.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ALARM_SUBSCRIPTION);
		String wsRequest = null;
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
							+appCfgURL.getAppValue1();
		HWWSField result = null;
		wsRequest = "<request><callbackUrl>"
				+"<master>"+hwTaskList.getCallbackURLMaster()+"</master>";
				if (!StringUtil.checkNull(hwTaskList.getCallbackURLSlave())) {
					wsRequest += "<slave>"+hwTaskList.getCallbackURLSlave()+"</slave>";
				}								
				wsRequest +="</callbackUrl>"
				+"<suspectId>"+hwTaskList.getTaskId()+"</suspectId>"
				+ "<dataType>"+hwTaskList.getTaskType()+"</dataType>"
				+"</request>";
		result = this.subscribeAlarm(hwVCM, wsRequest, appCfgURL, endPoint);
					
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "final result for subscribe alram:"+result.getResult().toString()));
		return result;	
	}
	
	private HWWSField subscribeAlarm(HWVCM hwVCM, String wsRequest, ApplicationCfg appCfgURL, String endPoint) {		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractSubscribeAlarm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField unSubscribeAlarm(HWCheckPointLibrary hwCheckPointLibrary) {
//		<request>
//			<suspectId>5be04360a2ef2e313048b06d</suspectId>
//			<dataType>2</dataType>
//		</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		</response>
		//for stop service
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ALARM_UNSUBSCRIPTION);				
		String wsRequest = null;
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();				
		HWWSField result = null;
		Iterator<HWTaskList> hwTaskLists = hwCheckPointLibrary.getHwTaskListList().iterator();
		HWTaskList hwTaskList = null;		
		while (hwTaskLists.hasNext()) {		
			hwTaskList = hwTaskLists.next();
			if (StringUtil.checkNull(hwTaskList.getTaskId())) {
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" task id task "+hwTaskList.getTaskName()+" is null no need to unsubscribe alarm"));
				result = new HWWSField();
				result.setResult(new ResultStatus(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE,"Task id is null"));
			}else {
				wsRequest = "<request><suspectId>"+hwTaskList.getTaskId()+"</suspectId><dataType>"+hwTaskList.getTaskType()+"</dataType></request>";
				result = this.unSubscribeAlarm(hwVCM, wsRequest, appCfgURL, endPoint);
			}					
		}						
		return result;	
	}
	
	private HWWSField unSubscribeAlarm(HWVCM hwVCM, String wsRequest, ApplicationCfg appCfgURL, String endPoint) {
		//for stop service
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
			//	wsResponse = "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
				result = HWXMLUtil.extractSubscribeAlarm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	
	//use for face image query person
	@Override
	public String getLivePhoto(HWVCM hwVCM, String fileId) {
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_PERSON_LIVE_PHOTO);
		ApplicationCfg appCfgParam = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_PERSON_LIVE_PHOTO_PARAM);
		String wsRequest = null;
		wsRequest = "?fileid="+fileId+"&type="+appCfgParam.getAppValue1();
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+wsRequest;
		Random rnd = new Random();		
		return this.getLivePhoto(endPoint, endPoint, this.transactionId+rnd.nextInt(1000)+appCfgParam.getAppValue2(), hwVCM.getVcmName());
	}
	
	//incase alarm we get url of image then we extract image from those URL
	@Override
	public String getLivePhoto(String imageUrl, String thumImageUrl, String imageName, String vcmName) {
		Logger.info(this,LogUtil.getLogStart(this.transactionId, "getLivePhoto", "in getLivePhoto url "+thumImageUrl+" file name:"+imageName));
		Date startDate = new Date();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();		
		String image = "";
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		int responseCode = 0;
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{
				    new X509TrustManager(){
				      @Override
				      public X509Certificate[] getAcceptedIssuers(){ return null; }
				      @Override
				      public void checkClientTrusted(X509Certificate[] certs, String authType) {}
				      @Override
				      public void checkServerTrusted(X509Certificate[] certs, String authType) {}											
				  }
			};
			try {
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			} catch (Exception e){
				e.printStackTrace();
			}
				
			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){
				    @Override
					public boolean verify(String hostname,
				            javax.net.ssl.SSLSession sslSession) {
				        return hostname.equals("192.168.2.53");
				    }
				});
		
			URL url = new URL(thumImageUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(this.appCfgHttpConnectionTimeout);
			if (this.globalVarService.getJSessionId(vcmName)!=null){
				conn.setRequestProperty("Cookie", this.globalVarService.getJSessionId(vcmName));				
			}
			responseCode = conn.getResponseCode();
			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK) {			
				inputStream = conn.getInputStream();
				byte[] chunk = new byte[4096];
		        int bytesRead;		        	       
		        while ((bytesRead = inputStream.read(chunk)) > 0) {
		            outputStream.write(chunk, 0, bytesRead);
		        }	
			}else {
				Logger.info(this,LogUtil.getLogStop(this.transactionId, "getLivePhoto", "out in getLivePhoto result:HTTP error code : "+ conn.getResponseCode()+":"+conn.getResponseMessage(), startDate));
				return "";
			}
			if (outputStream!=null) {
				image = ImageUtil.getBase64ImageFromByteArray(imageName, outputStream.toByteArray());
			}			
		}catch(Exception ex) {
			Logger.info(this,LogUtil.getLogStop(this.transactionId, "getLivePhoto", "error while invoke service "+ex.toString(), startDate));
			return "";
		}finally {
			try {
				if (outputStream!=null) {
					outputStream.close();
				}				
			}catch (Exception ex) {}	
			try {
				if (inputStream!=null) {
					inputStream.close();
				}				
			}catch (Exception ex) {}			
		}
		Logger.info(this,LogUtil.getLogStop(this.transactionId, "getLivePhoto", "out getLivePhoto http result:"+responseCode, startDate));
		return image;
	}
	
	@Override
	public HWWSField getUploadFileURL(HWVCM hwVCM, String fileName, String imageSize) {
		//name=lz4.mp4&length=64834263
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_GET_UPLOAD_URL);				
		String wsRequest = "name="+fileName+"&length="+imageSize;
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractGetUploadFileURL(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField uploadFileToServer(HWVCM hwVCM, String fileName, String fileSize, String uploadFileId, String uploadFileType, String endPoint, InputStream inputStream) {
		String serviceName = "uploadFileToServer";
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, serviceName, serviceName+" with endPoint:"+endPoint));
		String wsResponse = null;
		File file = null;
		OkHttpClient okHttpClient=null;
		try {						
			file = new File(fileName);
			FileUtils.copyInputStreamToFile(inputStream, file);			
			boolean executeAPISuccess = false;
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {					
				okHttpClient = this.getOKHttpConnection();				
		    	RequestBody fileBody = RequestBody.create(okhttp3.MediaType.parse(uploadFileType), file);
		        RequestBody requestBody =
		            new MultipartBody.Builder().setType(MultipartBody.FORM)
		            	.addFormDataPart("action", "upload")
		            	.addFormDataPart("uploaded-file-id", uploadFileId)
		            	.addFormDataPart("begin", "0")
		            	.addFormDataPart("length", fileSize)
		                .addFormDataPart("imgInput", fileName, fileBody)
		                .build();        
		        Request request = new Request.Builder().url(endPoint).post(requestBody).build();        
		        Response response;      
		        response = okHttpClient.newCall(request).execute();            
		        if (response.isSuccessful()){                
		            wsResponse = response.body().source().readUtf8();
		            Logger.info(this, LogUtil.getLogInfo(this.transactionId, serviceName, serviceName+" with result:"+wsResponse));
		            result = HWXMLUtil.extractUploadFileToServer(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);			        
		        }else{
		        	//throw new Exception("upload file to server with error code " + response);
		        	result = new HWWSField();
					result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, "end point:"+endPoint+" with error code "+response);
		        }
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
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+serviceName+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, serviceName, serviceName+" with result:"+result.getResult().toString()));
		return result;	
	}
	
	@Override
	public HWWSField publishUploadFile(HWVCM hwVCM, String fileName, String imageSize, String uploadFieldId) {
		//name=lz4.mp4&length=64834263
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_PUBLISH_UPLOAD_FILE);				
		ApplicationCfg appCfgSSId = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_PUBLISH_UPLOAD_FILE_PARAM_SSID);
		String wsRequest = "<request>" + 
				"<uploaded-file-id>"+uploadFieldId+"</uploaded-file-id>" + 
				"<casefile>" + 
				"<name>"+fileName+"</name>" + 
				"<modify-timestamp>"+StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)+"</modify-timestamp>" + 
				"<description></description>" + 
				"<direction></direction>" + 
				"<source-system-id>"+appCfgSSId.getAppValue1()+"</source-system-id>" + 
				"</casefile>" + 
				"</request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractPublishUploadFile(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	
	
//	public ResultStatus modifyImage() {
//		return null;
//	}
	
	@Override
	public HWWSField queryFaceFromFaceList(HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo) {
		return this.queryFaceFromFaceList(hwCheckPointLibrary, personInfo.getCertificateNo(), personInfo.getPersonCertificate().getThirdPartyCode());
	}
	@Override
	public HWWSField queryFaceFromFaceList(HWCheckPointLibrary hwCheckPointLibrary, String certificateNo, String certificateType) {
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_FACE_FROM_LIST);						
//		String wsRequest = "<request><repositorIds>"+hwCheckPointLibrary.getListNameId()+"</repositorIds><ids>"+personInfo.getHwPeopleId()+"</ids>"
//				+ "<page><no>1</no><orderName>time</orderName><size>10</size><sort>asc</sort></page></request>";		
		String wsRequest = "<request><repositorIds>"+hwCheckPointLibrary.getLibraryId()+"</repositorIds>"
				+ "<credentialNumber>"+certificateNo+"</credentialNumber>"
				+ "<credentialType>"+certificateType+"</credentialType>"
				+ "<page><no>1</no><orderName>time</orderName><size>10</size><sort>asc</sort></page></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;	
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryFaceFromList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField queryPerson(HWVCM hwVCM, String blackListId, String nameListId) {
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_PERSON);						
		String wsRequest = "<request><repositorIds>"+nameListId+"</repositorIds>" +
				"<ids>"+blackListId+"</ids><page><no>1</no><orderName>time</orderName>" + 
				"<size>1</size><sort>asc</sort></page></request>";		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
//			wsResponse = "<response>" + 
//					"<result>" + 
//					"<errmsg>Success</errmsg>" + 
//					"<code>0</code>" + 
//					"</result>" + 
//					"<number>1</number>" + 
//					"<peopleList>" + 
//					"<people>" + 
//					"<name></name>" + 
//					"<peopleId>a001</peopleId>" +//"<peopleId>a001</peopleId>" + 
//					"<faceList>" + 
//					"<face>" + 
//					"<fileId></fileId>" + 
//					"</face>" + 
//					"</faceList>" + 
//					"</people>" + 
//					"</peopleList>" + 
//					"</response>";
				result = HWXMLUtil.extractQueryPerson(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public ResultStatus getGroupId() {
		return null;
	}
	
	
	@Override
	public HWWSField addIntelligentAnalysisTasks(HWIPC hwIPC) {
		///////////////////////
//		<request>
//			<tasks>
//				<task>
//					<task_name>eddy</task_name>
//					<camera_id>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</camera_id>
//					<type>2</type>
//					<analyzeMode>0</analyzeMode>
//					<end_time>-1</end_time>
//					<start_time>-1</start_time>
//				</task>
//			</tasks>
//		</request>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <tasks>
//		        <task>
//		            <result>
//		                <errmsg>Success</errmsg>
//		                <code>0</code>
//		            </result>
//		            <taskId>154140799214839859</taskId>
//		            <taskName>eddy</taskName>
//		        </task>
//		    </tasks>
//		</response>
//		noted: system create 2 task with same name which eddy
		HWVCM hwVCM = hwIPC.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ADD_INTELLIGENT_ANALYSIS_TASKS_BATCHS);						
		String wsRequest = "<request><tasks>";		
		if (StringUtil.checkNull(hwIPC.getIpcTaskId())) {
			wsRequest += "<task><task_name>"+hwIPC.getTaskPrefix()+"_"+hwIPC.getIpcName()+"</task_name><camera_id>"+hwIPC.getIpcCode()+"</camera_id><type>"+hwIPC.getTaskType()+"</type><analyzeMode>"+hwIPC.getAnalyzeMode()+"</analyzeMode><start_time>-1</start_time><end_time>-1</end_time></task>";
		}		
        wsRequest += "</tasks></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddIntelligentAnalysisTasksBatches(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField queryIntelligentAnalysisTasks(HWIPC hwIPC) {
//		<request>
//			<cameraId>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraId>
//			<page no="1" pageSize="10" pageSort="desc" sortName="createDate"/>
//		</request>	
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <page>
//		        <currentPage>1</currentPage>
//		        <rows>10</rows>
//		        <total>1</total>
//		    </page>
//		    <task_list>
//		        <task>
//		            <algorithms>
//		                <algorithm>0104000100</algorithm>
//		            </algorithms>
//		            <cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>
//		            <createDate>1542603046165</createDate>
//		            <error_code>0</error_code>
//		            <id>14</id>
//		            <taskId>154260304609718359</taskId>
//		            <taskName>X1221-F-Facial recognition-yitu_face_sdk</taskName>
//		            <taskProgress>0</taskProgress>
//		            <taskStatus>1</taskStatus>
//		            <taskType>2</taskType>
//		            <videoType>0</videoType>
//		        </task>
//		    </task_list>
//		</response>
		HWVCM hwVCM = hwIPC.getHwCheckPointLibrary().getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_INTELLIGENT_ANALYSIS_TASKS_BATCHS);				
		String wsRequest = "<request><cameraId>"+hwIPC.getIpcCode()+"</cameraId><page no=\"1\" pageSize=\"10\" pageSort=\"desc\" sortName=\"createDate\"/></request>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryIntelligentAnalysisTasksBatches(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField queryFaceAlgorithm(HWVCM hwVCM) {
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_FACE_ALGORITHM);				
		ApplicationCfg appCfgParam = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_FACE_ALGORITHM_PARAM_TYPE);
		String wsRequest = "type="+appCfgParam.getAppValue1();		
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryAlgorithm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	
	
//	public ResultStatus modifyCameraFeature() {
//		return null;
////		<request>
////			<name>192_168_2_203</name>
////		    <sn>02985120000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</sn>
////		    <camera-feature>1</camera-feature>
////		    <camera-use>2</camera-use>
////		    <stream-url>192.168.2.203</stream-url>
////		    <camera-state>0</camera-state>
////		    <camera-type>3</camera-type>
////		    <plat-name>vcn</plat-name>
////		</request>
////		<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
////		<response>
////		    <result>
////		        <errmsg>Success.</errmsg>
////		        <code>0</code>
////		    </result>
////		</response>
//	}
	


//	public String invokeRestfulService(String tranId, String endPoint, String inputRequest, String module, String requestMethod, boolean refreshJsessionId) throws ConsumeWSException{		
//		Logger.info(this,LogUtil.getLogStart(tranId, module, "in invokeRestfulService: endpoint:"+endPoint+" input:"+inputRequest));
//		Date startDate = new Date();
//		String strOneline = null;
//		StringBuilder result = new StringBuilder();
//		HttpURLConnection conn = null;
//		OutputStream os = null;
//		BufferedReader br = null;		
//		try {				
//			TrustManager[] trustAllCerts = new TrustManager[]{
//					    new X509TrustManager(){
//					      @Override
//					      public X509Certificate[] getAcceptedIssuers(){ return null; }
//					      @Override
//					      public void checkClientTrusted(X509Certificate[] certs, String authType) {}
//					      @Override
//					      public void checkServerTrusted(X509Certificate[] certs, String authType) {}											
//					  }
//			};
//			try {
//				SSLContext sslContext = SSLContext.getInstance("SSL");
//				sslContext.init(null, trustAllCerts, new SecureRandom());
//				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//			} catch (Exception e){
//				e.printStackTrace();
//			}
//					
//			javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
//					new javax.net.ssl.HostnameVerifier(){
//					    public boolean verify(String hostname,
//					            javax.net.ssl.SSLSession sslSession) {
//					        return hostname.equals("192.168.2.53");
//					    }
//					});
//			
//			URL url = new URL(endPoint);
//			conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);			
//			if (requestMethod.equals("PATCH")) {
//				conn.setRequestMethod("POST");
//		        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
//			}else{
//				conn.setRequestMethod(requestMethod);
//			}			
//			conn.setRequestProperty("Accept-Charset", "UTF-8");
//			if (!StringUtil.checkNull(inputRequest)){	
//				conn.setDoOutput(true);
//				conn.setRequestProperty("Content-Type", MediaType.APPLICATION_XML.toString());
//				conn.setRequestProperty("Content-Length", "" + inputRequest.getBytes().length);
//			}
//			if (this.globalVarService.getJSessionId()!=null){
//				conn.setRequestProperty("Cookie", this.globalVarService.getJSessionId());				
//			}
//			//String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
//			if (!StringUtil.checkNull(inputRequest)){
//				os = conn.getOutputStream();	
//				os.write(inputRequest.getBytes());
//				os.flush();
//			}				
//			//200 is ok
//			if (conn.getResponseCode() != 200) {
//				Logger.info(this,LogUtil.getLogStop(tranId, module, "out invokeRestfulService result:HTTP error code : "+ conn.getResponseCode()+":"+conn.getResponseMessage(), startDate));
//				throw new ConsumeWSException("HTTP error code : "+ conn.getResponseCode()+":"+conn.getResponseMessage());
//			}
//			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//			strOneline = br.readLine();
//			while (strOneline != null) {
//				result.append(strOneline);				
//				strOneline = br.readLine();				
//			}			
//			if (this.globalVarService.getJSessionId()==null  || refreshJsessionId) {
//				this.globalVarService.setJSessionId(conn.getHeaderField("Set-Cookie"));
//			}			
//		}catch (ConsumeWSException e) {
//			throw e;
//		}catch (Exception e) {
//			Logger.info(this,LogUtil.getLogStop(tranId, module, "error while invoke service "+e.toString(), startDate));					
//			throw new ConsumeWSException(e.toString());
//		}finally{
//			try{
//				if (os!=null){
//					os.close();
//				}
//			}catch (Exception ex){}
//			try{
//				if (br!=null){
//					br.close();
//				}
//			}catch (Exception ex){}
//			try{
//				if (conn!=null){
//					conn.disconnect();
//				}
//			}catch (Exception ex){}			
//		}
//		Logger.info(this,LogUtil.getLogStop(tranId, module, "out invokeRestfulService result:"+result.toString()+" with sessionId:"+this.globalVarService.getJSessionId(), startDate));
//						
//		return result.toString();
//	}				
	private OkHttpClient getOKHttpConnection(){
    	OkHttpClient.Builder builder = new OkHttpClient.Builder();
    	try {
    		// Create a trust manager that does not validate certificate chains
    	    final TrustManager[] trustAllCerts = new TrustManager[] {
    	        new X509TrustManager() {
    	          @Override
    	          public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
    	          }

    	          @Override
    	          public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
    	          }

    	          @Override
    	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    	            return new java.security.cert.X509Certificate[]{};
    	          }
    	        }
    	    };

    	    // Install the all-trusting trust manager
    	    final SSLContext sslContext = SSLContext.getInstance("SSL");
    	    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
    	    // Create an ssl socket factory with our all-trusting manager
    	    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

    	    builder = new OkHttpClient.Builder();
    	    
    	    builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
    	    builder.hostnameVerifier(new HostnameVerifier() {
    	      @Override
    	      public boolean verify(String hostname, SSLSession session) {
    	        return true;
    	      }
    	    });
    	}catch (Exception ex){
    		
    	}
    	return builder.build();
    }	
	
	private HWWSField logOn(HWVCM hwVCM) {
		return super.logOn(this.transactionId, hwVCM, this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_LOGIN)
				, this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_WS_CODE_FIRST_LOGIN).getAppValue1());			
	}
	private HWWSField logOnSDK(HWVCM hwVCM) {
		return super.logOnSDK(this.transactionId, hwVCM, this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_LOGIN_SDK)
				, this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_WS_CODE_FIRST_LOGIN).getAppValue2());			
	}
	
	@Override
	public HWWSField addFaceToFaceList(String transactionId, PersonRegisterResult personRegister, HWCheckPointLibrary hwCheckPointLibrary) {		
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
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(transactionId, ApplicationCfg.KEY_API_ADD_FACE_TO_LIST);						
		String wsRequest = "<request><peopleList><people>" +
				"<credentialNumber>" + personRegister.getCertificateNo() + "</credentialNumber>" +
				"<credentialType>"+ personRegister.getCertificateThirdPartyCode() + "</credentialType>" +
				"<name>" + personRegister.getFullName() +"</name>" + 
				"<pictures>" + 
				"<picture>" +				
				"<base64>"+personRegister.getBase64Image()+"</base64>" + 
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
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceToList(transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;	
	}
	
	@Override
	public HWWSField modifyFaceToFaceList(String transactionId, HWCheckPointLibrary hwCheckPointLibrary, PersonInfo personInfo, String base64Image, String hwPeopleId) {
		HWVCM hwVCM = hwCheckPointLibrary.getHwVCM();
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(transactionId, ApplicationCfg.KEY_API_MODIFY_FACE_TO_LIST);						
		String wsRequest = "<request><people>" +
				"<credentialNumber>" + personInfo.getCertificateNo() + "</credentialNumber>" +
				"<credentialType>"+ personInfo.getPersonCertificate().getThirdPartyCode() + "</credentialType>" +
				"<name>" + personInfo.getFullName() +"</name>" +
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
	public HWWSField removeFaceFromFaceList(String transactionId, HWCheckPointLibrary hwCheckPointLibrary, String hwPeopleId, String certificateNo) {
		//https://192.168.2.53:8009/sdk_service/rest/facerepositories/5be00725a2ef2e313048b052/peoples?credentialnumbers=certificateNo
		//https://192.168.2.53:8009/sdk_service/rest/facerepositories/5be00725a2ef2e313048b052/peoples?ids=hwPeopleId
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
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(transactionId, ApplicationCfg.KEY_API_REMOVE_FACE_FROM_LIST);
		ApplicationCfg appStatus = this.xFaceBatchService.findACByAppKey(transactionId, ApplicationCfg.KEY_API_REMOVE_FACE_STATUS_NOT_EXIST);
		//delete by hwPeopleId first, if hwPeopleId is null then delete by credentialnumbers
		String wsRequest = StringUtil.checkNull(hwPeopleId) ? "credentialnumbers="+certificateNo : "ids="+hwPeopleId;
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;	
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, hwCheckPointLibrary.getLibraryId()==null? "": hwCheckPointLibrary.getLibraryId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveFaceFromList(transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon, appStatus.getAppValue1());
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
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;	
	}
	@Override
	public HWWSField registerCallBackSDK(HWVCM hwVCM) {
		return this.registerCallBackSDK(hwVCM, true);
	}
	@Override
	public HWWSField subscribeAlarmSDK(HWVCM hwVCM) {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_SUBSCRIBE_ALARM_SDK);
		HWWSField result = null;
		if (this.globalVarService.getJSessionId(hwVCM.getVcnName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE, hwVCM.getVcnName()));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" no jsession"));
			return result;
		}
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcnSDKIp()+":"+hwVCM.getVcnSDKLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;
		String wsRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esdk=\"esdk_ivs_professional_server\"><soapenv:Header/><soapenv:Body><esdk:subscribeAlarm><requestXML><![CDATA[<Content><DomainCode>"+hwVCM.getVcnSDKDomainCode()+"</DomainCode><Subscribe><SubscriberInfo><Subscriber>1</Subscriber><SubscriberID>"+hwVCM.getVcnSDKLoginUserId()+"</SubscriberID></SubscriberInfo></Subscribe></Content>]]></requestXML></esdk:subscribeAlarm></soapenv:Body></soapenv:Envelope>";
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeSoapService(this.transactionId, endPoint, wsRequest, appCfg.getAppValue2(), false, hwVCM.getVcnName(), appCfg.getAppValue3());
				result = HWXMLUtil.extractSubscribebAlarmSDK(this.transactionId, wsResponse, this.appCfgHWSDKSuccessCode, this.appCfgHWSDKPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOnSDK(hwVCM);				
				}else {
					executeAPISuccess = true;
					break;
				}
			}
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}					
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	private HWWSField registerCallBackSDK(HWVCM hwVCM, boolean isRegister) {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_REGISTER_CALLBACK_SDK);
		HWWSField result = null;
		if (this.globalVarService.getJSessionId(hwVCM.getVcnName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE, hwVCM.getVcnName()));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" no jsession"));
			return result;
		}
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcnSDKIp()+":"+hwVCM.getVcnSDKLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;
		//if url is blank mean unregister
		String wsRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esdk=\"esdk_ivs_professional_server\"><soapenv:Header/><soapenv:Body><esdk:registerNotification><wsUri>"+(isRegister ?hwVCM.getVcnSDKCallBackURL(): "")+"</wsUri></esdk:registerNotification></soapenv:Body></soapenv:Envelope>";
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeSoapService(this.transactionId, endPoint, wsRequest, appCfg.getAppValue2(), false, hwVCM.getVcnName(), appCfg.getAppValue3());
//				wsResponse =  "<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ns2:registerNotificationResponse xmlns:ns2="esdk_ivs_professional_server"><resultCode>0</resultCode></ns2:registerNotificationResponse></soap:Body></soap:Envelope>";
				result = HWXMLUtil.extractRegisterCallBackSDK(this.transactionId, wsResponse, this.appCfgHWSDKSuccessCode, this.appCfgHWSDKPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOnSDK(hwVCM);				
				}else {
					executeAPISuccess = true;
					break;
				}
			}			
			if (!executeAPISuccess) {
				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
			}	
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	@Override
	public HWWSField logOutSDK(HWVCM hwVCM) {
		ApplicationCfg appCfg = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_LOGOUT_SDK);
		HWWSField result = null;
		//if not j session id for this vcm then return immediately
		if (this.globalVarService.getJSessionId(hwVCM.getVcnName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE, hwVCM.getVcnName()));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" no jsession"));
			return result;
		}
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcnSDKIp()+":"+hwVCM.getVcnSDKLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;
		String wsRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esdk=\"esdk_ivs_professional_server\">" + 
				"<soapenv:Header/>" + 
				"<soapenv:Body>" + 
				"<esdk:logout/>" + 
				"</soapenv:Body>" + 
				"</soapenv:Envelope>";
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = super.invokeSoapService(this.transactionId, endPoint, wsRequest, appCfg.getAppValue2(), false, hwVCM.getVcnName(), appCfg.getAppValue3());
//			wsResponse =  "<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//			   <soap:Body>
//			      <ns2:logoutResponse xmlns:ns2="esdk_ivs_professional_server">
//			         <resultCode>0</resultCode>
//			      </ns2:logoutResponse>
//			   </soap:Body>
//			</soap:Envelope>";
			result = HWXMLUtil.extractLogOutSDK(this.transactionId, wsResponse, this.appCfgHWSDKSuccessCode);						
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfg.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	@Override
	public HWWSField unSubscribeAlarmSDK(HWVCM hwVCM) {
		return this.registerCallBackSDK(hwVCM, false);		
	}
	
	//remove person from ZK accept result 0:delete success or -22:The person does not exist
	@Override
	public HWWSField removePersonZK(String transactionId, String certificateNo) {		
		ApplicationCfg appServer = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_SERVER);
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_REMOVE_PERSON);
		//status value1=success, value2=person not found
		ApplicationCfg appZKStatus = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_STATUS);
	//	http://serverIP:serverPort/api/person/delete/{pin}?access_token={apitoken}
		String endPoint = appServer.getAppValue1()+appCfgURL.getAppValue1() + "/"+certificateNo+"?access_token="+appServer.getAppValue2();
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = super.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, null);											
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
	public HWWSField addPersonZK(String transactionId, PersonRegisterResult personRegister, int noOfDayRemovePerson) {
		ApplicationCfg appServer = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_SERVER);
		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_ADD_PERSON);
		//status value1=success, value2=person not found
		ApplicationCfg appZKStatus = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_STATUS);
		//status value1=dept code, value2 = access level		
		ApplicationCfg appParam = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ZK_API_ADD_PERSON_PARAM);
	//	http://serverIP:serverPort/api/person/add?access_token={apitoken}
		String endPoint = appServer.getAppValue1()+appCfgURL.getAppValue1() + "?access_token="+appServer.getAppValue2();		
		String endTime = StringUtil.dateToString(DateTimeUtil.addDate(personRegister.getRegisterDate().getTime(), noOfDayRemovePerson), StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);		
		String wsRequest = "{" + 
				"\"pin\": \""+personRegister.getCertificateNo()+"\"," + 
				"\"deptCode\": \""+appParam.getAppValue1()+"\"," + 
				"\"name\": \""+personRegister.getFullName()+"\"," + 
				"\"lastName\": \"\"," + 
				"\"gender\": \"F\"," + 
				"\"cardNo\": \"\"," + 
				"\"accLevelIds\": \""+appParam.getAppValue2()+"\"," + 
				"\"accStartTime\": \""+StringUtil.dateToString(personRegister.getRegisterDate(), StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)+"\"," + 
				"\"accEndTime\": \""+endTime+"\"" + 
				"}";
		String wsResponse = null;
		HWWSField result = null;
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
	
//	backup session
//	public HWWSField addIntelligentAnalysisTasksBatches() {
//		///////////////////////
////		<request>
////			<tasks>
////				<task>
////					<task_name>eddy</task_name>
////					<camera_id>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</camera_id>
////					<type>2</type>
////					<analyzeMode>0</analyzeMode>
////					<end_time>-1</end_time>
////					<start_time>-1</start_time>
////				</task>
////				<task>
////					<task_name>eddy</task_name>
////					<camera_id>01174380000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</camera_id>
////					<type>2</type>
////					<analyzeMode>0</analyzeMode>
////					<end_time>-1</end_time>
////					<start_time>-1</start_time>
////				</task>
////			</tasks>
////		</request>
////		<response>
////		    <result>
////		        <errmsg>Success.</errmsg>
////		        <code>0</code>
////		    </result>
////		    <tasks>
////		        <task>
////		            <result>
////		                <errmsg>Success</errmsg>
////		                <code>0</code>
////		            </result>
////		            <taskId>154140799214839859</taskId>
////		            <taskName>eddy</taskName>
////		        </task>
////		        <task>
////		            <result>
////		                <errmsg>Success</errmsg>
////		                <code>0</code>
////		            </result>
////		            <taskId>154140799214832790</taskId>
////		            <taskName>eddy</taskName>
////		        </task>
////		    </tasks>
////		</response>
////		noted: system create 2 task with same name which eddy
//		ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_ADD_INTELLIGENT_ANALYSIS_TASKS_BATCHS);						
//		String wsRequest = "<request><tasks>";
//		//add all camera
////		Iterator<HWCheckPointLibrary> hwCheckPointLibraryList = hwVCM.getHwCheckPointLibrarys().iterator();
////		Iterator<HWTaskList> hwTaskLists = null;
////		HWCheckPointLibrary hwCheckPointLibrary = null;
////		HWTaskList hwTaskList = null;		
////		Iterator<HWIPC> hwIPCList = null;
////		HWIPC hwIPC = null;
////		while(hwCheckPointLibraryList.hasNext()){
////        	hwCheckPointLibrary = hwCheckPointLibraryList.next();
////        	hwTaskLists = hwCheckPointLibrary.getHwTaskLists().iterator();
////        	while(hwTaskLists.hasNext()) {
////        		hwTaskList = hwTaskLists.next();        		
////        		hwIPCList = hwCheckPointLibrary.getHwIPCs().iterator();
////        		while (hwIPCList.hasNext()) {
////        			hwIPC = hwIPCList.next();
////        			wsRequest += "<task><task_name>"+hwTaskList.getTaskName()+"</task_name><camera_id>"+hwIPC.getIpcCode()+"</camera_id><type>2</type><analyzeMode>0</analyzeMode></task>";
////        		}        		
////        	}
////        }
//		Iterator<HWIPC> hwIPCList = null;		
//		HWIPC hwIPC = null;
//		hwIPCList = hwVCM.getHwIPCs().iterator();
//		while(hwIPCList.hasNext()) {
//			hwIPC = hwIPCList.next();
//			if (StringUtil.checkNull(hwIPC.getIpcTaskId())) {
//				wsRequest += "<task><task_name>"+hwIPC.getTaskPrefix()+"_"+hwIPC.getIpcName()+"</task_name><camera_id>"+hwIPC.getIpcCode()+"</camera_id><type>"+hwIPC.getTaskType()+"</type><analyzeMode>"+hwIPC.getAnalyzeMode()+"</analyzeMode></task>";
//			}
//		}
//		
//      wsRequest += "</tasks></request>";
//		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
//			+appCfgURL.getAppValue1();		
//		String wsResponse = null;
//		HWWSField result = null;
//		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//		boolean executeAPISuccess = false;
//		try {
//			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
//				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
//				result = HWXMLUtil.extractAddIntelligentAnalysisTasksBatches(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
//				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
//					//logon
//					this.logOn();				
//				}else {
//					executeAPISuccess = true;
//					break;
//				}				
//			}	
//			if (!executeAPISuccess) {
//				result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
//			}
//		}catch (ConsumeWSException ex) {
//			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//			result = new HWWSField();
//			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
//		}catch (Exception ex) {
//			Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//			result = new HWWSField();
//			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
//		}
//		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
//		return result;	
//	}
//	public HWWSField queryPersonDBPhoto(String personFileId, String imageType, String imageSize) {
//	ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_PERSON_DB_PHOTO);						
//	String wsRequest = "fileId="+personFileId+"&imageType="+imageType+"&imageSize="+imageSize;		
//	String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
//		+appCfgURL.getAppValue1()+"?"+wsRequest;
//	String wsResponse = null;
//	HWWSField result = null;
//	Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//	try {
//		wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3());
//		result = HWXMLUtil.extractQueryPersonDBPhoto(this.transactionId, wsResponse);			
//	}catch (ConsumeWSException ex) {
//		Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//		result = new HWWSField();
//		result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
//	}catch (Exception ex) {
//		Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//		result = new HWWSField();
//		result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
//	}
//	Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
//	return result;
//}		
//	public HWWSField queryCameraFromCheckPoint(HWIPC hwIPC, int pageNo, int pageSize) {
////	<request>
////		<sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
////		<page>
////			<no>1</no>
////			<pageSize>1000</pageSize>
////		</page>
////	</request>
////	<response>
////	    <cameraList>
////	        <camera>
////	            <name>192_168_2_202</name>
////	            <sn>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</sn>
////	        </camera>
////	    </cameraList>
////	    <result>
////	        <errmsg>Success</errmsg>
////	        <code>0</code>
////	    </result>
////	    <total>1</total>
////	</response>
//	ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_CAMERA_FROM_CHECK_POINT);				
//	String wsRequest = "<request><sn>"+hwIPC.getHwCheckPointLibrary().getCheckPointId()+"</sn><page><no>"+pageNo+"</no><pageSize>"+pageSize+"</pageSize></request>";
//	String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
//		+appCfgURL.getAppValue1();		
//	String wsResponse = null;
//	HWWSField result = null;
//	Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//	boolean executeAPISuccess = false;
//	try {			
//		for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
//			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
//			result = HWXMLUtil.extractQueryCameraFromCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
//			if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
//				//logon
//				this.logOn();				
//			}else {
//				executeAPISuccess = true;
//				break;
//			}
//		}
//		if (!executeAPISuccess) {
//			result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
//		}
//	}catch (ConsumeWSException ex) {
//		Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//		result = new HWWSField();
//		result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
//	}catch (Exception ex) {
//		Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//		result = new HWWSField();
//		result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
//	}
//	Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
//	return result;	
//}
//	public HWWSField queryPersonByPhotoV0(ArrayList<String> hwIPCList, String base64Photo, String algorithmCode, int pageNo, Date startDate, Date endDate, int confidenceThreshold) {
////<request>
////	<cameraSns>
////		<cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>
////	</cameraSns>
////	<pictures>
////		<picture>
////			<fileId>5c00dec0a2ef2e5abf20512d</fileId>
////			<base64>xxxxxx</base64>
////		</picture>
////	</pictures>
////	<algorithmCodes><algorithmCode>0104000100</algorithmCode></algorithmCodes>
////	<confidenceThreshold>60</confidenceThreshold>
////	<endTime>1543559381000</endTime>
////	<startTime>1542695381000</startTime>
////	<page>
////		<no>1</no>
////		<orderName>similarity</orderName>
////		<size>2</size>
////		<sort>desc</sort>
////	</page>
////</request>
//HWWSField result = null;
//if (hwIPCList==null || hwIPCList.size()==0) {
//	result = new HWWSField();
//	result.setResult(new ResultStatus(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Please provide camera code for search"));
//	return result;
//}
//ApplicationCfg appCfgURL = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_API_QUERY_PERSON_BY_PHOTO);				
//String wsRequest = "<request><cameraSns>";
//for (String ipcCode:hwIPCList) {
//	wsRequest += "<cameraSn>"+ipcCode+"</cameraSn>";
//}								
//wsRequest += "</cameraSns><pictures><picture><base64>"+base64Photo+"</base64></picture></pictures>"
//		+ "<confidenceThreshold>"+confidenceThreshold+"</confidenceThreshold>"
//		+ "<algorithmCodes><algorithmCode>"+algorithmCode+"</algorithmCode></algorithmCodes>"
//		+ "<endTime>"+endDate.getTime()+"</endTime>"
//		+ "<startTime>"+startDate.getTime()+"</startTime>"
//		+ "<page><no>"+pageNo+"</no><orderName>similarity</orderName><size>10</size><sort>desc</sort></page></request>";		
//String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
//	+appCfgURL.getAppValue1();		
//String wsResponse = null;		
//Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//boolean executeAPISuccess = false;
//try {
//	for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
//		wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, hwVCM.getVcmName());
//		result = HWXMLUtil.extractQueryPersonByPhoto(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
//		if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
//			//logon
//			this.logOn();				
//		}else {
//			executeAPISuccess = true;
//			break;
//		}
//	}
//	if (!executeAPISuccess) {
//		result.getResult().setStatusCode(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE, "maximum is "+this.appCfgHWNoOfRetryAPI);
//	}
//}catch (ConsumeWSException ex) {
//	Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//	result = new HWWSField();
//	result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
//}catch (Exception ex) {
//	Logger.error(this,LogUtil.getLogError(this.transactionId, "error while invoke "+appCfgURL.getAppValue2()+" service "+ex.toString(), ex));
//	result = new HWWSField();
//	result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
//}
//Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with result:"+result.getResult().toString()));
//return result;
//}

}
