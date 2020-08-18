package com.xpand.xface.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.HttpDeleteWithBody;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWIPCAnalyzeList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.HWVCN;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.exception.ConsumeWSException;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.HWAPIService;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HWAPIServiceImpl implements HWAPIService{		
	private GlobalVarService globalVarService;	
	private HashMap<String, ApplicationCfg> appCfgList;
	private String transactionId;
	private HWVCM hwVCM;
	private String appCfgHWSuccessCode;
	private String appCfgHWPlsLogon;
	private int appCfgHWNoOfRetryAPI;
	private int appCfgHttpConnectionTimeout;
	private boolean isInitiateClass = false;
	public HWAPIServiceImpl() {}
	public void initialClass(String transactionId, HashMap<String, ApplicationCfg> appCfgList, HWVCM hwVCM, GlobalVarService globalVarService) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "initial class"));
		this.transactionId = transactionId;
		this.appCfgList = appCfgList;
		this.hwVCM = hwVCM;		
		this.globalVarService = globalVarService;
		
		//hw error code
		ApplicationCfg appTmp = this.appCfgList.get(ApplicationCfg.KEY_WS_CODE_SUCCESS);
		this.appCfgHWSuccessCode = appTmp.getAppValue1();		
		appTmp = this.appCfgList.get(ApplicationCfg.KEY_WS_CODE_PLS_LOGIN_FIRST);
		this.appCfgHWPlsLogon = appTmp.getAppValue1();
		appTmp = this.appCfgList.get(ApplicationCfg.KEY_API_NO_OF_RETRY);
		this.appCfgHWNoOfRetryAPI = StringUtil.stringToInteger(appTmp.getAppValue1(),3);
		appTmp = this.appCfgList.get(ApplicationCfg.KEY_HTTP_CONNECTION_TIMEOUT);
		this.appCfgHttpConnectionTimeout = StringUtil.stringToInteger(appTmp.getAppValue1(),3) * 1000; 
		///////////////////
		
		this.isInitiateClass = true;
		Logger.info(this, LogUtil.getLogInfo(transactionId, "initial class done then ready to use"));
	}	
	public HWWSField logOn() {
		ApplicationCfg appCfg = this.appCfgList.get(ApplicationCfg.KEY_API_LOGIN);
		ApplicationCfg appCfgFirstLogin = this.appCfgList.get(ApplicationCfg.KEY_WS_CODE_FIRST_LOGIN);
		String wsRequest = "?account="+this.hwVCM.getLoginUserName()+"&pwd="+this.hwVCM.getLoginPassword();
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
						+appCfg.getAppValue1()+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfg.getAppValue2(), appCfg.getAppValue3(), true, this.hwVCM.getVcmName());
//			wsResponse =  "<response>" + 
//					"<result>" + 
//					"<errmsg>user is first login</errmsg>" + 
//					"<code>30873259114402</code>" + 
//					"</result>" + 
//					"</response>";
			result = HWXMLUtil.extractLogon(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon, appCfgFirstLogin.getAppValue1());						
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
	public HWWSField keepAlive() {
		HWWSField result = null;
		ApplicationCfg appCfg = this.appCfgList.get(ApplicationCfg.KEY_API_KEEP_ALIVE);
		//if not j session id for this vcm then return immediately
		if (this.globalVarService.getJSessionId(this.hwVCM.getVcmName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE, this.hwVCM.getVcmName()));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" no jsession"));
			return result;
		}		
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;
		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfg.getAppValue2(), appCfg.getAppValue3(), false, this.hwVCM.getVcmName());
//			wsResponse =  "<response><result><code>0</code><errmsg>Success.</errmsg></result></response>";
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
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;		
	}
	public HWWSField logOut() {
		ApplicationCfg appCfg = this.appCfgList.get(ApplicationCfg.KEY_API_LOGOUT);
		HWWSField result = null;
		//if not j session id for this vcm then return immediately
		if (this.globalVarService.getJSessionId(this.hwVCM.getVcmName())==null) {
			result = new HWWSField();
			result.setResult(new ResultStatus(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE, this.hwVCM.getVcmName()));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" no jsession"));
			return result;
		}
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
						+appCfg.getAppValue1();
		String wsResponse = null;		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfg.getAppValue2(), appCfg.getAppValue3(), false, this.hwVCM.getVcmName());
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
	public HWWSField changePwd(String newPwd) {
		ApplicationCfg appCfg = this.appCfgList.get(ApplicationCfg.KEY_API_CHANGE_PWD);		
		String wsRequest = "?OLD_PASSWORD="+this.hwVCM.getLoginPassword()+"&NEW_PASSWORD="+newPwd;
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfg.getAppValue1()+wsRequest;
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfg.getAppValue2(), appCfg.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfg.getAppValue2(), appCfg.getAppValue3(), false, this.hwVCM.getVcmName());
//			wsResponse = "<response>" + 
//					"<result>" + 
//					"<errmsg>Success.</errmsg>" + 
//					"<code>0</code>" + 
//					"</result>" + 
//					"</response>";
			result = HWXMLUtil.extractChangePwd(this.transactionId, wsResponse, this.appCfgHWSuccessCode);
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
	public HWWSField addFaceList(HWIPCAnalyzeList hwIPCAnalyzeList) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ADD_FACE_LIST);						
		String wsRequest = "<request><repository><type>"+hwIPCAnalyzeList.getLibraryType()+"</type><name>"+hwIPCAnalyzeList.getListName()+"</name></repository></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField removeFaceList(HWIPCAnalyzeList hwIPCAnalyzeList) {
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_REMOVE_FACE_LIST);						
		String wsRequest = hwIPCAnalyzeList.getListNameId();
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, wsRequest);
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveFaceList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField queryFaceList(HWIPCAnalyzeList hwIPCAnalyzeList) {
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_FACE_LIST);						
		String wsRequest = "size=1&no=1&sort=asc&ordername=time&" + (hwIPCAnalyzeList.getListName()==null ? "id="+hwIPCAnalyzeList.getListNameId() : "name="+hwIPCAnalyzeList.getListName());
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1() + "?" + wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryFaceList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField addCheckPoint(HWIPCAnalyzeList hwIPCAnalyzeList) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ADD_CAMERA_CHECK_POINT);				
		String wsRequest = "<request><name>"+hwIPCAnalyzeList.getCheckPointName()+"</name><type>"+hwIPCAnalyzeList.getCheckPointType()+"</type></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddCameraCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField removeCheckPoint(HWIPCAnalyzeList hwIPCAnalyzeList) {		
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_REMOVE_CAMERA_CHECK_POINT);				
		String wsRequest = "<request><bayonetList><sn>"+hwIPCAnalyzeList.getCheckPointSN()+"</sn></bayonetList></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveCameraCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField queryCheckPoint(HWIPCAnalyzeList hwIPCAnalyzeList) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_CAMERA_CHECK_POINT);				
		String wsRequest = "<request><sn>"+hwIPCAnalyzeList.getCheckPointSN()+"</sn><page><no>1</no><pageSize>1</pageSize></page></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryCameraCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField addSuspectTask(HWIPCAnalyzeList hwIPCAnalyzeList) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ADD_SUSPECT_TASK);				
		String wsRequest = "<request><type>"+hwIPCAnalyzeList.getSuspectType()+"</type><name>"+hwIPCAnalyzeList.getSuspectName()+"</name>"
						+"<startDate>"+hwIPCAnalyzeList.getSuspectStartDate()+"</startDate><endDate>"+hwIPCAnalyzeList.getSuspectEndDate()+"</endDate>"
						+"<timeList>"
						+"<time><day>0</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>1</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>2</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>3</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>4</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>5</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"<time><day>6</day><endTime>23:59:59</endTime><startTime>00:00:00</startTime></time>"
						+"</timeList>"
						+"<groupList><groupId>"+hwIPCAnalyzeList.getListNameId()+"</groupId></groupList>"
						+"<domains><domain>"
						+"<bayonetList><bayonetSn>"+hwIPCAnalyzeList.getCheckPointSN()+"</bayonetSn></bayonetList>"
						+"</domain></domains>"
						+"<request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddSuspectTask(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField removeSuspectTask(HWIPCAnalyzeList hwIPCAnalyzeList) {		
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_REMOVE_SUSPECT_TASK);				
		String wsRequest = "<request><suspectList><suspectId>"+hwIPCAnalyzeList.getSuspectId()+"</suspectId></suspectList></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveSuspectTask(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField querySuspectTask(HWIPCAnalyzeList hwIPCAnalyzeList) {		
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_SUSPECT_TASK);				
		String wsRequest = "<request><name>"+hwIPCAnalyzeList.getSuspectName()+"</name><page><no>1</no><pageSize>10</pageSize></page></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractQuerySuspectTask(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField addCameraToCheckPoint(HWIPC hwIPC) {		
//		<request>
//			<cameraList>
//				<cameraSn>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</cameraSn>
//				<cameraSn>01174380000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</cameraSn>
//			</cameraList>
//			<sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
//		</request> 
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		</response>
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ADD_CAMERA_TO_CHECK_POINT);				
		String wsRequest = "<request><cameraList><cameraSn>"+hwIPC.getIpcCode()+"</cameraSn></cameraList><sn>"+hwIPC.getHwIPCAnalyzeList().getCheckPointSN()+"</sn></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddCameraToCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField queryCameraFromCheckPoint(HWIPC hwIPC, int pageNo, int pageSize) {
//		<request>
//			<sn>aaf86369-727e-46b8-9ff7-c466c769f752</sn>
//			<page>
//				<no>1</no>
//				<pageSize>1000</pageSize>
//			</page>
//		</request>
//		<response>
//		    <cameraList>
//		        <camera>
//		            <name>192_168_2_202</name>
//		            <sn>09424010000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</sn>
//		        </camera>
//		    </cameraList>
//		    <result>
//		        <errmsg>Success</errmsg>
//		        <code>0</code>
//		    </result>
//		    <total>1</total>
//		</response>
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_CAMERA_FROM_CHECK_POINT);				
		String wsRequest = "<request><sn>"+hwIPC.getHwIPCAnalyzeList().getCheckPointSN()+"</sn><page><no>"+pageNo+"</no><pageSize>"+pageSize+"</pageSize></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryCameraFromCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_REMOVE_CAMERA_FROM_CHECK_POINT);				
		String wsRequest = "<request><cameraList><cameraSn>"+hwIPC.getIpcCode()+"</cameraSn></cameraList><sn>"+hwIPC.getHwIPCAnalyzeList().getCheckPointSN()+"</sn></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveCameraFromCheckPoint(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	
	public HWWSField queryCamera(HWIPC hwIPC) {
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_CAMERA);				
		String wsRequest = "sn="+hwIPC.getIpcCode()+"&page=1&limit=1";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1()+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryCamera(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public ResultStatus getCameraTask() {
		return null;
	}	
//	public HWWSField subscribeAlarm(HWIPCAnalyzeList hwIPCAnalyzeList) {		
//		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ALARM_SUBSCRIPTION);
//		String wsRequest = null;
//		String listOfSuspectId[] = hwIPCAnalyzeList.getSuspectId().split(",");
//		for (int i=0;i<listOfSuspectId.length;i++) {
//			
//		}
//		wsRequest = "<request><callbackUrl>"
//						+"<master>"+hwIPCAnalyzeList.getCallbackURLMaster()+"</master>";
//						if (!StringUtil.checkNull(hwIPCAnalyzeList.getCallbackURLSlave())) {
//							wsRequest += "<slave>"+hwIPCAnalyzeList.getCallbackURLSlave()+"</slave>";
//						}								
//						wsRequest +="</callbackUrl>"
//						+"<suspectId>"+hwIPCAnalyzeList.getSuspectId()+"</suspectId>"
//						+ "<dataType>"+2+"</dataType>"
//						+"</request>";
//		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
//			+appCfgURL.getAppValue1();		
//		String wsResponse = null;
//		HWWSField result = null;
//		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//		boolean executeAPISuccess = false;
//		try {			
//			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
//				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false);
//				result = HWXMLUtil.extractSubscribeAlarm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField subscribeAlarm(HWIPCAnalyzeList hwIPCAnalyzeList) {		
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ALARM_SUBSCRIPTION);
		String wsRequest = null;
		String listOfSuspectId[] = hwIPCAnalyzeList.getSuspectId().split(",");
		String listOfCallbackURL[] = hwIPCAnalyzeList.getCallbackURLMaster().split(",");
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
							+appCfgURL.getAppValue1();
		HWWSField result = null;
		for (int i=0;i<listOfSuspectId.length;i++) {
			wsRequest = "<request><callbackUrl>"
					+"<master>"+listOfCallbackURL[i]+"</master>";
					if (!StringUtil.checkNull(hwIPCAnalyzeList.getCallbackURLSlave())) {
						wsRequest += "<slave>"+hwIPCAnalyzeList.getCallbackURLSlave()+"</slave>";
					}								
					wsRequest +="</callbackUrl>"
					+"<suspectId>"+listOfSuspectId[i]+"</suspectId>"
					+ "<dataType>"+2+"</dataType>"
					+"</request>";
			result = this.subscribeAlarm(wsRequest, appCfgURL, endPoint);
			if (!result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				break;
			}
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "final result for subscribe alram:"+result.getResult().toString()));
		return result;	
	}
	public HWWSField subscribeAlarm(String wsRequest, ApplicationCfg appCfgURL, String endPoint) {		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractSubscribeAlarm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
//	public HWWSField unSubscribeAlarm(HWIPCAnalyzeList hwIPCAnalyzeList) {
//		//for stop service
//		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ALARM_UNSUBSCRIPTION);				
//		String wsRequest = "<request><suspectId>"+hwIPCAnalyzeList.getSuspectId()+"</suspectId><dataType>"+2+"</dataType></request>";
//		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
//			+appCfgURL.getAppValue1();		
//		String wsResponse = null;
//		HWWSField result = null;
//		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//		boolean executeAPISuccess = false;
//		try {			
//			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
//				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false);
//			//	wsResponse = "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
//				result = HWXMLUtil.extractSubscribeAlarm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
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
	public HWWSField unSubscribeAlarm(HWIPCAnalyzeList hwIPCAnalyzeList) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ALARM_UNSUBSCRIPTION);				
		String wsRequest = null;
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String listOfSuspectId[] = hwIPCAnalyzeList.getSuspectId().split(",");
		HWWSField result = null;
		for(int i=0;i<listOfSuspectId.length;i++) {
			wsRequest = "<request><suspectId>"+listOfSuspectId[i]+"</suspectId><dataType>"+2+"</dataType></request>";
			result = this.unSubscribeAlarm(wsRequest, appCfgURL, endPoint);			
		}						
		return result;	
	}
	public HWWSField unSubscribeAlarm(String wsRequest, ApplicationCfg appCfgURL, String endPoint) {
		//for stop service
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
			//	wsResponse = "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
				result = HWXMLUtil.extractSubscribeAlarm(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public ResultStatus searchPeopleLocation() {
		return null;
	}
	public HWWSField getUploadFileURL(String fileName, String imageSize) {
		//name=lz4.mp4&length=64834263
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_GET_UPLOAD_URL);				
		String wsRequest = "name="+fileName+"&length="+imageSize;
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractGetUploadFileURL(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField uploadFileToServer(String fileName, String fileSize, String uploadFileId, String uploadFileType, String endPoint, InputStream inputStream) {
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
					this.logOn();				
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
	public HWWSField publishUploadFile(String fileName, String imageSize, String uploadFieldId) {
		//name=lz4.mp4&length=64834263
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_PUBLISH_UPLOAD_FILE);				
		ApplicationCfg appCfgSSId = this.appCfgList.get(ApplicationCfg.KEY_API_PUBLISH_UPLOAD_FILE_PARAM_SSID);
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
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {			
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractPublishUploadFile(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField addFaceToList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo, String base64Image, String hwFileId) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ADD_FACE_TO_LIST);						
		String wsRequest = "<request><peopleList><people>" +
				"<credentialNumber>" + personInfo.getCertificationNo() + "</credentialNumber>" +
				"<credentialType>"+ personInfo.getPersonCertification().getThirdPartyCode() + "</credentialType>" +
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
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, StringUtil.checkNull(hwIPCAnalyzeList.getListNameId())?"":hwIPCAnalyzeList.getListNameId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceToList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField modifyFaceToList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo, String base64Image, String hwPeopleId) {
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_MODIFY_FACE_TO_LIST);						
		String wsRequest = "<request><people>" +
				"<credentialNumber>" + personInfo.getCertificationNo() + "</credentialNumber>" +
				"<credentialType>"+ personInfo.getPersonCertification().getThirdPartyCode() + "</credentialType>" +
				"<name>" + personInfo.getFullName() +"</name>" +
//				"<name>" + StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) +"</name>" +
				"<addPictures>" + 
				"<picture>" +				
				"<base64>"+base64Image+"</base64>" + 
				"</picture>" + 
				"</addPictures>" + 
				"</people>" + 				
				"</request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1()+ (StringUtil.checkNull(hwPeopleId)?"":"/"+hwPeopleId);	
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, hwIPCAnalyzeList.getListNameId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddFaceToList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public ResultStatus modifyImage() {
		return null;
	}
	public HWWSField removeFaceFromList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo) {
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
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_REMOVE_FACE_FROM_LIST);						
		String wsRequest = "ids="+personInfo.getHwPeopleId();
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;	
		endPoint = endPoint.replace(ApplicationCfg.API_PATH_PARAMETER, hwIPCAnalyzeList.getListNameId());
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, null, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractRemoveFaceFromList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField queryFaceFromList(HWIPCAnalyzeList hwIPCAnalyzeList, PersonInfo personInfo) {
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_FACE_FROM_LIST);						
//		String wsRequest = "<request><repositorIds>"+hwIPCAnalyzeList.getListNameId()+"</repositorIds><ids>"+personInfo.getHwPeopleId()+"</ids>"
//				+ "<page><no>1</no><orderName>time</orderName><size>10</size><sort>asc</sort></page></request>";		
		String wsRequest = "<request><repositorIds>"+hwIPCAnalyzeList.getListNameId()+"</repositorIds>"
				+ "<credentialNumber>"+personInfo.getCertificationNo()+"</credentialNumber>"
				+ "<credentialType>"+personInfo.getPersonCertification().getThirdPartyCode()+"</credentialType>"
				+ "<page><no>1</no><orderName>time</orderName><size>10</size><sort>asc</sort></page></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1()+"?"+wsRequest;	
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractQueryFaceFromList(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public HWWSField queryPerson(String blackListId, String nameListId) {
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_PERSON);						
		String wsRequest = "<request><repositorIds>"+nameListId+"</repositorIds>" +
				"<ids>"+blackListId+"</ids><page><no>1</no><orderName>time</orderName>" + 
				"<size>1</size><sort>asc</sort></page></request>";		
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
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
					this.logOn();				
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
	
	public HWWSField addIntelligentAnalysisTasksBatches() {
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
//				<task>
//					<task_name>eddy</task_name>
//					<camera_id>01174380000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</camera_id>
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
//		        <task>
//		            <result>
//		                <errmsg>Success</errmsg>
//		                <code>0</code>
//		            </result>
//		            <taskId>154140799214832790</taskId>
//		            <taskName>eddy</taskName>
//		        </task>
//		    </tasks>
//		</response>
//		noted: system create 2 task with same name which eddy
		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_ADD_INTELLIGENT_ANALYSIS_TASKS_BATCHS);						
		String wsRequest = "<request><tasks>";
		//add all camera
		Iterator<HWVCN> hwVCNList = this.hwVCM.getHwVCNs().iterator();
		Iterator<HWIPC> hwIPCList = null;
		HWVCN hwVCN = null;
		HWIPC hwIPC = null;
        while(hwVCNList.hasNext()){
        	hwVCN = hwVCNList.next();
        	hwIPCList = hwVCN.getHwIPCs().iterator();
        	while(hwIPCList.hasNext()) {
        		hwIPC = hwIPCList.next();
        		wsRequest += "<task><task_name>"+hwIPC.getHwIPCAnalyzeList().getSuspectName()+"</task_name><camera_id>"+hwIPC.getIpcCode()+"</camera_id><type>2</type><analyzeMode>0</analyzeMode></task>";
        	}
        }
        wsRequest += "</tasks></request>";
		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
			+appCfgURL.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
		boolean executeAPISuccess = false;
		try {
			for (int i=0; i<this.appCfgHWNoOfRetryAPI;i++) {
				wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3(), false, this.hwVCM.getVcmName());
				result = HWXMLUtil.extractAddIntelligentAnalysisTasksBatches(this.transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon);
				if (result.getResult().getStatusCode()==ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE) {
					//logon
					this.logOn();				
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
	public ResultStatus getGroupId() {
		return null;
	}
	public ResultStatus modifyCameraFeature() {
		return null;
//		<request>
//			<name>192_168_2_203</name>
//		    <sn>02985120000000000101#ab8df621bf3f4d91b61ce8cf5100c01a</sn>
//		    <camera-feature>1</camera-feature>
//		    <camera-use>2</camera-use>
//		    <stream-url>192.168.2.203</stream-url>
//		    <camera-state>0</camera-state>
//		    <camera-type>3</camera-type>
//		    <plat-name>vcn</plat-name>
//		</request>
//		<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		</response>
	}
	


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
	public String invokeRestfulService(String tranId, String endPoint, String inputRequest, String module, String requestMethod, boolean refreshJsessionId, String vcmName) throws ConsumeWSException{		
		Logger.info(this,LogUtil.getLogStart(tranId, module, "in invokeRestfulService: endpoint:"+endPoint+" input:"+inputRequest));
		Date startDate = new Date();
		String strOneline = null;
		StringBuilder result = new StringBuilder();
		HttpRequestBase conn = null;
		OutputStream os = null;
		BufferedReader br = null;		
		CloseableHttpClient httpclient = null;
		HttpResponse response = null;		
		try {							
			
			if ("GET".equals(requestMethod)){
				conn = new HttpGet(endPoint);
			}else if ("DELETE".equals(requestMethod)){
				if (StringUtil.checkNull(inputRequest)){
					conn = new HttpDelete(endPoint);
				}else {
					HttpDeleteWithBody conn1 = new HttpDeleteWithBody(endPoint);
					conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));					
					conn = conn1;
				}
			}else if ("HEAD".equals(requestMethod)){
				conn = new HttpHead(endPoint);
			}else if ("PATCH".equals(requestMethod)){
				HttpPatch conn1 = new HttpPatch(endPoint);
				if (!StringUtil.checkNull(inputRequest)){					
					conn1.setEntity(new StringEntity(inputRequest, ContentType.create("application/xml", "UTF-8")));
				}
				conn = conn1;
			}else if ("POST".equals(requestMethod)){
				HttpPost conn1 = new HttpPost(endPoint);
				if (!StringUtil.checkNull(inputRequest)){
					conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));
				}
				conn = conn1;
			}else if ("PUT".equals(requestMethod)){
				HttpPut conn1 = new HttpPut(endPoint);
				if (!StringUtil.checkNull(inputRequest)){
					conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));
				}
				conn = conn1;
			}					
			conn.setHeader("Accept-Charset", "UTF-8");			
			if (this.globalVarService.getJSessionId(vcmName)!=null){
				conn.setHeader("Cookie", this.globalVarService.getJSessionId(vcmName));				
			}					
			httpclient = this.createAcceptSelfSignedCertificateClient();
			response = httpclient.execute(conn);
			//200 is ok
			if (response.getStatusLine().getStatusCode() != 200) {
				Logger.info(this,LogUtil.getLogStop(tranId, module, "out invokeRestfulService result:HTTP error code : "+ response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase(), startDate));
				throw new ConsumeWSException("HTTP error code : "+ response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase());
			}
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			strOneline = br.readLine();
			while (strOneline != null) {
				result.append(strOneline);				
				strOneline = br.readLine();				
			}							        
			if (this.globalVarService.getJSessionId(vcmName)==null  || refreshJsessionId) {
				for (Header h:response.getAllHeaders()) {
//					Logger.info(this, LogUtil.getLogInfo(transactionId, h.getName()+":"+h.getValue()));
					if ("Set-Cookie".equals(h.getName())){
						this.globalVarService.setJSessionId(vcmName,h.getValue());
						break;
					}
				}																						
			}			
		}catch (ConsumeWSException e) {
			throw e;
		}catch (Exception e) {
			Logger.info(this,LogUtil.getLogStop(tranId, module, "error while invoke service "+e.toString(), startDate));					
			Logger.error(this, LogUtil.getLogError(transactionId, "error while invoke service "+e.toString(), e));
			throw new ConsumeWSException(e.toString());
		}finally{
			try{
				if (os!=null){
					os.close();
				}
			}catch (Exception ex){}
			try{
				if (br!=null){
					br.close();
				}				
			}catch (Exception ex){}					
			try{
								
			}catch (Exception ex){}
		}
		Logger.info(this,LogUtil.getLogStop(tranId, module, "out invokeRestfulService result:"+result.toString()+" with vcmName:"+vcmName+" sessionId:"+this.globalVarService.getJSessionId(vcmName), startDate));
						
		return result.toString();
	}	
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
	@Override
	public boolean isInitiate() {
		return this.isInitiateClass;
	} 	
	private CloseableHttpClient createAcceptSelfSignedCertificateClient() throws Exception {

	    // use the TrustSelfSignedStrategy to allow Self Signed Certificates
	    SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy())
	            .build();

	    // we can optionally disable hostname verification. 
	    // if you don't want to further weaken the security, you don't have to include this.
	    HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

	    // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
	    // and allow all hosts verifier.
	    SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

	    // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
	    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(this.appCfgHttpConnectionTimeout).build();
	    return HttpClients.custom().setSSLSocketFactory(connectionFactory).setDefaultRequestConfig(requestConfig).build();
	}
//	public HWWSField queryPersonDBPhoto(String personFileId, String imageType, String imageSize) {
//		ApplicationCfg appCfgURL = this.appCfgList.get(ApplicationCfg.KEY_API_QUERY_PERSON_DB_PHOTO);						
//		String wsRequest = "fileId="+personFileId+"&imageType="+imageType+"&imageSize="+imageSize;		
//		String endPoint = this.hwVCM.getConnectProtocol()+"://"+this.hwVCM.getVcmIp()+":"+this.hwVCM.getLoginPort() 
//			+appCfgURL.getAppValue1()+"?"+wsRequest;
//		String wsResponse = null;
//		HWWSField result = null;
//		Logger.info(this, LogUtil.getLogInfo(this.transactionId, appCfgURL.getAppValue2(), appCfgURL.getAppValue2()+" with endPoint:"+endPoint));
//		try {
//			wsResponse = this.invokeRestfulService(this.transactionId, endPoint, wsRequest, appCfgURL.getAppValue2(), appCfgURL.getAppValue3());
//			result = HWXMLUtil.extractQueryPersonDBPhoto(this.transactionId, wsResponse);			
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
	public void setHwVCM(HWVCM hwVCM) {
		this.hwVCM = hwVCM;
	}
}
