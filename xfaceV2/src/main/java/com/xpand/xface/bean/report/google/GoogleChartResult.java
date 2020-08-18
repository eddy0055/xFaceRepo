package com.xpand.xface.bean.report.google;

import java.util.ArrayList;

public class GoogleChartResult {

	private ArrayList<String> resultList;			
	public GoogleChartResult() {
		
	}
	public ArrayList<String> getResultList() {
		if (this.resultList==null) {
			this.resultList = new ArrayList<>();
		}
		return resultList;
	}
	public void setResultList(ArrayList<String> resultList) {
		this.resultList = resultList;
	}

}
