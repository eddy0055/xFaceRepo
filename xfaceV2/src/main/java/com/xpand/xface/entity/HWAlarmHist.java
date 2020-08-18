package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="tbl_alarm_hist")
public class HWAlarmHist implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//alarm type 
	public static final String HW_VCM_ALARM = "M";
	public static final String HW_VCN_ALARM = "N";
			
	public static final String VCN_ALM_IPC_ONLINE = "ALARM_IPC_ONLINE";
	public static final String VCN_ALM_IPC_OFFLINE = "ALARM_IPC_OFFLINE";
	public static final String VCN_ALM_DI = "ALARM_TYPE_DI";
		
	public static final String VCN_ALM_EVENT_SET = "10013";
	public static final String VCN_ALM_EVENT_CLEAR = "10017";
	
	@Id
	@Column(name="alarmhId", nullable=false)	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer alarmhId;
	
	@Column(name="alarmCode",length=100)
	private String alarmCode;
	@Column(name="taskId",length=50)
	private String taskId;
	@Column(name="cameraId",length=100)
	private String cameraId;
	
	@Column(name="resolution",length=50)
	private String resolution;
	@Column(name="cTime",length=50)
	private String cTime;
	@Column(name="suspectId",length=100)
	private String suspectId;
	@Column(name="blackListId",length=50)
	private String blackListId;	
	@Column(name="picImageUrl",length=500)
	private String picImageUrl;
	@Column(name="picThumImageUrl",length=500)
	private String picThumImageUrl;
	
	@Column(name="picIsURL",length=5)
	private String picIsURL;
	
	@Column(name="livePhoto")
	@Lob
	private String livePhoto;
	
	@Column(name="alarmPicName",length=50)
	private String alarmPicName;
		
	@Column(name="alarmTime")
	private Date alarmTime;	
	@Column(name="metaScr")
	private int metaScr;
	
	@Column(name="resultStatus")
	private String resultStatus;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="ipcId")
	private HWIPC hwIPC;

	@ManyToOne
	@JoinColumn(name="personId")
	private PersonInfo personInfo;
				
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;
	
	@Column(name="recMatch")	
	private byte recMatch;
	@Column(name="recUnMatch")	
	private byte recUnMatch;
		
	@Column(name="eventType",length=10)
	private String eventType;
	@Column(name="alarmType",length=20)
	private String alarmType;
	@Column(name="alarmSource",length=1)
	private String alarmSource;	

	public HWAlarmHist() {
		
	}				
	
	public Integer getAlarmhId() {
		return alarmhId;
	}
	public void setAlarmhId(Integer alarmhId) {
		this.alarmhId = alarmhId;
	}
	public String getAlarmCode() {
		return alarmCode;
	}
	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getCameraId() {
		return cameraId;
	}
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getcTime() {
		return cTime;
	}
	public void setcTime(String cTime) {
		this.cTime = cTime;
	}
	public String getSuspectId() {
		return suspectId;
	}
	public void setSuspectId(String suspectId) {
		this.suspectId = suspectId;
	}
	public String getBlackListId() {
		return blackListId;
	}
	public void setBlackListId(String blackListId) {
		this.blackListId = blackListId;
	}	
	public String getPicImageUrl() {
		return picImageUrl;
	}
	public void setPicImageUrl(String picImageUrl) {
		this.picImageUrl = picImageUrl;
	}
	public String getPicThumImageUrl() {
		return picThumImageUrl;
	}
	public void setPicThumImageUrl(String picThumImageUrl) {
		this.picThumImageUrl = picThumImageUrl;
	}
	public Date getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}	
	public HWIPC getHwIPC() {
		return hwIPC;
	}
	public void setHwIPC(HWIPC hwIPC) {
		this.hwIPC = hwIPC;
	}
	public PersonInfo getPersonInfo() {
		return personInfo;
	}
	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}	
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}	

	public String getPicIsURL() {
		return picIsURL;
	}

	public void setPicIsURL(String picIsURL) {
		this.picIsURL = picIsURL;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getLivePhoto() {
		return livePhoto;
	}
	public void setLivePhoto(String livePhoto) {
		this.livePhoto = livePhoto;
	}
	public byte getRecMatch() {
		return recMatch;
	}
	public void setRecMatch(byte recMatch) {
		this.recMatch = recMatch;
	}
	public byte getRecUnMatch() {
		return recUnMatch;
	}
	public void setRecUnMatch(byte recUnMatch) {
		this.recUnMatch = recUnMatch;
	}
	public String getAlarmPicName() {
		return alarmPicName;
	}
	public void setAlarmPicName(String alarmPicName) {
		this.alarmPicName = alarmPicName;
	}
	public int getMetaScr() {
		return metaScr;
	}
	public void setMetaScr(int metaScr) {
		this.metaScr = metaScr;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmSource() {
		return alarmSource;
	}

	public void setAlarmSource(String alarmSource) {
		this.alarmSource = alarmSource;
	}

}
