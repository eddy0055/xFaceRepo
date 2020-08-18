package com.xpand.xface.bean.landing;

import java.util.ArrayList;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;

public class DailyGateInfoList {	
	ResultStatus result;
	List<DailyGateInfoField> gateInfoList;
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public List<DailyGateInfoField> getGateInfoList() {
		if (this.gateInfoList==null) {
			this.gateInfoList = new ArrayList<>();
		}
		return gateInfoList;
	}
	public void setGateInfoList(List<DailyGateInfoField> gateInfoList) {
		this.gateInfoList = gateInfoList;
	}
	
	public void addList(DailyGateInfoField gateInfoField) {
		this.getGateInfoList();
		this.gateInfoList.add(gateInfoField);
	}
}
