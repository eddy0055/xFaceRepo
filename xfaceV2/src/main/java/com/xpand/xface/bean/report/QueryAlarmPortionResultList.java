package com.xpand.xface.bean.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;

public class QueryAlarmPortionResultList {
	private ResultStatus result;
	private List<QueryAlarmResultList> queryAlarmResultList;
	private Date maximumStartDate; 
	private Date maximumEndDate; 
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public List<QueryAlarmResultList> getQueryAlarmResultList() {
		if (this.queryAlarmResultList==null) {
			this.queryAlarmResultList = new ArrayList<>();
		}
		return queryAlarmResultList;
	}
	public void setQueryAlarmResultList(List<QueryAlarmResultList> queryAlarmResultList) {
		this.queryAlarmResultList = queryAlarmResultList;
	}
	public Date getMaximumEndDate() {
		return maximumEndDate;
	}
	public void setMaximumEndDate(Date maximumEndDate) {
		this.maximumEndDate = maximumEndDate;
	}
	public Date getMaximumStartDate() {
		return maximumStartDate;
	}
	public void setMaximumStartDate(Date maximumStartDate) {
		this.maximumStartDate = maximumStartDate;
	}
	
}
