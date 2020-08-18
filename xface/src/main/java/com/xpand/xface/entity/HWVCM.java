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
import javax.persistence.Transient;
@Entity
@Table(name="tbl_vcm")
public class HWVCM implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="vcmId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer vcmId;
	
	@Column(name="connectProtocol",length=20, nullable=false)
	private String connectProtocol;
	
	@Column(name="vcmIp",length=20, nullable=false)
	private String vcmIp;
	
	@Column(name="vcmName",length=50, nullable=false)
	private String vcmName;
					
	@ManyToOne
	@JoinColumn(name="buildingId")
	private LocationBuilding building;
	
	@OneToMany(mappedBy="hwVCM")
	private Set<HWVCN> hwVCNs;
	
	@Column(name="loginUserName",length=30, nullable=false)
	private String loginUserName;
	
	@Column(name="loginPassword",length=30, nullable=false)
	private String loginPassword;
	
	@Column(name="loginPort",length=10, nullable=false)
	private String loginPort;
	
	@Column(name="getImageUserName",length=30, nullable=false)
	private String getImageUserName;
	
	@Column(name="getImagePassword",length=30, nullable=false)
	private String getImagePassword;
	
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
	
	@Transient
	private boolean isUpdateRequire = false;
	
	@OneToMany(mappedBy="hwVCM", fetch=FetchType.EAGER)
	private Set<HWIPCAnalyzeList> hwIPCAnalyzeLists;
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	public Integer getVcmId() {
		return vcmId;
	}

	public void setVcmId(Integer vcmId) {
		this.vcmId = vcmId;
	}

	public String getVcmIp() {
		return vcmIp;
	}

	public void setVcmIp(String vcmIp) {
		this.vcmIp = vcmIp;
	}

	public String getVcmName() {
		return vcmName;
	}

	public void setVcmName(String vcmName) {
		this.vcmName = vcmName;
	}

	public LocationBuilding getBuilding() {
		return building;
	}

	public void setBuilding(LocationBuilding building) {
		this.building = building;
	}

	public Set<HWVCN> getHwVCNs() {
		return hwVCNs;
	}

	public void setHwVCNs(Set<HWVCN> hwVCNs) {
		this.hwVCNs = hwVCNs;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
		this.isUpdateRequire = true;
	}

	public String getLoginPort() {
		return loginPort;
	}

	public void setLoginPort(String loginPort) {
		this.loginPort = loginPort;
	}

	public String getGetImageUserName() {
		return getImageUserName;
	}

	public void setGetImageUserName(String getImageUserName) {
		this.getImageUserName = getImageUserName;
	}

	public String getGetImagePassword() {
		return getImagePassword;
	}

	public void setGetImagePassword(String getImagePassword) {
		this.getImagePassword = getImagePassword;
	}

	public String getConnectProtocol() {
		return connectProtocol;
	}

	public void setConnectProtocol(String connectProtocol) {
		this.connectProtocol = connectProtocol;
	}

	public boolean isUpdateRequire() {
		return isUpdateRequire;
	}

	public void setUpdateRequire(boolean isUpdateRequire) {
		this.isUpdateRequire = isUpdateRequire;
	}

	@Override
	public String toString() {
		return "HWVCM [vcmId=" + vcmId + ", connectProtocol=" + connectProtocol + ", vcmIp=" + vcmIp + ", vcmName="
				+ vcmName + ", building=" + building.toString() + ", loginUserName=" + loginUserName + ", loginPassword="
				+ loginPassword + ", loginPort=" + loginPort + ", getImageUserName=" + getImageUserName
				+ ", getImagePassword=" + getImagePassword + "]";
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

	public Set<HWIPCAnalyzeList> getHwIPCAnalyzeLists() {
		return hwIPCAnalyzeLists;
	}

	public void setHwIPCAnalyzeLists(Set<HWIPCAnalyzeList> hwIPCAnalyzeLists) {
		this.hwIPCAnalyzeLists = hwIPCAnalyzeLists;
	}
}
