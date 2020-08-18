package com.xpand.xface.bean.landing;

import com.xpand.xface.util.StringUtil;

public class DailyGateInfoField {	
	private String gateCode;
	private String gateName;
	private Integer direction;
	private Integer noOfPassenger;
	private Integer noOfIN;
	private Integer noOfOut;	
	public DailyGateInfoField() {
		
	}
	public DailyGateInfoField(Object[] columns) {
		this.gateCode = ""+columns[0];
		this.gateName = ""+columns[1];		
		this.direction = StringUtil.stringToInteger(""+columns[2], 0);		
		this.noOfPassenger = StringUtil.stringToInteger(""+columns[3], 0);
	}
	public String getGateCode() {
		return gateCode;
	}
	public void setGateCode(String gateCode) {
		this.gateCode = gateCode;
	}
	public String getGateName() {
		return gateName;
	}
	public void setGateName(String gateName) {
		this.gateName = gateName;
	}
	public Integer getNoOfIN() {
		return noOfIN;
	}
	public void setNoOfIN(Integer noOfIN) {
		this.noOfIN = noOfIN;
	}
	public Integer getNoOfOut() {
		return noOfOut;
	}
	public void setNoOfOut(Integer noOfOut) {
		this.noOfOut = noOfOut;
	}
	public Integer getDirection() {
		return direction;
	}
	public void setDirection(Integer direction) {
		this.direction = direction;
	}
	public Integer getNoOfPassenger() {
		return noOfPassenger;
	}
	public void setNoOfPassenger(Integer noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}					
}
