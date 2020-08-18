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

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "areaId")
	private LocationArea area;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "vcnId")
	private HWVCN hwVCN;
		
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ipcanalId")
	private HWIPCAnalyzeList hwIPCAnalyzeList;

	@OneToMany(mappedBy = "hwIPC", fetch=FetchType.EAGER)
	private Set<IPCGroupDetail> ipcGroupDetail;

	@Column(name = "receiveAlarm", length = 1)
	private String receiveAlarm;

	// 2=face blacklist, 3=face whitelist, 4=face redlist
	@Column(name = "imageLibraryType", length = 1)
	private String imageLibraryType;

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
	
	@OneToMany(mappedBy="hwIPC")
	private Set<HWAlarmHist> hwAlarmHists;
	
	@Column(name = "IgnoreUnknownAlarm")
	private int ignoreUnknownAlarm;

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
	public LocationArea getArea() {
		return area;
	}

	public void setArea(LocationArea area) {
		this.area = area;
	}

	@JsonIgnore
	public HWVCN getHwVCN() {
		return hwVCN;
	}

	public void setHwVCN(HWVCN hwVCN) {
		this.hwVCN = hwVCN;
	}

	@JsonIgnore
	public Set<IPCGroupDetail> getIpcGroupDetail() {
		return ipcGroupDetail;
	}

	public void setIpcGroupDetail(Set<IPCGroupDetail> ipcGroupDetail) {
		this.ipcGroupDetail = ipcGroupDetail;
	}
	
	public String getReceiveAlarm() {
		return receiveAlarm;
	}

	public void setReceiveAlarm(String receiveAlarm) {
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
				+ ", area=" + area.toString() + ", hwVCN=" + hwVCN.toString() + ", hwAlarmHists=" + hwAlarmHists
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

	public String getImageLibraryType() {
		return imageLibraryType;
	}

	public void setImageLibraryType(String imageLibraryType) {
		this.imageLibraryType = imageLibraryType;
	}

	public HWIPCAnalyzeList getHwIPCAnalyzeList() {
		return hwIPCAnalyzeList;
	}

	public void setHwIPCAnalyzeList(HWIPCAnalyzeList hwIPCAnalyzeList) {
		this.hwIPCAnalyzeList = hwIPCAnalyzeList;
	}
	
	public Set<HWAlarmHist> getHwAlarmHists() {
		return hwAlarmHists;
	}

	public void setHwAlarmHist(Set<HWAlarmHist> hwAlarmHists) {
		this.hwAlarmHists = hwAlarmHists;
	}

	public int getIgnoreUnknownAlarm() {
		return ignoreUnknownAlarm;
	}

	public void setIgnoreUnknownAlarm(int ignoreUnknownAlarm) {
		this.ignoreUnknownAlarm = ignoreUnknownAlarm;
	}
}
