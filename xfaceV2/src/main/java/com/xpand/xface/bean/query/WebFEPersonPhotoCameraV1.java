package com.xpand.xface.bean.query;

import java.util.ArrayList;
import java.util.List;

public class WebFEPersonPhotoCameraV1 {
	private String ipcCode;
	private String ipcName;
	private List<WebPersonPhotoDetailV1> personPhotoDetailList;
	public WebFEPersonPhotoCameraV1() {
		
	}
	public String getIpcCode() {
		return ipcCode;
	}
	public void setIpcCode(String ipcCode) {
		this.ipcCode = ipcCode;
	}
	public String getIpcName() {
		return ipcName;
	}
	public void setIpcName(String ipcName) {
		this.ipcName = ipcName;
	}
	public List<WebPersonPhotoDetailV1> getPersonPhotoDetailList() {
		if (this.personPhotoDetailList==null) {
			this.personPhotoDetailList = new ArrayList<>();
		}
		return personPhotoDetailList;
	}
	public void setPersonPhotoDetailList(List<WebPersonPhotoDetailV1> personPhotoDetailList) {
		this.personPhotoDetailList = personPhotoDetailList;
	}
	public void addPersonPhotoDetailList(WebPersonPhotoDetailV1 personPhotoDetail) {
		this.getPersonPhotoDetailList();
		int index = 0;
		for (WebPersonPhotoDetailV1 detail: this.personPhotoDetailList) {
			//a > b 
			if (detail.getAlarmDateTime().compareTo(personPhotoDetail.getAlarmDateTime()) > 0) {
				//sort by datetime
				this.personPhotoDetailList.add(index, personPhotoDetail);
				return;
			}
			index++;				
		}
	}

}
