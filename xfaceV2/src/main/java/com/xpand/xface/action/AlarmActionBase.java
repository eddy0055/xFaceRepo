package com.xpand.xface.action;

public interface AlarmActionBase {
	public void doAction(String transactionId, String alarmData);
	public String getActionName();
}
