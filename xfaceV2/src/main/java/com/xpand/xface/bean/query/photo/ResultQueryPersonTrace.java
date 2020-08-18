package com.xpand.xface.bean.query.photo;

import java.util.Date;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.StringUtil;

public class ResultQueryPersonTrace {
	private Date alarmDate;
	private String livePhoto;
	private String dbPhoto;
	private String fullName;
	private String certificateNo;
	private String title;
	private String certificateType;	
	private String ipcCode;
	private String ipcName;
	private String mapLocationX;
	private String mapLocationY;
	private String mapCode;
	private String mapName;
	private String mapPhoto;
	private String alarmCode;
	private int mapId;
	private int ipcId;
	private ResultStatus resultStatus;
	public ResultQueryPersonTrace(Object[] columns) {
//		SELECT ah.alarm_time, ah.live_photo, per.person_photo, per.full_name, per.certificate_no,tit.title_name,cert.certificate_name
//		,cam.ipc_code, cam.ipc_name,cam.map_locationx,cam.map_locationy,map.map_code, map.map_name, map.map_photo,ah.alarmh_id, map.map_id,cam.ipc_id
		this.alarmDate = (Date) columns[0];
		this.livePhoto = ""+columns[1];
		this.ipcCode = ""+columns[7];
		this.ipcName = ""+columns[8];
		this.mapLocationX = ""+columns[9];
		this.mapLocationY = ""+columns[10];
		this.mapCode = ""+columns[11];
		this.mapName = ""+columns[12];
		this.mapPhoto = ""+columns[13];							
		this.alarmCode = ""+columns[14];
		this.mapId = StringUtil.stringToInteger(""+columns[15], 0);
		this.ipcId = StringUtil.stringToInteger(""+columns[16], 0);
		if (StringUtil.checkNull(columns[4]+"")) {
			this.dbPhoto = ConstUtil.UNKNOWN_PERSON_DBPHOTO;
			this.fullName = ConstUtil.UNKNOWN_PERSON_FULLNAME;
			this.certificateNo = ConstUtil.UNKNOWN_PERSON_CERTIFICATE_NO;
			this.title = ConstUtil.UNKNOWN_PERSON_TITLE;
			this.certificateType = ConstUtil.UNKNOWN_PERSON_CERTIFICATE_TYPE;
		}else {
			this.dbPhoto = ""+columns[2];
			this.fullName = ""+columns[3];
			this.certificateNo = ""+columns[4];
			this.title = ""+columns[5];
			this.certificateType = ""+columns[6];
		}
	}	
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}	
	public String getLivePhoto() {
		return livePhoto;
	}
	public void setLivePhoto(String livePhoto) {
		this.livePhoto = livePhoto;
	}
	public String getDbPhoto() {
		return dbPhoto;
	}
	public void setDbPhoto(String dbPhoto) {
		this.dbPhoto = dbPhoto;
	}
	public Date getAlarmDate() {
		return alarmDate;
	}
	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}	
	public ResultStatus getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getMapLocationX() {
		return mapLocationX;
	}
	public void setMapLocationX(String mapLocationX) {
		this.mapLocationX = mapLocationX;
	}
	public String getMapLocationY() {
		return mapLocationY;
	}
	public void setMapLocationY(String mapLocationY) {
		this.mapLocationY = mapLocationY;
	}
	public String getMapCode() {
		return mapCode;
	}
	public void setMapCode(String mapCode) {
		this.mapCode = mapCode;
	}
	public String getMapName() {
		return mapName;
	}
	public String getAlarmCode() {
		return alarmCode;
	}
	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
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
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	public int getIpcId() {
		return ipcId;
	}
	public void setIpcId(int ipcId) {
		this.ipcId = ipcId;
	}
}
