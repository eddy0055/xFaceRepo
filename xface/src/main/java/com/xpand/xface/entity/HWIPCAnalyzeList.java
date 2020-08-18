package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpand.xface.util.StringUtil;
@Entity
@Table(name="tbl_ipc_analyze_list",
	indexes = { @Index(name = "tbl_ipc_analyze_list_idx1", columnList = "suspectId")})
public class HWIPCAnalyzeList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ipcanalId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer ipcanalId;
	
	@Column(name="listName",length=20, nullable=false)
	private String listName;
	
	@Column(name="listNameId",length=100)
	private String listNameId;
	
	@Column(name="libraryType",length=2, nullable=false)
	private String libraryType;
	
	@Column(name="checkPointName",length=20, nullable=false)
	private String checkPointName;
	
	//1 = license plate, 2 = face image
	@Column(name="checkPointType",length=20, nullable=false)
	private String checkPointType;
	
	@Column(name="checkPointSN",length=100)
	private String checkPointSN;
	
	@Column(name="suspectId",length=100, nullable=false)
	private String suspectId;
	
	@Column(name="suspectName",length=20, nullable=false)
	private String suspectName;
	
	//1 = license plate, 2 = face image
	@Column(name="suspectType",length=20, nullable=false)
	private String suspectType;
	
	//2018-01-01
	@Column(name="suspectStartDate",length=10, nullable=false)
	private String suspectStartDate;
	//2018-01-01
	@Column(name="suspectEndDate",length=10, nullable=false)
	private String suspectEndDate;
	

	@Column(name="callbackURLMaster",length=100, nullable=false)
	private String callbackURLMaster;
	
	@Column(name="callbackURLSlave",length=100, nullable=false)
	private String callbackURLSlave;
				
	@Column(name="alarmDataType",length=2, nullable=false)
	private String alarmDataType;
	
	@JsonIgnore
	@OneToMany(mappedBy="hwIPCAnalyzeList", fetch=FetchType.LAZY , cascade = CascadeType.REFRESH)
	private Set<HWIPC> hwIPCs;
	
	@JsonIgnore
	@OneToMany(mappedBy="hwIPCAnalyzeList" )	
	private Set<PersonCategory> personCategorys;
	
	@JsonIgnore				
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", nullable=false)	
	private Date dateUpdated;
	
	@Column(name="userCreated", updatable=false)
	String userCreated;
	
	@Column(name="userUpdated", nullable=false)
	String userUpdated;
	
	@ManyToOne
	@JoinColumn(name="vcmId")
	private HWVCM hwVCM;
	
	//value > 0 mean incase alarm from same person  
	@Column(name="ignoreSamePersonTime")
	private Integer ignoreSamePersonTime;
	
	@ManyToOne
	@JoinColumn(name="ignSMPTuId", referencedColumnName="tuId")
	private TimeUnit ignoreSamePersonTimeUnit;
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }
	
	public Set<HWIPC> getHwIPCs() {
		return hwIPCs;
	}

	public void setHwIPCs(Set<HWIPC> hwIPCs) {
		this.hwIPCs = hwIPCs;
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

	public Integer getIpcanalId() {
		return ipcanalId;
	}

	public void setIpcanalId(Integer ipcanalId) {
		this.ipcanalId = ipcanalId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getLibraryType() {
		return libraryType;
	}

	public void setLibraryType(String libraryType) {
		this.libraryType = libraryType;
	}

	public String getCheckPointName() {
		return checkPointName;
	}

	public void setCheckPointName(String checkPointName) {
		this.checkPointName = checkPointName;
	}

	public String getCheckPointType() {
		return checkPointType;
	}

	public void setCheckPointType(String checkPointType) {
		this.checkPointType = checkPointType;
	}

	@Override
	public String toString() {
		return "HWIPCAnalyzeList [ipcanalId=" + ipcanalId + ", listName=" + listName + ", libraryType=" + libraryType
				+ ", checkPointName=" + checkPointName + ", checkPointType=" + checkPointType + ", hwIPCs=" + StringUtil.arrayToString(hwIPCs)
				+ ", userCreated=" + userCreated + ", userUpdated=" + userUpdated + "]";
	}

	public String getSuspectName() {
		return suspectName;
	}

	public void setSuspectName(String suspectName) {
		this.suspectName = suspectName;
	}

	public String getSuspectType() {
		return suspectType;
	}

	public void setSuspectType(String suspectType) {
		this.suspectType = suspectType;
	}

	public String getSuspectStartDate() {
		return suspectStartDate;
	}

	public void setSuspectStartDate(String suspectStartDate) {
		this.suspectStartDate = suspectStartDate;
	}

	public String getSuspectEndDate() {
		return suspectEndDate;
	}

	public void setSuspectEndDate(String suspectEndDate) {
		this.suspectEndDate = suspectEndDate;
	}

	public String getListNameId() {
		return listNameId;
	}

	public void setListNameId(String listNameId) {
		this.listNameId = listNameId;
	}

	public String getCheckPointSN() {
		return checkPointSN;
	}

	public void setCheckPointSN(String checkPointSN) {
		this.checkPointSN = checkPointSN;
	}

	public String getCallbackURLMaster() {
		return callbackURLMaster;
	}

	public void setCallbackURLMaster(String callbackURLMaster) {
		this.callbackURLMaster = callbackURLMaster;
	}

	public String getCallbackURLSlave() {
		return callbackURLSlave;
	}

	public void setCallbackURLSlave(String callbackURLSlave) {
		this.callbackURLSlave = callbackURLSlave;
	}

	public String getAlarmDataType() {
		return alarmDataType;
	}

	public void setAlarmDataType(String alarmDataType) {
		this.alarmDataType = alarmDataType;
	}

	public String getSuspectId() {
		return suspectId;
	}

	public void setSuspectId(String suspectId) {
		this.suspectId = suspectId;
	}

//	public Set<PersonInfo> getPersonInfos() {
//		return personInfos;
//	}
//
//	public void setPersonInfos(Set<PersonInfo> personInfos) {
//		this.personInfos = personInfos;
//	}

	public HWVCM getHwVCM() {
		return hwVCM;
	}

	public void setHwVCM(HWVCM hwVCM) {
		this.hwVCM = hwVCM;
	}

	public Integer getIgnoreSamePersonTime() {
		return ignoreSamePersonTime;
	}

	public void setIgnoreSamePersonTime(Integer ignoreSamePersonTime) {
		this.ignoreSamePersonTime = ignoreSamePersonTime;
	}

	public TimeUnit getIgnoreSamePersonTimeUnit() {
		return ignoreSamePersonTimeUnit;
	}

	public void setIgnoreSamePersonTimeUnit(TimeUnit ignoreSamePersonTimeUnit) {
		this.ignoreSamePersonTimeUnit = ignoreSamePersonTimeUnit;
	}

	public Set<PersonCategory> getPersonCategorys() {
		return personCategorys;
	}

	public void setPersonCategorys(Set<PersonCategory> personCategorys) {
		this.personCategorys = personCategorys;
	}
	
}
