package com.xpand.xface.bean;

public class QuerySuspectTaskResp {
	private String suspectName;
	private String suspectId;
	private String suspectType;
	public QuerySuspectTaskResp(String suspectName, String suspectId, String suspectType) {
		this.suspectId = suspectId;
		this.suspectName = suspectName;
		this.suspectType = suspectType;
	}
	public String getSuspectName() {
		return suspectName;
	}
	public void setSuspectName(String suspectName) {
		this.suspectName = suspectName;
	}
	public String getSuspectId() {
		return suspectId;
	}
	public void setSuspectId(String suspectId) {
		this.suspectId = suspectId;
	}
	public String getSuspectType() {
		return suspectType;
	}
	public void setSuspectType(String suspectType) {
		this.suspectType = suspectType;
	}
}
