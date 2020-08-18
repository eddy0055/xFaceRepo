package com.xpand.xface.bean.report.google;

import java.util.ArrayList;

import com.xpand.xface.bean.ResultStatus;

public class GoogleChartResultList {

	private String title;
	private String hAxisTitle;
	private String vAxisTitle;
	private ArrayList<GoogleChartResult> chartResultList;	
	private ResultStatus resultStatus;
	private String footer;
	public GoogleChartResultList() {
		this.chartResultList = new ArrayList<>();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}	
	public ArrayList<GoogleChartResult> getChartResultList() {
		return chartResultList;
	}
	public void setChartResultList(ArrayList<GoogleChartResult> chartResultList) {
		this.chartResultList = chartResultList;
	}
	public void addResult(GoogleChartResult chartResultList) {
		this.getChartResultList().add(chartResultList);
	}
	public ResultStatus getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getvAxisTitle() {
		return vAxisTitle;
	}
	public void setvAxisTitle(String vAxisTitle) {
		this.vAxisTitle = vAxisTitle;
	}
	public String gethAxisTitle() {
		return hAxisTitle;
	}
	public void sethAxisTitle(String hAxisTitle) {
		this.hAxisTitle = hAxisTitle;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}

}
