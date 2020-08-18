package com.xpand.xface.bean.report.google;

import com.xpand.xface.util.StringUtil;

public class PassengerRegisterField {
	private String nationality;
	private Integer noOfRegistered;
	private Integer noOfVisited;
	public PassengerRegisterField(Object[] columns) {
		//nationality, noofvisited, noofregister
		this.nationality = ""+columns[0];
		this.noOfVisited = StringUtil.stringToInteger(""+columns[1], 0);
		this.noOfRegistered = StringUtil.stringToInteger(""+columns[2], 0);
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public Integer getNoOfRegistered() {
		return noOfRegistered;
	}
	public void setNoOfRegistered(Integer noOfRegistered) {
		this.noOfRegistered = noOfRegistered;
	}
	public Integer getNoOfVisited() {
		return noOfVisited;
	}
	public void setNoOfVisited(Integer noOfVisited) {
		this.noOfVisited = noOfVisited;
	}	
}
