package com.xpand.xface.bean.query;

import java.util.Date;

//<cameraSn>03855590000000000101#08815f31f7554141aa012820c52fc8e5</cameraSn>
//<confidence>96</confidence>
//<fileId>258d5f6fc0c549e480a824e1097dbbe0#2403785860353703936@10</fileId>
//<pos>
//    <bottom>1063</bottom>
//    <left>1565</left>
//    <right>1774</right>
//    <top>857</top>
//</pos>
//<sourceDevice>1</sourceDevice>
//<recordTime>1542880971727</recordTime>
public class QueryPersonByPhotoRespV1 {
	private String cameraSn;
	private String cameraName;
	private int confidenceLevel;	
	private String fileId;
	private int posBottom;
	private int posLeft;
	private int posRight;
	private int posTop;
	private Date recordTime;
	public QueryPersonByPhotoRespV1() {
		
	}	
	public String getCameraSn() {
		return cameraSn;
	}
	public void setCameraSn(String cameraSn) {
		this.cameraSn = cameraSn;
	}
	public int getConfidenceLevel() {
		return confidenceLevel;
	}
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public int getPosBottom() {
		return posBottom;
	}
	public void setPosBottom(int posBottom) {
		this.posBottom = posBottom;
	}
	public int getPosLeft() {
		return posLeft;
	}
	public void setPosLeft(int posLeft) {
		this.posLeft = posLeft;
	}
	public int getPosRight() {
		return posRight;
	}
	public void setPosRight(int posRight) {
		this.posRight = posRight;
	}
	public int getPosTop() {
		return posTop;
	}
	public void setPosTop(int posTop) {
		this.posTop = posTop;
	}
	public Date getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

}
