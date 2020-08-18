package com.xpand.xface.bean.query.photo;

import java.util.ArrayList;

public class ResultDataIPC {
	private int ipcId;
	private String ipcCode;
	private String ipcName;
	private String locationX;
	private String locationY;
	private boolean isLatest = false;	
	private ArrayList<ResultDataFace> dataFaceList;
	public int getDataFace(String alarmCode) {
		this.getDataFaceList();
		int returnIndex = -1;
		boolean isFound = false;
		for (ResultDataFace item: this.dataFaceList) {
			returnIndex++;
			if (item.getAlarmCode().equals(alarmCode)) {
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
	public ArrayList<ResultDataFace> getDataFaceList() {
		if (this.dataFaceList==null) {
			this.dataFaceList = new ArrayList<>();
		}
		return dataFaceList;
	}
	public void setDataFaceList(ArrayList<ResultDataFace> dataFaceList) {		
		this.dataFaceList = dataFaceList;
	}
	public String getIpcCode() {
		return ipcCode;
	}
	public void setIpcCode(String ipcCode) {
		this.ipcCode = ipcCode;
	}
	public String getLocationX() {
		return locationX;
	}
	public void setLocationX(String locationX) {
		this.locationX = locationX;
	}
	public String getLocationY() {
		return locationY;
	}
	public void setLocationY(String locationY) {
		this.locationY = locationY;
	}
	public String getIpcName() {
		return ipcName;
	}
	public void setIpcName(String ipcName) {
		this.ipcName = ipcName;
	}
	public int getIpcId() {
		return ipcId;
	}
	public void setIpcId(int ipcId) {
		this.ipcId = ipcId;
	}
	public boolean isLatest() {
		return isLatest;
	}
	public void setLatest(boolean isLatest) {
		this.isLatest = isLatest;
	}
	
}
