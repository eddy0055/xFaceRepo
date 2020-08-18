package com.xpand.xface.bean;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.BooleanUtil;
import com.xpand.xface.util.HWXMLUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

public class AlarmActionList {
	private List<LoadAlarmActionResult> alarmActionList;
	private ApplicationCfg appCfg;
	private String transactionId;
	private String threadName;
	public AlarmActionList(String transactionId, ApplicationCfg appCfg, String threadName) {
		this.appCfg = appCfg;
		this.transactionId = transactionId;
		this.threadName = threadName;
		this.loadPlugin();
	}
	public void doAction(String personNotification, GlobalVarService globalVarService) {		
		if (this.alarmActionList==null) {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no alarm action need to send"));
			return;
		}else {
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send "+this.alarmActionList.size()+" alarm action"));
		}
		Long actionTime = null;
		boolean allowAction = false;
		long nowDate = (new Date()).getTime();
		try {
			for (LoadAlarmActionResult alarmAction:this.alarmActionList) {
				actionTime = globalVarService.getLastSendAction(alarmAction.getClassName(), alarmAction.getThreadName());
				if (actionTime==null || actionTime==0) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "this is first time for send action:"+alarmAction.getClassName()+" thread:"+alarmAction.getThreadName()));
					allowAction = true;
				}else if ((nowDate - actionTime)>alarmAction.getTimeNotSendActionAfterSendPerviouseAction()){
					allowAction = true;
				}else {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "not send action:"+alarmAction.getClassName()+" thread:"+alarmAction.getThreadName()
					+" coz last send:"+StringUtil.dateToString(actionTime, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)
					+" current:"+StringUtil.dateToString(nowDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)
					+" diff time config:"+alarmAction.getTimeNotSendActionAfterSendPerviouseAction()
					+" diff:"+(nowDate - actionTime)));
				}
				if (allowAction) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send alarm action "+alarmAction.getActionName()));
					//alarmAction.getAlarmAction().doAction(this.transactionId, personNotification);
					alarmAction.getAlarmActionMethod().invoke(alarmAction.getAlarmActionInstance(), this.transactionId, personNotification);
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send alarm action "+alarmAction.getActionName()+" success"));
					//reset 
					allowAction = false;
					globalVarService.addLastSendAction(alarmAction.getClassName(), alarmAction.getThreadName(), nowDate);
				}			
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}		
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "send "+this.alarmActionList.size()+" alarm action success"));
	}
	private void loadPlugin() {
		//<plugin>
		//<jarEntry>
		//<fileName>xxxxx</fileName>
		//<classEntry>
		//<className>xxx.yyy.MM1</className>
		//<className>xxx.yyy.MM2</className>
		//</classEntry>
		//</jarEntry>
		//</plugin>
		//read xml
		//path+deliminator+xml file
		String alarmPluginXML = this.appCfg.getAppValue1()+this.appCfg.getAppValue2()+this.appCfg.getAppValue3();
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load plugin from "+alarmPluginXML));
		File fXmlFile = new File(this.appCfg.getAppValue1()+this.appCfg.getAppValue2()+this.appCfg.getAppValue3());
		if (!fXmlFile.exists()) {
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "fail to load plugin from "+alarmPluginXML+" coz file didn't exist."));
			return;
		}
		DocumentBuilderFactory dbFactory = null;
		DocumentBuilder dBuilder = null;
		Document document = null;				
		NodeList jarList = null;
		NodeList classEntry = null;
		NodeList nodeListTmp = null; 
		String jarFileName = null;		
		String className = null;
		//this.alarmActionList = new ArrayList<AlarmActionBase>();
		LoadAlarmActionResult result = null;		
		Element element = null;
		int timeNotSendAction=0;
		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(fXmlFile);				
			jarList = document.getElementsByTagName("jarEntry");
			for (int i=0; i<jarList.getLength(); i++) {
				if (BooleanUtil.stringToBoolean(jarList.item(i).getAttributes().getNamedItem("enable"), true)) {
					element = (Element) jarList.item(i);
					nodeListTmp = element.getElementsByTagName("fileName");
					jarFileName = this.appCfg.getAppValue1()+this.appCfg.getAppValue2()+ HWXMLUtil.getCharacterDataFromElement((Element) nodeListTmp.item(0));
					classEntry = element.getElementsByTagName("classEntry");
					for (int j=0;j<classEntry.getLength(); j++) {
						if (BooleanUtil.stringToBoolean(classEntry.item(j).getAttributes().getNamedItem("enable"), true)) {
							timeNotSendAction = StringUtil.stringToInteger(classEntry.item(j).getAttributes().getNamedItem("timeNotSendActionAfterSendPerviouseAction"),0);
							element = (Element) classEntry.item(j);
							nodeListTmp = element.getElementsByTagName("className");
							className = HWXMLUtil.getCharacterDataFromElement((Element) nodeListTmp.item(0));										
							Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load jarFile:"+jarFileName+" className:"+className));
							result = this.loadAlarmAction(transactionId, jarFileName, className);							
							if (result.getResult().getStatusCode()==ResultStatus.SUCCESS_CODE) {
								if (this.alarmActionList==null) {
									this.alarmActionList = new ArrayList<LoadAlarmActionResult>();
								}
								result.setTimeNotSendActionAfterSendPerviouseAction(timeNotSendAction);
								result.setClassName(className);
								result.setThreadName(this.threadName);
								this.alarmActionList.add(result);						
								Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load jarFile:"+jarFileName+" className:"+className+" success"));
							}else {
								Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load jarFile:"+jarFileName+" className:"+className+" fail coz:"+result.getResult().toString()));						
							}
						}else {
							Logger.info(this, LogUtil.getLogInfo(this.transactionId, "className under jarFile:"+jarFileName+" disable"));
						}
					}
				}else {
					Logger.info(this, LogUtil.getLogInfo(this.transactionId, "jarEntry is disable"));
				}
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while loading alarm action plugin: "+alarmPluginXML, ex));
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "load plugin from "+alarmPluginXML+" complete with "+(this.alarmActionList==null?0:this.alarmActionList.size())+" alarm action"));
	}
	public void unLoadPlugin() {
		String message = "unload plugin with "+(this.alarmActionList==null?0:this.alarmActionList.size())+" alarm action";
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, message));
		if (this.alarmActionList!=null) {
			for (LoadAlarmActionResult alarmAction:this.alarmActionList) {
				try {
					Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unload "+alarmAction.getActionName()));
					alarmAction.getUrlClassLoader().close();
					Logger.info(this, LogUtil.getLogInfo(this.transactionId, "unload "+alarmAction.getActionName()+" success"));
				}catch(Exception ex) {
					ex.printStackTrace();
				}				
			}
		}
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, message+" success."));
	}
	private LoadAlarmActionResult loadAlarmAction(String transactionId, String pathJar, String className) {
//		call by actionMethod.invoke(this.actionInstance, this.transactionId, StringUtil.getJson(personNotification, PersonNotification.class));
		LoadAlarmActionResult loadResult = new LoadAlarmActionResult();
		try {		
			File file = new File(pathJar);		
			if (!file.exists()) {
				loadResult.getResult().setStatusCode(ResultStatus.ALARM_ACTION_JAR_NOTFOUND_ERROR_CODE, pathJar);
				return loadResult;
			}
			URL[] urls = new URL[] {file.toURL()};
			URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());			
			Class classToLoad = Class.forName(className, true, child);
			Method actionMethod = classToLoad.getDeclaredMethod("getActionName");
			Object actionInstance = classToLoad.newInstance();
			loadResult.setActionName(actionMethod.invoke(actionInstance).toString());
			actionMethod = classToLoad.getDeclaredMethod("doAction", String.class, String.class);
			actionInstance = classToLoad.newInstance();
			loadResult.setAlarmActionMethod(actionMethod);
			loadResult.setAlarmActionInstance(actionInstance);
			loadResult.setUrlClassLoader(child);
		} catch (ClassNotFoundException ex) {
			loadResult.getResult().setStatusCode(ResultStatus.ALARM_ACTION_CLASS_NOTFOUND_ERROR_CODE, className);
		} catch (Exception ex) {
			loadResult.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, "jar:"+pathJar+",class:"+className+",ex:"+ex.toString());
		}
		return loadResult;
	}
}
