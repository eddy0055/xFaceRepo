package com.xpand.xface.bean.query.photo;

import java.util.ArrayList;

public class ResultDataPerson {
	private String title;
	private String fullName;
	private String dbPhoto;
	private String certificateType;
	private String certificateNo;
	private ArrayList<ResultDataMap> dataMapList;
	public int getDataMap(String mapCode) {
		this.getDataMapList();
		int returnIndex = -1;
		boolean isFound = false;
		for (ResultDataMap item: this.dataMapList) {
			returnIndex++;
			if (item.getMapCode().equals(mapCode)) {
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getDbPhoto() {
		return dbPhoto;
	}
	public void setDbPhoto(String dbPhoto) {
		this.dbPhoto = dbPhoto;
	}
	public String getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}	
	public ArrayList<ResultDataMap> getDataMapList() {
		if (this.dataMapList==null) {
			this.dataMapList = new ArrayList<>();
		}
		return dataMapList;
	}
	public void setDataMapList(ArrayList<ResultDataMap> dataMapList) {
		this.dataMapList = dataMapList;
	}	
	
}
