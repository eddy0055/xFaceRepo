package com.xpand.xface.bean;

public class QueryCameraResp {
	private String cameraName;
	private String cameraSN;
	public QueryCameraResp(String cameraName, String cameraSN) {
		this.cameraName = cameraName;
		this.cameraSN = cameraSN;
	}
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	public String getCameraSN() {
		return cameraSN;
	}
	public void setCameraSN(String cameraSN) {
		this.cameraSN = cameraSN;
	}
}
