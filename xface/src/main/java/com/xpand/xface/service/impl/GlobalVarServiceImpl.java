package com.xpand.xface.service.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.xpand.xface.bean.QueueContent;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.service.GlobalVarService;

@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class GlobalVarServiceImpl implements GlobalVarService {
	private static final String PREFIX_KEY_PERSON_ALARM = "PERSON";
	private ConcurrentHashMap<String, WebSocketHolder> sessionList;
	private ConcurrentHashMap<String, Long> lastAlarmPersonDateTimeList;
	private ConcurrentHashMap<String, Long> lastSendAction;
//	private String jSessionId;
	private ConcurrentHashMap<String, String> jSessionList;
	
	private Queue<QueueContent> queueContentList;
	@Override
	public String getJSessionId(String vcmName) {
		if (this.jSessionList==null) {
			return null;
		}else {
			return this.jSessionList.get(vcmName);
		}		
	}
	@Override
	public void setJSessionId(String vcmName, String jSessionId) {
		if (this.jSessionList==null) {
			this.jSessionList = new ConcurrentHashMap<>();
		}
		this.jSessionList.put(vcmName, jSessionId);				
	}
	
	@Override
	public WebSocketHolder getWebSocketHolder(String userName) {
		if (this.sessionList==null) {
			return null;
		}else {
			return this.sessionList.get(userName);
		}
	}
	@Override
	public synchronized void addWebSocketHolder(WebSocketHolder webSocketHolder) {
		if (this.sessionList==null) {
			this.sessionList = new ConcurrentHashMap<String, WebSocketHolder>();
		}
		this.sessionList.put(webSocketHolder.getUserName(), webSocketHolder);		
	}
	@Override
	public synchronized void removeWebSocketHolder(WebSocketHolder webSocketHolder) {
		this.removeWebSocketHolder(webSocketHolder.getUserName());				
	}
	@Override
	public synchronized void removeWebSocketHolder(String userName) {
		if (this.sessionList!=null) {
			this.sessionList.remove(userName);
		}	
	}
	@Override
	public int getSizeOfSession() {
		if (this.sessionList==null) {
			return 0;
		}else {
			return this.sessionList.size();
		}		
	}
	@Override
	public ConcurrentHashMap<String, WebSocketHolder> getWebSocketHolderList() {
		return this.sessionList;
	}
	@Override
	public synchronized void pushContent(int contentType, String content) {		
		if (this.queueContentList == null) {
			this.queueContentList = new ConcurrentLinkedQueue<QueueContent>();
		}		 
		this.queueContentList.add(new QueueContent(contentType, content));		
	}
	@Override
	public synchronized QueueContent popContent() {		
		if (this.queueContentList == null || this.queueContentList.isEmpty()) {
			return null;
		}else {		
			return this.queueContentList.poll();
		}			
	}
	@Override
	public void closeAllWebSocket() {
		if (this.sessionList==null) {
			return;
		}	
		WebSocketHolder wsh;
		for (String userName:this.sessionList.keySet()) {
			wsh = this.getWebSocketHolder(userName);
			if (wsh!=null) {
				wsh.closeSocket();
				this.removeWebSocketHolder(userName);
			}			
		}
	}
	@Override
	public Long getLastAlarmPersonDateTime(Integer personId) {
		if (this.lastAlarmPersonDateTimeList==null) {
			return null;
		}else {
			return this.lastAlarmPersonDateTimeList.get(GlobalVarServiceImpl.PREFIX_KEY_PERSON_ALARM+personId);
		}		
	}
	@Override
	public void addLastAlarmPersonDateTime(Integer personId, long alarmTime) {
		if (this.lastAlarmPersonDateTimeList==null) {
			this.lastAlarmPersonDateTimeList = new ConcurrentHashMap<String, Long>();
		}
		this.lastAlarmPersonDateTimeList.put(GlobalVarServiceImpl.PREFIX_KEY_PERSON_ALARM+personId, alarmTime);
	}
	@Override
	public int getSizeOfAlarmQueue() {
		if (this.queueContentList==null) {
			return 0;
		}else {
			return this.queueContentList.size();
		}		
	}
	@Override
	public void clearLastAlarmPersonDateTime() {
		if (this.lastAlarmPersonDateTimeList!=null) {
			this.lastAlarmPersonDateTimeList.clear();
		}		
	}
	@Override
	public int getSizeOfLastAlarmPersonDateTime() {
		if (this.lastAlarmPersonDateTimeList==null) {
			return 0;
		}else {
			return this.lastAlarmPersonDateTimeList.size();
		}
	}
	@Override
	public Long getLastSendAction(String actionClass, String threadName) {
		if (this.lastSendAction==null) {
			return null;
		}else {
			return this.lastSendAction.get(actionClass+threadName);
		}
	}
	@Override
	public void addLastSendAction(String actionClass, String threadName, long actionTime) {
		if (this.lastSendAction==null) {
			this.lastSendAction = new ConcurrentHashMap<String, Long>();
		}
		this.lastSendAction.put(actionClass+threadName, actionTime);
	}
	@Override
	public int getSizeOfLastSendAction() {
		if (this.lastSendAction==null) {
			return 0;
		}else {
			return this.lastSendAction.size();
		}	
	}
	@Override
	public void clearLastSendAction() {
		if (this.queueContentList!=null) {
			this.lastSendAction.clear();
		}	
		
	}
}