package com.xpand.xface.bean;

public class QueryCheckPointResp {
	private String checkPointName;
	private String checkPointSN;
	public QueryCheckPointResp(String checkPointName, String checkPointSN) {
		this.checkPointName = checkPointName;
		this.checkPointSN = checkPointSN;
	}
	public String getCheckPointName() {
		return checkPointName;
	}
	public void setCheckPointName(String checkPointName) {
		this.checkPointName = checkPointName;
	}
	public String getCheckPointSN() {
		return checkPointSN;
	}
	public void setCheckPointSN(String checkPointSN) {
		this.checkPointSN = checkPointSN;
	}
	
}
