package com.xpand.xface.bean.landing;

import java.util.ArrayList;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;

public class DailyAlarmInfoList {
	private List<DailyAlarmInfoField> alarmInfoList;
	private ResultStatus result;
	public DailyAlarmInfoList() {
		// TODO Auto-generated constructor stub
	}
	public List<DailyAlarmInfoField> getAlarmInfoList() {
		if (this.alarmInfoList==null) {
			this.alarmInfoList = new ArrayList<DailyAlarmInfoField>();
		}
		return alarmInfoList;
	}
	public void setAlarmInfoList(List<DailyAlarmInfoField> alarmInfoList) {		
		this.alarmInfoList = alarmInfoList;
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public void addList(DailyAlarmInfoField alarmInfoField) {
		this.getAlarmInfoList();
		this.alarmInfoList.add(alarmInfoField);
	}

}
