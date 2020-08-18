package com.xpand.xface.util;

import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.log.Logger;
import com.xpand.xface.bean.AddIntelligentAnalsisBatchesResp;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.query.QueryCameraResp;
import com.xpand.xface.bean.query.QueryCheckPointResp;
import com.xpand.xface.bean.query.QueryFaceListResp;
import com.xpand.xface.bean.query.QueryPersonByPhotoRespV1;
import com.xpand.xface.bean.query.QuerySuspectTaskResp;
import com.xpand.xface.bean.zk.ZKPersonInfo;
import com.xpand.xface.entity.HWAlarmHist;

public class HWXMLUtil {
	public static Document parseXmlFromString(String xmlString) throws Exception{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource  inputSource = new InputSource();
	    inputSource.setCharacterStream(new StringReader(xmlString));
	    return builder.parse(inputSource);	    
	}	
	public static HWWSField extractLogon(String transactionId, String wsResponse, String successCode, String loginRequireCode, String firstLogin) throws Exception{
		//<response><result><errmsg>user is first login</errmsg><code>30873259114402</code></result></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}		
		HWWSField result = new HWWSField();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode, firstLogin, result.getResult()));
		return result;
	}	
	public static HWWSField extractLogonSDK(String transactionId, String wsResponse, String successCode, String loginRequireCode, String firstLogin) throws Exception{
		//app error
		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ns2:loginResponse xmlns:ns2="esdk_ivs_professional_server"><resultCode>0</resultCode></ns2:loginResponse></soap:Body></soap:Envelope>
		//server error
//		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//		   <soap:Body>
//		      <soap:Fault>
//		         <faultcode>soap:571052032</faultcode>
//		         <faultstring/>
//		      </soap:Fault>
//		   </soap:Body>
//		</soap:Envelope>		
		Document document = HWXMLUtil.parseXmlFromString(wsResponse);
	    NodeList nodeList = document.getElementsByTagName("soap:Body");		
	    String errCode = null;	
		if (nodeList.getLength()>0) {			
			Element element1 = (Element) nodeList.item(0).getChildNodes().item(0);
			NodeList codeNodeList = element1.getElementsByTagName("resultCode");
			Element element2 = (Element) codeNodeList.item(0);
			if (element2 == null) {
				codeNodeList = element1.getElementsByTagName("faultcode");				
			}
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));						
		}					
		HWWSField result = new HWWSField();
		result.setResult(HWXMLUtil.getResultStatusSDK(errCode, null, successCode, loginRequireCode, firstLogin, result.getResult()));
		return result;
	}
	public static HWWSField extractKeepAlive(String transactionId, String wsResponse, String successCode) throws Exception{
		//same as logon
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, "", "");
	}
	public static HWWSField extractKeepAliveSDK(String transactionId, String wsResponse, String successCode) throws Exception{
		//same as logon
		return HWXMLUtil.extractLogonSDK(transactionId, wsResponse, successCode, "", "");
	}
	public static HWWSField extractLogOut(String transactionId, String wsResponse, String successCode) throws Exception{
		//same as logon
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, "", "");
	}
	public static HWWSField extractLogOutSDK(String transactionId, String wsResponse, String successCode) throws Exception{
		//same as logon
		return HWXMLUtil.extractLogonSDK(transactionId, wsResponse, successCode, "", "");
	}
	public static HWWSField extractChangePwd(String transactionId, String wsResponse, String successCode) throws Exception{
		//same as logon
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, "", "");
	}
	public static HWWSField extractQueryCamera(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 		
		String errCode = null;
		String errMsg = null;		
		Element element = null;
		NodeList level1NodeList = document.getElementsByTagName("result");
		NodeList level2NodeList = null;
		int level1NodeLength = 0;
		if (level1NodeList.getLength()>0) {
			element = (Element) level1NodeList.item(0);
			level2NodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
			level2NodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
		}
		String cameraName = null;
		String cameraSN = null;		
		String cameraIP = null;
		String cameraState = null;
		HWWSField result = new HWWSField();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			level1NodeList = document.getElementsByTagName("camera");
			level1NodeLength = level1NodeList.getLength();
			if (level1NodeLength>0) {
				for (int index=0; index<level1NodeLength; index++) {
					element = (Element) level1NodeList.item(index);
					//camera-state
					level2NodeList = element.getElementsByTagName("name");
					cameraName = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
					level2NodeList = element.getElementsByTagName("sn");				
					cameraSN = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));				
					level2NodeList = element.getElementsByTagName("stream-url");				
					cameraIP = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
					level2NodeList = element.getElementsByTagName("camera-state");
					cameraState = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
					result.addQueryCameraRespList(new QueryCameraResp(cameraName, cameraSN, cameraIP, cameraState));					
				}				
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}			
		return result;
	}
	public static HWWSField extractAddFaceList(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<response><result><errmsg>Success</errmsg><code>0</code></result><id>5a9f9670d650637f801a7695</id></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		String addId = null;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode, result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("id");
			if (nodeList.getLength()>0) {
				addId = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setAddFaceListId(addId);
			}
		}		
		return result;
	}
	public static HWWSField extractRemoveFaceList(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	public static HWWSField extractQueryFaceList(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
//		<response>
//		<result>
//		<errmsg>Success.</errmsg>
//		<code>0</code>
//		</result>
//		<total>19</total>
//		<repositories>
//		<repository>
//		<createtime>1523847151194</createtime>
//		<description></description>
//		<id>5ad40fefd65063125709135f</id>
//		<name>A</name>
//		<owner>false</owner>
//		<reserved1></reserved1>
//		<status>false</status>
//		<type>2</type>
//		</repository>
//		<repository>
//		<createtime>1523847163266</createtime>
//		<description></description>
//		<id>5ad40ffbd650631257091360</id>
//		<name>B</name>
//		<owner>false</owner>
//		<reserved1></reserved1>
//		<status>true</status>
//		<type>2</type>
//		</repository>
//		</repositories>
//		</response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		int totalNameList = 0;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("total");
			if (nodeList.getLength()>0) {
				totalNameList = StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0)),0);				
			}
			if (totalNameList>0) {
				nodeList = document.getElementsByTagName("repository");
				if (nodeList.getLength()>0) {
					String faceListName = null;
					String faceListId = null;
					Element element = (Element) nodeList.item(0);
					NodeList codeNodeList = element.getElementsByTagName("name");
					faceListName = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
					NodeList errMsgNodeList = element.getElementsByTagName("id");
					faceListId = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
					result.setQueryFaceListResp(new QueryFaceListResp(faceListName, faceListId));
				}
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}		
		return result;
	}
	public static HWWSField extractAddCameraCheckPoint(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<response><result><errmsg>Success.</errmsg><code>0</code></result><sn>54775041-4688-450f-85e0-1649a1b17a53</sn></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		String checkPointSN = null;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("sn");
			if (nodeList.getLength()>0) {
				checkPointSN = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setCameraCheckPointSN(checkPointSN);
			}
		}		
		return result;
	}
	public static HWWSField extractRemoveCameraCheckPoint(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	public static HWWSField extractQueryCameraCheckPoint(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		// <response>
		// <bayonetList>
		// <bayonet>
		// <createTime>2017-12-07 10:29:12</createTime>
		// <name>car1</name>
		// <sn>ad55a03e-b7e2-47f0-a68a-3086e2a9a975</sn>
		// <type>1</type>
		// </bayonet>
		// <bayonet>
		// <createTime>2017-12-07 10:44:37</createTime>
		// <name>car2</name>
		// <sn>08a39227-4d08-4b48-a193-31a7f109b1db</sn>
		// <type>1</type>
		// </bayonet>
		// <bayonet>
		// <createTime>2017-12-07 10:44:45</createTime>
		// <name>face2</name>
		// <sn>54775041-4688-450f-85e0-1649a1b17a53</sn>
		// <type>2</type>
		// </bayonet>
		// </bayonetList>
		// <result>
		// <errmsg/>
		// <code>0</code>
		// </result>
		// <total>5</total>
		// </response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		int totalNameList = 0;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("total");
			if (nodeList.getLength()>0) {
				totalNameList = StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0)),0);				
			}
			if (totalNameList>0) {
				nodeList = document.getElementsByTagName("bayonet");
				if (nodeList.getLength()>0) {
					String checkPointName = null;
					String checkPointSN = null;
					Element element = (Element) nodeList.item(0);
					NodeList codeNodeList = element.getElementsByTagName("name");
					checkPointName = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
					NodeList errMsgNodeList = element.getElementsByTagName("sn");
					checkPointSN = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
					result.setQueryCheckPointResp(new QueryCheckPointResp(checkPointName, checkPointSN));
				}
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}		
		return result;
	}
	public static HWWSField extractQueryIntelligentAnalysisTasksBatches(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
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
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		int totalNameList = 0;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("total");
			if (nodeList.getLength()>0) {
				totalNameList = StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0)),0);				
			}
			if (totalNameList>0) {
				nodeList = document.getElementsByTagName("task");
				if (nodeList.getLength()>0) {
					String taskId = null;
					Element element = (Element) nodeList.item(0);
					NodeList codeNodeList = element.getElementsByTagName("taskId");
					taskId = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));					
					result.setQueryIntelligentTaskId(taskId);
				}
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}		
		return result;
	}
	public static HWWSField extractAddCameraToCheckPoint(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, loginRequireCode, "");		
	}
	public static HWWSField extractRemoveCameraFromCheckPoint(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	public static HWWSField extractAddSuspectTask(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<response><result><errmsg>Success.</errmsg><code>0</code></result><suspectId>54775041-4688-450f-85e0-1649a1b17a53</suspectId></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		String suspectId = null;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("suspectId");
			if (nodeList.getLength()>0) {
				suspectId = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setAddSuspectTaskId(suspectId);
			}				
		}		
		return result;
	}
	public static HWWSField extractRemoveSuspectTask(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	public static HWWSField extractQuerySuspectTask(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		// <response>
		// <suspect><suspectId>xxx</suspectId><name>kjsfdsf</name></suspect>
		// <result>
		// <errmsg/>
		// <code>0</code>
		// </result>
		// <total>3</total>
		// </response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		int totalNameList = 0;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("total");
			if (nodeList.getLength()>0) {
				totalNameList = StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0)),0);				
			}
			if (totalNameList>0) {
				nodeList = document.getElementsByTagName("suspect");
				String suspectName = null;
				String suspectId = "";
				String suspectType = null;
//				if (nodeList.getLength()>0) {					
//					Element element = (Element) nodeList.item(0);
//					NodeList tmpNodeList = element.getElementsByTagName("name");
//					suspectName = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
//					tmpNodeList = element.getElementsByTagName("suspectId");
//					suspectId = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
//					tmpNodeList = element.getElementsByTagName("type");
//					suspectType = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
//					result.setQuerySuspectTaskResp(new QuerySuspectTaskResp(suspectName, suspectId, suspectType));
//				}
				if (nodeList.getLength()>0) {
					for (int i=0; i<nodeList.getLength();i++) {
						Element element = (Element) nodeList.item(i);
						NodeList tmpNodeList = element.getElementsByTagName("name");
						suspectName = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
						tmpNodeList = element.getElementsByTagName("suspectId");
						suspectId = suspectId+HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0))+",";
						tmpNodeList = element.getElementsByTagName("type");
						suspectType = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));					
					}
				}							
				if (suspectId.length()>0) {
					suspectId = suspectId.substring(0, suspectId.length()-1);
				}							
				result.setQuerySuspectTaskResp(new QuerySuspectTaskResp(suspectName, suspectId, suspectType));
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}		
		return result;
	}
	public static HWWSField extractSubscribeAlarm(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		return HWXMLUtil.extractLogon(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	public static HWWSField extractGetUploadFileURL(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<?xml version="1.0" encoding="UTF-8"?>
		//<response><result><code>0</code><errmsg>0</errmsg><upload>
		//<url>http://100.109.244.165:11131/mp_mcss/fileupload.do</url>
		//<uploaded-file-id>nk2SrpBb0a3VkYcdCCIeV6xcAHZBSfEa</uploaded-file-id></upload></result></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}		
		String uploadUrl = null;
		String uploadFileId = null;
		HWWSField result = new HWWSField();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("url");		
			if (nodeList.getLength()>0) {
				uploadUrl = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setUploadFileURL(uploadUrl);
			}						
			nodeList = document.getElementsByTagName("uploaded-file-id");		
			if (nodeList.getLength()>0) {
				uploadFileId = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setUploadFileFileId(uploadFileId);
			}
		}
		return result;
	}
	public static HWWSField extractUploadFileToServer(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<response><result><code>0</code><errmsg>NO_ERROR</errmsg></result><upload><casefile-id>
		//</casefile-id><uploaded-file-id>nk2SrpBb0a3VkYcdCCIeV6xcAHZBSfEa</uploaded-file-id>
		//<filesize>64834263</filesize><uploaded-size>64834263</uploaded-size>
		//<finished>true</finished></upload></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}		
		String caseFileId = null;		
		String uploadFileId = null;
		HWWSField result = new HWWSField();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("casefile-id");		
			if (nodeList.getLength()>0) {
				caseFileId = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setUploadCaseFileId(caseFileId);
			}
			nodeList = document.getElementsByTagName("uploaded-file-id");		
			if (nodeList.getLength()>0) {
				uploadFileId = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setUploadFileFileId(uploadFileId);
			}			
		}
		return result;
	}
	public static HWWSField extractPublishUploadFile(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<?xml version="1.0" encoding="UTF-8"?>
		//<response><result><code>0</code><errmsg>0</errmsg><casefileId>5a058dc3ae96adefa66e8a0b</casefileId></result></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		String caseFileId = null;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("casefileId");
			if (nodeList.getLength()>0) {
				caseFileId = HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0));
				result.setUploadCaseFileId(caseFileId);
			}				
		}		
		return result;
	}
	public static HWWSField extractAddFaceToList(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<response><result><errmsg>Success</errmsg><code>0</code></result><ids><id>5a9f9670d650637f801a7695</id></ids></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		String addFaceToListId = null;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("ids");
			if (nodeList.getLength()>0) {
				Element element = (Element) nodeList.item(0);
				NodeList codeNodeList = element.getElementsByTagName("id");
				addFaceToListId = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
				result.setAddFaceToListId(addFaceToListId);				
			}
		}		
		return result;
	}
	public static HWWSField extractRemoveFaceFromList(String transactionId, String wsResponse, String successCode, String loginRequireCode, String faceNotExistCode) throws Exception{
		//<response><result><errmsg>user is first login</errmsg><code>30873259114402</code></result></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}		
		HWWSField result = new HWWSField();
		ResultStatus resultStatus = new ResultStatus();
		//if result is success or face not exist we convert to success only 
		if (successCode.equals(errCode) || faceNotExistCode.equals(errCode)) {
			resultStatus.setStatusCode(ResultStatus.SUCCESS_CODE, null);
		}else if (loginRequireCode.equals(errCode)) {
			resultStatus.setStatusCode(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE, null);
		}else {
			resultStatus.setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, errCode+":"+errMsg);
		}
		result.setResult(resultStatus);
		return result;
	}
	public static HWWSField extractQueryFaceFromList(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//<response><result><errmsg>user is first login</errmsg><code>30873259114402</code></result></response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}		
		/// hwPeopleId		
		HWWSField result = new HWWSField();
		String hwPeopleId = null;
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode, "", result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("peopleList");
			if (nodeList.getLength()>0) {
				Element element = (Element) nodeList.item(0);
				nodeList = element.getElementsByTagName("people");
				if (nodeList.getLength()>0) {
					element = (Element) nodeList.item(0);
					NodeList codeNodeList = element.getElementsByTagName("peopleId");
					hwPeopleId = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
					result.setHwPersonId(hwPeopleId);
				}								
			}
		}
		return result;
	}
	private static ResultStatus getResultStatus(String errCode, String errMsg, String successCode, String loginRequireCode, ResultStatus result) {
		return HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode, "", result);
	}
	public static HWWSField extractVCMAlarm(String transactionId, String wsResponse) throws Exception{
		Document document = HWXMLUtil.parseXmlFromString(wsResponse);
		//common-info pass
		NodeList nodeList = document.getElementsByTagName("common-info");
		HWAlarmHist hwAlarmHist = new HWAlarmHist();
//		hwAlarmHist.setTaskId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "task-id"));
//		hwAlarmHist.setCameraId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "camera-id"));
//		hwAlarmHist.setCameraName(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "camera-name"));
//		hwAlarmHist.setCameraIndex(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "camera-index"),0));
//		hwAlarmHist.setVstationSn(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "vstation-sn"));
//		hwAlarmHist.setVstationName(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "vstation-name"));
//		hwAlarmHist.setVstationIndex(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "vstation-index"));
//		hwAlarmHist.setCaseId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "case-id"));
//		hwAlarmHist.setCaseFileId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "case-file-id"));
//		hwAlarmHist.setSliceNum(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "slice-num"));
//		hwAlarmHist.setSource(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "source"));
//		hwAlarmHist.setSourceSystemId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "source-system-id"));
//		hwAlarmHist.setResolution(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "resolution"));
//		hwAlarmHist.setAlarmCode(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-id"));
//		hwAlarmHist.setAlarmLevel(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-level"));
//		hwAlarmHist.setAlarmTime(DateTimeUtil.epochToDate(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-time"),0));
//		hwAlarmHist.setAlarmType(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-type"));
//		hwAlarmHist.setAlarmPicName(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-pic-name"));
//		hwAlarmHist.setcTime(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "ctime"));
//		hwAlarmHist.setRuleType(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "rule-type"));
//		hwAlarmHist.setConfirm(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "confirm"));
//		hwAlarmHist.setClosed(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "closed"));
//		hwAlarmHist.setObjectId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "object-id"));
//		hwAlarmHist.setVideoType(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "video-type"));
//		hwAlarmHist.setObjectType(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "object-type"));
//		hwAlarmHist.setSuspectId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "suspect-id"));
//		hwAlarmHist.setBlkgrpId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "blkgrp-id"));
//		hwAlarmHist.setBlackListId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "blacklist-id"));
//		hwAlarmHist.setDomainCode(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "domain-code"));
//		//<private-info>		
//		nodeList = document.getElementsByTagName("meta-data");
//		hwAlarmHist.setMetaColor(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "color"));
//		hwAlarmHist.setMetaVehicleBrand(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "vehiclebrand"));
//		hwAlarmHist.setMetaVehicleSub(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "vehicleSub"));
//		hwAlarmHist.setMetaYear(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "year"));
//		hwAlarmHist.setMetaPType(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "ptype"));
//		hwAlarmHist.setMetaPColor(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "pcolor"));
//		hwAlarmHist.setMetaPnr(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "pnr"));
//		hwAlarmHist.setMetaCarType(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "cartype"));
//		hwAlarmHist.setMetaDirec(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "direc"));
//		
//		hwAlarmHist.setMetaScr(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "scr"),-1));
//		hwAlarmHist.setMetaAlgorithmCode(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "algorithm-code"));
//		hwAlarmHist.setMetaAlgorithmName(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "algorithm-name"));
//		hwAlarmHist.setMetaAlarmMold(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-mold"),-1));
//		hwAlarmHist.setMetaAlarmMatch(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-match"),-1));
//		
//		//private-info -> meta -> pos
//		nodeList = document.getElementsByTagName("pos");
//		hwAlarmHist.setMetaPosLeft(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "left"),-1));
//		hwAlarmHist.setMetaPosTop(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "top"),-1));
//		hwAlarmHist.setMetaPosRight(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "right"),-1));
//		hwAlarmHist.setMetaPosBottom(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "bottom"),-1));
////		<pic>
////		private-info -> pic
//		nodeList = document.getElementsByTagName("pic");
//		hwAlarmHist.setPicCaseFileId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "casefile-id"));
//		hwAlarmHist.setPicFileId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_fileId"));
//		hwAlarmHist.setPicStartPos(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_startPos"));
//		hwAlarmHist.setPicThumbLen(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_thumbLen"));
//		hwAlarmHist.setPicLen(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_len"));
//		hwAlarmHist.setPicMId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_m_id"));
//		hwAlarmHist.setPicSInx(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_s_inx"));
//		hwAlarmHist.setPicImageUrl(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_imageUrl"));
//		hwAlarmHist.setPicThumImageUrl(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_thumImageUrl"));	
//		
//		hwAlarmHist.setPicFeatureValue(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "feature-value"));
//		hwAlarmHist.setPicFeatureLength(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "feature-length"),-1));
//		hwAlarmHist.setPicFeatureId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "feature-id"));
//		hwAlarmHist.setPicFeatureIndex(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "feature-index"));
//		hwAlarmHist.setPicIsURL(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "is-url"));		
		hwAlarmHist.setAlarmSource(HWAlarmHist.HW_VCM_ALARM);
		hwAlarmHist.setTaskId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "task-id"));
		hwAlarmHist.setCameraId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "camera-id"));
		hwAlarmHist.setResolution(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "resolution"));
		hwAlarmHist.setAlarmCode(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-id"));
		hwAlarmHist.setAlarmTime(DateTimeUtil.epochToDate(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-time"),0));
		hwAlarmHist.setAlarmPicName(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "alarm-pic-name"));
		hwAlarmHist.setcTime(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "ctime"));
		hwAlarmHist.setSuspectId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "suspect-id"));
		hwAlarmHist.setBlackListId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "blacklist-id"));
		//<private-info>		
		nodeList = document.getElementsByTagName("meta-data");
		hwAlarmHist.setMetaScr(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "scr"),-1));
//		<pic>
//		private-info -> pic
		nodeList = document.getElementsByTagName("pic");
		hwAlarmHist.setPicImageUrl(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_imageUrl"));
		hwAlarmHist.setPicThumImageUrl(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "_thumImageUrl"));	
		hwAlarmHist.setPicIsURL(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "is-url"));
//		for test send livephoto along with alarm
		hwAlarmHist.setLivePhoto(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "livePhoto"));
		
		hwAlarmHist.setDateCreated(new Date());
		HWWSField result = new HWWSField();
		result.setHwAlarmHist(hwAlarmHist);
		result.getResult().setStatusCode(ResultStatus.SUCCESS_CODE, null);
		return result;
	}
	
	//for extract XML of emergency button
	public static HWWSField extractVCNAlarmSOSBotton(String transactionId, String wsResponse) throws Exception{
		return HWXMLUtil.extractVCNAlarm(transactionId, wsResponse, HWAlarmHist.VCN_ALM_EVENT_SET, HWAlarmHist.VCN_ALM_DI, "OccurTime");
	}
	//for extract XML of clear emergency button
	public static HWWSField extractVCNClearSOSBotton(String transactionId, String wsResponse) throws Exception{		
		return HWXMLUtil.extractVCNAlarm(transactionId, wsResponse, HWAlarmHist.VCN_ALM_EVENT_CLEAR, HWAlarmHist.VCN_ALM_DI, null);
	}
	//for extract XML of IPC online
	public static HWWSField extractVCNIPCOnline(String transactionId, String wsResponse) throws Exception{		
		return HWXMLUtil.extractVCNAlarm(transactionId, wsResponse, HWAlarmHist.VCN_ALM_EVENT_SET, HWAlarmHist.VCN_ALM_IPC_ONLINE, "OccurTime");
	}
	//for extract XML of IPC offline
	public static HWWSField extractVCNIPCOffline(String transactionId, String wsResponse) throws Exception{
		return HWXMLUtil.extractVCNAlarm(transactionId, wsResponse, HWAlarmHist.VCN_ALM_EVENT_CLEAR, HWAlarmHist.VCN_ALM_IPC_OFFLINE, "OccurTime");
	}
	public static HWWSField extractVCNAlarm(String transactionId, String wsResponse, String eventType, String alarmType, String alarmTimeField) throws Exception{
		Document document = HWXMLUtil.parseXmlFromString(wsResponse);
		NodeList nodeList = document.getElementsByTagName("Content");
		HWAlarmHist hwAlarmHist = new HWAlarmHist();
		hwAlarmHist.setEventType(eventType);
		hwAlarmHist.setAlarmSource(HWAlarmHist.HW_VCN_ALARM);
		hwAlarmHist.setAlarmType(alarmType);
		hwAlarmHist.setAlarmCode(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "AlarmEventId"));
		hwAlarmHist.setCameraId(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "AlarmInCode"));
		if (StringUtil.checkNull(alarmTimeField)) {
			hwAlarmHist.setAlarmTime(new Date());		
			hwAlarmHist.setDateCreated(hwAlarmHist.getAlarmTime());
		}else {
			hwAlarmHist.setAlarmTime(StringUtil.stringToDate(HWXMLUtil.getCharacterDataFromNodeList(nodeList, "OccurTime"), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));		
			hwAlarmHist.setDateCreated(new Date());
		}
		HWWSField result = new HWWSField();
		result.setHwAlarmHist(hwAlarmHist);
		result.getResult().setStatusCode(ResultStatus.SUCCESS_CODE, null);
		return result;		
	}
	public static HWWSField extractQueryPerson(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
//		<response>
//		<result>
//		<errmsg>Success</errmsg>
//		<code>0</code>
//		</result>
//		<number>1</number>
//		<peopleList>
//		<people>
//		<name></name>
//		<peopleId></peopleId>
//		<faceList>
//		<face>
//		<fileId></fileId>
//		</face>
//		</faceList>
//		</people>
//		</peopleList>		
//		</response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		String personId = null;
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			nodeList = document.getElementsByTagName("people");
			if (nodeList.getLength()>0) {
				Element element = (Element) nodeList.item(0);
				NodeList codeNodeList = element.getElementsByTagName("peopleId");
				personId = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
				result.setHwPersonId(personId);
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}		
		return result;
	}
	public static HWWSField extractAddIntelligentAnalysisTasksBatches(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
//		<?xml version="1.0"?>
//		<response>
//		 <result>
//		 <errmsg>Success.</errmsg>
//		 <code>0</code>
//		 </result>
//		 <tasks>
//		 <task>
//		 <result>
//		 <code>0</code>
//		 <taskId>152385811473390880</taskId>
//		 <taskName>55500100000000000101-License plate recognition</taskName></code>
//		 </result>
//		 </task>
//		 </tasks>
//		</response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
		NodeList nodeList = document.getElementsByTagName("result");
		String errCode = null;
		String errMsg = null;		
		HWWSField result = new HWWSField();
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList codeNodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
			NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
		}
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {								
			nodeList = document.getElementsByTagName("tasks");
			String resultCode = "";
			String taskId = "";
			String taskName = "";
			AddIntelligentAnalsisBatchesResp addIntelligentAnalsisBatchesResp = null;  
			if (nodeList.getLength()>0) {
//				for (int i=0; i<nodeList.getLength();i++) {
					Element element = (Element) nodeList.item(0);
					NodeList tmpNodeList = element.getElementsByTagName("code");
					resultCode = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
					tmpNodeList = element.getElementsByTagName("taskId");
					taskId = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
					tmpNodeList = element.getElementsByTagName("taskName");
					taskName = HWXMLUtil.getCharacterDataFromElement((Element) tmpNodeList.item(0));
					addIntelligentAnalsisBatchesResp = new AddIntelligentAnalsisBatchesResp(resultCode, taskId, taskName);
//				}
			}										
			result.setAddIntelligentAnalsisBatchesResp(addIntelligentAnalsisBatchesResp);
		}		
		return result;
	}	
	
	public static HWWSField extractQueryPersonByPhoto(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		
//		<response>
//		<peoplefaces>
//			<peopleface>
//				<age>3</age>
//				<cameraName>26500060000000000101</cameraName>
//				<cameraSn>26500060000000000101#007a6a0499b844638288064faf520891</cameraSn>  --> ok
//				<confidence>0</confidence> --> ok
//				<domainCode>cb08eab831134bdab1b5929ca2d88715</domainCode>
//				<domainName>121 lower-level domain</domainName>
//				<gender>2</gender>
//				<lowerColor>1</lowerColor>
//				<lowerStyle>0</lowerStyle>
//				<otype>4</otype>
//				<picUrl></picUrl>
//				<downloadId>6a754ec8-254d-4d9f-955d-d94bb7a0e2ff</downloadId>
//				<historyFileId>5b30e5adb45caf43a5f1fcf9</historyFileId>
//				<fileId>cb08eab831134bdab1b5929ca2d88715#78763905564451076@12</fileId> --> ok
//				<pos>
//					<bottom>1282</bottom>
//					<left>1742</left>
//					<right>1994</right>
//					<top>767</top>
//				</pos>
//				<sourceDevice>1</sourceDevice>
//				<recordTime>1523862041290</recordTime>
//				<upperColor>1</upperColor>
//				<upperStyle>1</upperStyle>
//				<upperTexture>1</upperTexture>
//			</peopleface>
//		</peoplefaces>
//		<result>
//			<errmsg />
//			<code>0</code>
//		</result>
//		<total>1</total>
//	</response>
		
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 		
		String errCode = null;
		String errMsg = null;		
		Element element = null;
		NodeList level1NodeList = document.getElementsByTagName("result");
		NodeList level2NodeList = null;			
		int level1NodeLength = 0;
		int level2NodeLength = 0;	
		
		if (level1NodeList.getLength()>0) {
			element = (Element) level1NodeList.item(0);
			level2NodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
			level2NodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
		}			
		HWWSField result = new HWWSField();
		//get total
		level1NodeList = document.getElementsByTagName("response");
		int totalResult = 0;
		if (level1NodeList.getLength()>0) {
			element = (Element) level1NodeList.item(0);
			level2NodeList = element.getElementsByTagName("total");			
			totalResult = StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)),0);
		}			
		result.getQueryPersonByPhotoListResp().setTotalRecord(totalResult);
		QueryPersonByPhotoRespV1 queryPersonByPhotoResp;
		Date nowDate = new Date();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			level1NodeList = document.getElementsByTagName("peopleface");
			level1NodeLength = level1NodeList.getLength(); 
			if (level1NodeLength>0) {
				for (int index=0;index<level1NodeLength;index++) {
					element = (Element) level1NodeList.item(index);																						
					queryPersonByPhotoResp = new QueryPersonByPhotoRespV1();
					level2NodeList = element.getElementsByTagName("cameraSn");																
					queryPersonByPhotoResp.setCameraSn(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)));
					level2NodeList = element.getElementsByTagName("confidence");																
					queryPersonByPhotoResp.setConfidenceLevel(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), 0));
					level2NodeList = element.getElementsByTagName("fileId");																
					queryPersonByPhotoResp.setFileId(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)));								
					level2NodeList = element.getElementsByTagName("recordTime");																
					queryPersonByPhotoResp.setRecordTime(DateTimeUtil.epochToDate(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), nowDate.getTime()));																																									
					level2NodeList = element.getElementsByTagName("pos");					
					level2NodeLength = level2NodeList.getLength();
					if (level2NodeLength>0) {
						element = (Element) level2NodeList.item(0);
						level2NodeList = element.getElementsByTagName("bottom");																								
						queryPersonByPhotoResp.setPosBottom(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), 0));
						level2NodeList = element.getElementsByTagName("left");																
						queryPersonByPhotoResp.setPosLeft(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), 0));
						level2NodeList = element.getElementsByTagName("right");																
						queryPersonByPhotoResp.setPosRight(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), 0));
						level2NodeList = element.getElementsByTagName("top");																
						queryPersonByPhotoResp.setPosTop(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), 0));
					}	
					result.getQueryPersonByPhotoListResp().addResultList(queryPersonByPhotoResp);
				}															
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}			
		return result;
	}
	
	public static HWWSField extractQueryPersonByPhotoV0(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
//		<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
//		<response>
//		    <result>
//		        <errmsg></errmsg>
//		        <code>0</code>
//		    </result>
//		    <algorithmResults>
//		        <algorithmResult>
//		            <algorithmCode>0104000100</algorithmCode>
//		            <peoplefaceinfos>
//		                <peoplefaceinfo>
//		                    <cameraName>X1221-F</cameraName>
//		                    <cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>
//		                    <confidence>96</confidence>
//		                    <fileId>258d5f6fc0c549e480a824e1097dbbe0#2403785860353703936@10</fileId>
//		                    <pos>
//		                        <bottom>1063</bottom>
//		                        <left>1565</left>
//		                        <right>1774</right>
//		                        <top>857</top>
//		                    </pos>
//		                    <sourceDevice>1</sourceDevice>
//		                    <recordTime>1542880971727</recordTime>
//		                </peoplefaceinfo>
//		            </peoplefaceinfos>
//		            <result>
//		                <errmsg></errmsg>
//		                <code>0</code>
//		            </result>
//		            <total>1</total>
//		        </algorithmResult>
//		    </algorithmResults>
//		</response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 		
		String errCode = null;
		String errMsg = null;		
		Element element = null;
		NodeList level1NodeList = document.getElementsByTagName("result");
		NodeList level2NodeList = null;
		NodeList level4NodeList = null;		
		int level2NodeLength = 0;		
		int level4NodeLength = 0;
		if (level1NodeList.getLength()>0) {
			element = (Element) level1NodeList.item(0);
			level2NodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
			level2NodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
		}			
		HWWSField result = new HWWSField();
		QueryPersonByPhotoRespV1 queryPersonByPhotoResp;
		Date nowDate = new Date();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			level1NodeList = document.getElementsByTagName("algorithmResult");			
			//check algorithmResult
			//algorithm result alway have 1 coz we search with algorithmCode
			if (level1NodeList.getLength()>0) {
				//loop thought element under algorithmResult				
				element = (Element) level1NodeList.item(0);
				level2NodeList = element.getElementsByTagName("code");
				errCode = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
				//if error code under algorithnResult = 0
				if (ResultStatus.SUCCESS_CODE.equals(errCode)) {						
					level2NodeList = element.getElementsByTagName("algorithmCode");
//					result.getQueryPersonByPhotoListResp().setAlgorithmCode(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)));						
					level2NodeList = element.getElementsByTagName("total");
					result.getQueryPersonByPhotoListResp().setTotalRecord(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)), 0));
					level2NodeList = element.getElementsByTagName("peoplefaceinfo");
					level2NodeLength = level2NodeList.getLength();
					//loop thought element under peoplefaceinfo
					for (int level2=0; level2 < level2NodeLength; level2++) {
						element = (Element) level2NodeList.item(level2);																						
						queryPersonByPhotoResp = new QueryPersonByPhotoRespV1();
						level4NodeList = element.getElementsByTagName("cameraSn");																
						queryPersonByPhotoResp.setCameraSn(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)));
						level4NodeList = element.getElementsByTagName("confidence");																
						queryPersonByPhotoResp.setConfidenceLevel(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)), 0));
						level4NodeList = element.getElementsByTagName("fileId");																
						queryPersonByPhotoResp.setFileId(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)));								
						level4NodeList = element.getElementsByTagName("recordTime");																
						queryPersonByPhotoResp.setRecordTime(DateTimeUtil.epochToDate(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)), nowDate.getTime()));																																									
						level4NodeList = element.getElementsByTagName("pos");					
						level4NodeLength = level4NodeList.getLength();
						if (level4NodeLength>0) {
							element = (Element) level4NodeList.item(0);
							level4NodeList = element.getElementsByTagName("bottom");																								
							queryPersonByPhotoResp.setPosBottom(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)), 0));
							level4NodeList = element.getElementsByTagName("left");																
							queryPersonByPhotoResp.setPosLeft(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)), 0));
							level4NodeList = element.getElementsByTagName("right");																
							queryPersonByPhotoResp.setPosRight(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)), 0));
							level4NodeList = element.getElementsByTagName("top");																
							queryPersonByPhotoResp.setPosTop(StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) level4NodeList.item(0)), 0));
						}	
						result.getQueryPersonByPhotoListResp().addResultList(queryPersonByPhotoResp);												
					}						
				}																	
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}			
		return result;
	}
	public static HWWSField extractQueryAlgorithm(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
//		<response>
//		    <result>
//		        <errmsg>Success.</errmsg>
//		        <code>0</code>
//		    </result>
//		    <algorithms>
//		        <algorithm>
//		            <chlParam></chlParam>
//		            <code>0104000100</code>
//		            <cpu>3</cpu>
//		            <disk>2</disk>
//		            <initParam></initParam>
//		            <installDate>2018-10-09 20:49:58</installDate>
//		            <license></license>
//		            <mem>2</mem>
//		            <name>yitu_face_sdk</name>
//		            <status>enable</status>
//		            <supplier>yitu</supplier>
//		            <type>4</type>
//		            <version>1.0.0</version>
//		        </algorithm>
//		    </algorithms>
//		</response>
		Document document = HWXMLUtil.parseXmlFromString(wsResponse); 		
		String errCode = null;
		String errMsg = null;		
		Element element = null;
		NodeList level1NodeList = document.getElementsByTagName("result");
		NodeList level2NodeList = null;
		int level1NodeLength = 0;
		if (level1NodeList.getLength()>0) {
			element = (Element) level1NodeList.item(0);
			level2NodeList = element.getElementsByTagName("code");
			errCode = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
			level2NodeList = element.getElementsByTagName("errmsg");
			errMsg = HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0));
		}			
		HWWSField result = new HWWSField();
		result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			level1NodeList = document.getElementsByTagName("algorithm");
			level1NodeLength = level1NodeList.getLength();
			//check algorithmResult
			if (level1NodeLength>0) {
				//loop thought element under algorithmResult
				for (int level1=0; level1 < level1NodeLength; level1++) {
					element = (Element) level1NodeList.item(level1);
					level2NodeList = element.getElementsByTagName("code");
					result.addAlgorithmCodeList(HWXMLUtil.getCharacterDataFromElement((Element) level2NodeList.item(0)));					
				}															
			}else {
				result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
			}
		}			
		return result;
	}
	public static HWWSField extractRegisterCallBackSDK(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//same as logonSDK
		return HWXMLUtil.extractLogonSDK(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	public static HWWSField extractSubscribebAlarmSDK(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
		//same as logonSDK
		return HWXMLUtil.extractLogonSDK(transactionId, wsResponse, successCode, loginRequireCode, "");
	}
	private static ResultStatus getResultStatus(String errCode, String errMsg, String successCode, String loginRequireCode, String firstLogin, ResultStatus result) {
		if (successCode.equals(errCode)) {
			result.setStatusCode(ResultStatus.SUCCESS_CODE, null);
		}else if (loginRequireCode.equals(errCode)) {
			result.setStatusCode(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE, null);
		}else if (firstLogin.equals(errCode)) {
			result.setStatusCode(ResultStatus.HW_CHANGE_PWD_ERROR_CODE, null);
		}else {
			result.setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, errCode+":"+errMsg);
		}
		return result;
	}
	private static ResultStatus getResultStatusSDK(String errCode, String errMsg, String successCode, String loginRequireCode, String firstLogin, ResultStatus result) {
		if (successCode.equals(errCode)) {
			result.setStatusCode(ResultStatus.HW_WS_SDK_SUCCESS_CODE, null);
		}else if (loginRequireCode.equals(errCode)) {
			result.setStatusCode(ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE, null);
		}else if (firstLogin.equals(errCode)) {
			result.setStatusCode(ResultStatus.HW_SDK_CHANGE_PWD_ERROR_CODE, null);
		}else {
			result.setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, errCode+":"+errMsg);
		}		
		return result;
	}
	public static String getCharacterDataFromNodeList(NodeList nodeList, String tagName) {
		if (nodeList.getLength()>0) {
			Element element = (Element) nodeList.item(0);
			NodeList dataNodeList = element.getElementsByTagName(tagName);
			return HWXMLUtil.getCharacterDataFromElement((Element) dataNodeList.item(0));
		}else {
			return "";
		}
	}
	public static String getCharacterDataFromElement(Element e) {
		if (e==null) {
			return "";
		}
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	    	CharacterData cd = (CharacterData) child;
	    	return cd.getData();
	    }
	    return "";
	}
	
	public static HWWSField extractRemovePersonZK(String transactionId, Object classSource, String wsResponse, String successCode, String personNotFound) throws Exception{
		//{"code":0, "message": "success"}		
		HWWSField result = new HWWSField();
		ZKPersonInfo zkPersonInfo = null;
		ObjectMapper mapper = new ObjectMapper();
		int successCodeInteger = StringUtil.stringToInteger(successCode, -1);
		int personNotFoundInteger =  StringUtil.stringToInteger(personNotFound, -1);
 		try {			
 			zkPersonInfo = mapper.readValue(wsResponse, ZKPersonInfo.class);
 			if (!(zkPersonInfo.getCode()==successCodeInteger || zkPersonInfo.getCode()==personNotFoundInteger)) { 				
 				//UNEXPECTED_ERROR_CODE
 				result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, zkPersonInfo.getCode()+":"+zkPersonInfo.getMessage()); 				
 			}
 			result.setZkPersonInfo(zkPersonInfo);
 		}catch(Exception ex) {
 			Logger.error(classSource, LogUtil.getLogError(transactionId, "error while parser ZKPersonInfo(RemoveAPI):"+wsResponse, ex));
 			result.getResult().setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}		
		return result;
	}
	
	public static HWWSField extractAddPersonZK(String transactionId, Object classSource, String wsResponse, String successCode, String personNotFound) throws Exception{
		//{"code":0, "message": "success"}		
		HWWSField result = new HWWSField();
		ZKPersonInfo zkPersonInfo = null;
		ObjectMapper mapper = new ObjectMapper();
		int successCodeInteger = StringUtil.stringToInteger(successCode, -1);
		int personNotFoundInteger =  StringUtil.stringToInteger(personNotFound, -1);
 		try {			
 			zkPersonInfo = mapper.readValue(wsResponse, ZKPersonInfo.class);
 			if (!(zkPersonInfo.getCode()==successCodeInteger || zkPersonInfo.getCode()==personNotFoundInteger)) { 				
 				//UNEXPECTED_ERROR_CODE
 				result.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, zkPersonInfo.getCode()+":"+zkPersonInfo.getMessage()); 				
 			}
 			result.setZkPersonInfo(zkPersonInfo);
 		}catch(Exception ex) {
 			Logger.error(classSource, LogUtil.getLogError(transactionId, "error while parser ZKPersonInfo(RemoveAPI):"+wsResponse, ex));
 			result.getResult().setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}		
		return result;
	}
//	backup zone
//	public static HWWSField extractQueryCameraFromCheckPoint(String transactionId, String wsResponse, String successCode, String loginRequireCode) throws Exception{
//	// <response>
//	// <cameraList>
//	// <camera>
//	// <name>84100050000000000101</name>
//	// <sn> 84100050000000000101#00ca188d4048413791b7372e90e8ca36
//	// </sn>
//	// </camera>
//	// <camera>
//	// <name>84100060000000000101</name>
//	// <sn> 84100060000000000101#00ca188d4048413791b7372e90e8ca36
//	// </sn>
//	// </camera>
//	// <camera>
//	// <name>84100070000000000101</name>
//	// <sn> 84100070000000000101#00ca188d4048413791b7372e90e8ca36
//	// </sn>
//	// </camera>
//	// </cameraList>
//	// <result>
//	// <errmsg/>
//	// <code>0</code>
//	// </result>
//	// <total>3</total>
//	// </response>
//	Document document = HWXMLUtil.parseXmlFromString(wsResponse); 
//	NodeList nodeList = document.getElementsByTagName("result");
//	String errCode = null;
//	String errMsg = null;		
//	int totalNameList = 0;
//	HWWSField result = new HWWSField();
//	if (nodeList.getLength()>0) {
//		Element element = (Element) nodeList.item(0);
//		NodeList codeNodeList = element.getElementsByTagName("code");
//		errCode = HWXMLUtil.getCharacterDataFromElement((Element) codeNodeList.item(0));
//		NodeList errMsgNodeList = element.getElementsByTagName("errmsg");
//		errMsg = HWXMLUtil.getCharacterDataFromElement((Element) errMsgNodeList.item(0));
//	}
//	result.setResult(HWXMLUtil.getResultStatus(errCode, errMsg, successCode, loginRequireCode,result.getResult()));
//	if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
//		nodeList = document.getElementsByTagName("total");
//		NodeList nodeListTmp = null;
//		if (nodeList.getLength()>0) {
//			totalNameList = StringUtil.stringToInteger(HWXMLUtil.getCharacterDataFromElement((Element) nodeList.item(0)),0);				
//		}
//		if (totalNameList>0) {
//			nodeList = document.getElementsByTagName("camera");
//			String cameraName = null;
//			String cameraSN = null;
//			String cameraIP = null;
//			for (int index=0; index<nodeList.getLength()-1;index++) {					
//				Element element = (Element) nodeList.item(index);
//				nodeListTmp = element.getElementsByTagName("name");
//				cameraName = HWXMLUtil.getCharacterDataFromElement((Element) nodeListTmp.item(0));
//				nodeListTmp = element.getElementsByTagName("sn");
//				cameraSN = HWXMLUtil.getCharacterDataFromElement((Element) nodeListTmp.item(0));
//				nodeListTmp = element.getElementsByTagName("stream-url");
//				cameraIP = HWXMLUtil.getCharacterDataFromElement((Element) nodeListTmp.item(0));
//				result.addQueryCameraRespList(new QueryCameraResp(cameraName, cameraSN, cameraIP, null));
//			}
//		}else {
//			result.getResult().setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, null);
//		}
//	}		
//	return result;
//}
}
