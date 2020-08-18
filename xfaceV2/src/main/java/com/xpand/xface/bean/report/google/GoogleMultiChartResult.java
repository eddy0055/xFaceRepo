package com.xpand.xface.bean.report.google;

import java.util.ArrayList;

import com.xpand.xface.bean.ResultStatus;

public class GoogleMultiChartResult {
	private ArrayList<GoogleChartResultList> chartResultList;
	private ResultStatus result;
	public ArrayList<GoogleChartResultList> getChartResultList() {
		if (this.chartResultList==null) {
			this.chartResultList = new ArrayList<>();
		}
		return this.chartResultList;
	}

	public void setChartResultList(ArrayList<GoogleChartResultList> chartResultList) {
		this.chartResultList = chartResultList;
	} 	
	
	public void addGoogleChart(GoogleChartResultList chartResultList) {
		this.getChartResultList().add(chartResultList);
	}

	public ResultStatus getResult() {
		return result;
	}

	public void setResult(ResultStatus result) {
		this.result = result;
	}
}
