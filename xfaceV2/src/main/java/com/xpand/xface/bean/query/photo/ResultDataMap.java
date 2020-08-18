package com.xpand.xface.bean.query.photo;

import java.util.ArrayList;

public class ResultDataMap {
	private String mapName;
	private String mapPhoto;
	private String mapCode;
	private int mapId;
	private ArrayList<ResultDataIPC> dataIpcList;
	public int getDataIpc(String ipcCode) {
		this.getDataIpcList();
		int returnIndex = -1;
		boolean isFound = false;
		for (ResultDataIPC item: this.dataIpcList) {
			returnIndex++;
			if (item.getIpcCode().equals(ipcCode)) {
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
	public ArrayList<ResultDataIPC> getDataIpcList() {
		if (this.dataIpcList==null) {
			this.dataIpcList = new ArrayList<>();
		}
		return dataIpcList;
	}
	public void setDataIpcList(ArrayList<ResultDataIPC> dataIpcList) {
		this.dataIpcList = dataIpcList;
	}
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getMapPhoto() {
		return mapPhoto;
	}
	public void setMapPhoto(String mapPhoto) {
		this.mapPhoto = mapPhoto;
	}
	public String getMapCode() {
		return mapCode;
	}
	public void setMapCode(String mapCode) {
		this.mapCode = mapCode;
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	
}
