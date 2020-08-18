package com.xpand.xface.service;

import java.util.concurrent.ConcurrentHashMap;

import com.xpand.xface.bean.QueueContent;
import com.xpand.xface.bean.WebSocketHolder;

public interface GlobalVarService {
	public String getJSessionId(String vcmName);
	public void setJSessionId(String vcmName, String jSessionId);
	
	public WebSocketHolder getWebSocketHolder(String userName);
	//last alarm notification for a person date time
	public Long getLastAlarmPersonDateTime(Integer personId);
	public void addLastAlarmPersonDateTime(Integer personId, long alarmTime);
	public int getSizeOfLastAlarmPersonDateTime();
	public void clearLastAlarmPersonDateTime();
	///last action notification
	public Long getLastSendAction(String actionClass, String threadName);
	public void addLastSendAction(String actionClass, String threadName, long actionTime);
	public int getSizeOfLastSendAction();
	public void clearLastSendAction();
	////////
	public void addWebSocketHolder(WebSocketHolder webSocketHolder);
	public void removeWebSocketHolder(WebSocketHolder webSocketHolder);
	public void removeWebSocketHolder(String userName);
	public int getSizeOfSession();
	public ConcurrentHashMap<String, WebSocketHolder> getWebSocketHolderList();
	public void pushContent(int contentType, String content);
	public QueueContent popContent();
	public void closeAllWebSocket();
	public int getSizeOfAlarmQueue();
}