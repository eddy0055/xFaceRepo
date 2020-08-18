package com.xpand.xface.bean.report;

import java.util.Date;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.StringUtil;

public class QueryAlarmResult {
	private Integer alarmId;
	private Integer personId;
	private String certificateType;
	private String certificateNo;
	private String title;
	private String fullName;	
	private String livePhoto;
	private String dbPhoto;
	private Date alarmDate;
	private String gateInfoName;
	private String ipcName;
	private String percentMatch;
	private ResultStatus resultStatus;
	private String category;
	private String boatShortName;
		
	public QueryAlarmResult(Object[] columns) {
//		SELECT ah.alarmh_id,ah.livePhoto,ah.alarm_time,per.person_id,per.person_photo,per.full_name,per.certificate_no,ipc_name,category_name
//		,boat_short_name
		this.alarmId = StringUtil.stringToInteger(""+columns[0],0);
		this.livePhoto = ""+columns[1];
		this.alarmDate = (Date) columns[2];
		if (columns[3]==null) {
			this.personId = -1;
			this.dbPhoto = "";			
			this.title = "";
			this.fullName = ConstUtil.UNKNOWN_PERSON_FULLNAME;
			this.certificateType = "";
			this.certificateNo = "";
			this.percentMatch = "0";
			this.category = ConstUtil.UNKNOWN_PERSON_CATEGORY;
		
		}else {
			this.personId = StringUtil.stringToInteger(""+columns[3], 0);
			this.dbPhoto = ""+columns[4];
			this.fullName = ""+columns[5];			
			this.certificateNo = ""+columns[6];
			this.certificateType = ""+columns[8];
			this.title = ""+columns[9];
			this.percentMatch = ""+columns[10];
			this.category = ""+columns[11];
			
		}
		if (columns[7]==null) {			
			this.ipcName = 	ConstUtil.UNKNOWN_IPC_CODE;
		}else {
			this.ipcName = ""+columns[7];
		}
		if (columns[12]==null) {			
			this.gateInfoName = ConstUtil.UNKNOWN_GATEINFO_CODE;
		}else {
			this.gateInfoName = ""+columns[12];
		}
		if (columns[13]==null) {
			this.boatShortName = Boat.BOAT_NONE_SHORT_NAME;
		}else {
			this.boatShortName = ""+columns[13];
		}		
	}
	
	
	
	
	public Integer getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(Integer alarmId) {
		this.alarmId = alarmId;
	}
	public Integer getPersonId() {
		return personId;
	}
	public void setPersonId(Integer personId) {
		this.personId = personId;
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
	public String getIpcName() {
		return ipcName;
	}
	public void setIpcName(String ipcName) {
		this.ipcName = ipcName;
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
	public String getPercentMatch() {
		return percentMatch;
	}
	public void setPercentMatch(String percentMatch) {
		this.percentMatch = percentMatch;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getGateInfoName() {
		return gateInfoName;
	}
	public void setGateInfoName(String gateInfoName) {
		this.gateInfoName = gateInfoName;
	}
	public String getBoatShortName() {
		return boatShortName;
	}
	public void setBoatShortName(String boatShortName) {
		this.boatShortName = boatShortName;
	}
}
