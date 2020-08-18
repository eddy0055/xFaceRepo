package com.xpand.xface.service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.PassengerBoatActivity;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.bean.queue.QueueCustomerRegister;
import com.xpand.xface.bean.queue.QueueGateActivity;
import com.xpand.xface.bean.queue.QueueHWAlarm;
import com.xpand.xface.entity.PersonInfo;

public interface GlobalVarService {
	public String getJSessionId(String vcmName);
	public void setJSessionId(String vcmName, String jSessionId);
	
	public WebSocketHolder getWebSocketHolder(String userNameNSessionId);
	public WebSocketHolder getWebSocketHolderMatchUserName(String userName);
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
	public void removeWebSocketHolder(String userNameNSessionId);	
	public int getSizeOfSession();
	public ConcurrentHashMap<String, WebSocketHolder> getWebSocketHolderList();
	public void pushAlarm(String alarmContent);
	public void pushGateActivity(PassengerBoatActivity passengerBoatActivity);
	public void pushCustomerRegister(CustomerRegister customerRegister, PersonInfo existingPersonInfo);
	
	public QueueHWAlarm popAlarm();
	public QueueGateActivity popGateActivity();
	public QueueCustomerRegister popCustomerRegister();
	
	public void closeAllWebSocket();
	public int getSizeOfAlarmQueue();
	public int getSizeOfGateActivityQueue();
	public int getSizeOfCustomerRegisterQueue();
	public ArrayList<Integer> getGridJumpPage();
}