package com.xpand.xface.bean.report.google;

import com.xpand.xface.util.StringUtil;

public class DailyFaceChartField {
	private String fieldLabel;
	private Integer noOfMatch;
	private Integer noOfUnMatch;
	private String fieldGroup;
	public DailyFaceChartField(Object[] columns) {
		//label, noofmatch, noofunmatch
		this.fieldLabel = ""+columns[0];
		this.noOfMatch = StringUtil.stringToInteger(""+columns[1], 0);
		this.noOfUnMatch = StringUtil.stringToInteger(""+columns[2], 0);
		if (columns.length>3) {
			this.fieldGroup = ""+columns[3];
		}
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	public Integer getNoOfMatch() {
		return noOfMatch;
	}
	public void setNoOfMatch(Integer noOfMatch) {
		this.noOfMatch = noOfMatch;
	}
	public Integer getNoOfUnMatch() {
		return noOfUnMatch;
	}
	public void setNoOfUnMatch(Integer noOfUnMatch) {
		this.noOfUnMatch = noOfUnMatch;
	}
	public String getFieldGroup() {
		return fieldGroup;
	}
	public void setFieldGroup(String fieldGroup) {
		this.fieldGroup = fieldGroup;
	}
	
		
}
