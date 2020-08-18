package com.xpand.xface.bean.query;

public class QueryCameraResp {
	private String cameraName;
	private String cameraSN;
	private String cameraIP;
	private String cameraState;
	public QueryCameraResp(String cameraName, String cameraSN, String cameraIP, String cameraState) {
		this.cameraName = cameraName;
		this.cameraSN = cameraSN;
		this.cameraIP = cameraIP;
		this.cameraState = cameraState;
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
	public String getCameraIP() {
		return cameraIP;
	}
	public void setCameraIP(String cameraIP) {
		this.cameraIP = cameraIP;
	}
	public String getCameraState() {
		return cameraState;
	}
	public void setCameraState(String cameraState) {
		this.cameraState = cameraState;
	}
}
