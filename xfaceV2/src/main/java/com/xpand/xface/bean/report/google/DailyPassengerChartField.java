package com.xpand.xface.bean.report.google;

import com.xpand.xface.util.StringUtil;

public class DailyPassengerChartField {	
	private String fieldLabel;	
	private Integer eqdirectionId;
	private Integer noOfPassenger;
	private String fieldGroup;
	public DailyPassengerChartField(Object[] columns) {
		this.fieldLabel = ""+columns[0];
		this.eqdirectionId = StringUtil.stringToInteger(""+columns[1], 0);
		this.noOfPassenger = StringUtil.stringToInteger(""+columns[2], 0);
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
	public Integer getEqdirectionId() {
		return eqdirectionId;
	}
	public void setEqdirectionId(Integer eqdirectionId) {
		this.eqdirectionId = eqdirectionId;
	}
	public Integer getNoOfPassenger() {
		return noOfPassenger;
	}
	public void setNoOfPassenger(Integer noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}
	public String getFieldGroup() {
		return fieldGroup;
	}
	public void setFieldGroup(String fieldGroup) {
		this.fieldGroup = fieldGroup;
	}				
}
