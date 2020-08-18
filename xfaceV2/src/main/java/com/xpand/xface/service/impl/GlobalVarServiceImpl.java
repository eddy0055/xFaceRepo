package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.PassengerBoatActivity;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.bean.queue.QueueCustomerRegister;
import com.xpand.xface.bean.queue.QueueGateActivity;
import com.xpand.xface.bean.queue.QueueHWAlarm;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.util.StringUtil;

@ApplicationScope
@Component
public class GlobalVarServiceImpl implements GlobalVarService , Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	XFaceBatchService xFaceBatchService;
	
	private static final String PREFIX_KEY_PERSON_ALARM = "PERSON";
	private ConcurrentHashMap<String, WebSocketHolder> sessionList;
	private ConcurrentHashMap<String, Long> lastAlarmPersonDateTimeList;
	private ConcurrentHashMap<String, Long> lastSendAction;
	private ConcurrentHashMap<String, String> jSessionList;	
	private Queue<QueueHWAlarm> queueVCMAlarmList;
	private Queue<QueueGateActivity> queueGateActivityList;
	private Queue<QueueCustomerRegister> queueCustomerRegisterList;
	
	
	private ArrayList<Integer> gridJumpPage=null;
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
	public WebSocketHolder getWebSocketHolder(String userNameNSessionId) {
		if (this.sessionList==null) {
			return null;
		}else {
			return this.sessionList.get(userNameNSessionId);
		}
	}
	@Override
	public WebSocketHolder getWebSocketHolderMatchUserName(String userName) {
		if (this.sessionList==null) {
			return null;
		}else {
			WebSocketHolder wsh;
			for (String userNameNSessionId:this.sessionList.keySet()) {
				wsh = this.getWebSocketHolder(userNameNSessionId);
				if (wsh!=null && wsh.getUserName().equals(userName)) {
					return wsh;
				}			
			}
			return null;
		}
	}
	@Override
	public synchronized void addWebSocketHolder(WebSocketHolder webSocketHolder) {
		if (this.sessionList==null) {
			this.sessionList = new ConcurrentHashMap<String, WebSocketHolder>();
		}
		this.sessionList.put(webSocketHolder.getUserName()+webSocketHolder.getSessionId(), webSocketHolder);		
	}
	@Override
	public synchronized void removeWebSocketHolder(WebSocketHolder webSocketHolder) {
		this.removeWebSocketHolder(webSocketHolder.getUserName()+webSocketHolder.getSessionId());
	}
	@Override
	public synchronized void removeWebSocketHolder(String userNameNSessionId) {
		if (this.sessionList!=null) {
			this.sessionList.remove(userNameNSessionId);
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
		if (this.sessionList==null) {
			this.sessionList = new ConcurrentHashMap<>();
		}
		return this.sessionList;
	}
	@Override
	public synchronized void pushAlarm(String content) {		
		if (this.queueVCMAlarmList == null) {
			this.queueVCMAlarmList = new ConcurrentLinkedQueue<QueueHWAlarm>();
		}		 
		this.queueVCMAlarmList.add(new QueueHWAlarm(content));			
	}
	@Override
	public synchronized QueueHWAlarm popAlarm() {		
		if (this.queueVCMAlarmList == null || this.queueVCMAlarmList.isEmpty()) {
			return null;
		}else {		
			return this.queueVCMAlarmList.poll();
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
				//wsh.closeSocket();
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
		if (this.queueVCMAlarmList==null) {
			return 0;
		}else {
			return this.queueVCMAlarmList.size();
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
		if (this.lastSendAction!=null) {
			this.lastSendAction.clear();
		}	
		
	}	
	@Override
	public ArrayList<Integer> getGridJumpPage() {
		// TODO Auto-generated method stub
		try {
			if (this.gridJumpPage==null) {
				this.gridJumpPage = new ArrayList<>();
				String arrayValue[] =  this.xFaceBatchService.findACByAppKey("globalVarServiceImpl.transactionId", ApplicationCfg.KEY_GUI_GRID_PAGE_JUMP).getAppValue1().split(",");
				for (int i=0; i<arrayValue.length; i++) {
					this.gridJumpPage.add(StringUtil.stringToInteger(arrayValue[i],20));
				}
			}
		}catch (Exception ex) {
			//if error add default
			this.gridJumpPage = new ArrayList<>();
			this.gridJumpPage.add(10);
			this.gridJumpPage.add(20);
			this.gridJumpPage.add(30);
		}			
		return this.gridJumpPage;
	}
	
	@Override
	public synchronized void pushGateActivity(PassengerBoatActivity pushGateActivity) {		
		if (this.queueGateActivityList == null) {
			this.queueGateActivityList = new ConcurrentLinkedQueue<QueueGateActivity>();
		}		 
		this.queueGateActivityList.add(new QueueGateActivity(pushGateActivity));			
	}
	@Override
	public synchronized QueueGateActivity popGateActivity() {		
		if (this.queueGateActivityList == null || this.queueGateActivityList.isEmpty()) {
			return null;
		}else {		
			return this.queueGateActivityList.poll();
		}			
	}
	@Override
	public synchronized void pushCustomerRegister(CustomerRegister customerRegister, PersonInfo existingPersonInfo) {		
		if (this.queueCustomerRegisterList == null) {
			this.queueCustomerRegisterList = new ConcurrentLinkedQueue<QueueCustomerRegister>();
		}		 
		this.queueCustomerRegisterList.add(new QueueCustomerRegister(customerRegister, existingPersonInfo));			
	}
	@Override
	public synchronized QueueCustomerRegister popCustomerRegister() {		
		if (this.queueCustomerRegisterList == null || this.queueCustomerRegisterList.isEmpty()) {
			return null;
		}else {		
			return this.queueCustomerRegisterList.poll();
		}			
	}
	@Override
	public int getSizeOfGateActivityQueue() {
		if (this.queueGateActivityList==null) {
			return 0;
		}else {
			return this.queueGateActivityList.size();
		}		
	}
	@Override
	public int getSizeOfCustomerRegisterQueue() {
		if (this.queueCustomerRegisterList==null) {
			return 0;
		}else {
			return this.queueCustomerRegisterList.size();
		}		
	}
		
//	@Override
//	public void forceRemoveWebSocketHolder(String userName) {
//		//when close wsh we have to force client to close web socket
//		WebSocketHolder wsh = this.getWebSocketHolder(userName);
//		if (wsh!=null) {
//			try {
//				this.msgTemplate.convertAndSend(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+userName+wsh.getSessionId()+"/"+wsh.getWebSocketModule(), ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
//			}catch (Exception ex) {}			
//			this.removeWebSocketHolder(userName);
//		}		
//	} 	
}