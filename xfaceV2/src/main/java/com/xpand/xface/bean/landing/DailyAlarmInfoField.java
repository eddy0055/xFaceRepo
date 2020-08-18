package com.xpand.xface.bean.landing;

import com.xpand.xface.util.StringUtil;

public class DailyAlarmInfoField {

	private String alarmId;
	private String ipcName;
	private String alarmDate;	
	
	//alarmInfoList
	private int noOfAlarmOnLine;
	private int noOfAlarmOffLine;
	private int noOfAlarmList;
	
	public DailyAlarmInfoField(Object[] columns) {
		this.alarmId = ""+columns[0];
		this.ipcName = ""+columns[1];		
		this.alarmDate = ""+columns[2];
		this.noOfAlarmOnLine = StringUtil.stringToInteger(""+columns[3], 0);
		this.noOfAlarmOffLine = StringUtil.stringToInteger(""+columns[4],0);
		this.noOfAlarmList = StringUtil.stringToInteger(""+columns[5],0);
	}
	public int getNoOfAlarmOnLine() {
		return noOfAlarmOnLine;
	}
	public void setNoOfAlarmOnLine(int noOfAlarmOnLine) {
		this.noOfAlarmOnLine = noOfAlarmOnLine;
	}
	public int getNoOfAlarmOffLine() {
		return noOfAlarmOffLine;
	}
	public void setNoOfAlarmOffLine(int noOfAlarmOffLine) {
		this.noOfAlarmOffLine = noOfAlarmOffLine;
	}
	public int getNoOfAlarmList() {
		return noOfAlarmList;
	}
	public void setNoOfAlarmList(int noOfAlarmList) {
		this.noOfAlarmList = noOfAlarmList;
	}
	
	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getIpcName() {
		return ipcName;
	}

	public void setIpcName(String ipcName) {
		this.ipcName = ipcName;
	}

	public String getAlarmTime() {
		return alarmDate;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmDate = alarmTime;
	}
	

}
