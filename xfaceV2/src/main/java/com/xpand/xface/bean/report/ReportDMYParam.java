package com.xpand.xface.bean.report;

import java.util.Date;

import com.xpand.xface.util.StringUtil;

public class ReportDMYParam {
	public Date startDate;
	public Date endDate;
	public String dateFormat;
	public ReportDMYParam(Date startDate, Date endDate, String dateFormat) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.dateFormat = dateFormat;
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
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	@Override
	public String toString() {
		return "ReportDMYParam [startDate=" + StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) 
			+ ", endDate=" + StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) + ", dateFormat=" + dateFormat + "]";
	}

}
