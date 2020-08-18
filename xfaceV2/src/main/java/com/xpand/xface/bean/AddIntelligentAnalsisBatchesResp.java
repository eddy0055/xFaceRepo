package com.xpand.xface.bean;

public class AddIntelligentAnalsisBatchesResp {
	private String resultCode;
	private String taskId;
	private String taskName;
	public AddIntelligentAnalsisBatchesResp(String resultCode, String taskId, String taskName) {
		this.resultCode = resultCode;
		this.taskId = taskId;
		this.taskName = taskName;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}