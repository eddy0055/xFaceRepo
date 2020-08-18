package com.xpand.xface.bean.landing;

import java.util.ArrayList;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;

public class DailyIPCStatusInfoList {
	
	
	private List<DailyIPCStatusInfoField> ipcInfoList;
	//private id;
	private ResultStatus result;
	public DailyIPCStatusInfoList() {
		// TODO Auto-generated constructor stub
	}
	public List<DailyIPCStatusInfoField> getIPCStatusInfoList() {
		if (this.ipcInfoList==null) {
			this.ipcInfoList = new ArrayList<DailyIPCStatusInfoField>();
		}
		return ipcInfoList;
	}
	
	public List<DailyIPCStatusInfoField> getIpcInfoList() {
		return ipcInfoList;
	}
	public void setIpcInfoList(List<DailyIPCStatusInfoField> ipcInfoList) {
		this.ipcInfoList = ipcInfoList;
	}
	public void setAlarmInfoList(List<DailyIPCStatusInfoField> ipcInfoList) {		
		this.ipcInfoList = ipcInfoList;
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public void addList(DailyIPCStatusInfoField ipcInfo) {
		this.getIPCStatusInfoList();
		this.ipcInfoList.add(ipcInfo);
	}

}
