package com.xpand.xface.bean.landing;

public class DailyIPCStatusInfoField {
	//ipc_code,gate_id,ipc_status
	private String ipcCode;
	private String gateId;
	private String ipcStatus;
	public DailyIPCStatusInfoField(Object[] columns) {
		this.ipcCode = ""+columns[0];
		this.gateId = ""+columns[1];
		this.ipcStatus = ""+columns[2];	
		
	}
	public String getIpcCode() {
		return ipcCode;
	}
	public void setIpcCode(String ipcCode) {
		this.ipcCode = ipcCode;
	}
	public String getGateId() {
		return gateId;
	}
	public void setGateId(String gateId) {
		this.gateId = gateId;
	}
	public String getIpcStatus() {
		return ipcStatus;
	}
	public void setIpcStatus(String ipcStatus) {
		this.ipcStatus = ipcStatus;
	}
	
}
