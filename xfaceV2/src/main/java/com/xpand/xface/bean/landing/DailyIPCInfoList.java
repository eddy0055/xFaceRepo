package com.xpand.xface.bean.landing;

import java.util.ArrayList;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.report.google.DailyFaceChartField;

public class DailyIPCInfoList {	
	ResultStatus result;
	List<DailyFaceChartField> ipcInfoList;
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}	
	public void addList(DailyFaceChartField ipcInfoField) {
		this.getIPCInfoList();
		this.ipcInfoList.add(ipcInfoField);
	}
	public List<DailyFaceChartField> getIPCInfoList() {
		if (this.ipcInfoList==null) {
			this.ipcInfoList = new ArrayList<DailyFaceChartField>();
		}
		return ipcInfoList;
	}
	public void setIPCInfoList(List<DailyFaceChartField> ipcInfoList) {
		this.ipcInfoList = ipcInfoList;
	}
}
