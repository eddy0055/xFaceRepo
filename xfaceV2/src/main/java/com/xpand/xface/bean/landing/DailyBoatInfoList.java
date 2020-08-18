package com.xpand.xface.bean.landing;

import java.util.ArrayList;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;

public class DailyBoatInfoList {
	private List<DailyBoatInfoField> boatInfoList;
	private ResultStatus result;
	public DailyBoatInfoList() {
		// TODO Auto-generated constructor stub
	}
	public List<DailyBoatInfoField> getBoatInfoList() {
		if (this.boatInfoList==null) {
			this.boatInfoList = new ArrayList<DailyBoatInfoField>();
		}
		return boatInfoList;
	}
	public void setBoatInfoList(List<DailyBoatInfoField> boatInfoList) {		
		this.boatInfoList = boatInfoList;
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public void addList(DailyBoatInfoField boatInfoField) {
		this.getBoatInfoList();
		this.boatInfoList.add(boatInfoField);
	}
}
