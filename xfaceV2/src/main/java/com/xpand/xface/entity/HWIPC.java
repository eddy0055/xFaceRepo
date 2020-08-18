package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_ipc")
public class HWIPC implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int IPC_STATUS_ONLINE = 1;
	public static final int IPC_STATUS_OFFLINE = 0;
	public static final int IPC_STATUS_ALARM = 2;
	
	@Id
	@Column(name = "ipcId", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer ipcId;

	@Column(name = "ipcCode", length = 100, nullable = false)
	private String ipcCode;

	@Column(name = "ipcIp", length = 50, nullable = false)
	private String ipcIp;

	@Column(name = "ipcName", length = 50, nullable = false)
	private String ipcName;
	
	@Column(name = "ipcShortName", length = 10, nullable = false)
	private String ipcShortName;
	
	@Column(name = "ipcTaskId", length = 50)
	private String ipcTaskId;
	
	@Column(name = "checkPointId", length = 50)
	private String checkPointId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "vcmId")
	private HWVCM hwVCM;
			
	@Column(name = "receiveAlarm")
	private Integer receiveAlarm;

	// 2=face blacklist, 3=face whitelist, 4=face redlist
	@Column(name = "imageLibraryType")
	private Integer imageLibraryType;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateCreated", updatable = false)
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateUpdated", nullable = false)
	private Date dateUpdated;

	@JsonIgnore
	@Column(name = "userCreated", updatable = false)
	private String userCreated;

	@JsonIgnore
	@Column(name = "userUpdated", nullable = false)
	private String userUpdated;
	
	
	@OneToMany(mappedBy="hwIPC", fetch=FetchType.LAZY)
	private Set<HWAlarmHist> hwAlarmHistList;
	
	@Column(name = "ignoreUnknownAlarm")
	private int ignoreUnknownAlarm;
	
	@ManyToOne
	@JoinColumn(name="chkponlibId")
	private HWCheckPointLibrary hwCheckPointLibrary;

	@Column(name = "taskPrefix", length = 20, nullable=false)
	private String taskPrefix;
	
	//from HW we have to put value is 2
	@Column(name = "taskType", nullable=false)
	private Integer taskType;
	
	//from HW we have to put value is 0
	@Column(name = "analyzeMode", nullable=false)
	private Integer analyzeMode;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "gateId")
	private HWGateInfo hwGateInfo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "mapId")
	private LocationMap locationMap;
	
	@Column(name = "mapLocationX", length=10)
	private String mapLocationX;
	
	@Column(name = "mapLocationY", length=10)
	private String mapLocationY;
	
	@Column(name = "nameColor", length=20)
	private String nameColor;
	
	@Column(name = "iconWidth", length=10)
	private String iconWidth;
	
	@Column(name = "iconHeight", length=10)
	private String iconHeight;
	
	@Column(name = "nameLocationX", length=10)
	private String nameLocationX;
	
	@Column(name = "nameLocationY", length=10)
	private String nameLocationY;
	
	@Column(name = "iconTransformX", length=10)
	private String iconTransformX;
	
	@Column(name = "iconTransformY", length=10)
	private String iconTransformY;
	
	@Column(name = "nameSize", length=10)
	private String nameSize;		
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "eqdirectionId")
	private EquipmentDirection equipmentDirection;
	
	@Column(name = "ipcStatus")
	private int ipcStatus;
	
	public String getIpcShortName() {
		return ipcShortName;
	}

	public void setIpcShortName(String ipcShortName) {
		this.ipcShortName = ipcShortName;
	}

	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.dateUpdated = new Date();
	}

	public Integer getIpcId() {
		return ipcId;
	}

	public void setIpcId(Integer ipcId) {
		this.ipcId = ipcId;
	}

	public String getIpcCode() {
		return ipcCode;
	}

	public void setIpcCode(String ipcCode) {
		this.ipcCode = ipcCode;
	}

	public String getIpcIp() {
		return ipcIp;
	}

	public void setIpcIp(String ipcIp) {
		this.ipcIp = ipcIp;
	}

	public String getIpcName() {
		return ipcName;
	}

	public void setIpcName(String ipcName) {
		this.ipcName = ipcName;
	}
	
	@JsonIgnore
	public HWVCM getHwVCM() {
		return hwVCM;
	}

	public void setHwVCM(HWVCM hwVCM) {
		this.hwVCM = hwVCM;
	}
	
	public Integer getReceiveAlarm() {
		return receiveAlarm;
	}

	public void setReceiveAlarm(Integer receiveAlarm) {
		this.receiveAlarm = receiveAlarm;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	@Override
	public String toString() {
		return "HWIPC [ipcId=" + ipcId + ", ipcCode=" + ipcCode + ", ipcIp=" + ipcIp + ", ipcName=" + ipcName + "]";
	}

	public String toStringLevel2() {
		return "HWIPC [ipcId=" + ipcId + ", ipcCode=" + ipcCode + ", ipcIp=" + ipcIp + ", ipcName=" + ipcName
				+ ", hwVCM=" + hwVCM.toString()
				+ ", receiveAlarm=" + receiveAlarm + "]";
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getUserUpdated() {
		return userUpdated;
	}

	public void setUserUpdated(String userUpdated) {
		this.userUpdated = userUpdated;
	}

	public Integer getImageLibraryType() {
		return imageLibraryType;
	}

	public void setImageLibraryType(Integer imageLibraryType) {
		this.imageLibraryType = imageLibraryType;
	}
	
	public Set<HWAlarmHist> getHwAlarmHistList() {
		return hwAlarmHistList;
	}

	public void setHwAlarmHist(Set<HWAlarmHist> hwAlarmHistList) {
		this.hwAlarmHistList = hwAlarmHistList;
	}

	public int getIgnoreUnknownAlarm() {
		return ignoreUnknownAlarm;
	}

	public void setIgnoreUnknownAlarm(int ignoreUnknownAlarm) {
		this.ignoreUnknownAlarm = ignoreUnknownAlarm;
	}

	public String getIpcTaskId() {
		return ipcTaskId;
	}

	public void setIpcTaskId(String ipcTaskId) {
		this.ipcTaskId = ipcTaskId;
	}

	public String getCheckPointId() {
		return checkPointId;
	}

	public void setCheckPointId(String checkPointId) {
		this.checkPointId = checkPointId;
	}

	public HWCheckPointLibrary getHwCheckPointLibrary() {
		return hwCheckPointLibrary;
	}

	public void setHwCheckPointLibrary(HWCheckPointLibrary hwCheckPointLibrary) {
		this.hwCheckPointLibrary = hwCheckPointLibrary;
	}

	public String getTaskPrefix() {
		return taskPrefix;
	}

	public void setTaskPrefix(String taskPrefix) {
		this.taskPrefix = taskPrefix;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getAnalyzeMode() {
		return analyzeMode;
	}

	public void setAnalyzeMode(Integer analyzeMode) {
		this.analyzeMode = analyzeMode;
	}

	public HWGateInfo getHwGateInfo() {
		return hwGateInfo;
	}

	public void setHwGateInfo(HWGateInfo hwGateInfo) {
		this.hwGateInfo = hwGateInfo;
	}

	public LocationMap getLocationMap() {
		return locationMap;
	}

	public void setLocationMap(LocationMap locationMap) {
		this.locationMap = locationMap;
	}
	
	public EquipmentDirection getEquipmentDirection() {
		return equipmentDirection;
	}

	public void setEquipmentDirection(EquipmentDirection equipmentDirection) {
		this.equipmentDirection = equipmentDirection;
	}

	public String getNameColor() {
		return nameColor;
	}

	public void setNameColor(String nameColor) {
		this.nameColor = nameColor;
	}

	public String getIconWidth() {
		return iconWidth;
	}

	public void setIconWidth(String iconWidth) {
		this.iconWidth = iconWidth;
	}

	public String getIconHeight() {
		return iconHeight;
	}

	public void setIconHeight(String iconHeight) {
		this.iconHeight = iconHeight;
	}

	public String getNameLocationX() {
		return nameLocationX;
	}

	public void setNameLocationX(String nameLocationX) {
		this.nameLocationX = nameLocationX;
	}

	public String getNameLocationY() {
		return nameLocationY;
	}

	public void setNameLocationY(String nameLocationY) {
		this.nameLocationY = nameLocationY;
	}

	public String getIconTransformX() {
		return iconTransformX;
	}

	public void setIconTransformX(String iconTransformX) {
		this.iconTransformX = iconTransformX;
	}

	public String getIconTransformY() {
		return iconTransformY;
	}

	public void setIconTransformY(String iconTransformY) {
		this.iconTransformY = iconTransformY;
	}

	public String getNameSize() {
		return nameSize;
	}

	public void setNameSize(String nameSize) {
		this.nameSize = nameSize;
	}

	public void setMapLocationX(String mapLocationX) {
		this.mapLocationX = mapLocationX;
	}

	public void setMapLocationY(String mapLocationY) {
		this.mapLocationY = mapLocationY;
	}

	public String getMapLocationX() {
		return mapLocationX;
	}

	public String getMapLocationY() {
		return mapLocationY;
	}

	public int getIpcStatus() {
		return ipcStatus;
	}

	public void setIpcStatus(int ipcStatus) {
		this.ipcStatus = ipcStatus;
	}
}
