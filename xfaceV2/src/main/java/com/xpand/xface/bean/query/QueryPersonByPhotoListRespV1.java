package com.xpand.xface.bean.query;

import java.util.ArrayList;
import java.util.List;

public class QueryPersonByPhotoListRespV1 {
	private List<QueryPersonByPhotoRespV1> resultList;
	private int totalRecord;		
	public QueryPersonByPhotoListRespV1() {
	}
	public List<QueryPersonByPhotoRespV1> getResultList() {
		if (this.resultList==null) {
			this.resultList = new ArrayList<>();
		}
		return resultList;
	}
	public void setResultList(List<QueryPersonByPhotoRespV1> resultList) {
		this.resultList = resultList;
	}
	public void addResultList(QueryPersonByPhotoRespV1 result) {
		this.getResultList();
		this.resultList.add(result);
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
}
