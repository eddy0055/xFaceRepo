package com.xpand.xface.bean.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;

public class QueryAlarmResultList {
	private ResultStatus result;
	private List<QueryAlarmResult> queryAlarmResultList;
	private Date startDate;
	private Date endDate;
	private String startDateDisplay;
	private String endDateDisplay;
	private long totalRecord;
	private int currentPage;
	private int maximumPage;
	private int pageSize;
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public List<QueryAlarmResult> getQueryAlarmResultList() {
		if (this.queryAlarmResultList==null) {
			this.queryAlarmResultList = new ArrayList<>();
		}
		return queryAlarmResultList;
	}
	public void setQueryAlarmResultList(List<QueryAlarmResult> queryAlarmResultList) {
		this.queryAlarmResultList = queryAlarmResultList;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getStartDateDisplay() {
		return startDateDisplay;
	}
	public void setStartDateDisplay(String startDateDisplay) {
		this.startDateDisplay = startDateDisplay;
	}
	public String getEndDateDisplay() {
		return endDateDisplay;
	}
	public void setEndDateDisplay(String endDateDisplay) {
		this.endDateDisplay = endDateDisplay;
	}
	public long getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getMaximumPage() {
		return maximumPage;
	}
	public void setMaximumPage(int maximumPage) {
		this.maximumPage = maximumPage;
	}
	
}
