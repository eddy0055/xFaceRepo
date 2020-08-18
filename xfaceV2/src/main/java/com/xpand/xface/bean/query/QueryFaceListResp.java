package com.xpand.xface.bean.query;

public class QueryFaceListResp {
	private String faceListName;
	private String faceListId;
	public QueryFaceListResp(String faceListName, String faceListId) {
		this.faceListId = faceListId;
		this.faceListName = faceListName;
	}
	public String getFaceListName() {
		return faceListName;
	}
	public void setFaceListName(String faceListName) {
		this.faceListName = faceListName;
	}
	public String getFaceListId() {
		return faceListId;
	}
	public void setFaceListId(String faceListId) {
		this.faceListId = faceListId;
	}
	
	
}
