package com.xpand.xface.service.impl.hwapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.HttpDeleteWithBody;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.exception.ConsumeWSException;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

public class HWAPIBaseImpl{
	@Autowired
	protected GlobalVarService globalVarService;
	
	protected String transactionId;	
	protected String appCfgHWSuccessCode;
	protected String appCfgHWSDKSuccessCode;
	protected String appCfgHWPlsLogon;
	protected String appCfgHWSDKPlsLogon;
	protected int appCfgHWNoOfRetryAPI;
	protected int appCfgHttpConnectionTimeout;
	protected boolean isInitialClass = false;
	protected HWWSField logOn(String transactionId, HWVCM hwVCM, ApplicationCfg appCfgAPI, String firstLoginCode) {
		String wsRequest = "?account="+hwVCM.getVcmLoginUser()+"&pwd="+hwVCM.getVcmLoginPwd();
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
						+appCfgAPI.getAppValue1()+wsRequest;		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(transactionId, endPoint, null, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue3()
						, true, hwVCM.getVcmName());
//			wsResponse =  "<response>" + 
//					"<result>" + 
//					"<errmsg>user is first login</errmsg>" + 
//					"<code>30873259114402</code>" + 
//					"</result>" + 
//					"</response>";
			result = HWXMLUtil.extractLogon(transactionId, wsResponse, this.appCfgHWSuccessCode, this.appCfgHWPlsLogon, firstLoginCode);						
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgAPI.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgAPI.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	protected HWWSField logOnSDK(String transactionId, HWVCM hwVCM, ApplicationCfg appCfgAPI, String firstLoginCode) {
		String wsRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esdk=\"esdk_ivs_professional_server\"><soapenv:Header/><soapenv:Body><esdk:login><userName>"+hwVCM.getVcnSDKLoginUser()+"</userName><password>"+hwVCM.getVcnSDKLoginPwd()+"</password></esdk:login></soapenv:Body></soapenv:Envelope>";
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcnSDKIp()+":"+hwVCM.getVcnSDKLoginPort() 
						+appCfgAPI.getAppValue1();		
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue2()+" with endPoint:"+endPoint));
		try {			
			wsResponse = this.invokeSoapService(this.transactionId, endPoint, wsRequest, appCfgAPI.getAppValue2(), false, hwVCM.getVcnName(), appCfgAPI.getAppValue3());			
//			wsResponse =  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:loginResponse xmlns:ns2=\"esdk_ivs_professional_server\"><resultCode>0</resultCode></ns2:loginResponse></soap:Body></soap:Envelope>";
			result = HWXMLUtil.extractLogonSDK(transactionId, wsResponse, this.appCfgHWSDKSuccessCode, this.appCfgHWSDKPlsLogon, firstLoginCode);						
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgAPI.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgAPI.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	protected HWWSField changePwd(String transactionId, HWVCM hwVCM, ApplicationCfg appCfgAPI, String newPwd) {
		String wsRequest = "?OLD_PASSWORD="+hwVCM.getVcmLoginPwd()+"&NEW_PASSWORD="+newPwd;
		String endPoint = hwVCM.getConnectProtocol()+"://"+hwVCM.getVcmIp()+":"+hwVCM.getVcmLoginPort() 
			+appCfgAPI.getAppValue1()+wsRequest;
		String wsResponse = null;
		HWWSField result = null;
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue2()+" with endPoint:"+endPoint));
		try {
			wsResponse = this.invokeRestfulService(transactionId, endPoint, null, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue3()
						, false, hwVCM.getVcmName());
//			wsResponse = "<response>" + 
//					"<result>" + 
//					"<errmsg>Success.</errmsg>" + 
//					"<code>0</code>" + 
//					"</result>" + 
//					"</response>";
			result = HWXMLUtil.extractChangePwd(transactionId, wsResponse, this.appCfgHWSuccessCode);
		}catch (ConsumeWSException ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgAPI.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.HW_CONSUME_WS_ERROR_CODE, ex.toString());
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while invoke "+appCfgAPI.getAppValue2()+" service "+ex.toString(), ex));
			result = new HWWSField();
			result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, ex.toString());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, appCfgAPI.getAppValue2(), appCfgAPI.getAppValue2()+" with result:"+result.getResult().toString()));
		return result;
	}
	protected String invokeRestfulService(String transactionId, String endPoint, String inputRequest, String module, String requestMethod, boolean refreshJsessionId, String jSessionIdKey) throws ConsumeWSException{		
		Logger.info(this,LogUtil.getLogStart(transactionId, module, "in invokeRestfulService: endpoint:"+endPoint+" input:"+inputRequest));
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
					//conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));					
					conn1.setEntity(new StringEntity(inputRequest,ContentType.APPLICATION_JSON));
					conn = conn1;
				}
			}else if ("HEAD".equals(requestMethod)){
				conn = new HttpHead(endPoint);
			}else if ("PATCH".equals(requestMethod)){
				HttpPatch conn1 = new HttpPatch(endPoint);
				if (!StringUtil.checkNull(inputRequest)){					
					//conn1.setEntity(new StringEntity(inputRequest, ContentType.create("application/xml", "UTF-8")));
					conn1.setEntity(new StringEntity(inputRequest, ContentType.create("application/json", "UTF-8")));
				}
				conn = conn1;
			}else if ("POST".equals(requestMethod)){
				HttpPost conn1 = new HttpPost(endPoint);
				if (!StringUtil.checkNull(inputRequest)){
					//conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));
					conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_JSON));
				}
				conn = conn1;
			}else if ("PUT".equals(requestMethod)){
				HttpPut conn1 = new HttpPut(endPoint);
				if (!StringUtil.checkNull(inputRequest)){
					//conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));
					conn1.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_JSON));
				}
				conn = conn1;
			}					
			conn.setHeader("Accept-Charset", "UTF-8");			
			if (this.globalVarService.getJSessionId(jSessionIdKey)!=null){
				conn.setHeader("Cookie", this.globalVarService.getJSessionId(jSessionIdKey));				
			}					
			httpclient = this.createAcceptSelfSignedCertificateClient();
			response = httpclient.execute(conn);
			//200 is ok
			if (response.getStatusLine().getStatusCode() != 200) {
				Logger.info(this,LogUtil.getLogStop(transactionId, module, "out invokeRestfulService result:HTTP error code : "+ response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase(), startDate));
				throw new ConsumeWSException("HTTP error code : "+ response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase());
			}
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			strOneline = br.readLine();
			while (strOneline != null) {
				result.append(strOneline);				
				strOneline = br.readLine();				
			}							        
			if (this.globalVarService.getJSessionId(jSessionIdKey)==null  || refreshJsessionId) {
				for (Header h:response.getAllHeaders()) {
//					Logger.info(this, LogUtil.getLogInfo(transactionId, h.getName()+":"+h.getValue()));
					if ("Set-Cookie".equals(h.getName())){
						this.globalVarService.setJSessionId(jSessionIdKey, h.getValue());
						break;
					}
				}																						
			}			
		}catch (ConsumeWSException e) {
			throw e;
		}catch (Exception e) {
			Logger.info(this,LogUtil.getLogStop(transactionId, module, "error while invoke service "+e.toString(), startDate));					
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
		Logger.info(this,LogUtil.getLogStop(transactionId, module, "out invokeRestfulService result:"+result.toString()+" with jSessionIdKey:"+jSessionIdKey+" sessionId:"+this.globalVarService.getJSessionId(jSessionIdKey), startDate));
		return result.toString();
	}
	protected String invokeSoapService(String transactionId, String endPoint, String inputRequest, String module, boolean refreshJsessionId, String jSessionIdKey, String soapAction) throws ConsumeWSException{		
		Logger.info(this,LogUtil.getLogStart(transactionId, module, "in invokeSoapService: endpoint:"+endPoint+" input:"+inputRequest));
		Date startDate = new Date();
		String strOneline = null;
		StringBuilder result = new StringBuilder();
		HttpRequestBase conn = null;
		OutputStream os = null;
		BufferedReader br = null;		
		CloseableHttpClient httpclient = null;
		HttpResponse response = null;		
		try {													
			HttpPost connPost = new HttpPost(endPoint);			
			connPost.setEntity(new StringEntity(inputRequest, ContentType.APPLICATION_XML));			
			conn = connPost;							
			conn.setHeader("Accept-Charset", "UTF-8");	
			conn.setHeader("Content-Length",String.valueOf(inputRequest.length()));
			conn.setHeader("Content-Type", "text/xml;charset=UTF-8");
			conn.setHeader("SOAPAction", soapAction);			
			if (this.globalVarService.getJSessionId(jSessionIdKey)!=null){
				conn.setHeader("Cookie", this.globalVarService.getJSessionId(jSessionIdKey));				
			}					
			httpclient = this.createAcceptSelfSignedCertificateClient();
			response = httpclient.execute(conn);
			//200 is ok
			if (response.getStatusLine().getStatusCode() != 200) {
				Logger.info(this,LogUtil.getLogStop(transactionId, module, "out invokeSoapService result:HTTP error code : "+ response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase(), startDate));
				throw new ConsumeWSException("HTTP error code : "+ response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase());
			}
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			strOneline = br.readLine();
			while (strOneline != null) {
				result.append(strOneline);				
				strOneline = br.readLine();				
			}							        
			if (this.globalVarService.getJSessionId(jSessionIdKey)==null  || refreshJsessionId) {
				for (Header h:response.getAllHeaders()) {
//					Logger.info(this, LogUtil.getLogInfo(transactionId, h.getName()+":"+h.getValue()));
					if ("Set-Cookie".equals(h.getName())){
						this.globalVarService.setJSessionId(jSessionIdKey, h.getValue());
						break;
					}
				}																						
			}			
		}catch (ConsumeWSException e) {
			throw e;
		}catch (Exception e) {
			Logger.info(this,LogUtil.getLogStop(transactionId, module, "error while invoke service "+e.toString(), startDate));					
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
		Logger.info(this,LogUtil.getLogStop(transactionId, module, "out invokeSoapService result:"+result.toString()+" with jSessionIdKey:"+jSessionIdKey+" sessionId:"+this.globalVarService.getJSessionId(jSessionIdKey), startDate));					
		return result.toString();
	}
	protected CloseableHttpClient createAcceptSelfSignedCertificateClient() throws Exception {

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
}
