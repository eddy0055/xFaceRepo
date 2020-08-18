package com.xpand.xface.bean.query.photo;

import java.util.Date;

import com.xpand.xface.util.StringUtil;

public class ResultDataFace {	
	private String facePhoto;
	private Date photoDate;	
	private String alarmCode;
	private int noOfFace;	
	private String alarmCodeList;
	public String getFacePhoto() {
		return facePhoto;
	}
	public void setFacePhoto(String facePhoto) {
		this.facePhoto = facePhoto;
	}
	public Date getPhotoDate() {
		return photoDate;
	}
	public void setPhotoDate(Date photoDate) {
		this.photoDate = photoDate;
	}
	public String getAlarmCode() {
		return alarmCode;
	}
	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}
	public int getNoOfFace() {
		return noOfFace;
	}
	public void setNoOfFace(int noOfFace) {
		this.noOfFace = noOfFace;
	}
	public void increaseNoOfFace(String alarmCode) {
		this.noOfFace += 1;
		if (StringUtil.checkNull(this.alarmCodeList)) {
			this.alarmCodeList = alarmCode;
		}else {
			this.alarmCodeList += ","+alarmCode;
		}
	}
	public String getAlarmCodeList() {
		return alarmCodeList;
	}
	public void setAlarmCodeList(String alarmCodeList) {
		this.alarmCodeList = alarmCodeList;
	}
	
}
