package com.xpand.xface.bean.query.photo;

import java.util.ArrayList;

import com.xpand.xface.bean.ResultStatus;

public class ResultPersonRespList {
	private ArrayList<ResultDataPerson> dataPersonList;
	private ResultStatus resultStatus;
	
	//get result master map from array masterMapList
	public int getDataPerson(String certificateNo) {
		this.getDataPersonList();
		int returnIndex = -1;
		boolean isFound = false;
		for (ResultDataPerson item: this.dataPersonList) {
			returnIndex++;
			if (item.getCertificateNo().equals(certificateNo)) {
				isFound = true;
				break;
			}
		}
		if (isFound) {
			return returnIndex;
		}else {
			//not found
			return -1; 
		}
	}
	public ResultStatus getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
	public ArrayList<ResultDataPerson> getDataPersonList() {
		if (this.dataPersonList==null) {
			this.dataPersonList = new ArrayList<>();
		}
		return dataPersonList;
	}
	public void setDataPersonList(ArrayList<ResultDataPerson> dataPersonList) {
		this.dataPersonList = dataPersonList;
	}	
}
