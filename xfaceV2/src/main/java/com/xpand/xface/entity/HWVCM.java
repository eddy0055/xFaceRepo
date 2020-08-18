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
					
	@Column(name="vcmLoginUser",length=30, nullable=false)
	private String vcmLoginUser;
	
	@Column(name="vcmLoginPwd",length=30, nullable=false)
	private String vcmLoginPwd;
	
	@Column(name="vcmLoginPort",length=10, nullable=false)
	private String vcmLoginPort;
	
	@Column(name="vcmGetImageUser",length=30, nullable=false)
	private String vcmGetImageUser;
	
	@Column(name="vcmGetImagePwd",length=30, nullable=false)
	private String vcmGetImagePwd;
	
	@Column(name="vcnIp",length=20, nullable=false)
	private String vcnIp;
	
	@Column(name="vcnName",length=50, nullable=false)
	private String vcnName;
	
	@OneToMany(mappedBy="hwVCM", cascade = CascadeType.REFRESH)
	private Set<HWIPC> hwIPCList;
	
	@Column(name="vcnLoginUser",length=50, nullable=false)
	private String vcnLoginUser;
	
	@Column(name="vcnLoginPwd",length=50, nullable=false)
	private String vcnLoginPwd;
	
	@Column(name="vcnLoginPort",length=10, nullable=false)
	private String vcnLoginPort;
	
	
	@Column(name="vcnSDKIp",length=20, nullable=false)
	private String vcnSDKIp;
	
	@Column(name="vcnSDKLoginPort",length=10, nullable=false)
	private String vcnSDKLoginPort;
		
	@Column(name="vcnSDKLoginUser",length=50, nullable=false)
	private String vcnSDKLoginUser;
	
	@Column(name="vcnSDKLoginUserId",length=10, nullable=false)
	private String vcnSDKLoginUserId;
	
	@Column(name="vcnSDKDomainCode",length=50, nullable=false)
	private String vcnSDKDomainCode;
	
	@Column(name="vcnSDKLoginPwd",length=50, nullable=false)
	private String vcnSDKLoginPwd;
	
	@Column(name="vcnSDKCallBackURL",length=100, nullable=false)
	private String vcnSDKCallBackURL;
		
	
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
	private Set<HWCheckPointLibrary> hwCheckPointLibraryList;
	
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

	public Set<HWCheckPointLibrary> getHwCheckPointLibraryList() {
		return hwCheckPointLibraryList;
	}

	public void setHwCheckPointLibraryList(Set<HWCheckPointLibrary> hwCheckPointLibraryList) {
		this.hwCheckPointLibraryList = hwCheckPointLibraryList;
	}

	public String getVcmLoginUser() {
		return vcmLoginUser;
	}

	public void setVcmLoginUser(String vcmLoginUser) {
		this.vcmLoginUser = vcmLoginUser;
	}

	public String getVcmLoginPwd() {
		return vcmLoginPwd;
	}

	public void setVcmLoginPwd(String vcmLoginPwd) {
		this.vcmLoginPwd = vcmLoginPwd;
		this.isUpdateRequire = true;
	}

	public String getVcmLoginPort() {
		return vcmLoginPort;
	}

	public void setVcmLoginPort(String vcmLoginPort) {
		this.vcmLoginPort = vcmLoginPort;
	}

	public String getVcmGetImageUser() {
		return vcmGetImageUser;
	}

	public void setVcmGetImageUser(String vcmGetImageUser) {
		this.vcmGetImageUser = vcmGetImageUser;
	}

	public String getVcmGetImagePwd() {
		return vcmGetImagePwd;
	}

	public void setVcmGetImagePwd(String vcmGetImagePwd) {
		this.vcmGetImagePwd = vcmGetImagePwd;
	}

	public String getVcnIp() {
		return vcnIp;
	}

	public void setVcnIp(String vcnIp) {
		this.vcnIp = vcnIp;
	}

	public String getVcnName() {
		return vcnName;
	}

	public void setVcnName(String vcnName) {
		this.vcnName = vcnName;
	}

	public String getVcnLoginUser() {
		return vcnLoginUser;
	}

	public void setVcnLoginUser(String vcnLoginUser) {
		this.vcnLoginUser = vcnLoginUser;
	}

	public String getVcnLoginPwd() {
		return vcnLoginPwd;
	}

	public void setVcnLoginPwd(String vcnLoginPwd) {
		this.vcnLoginPwd = vcnLoginPwd;
	}

	public String getVcnLoginPort() {
		return vcnLoginPort;
	}

	public void setVcnLoginPort(String vcnLoginPort) {
		this.vcnLoginPort = vcnLoginPort;
	}

	public Set<HWIPC> getHwIPCList() {
		return hwIPCList;
	}

	public void setHwIPCList(Set<HWIPC> hwIPCList) {
		this.hwIPCList = hwIPCList;
	}

	public String getVcnSDKIp() {
		return vcnSDKIp;
	}

	public void setVcnSDKIp(String vcnSDKIp) {
		this.vcnSDKIp = vcnSDKIp;
	}

	public String getVcnSDKLoginPort() {
		return vcnSDKLoginPort;
	}

	public void setVcnSDKLoginPort(String vcnSDKLoginPort) {
		this.vcnSDKLoginPort = vcnSDKLoginPort;
	}

	public String getVcnSDKLoginUser() {
		return vcnSDKLoginUser;
	}

	public void setVcnSDKLoginUser(String vcnSDKLoginUser) {
		this.vcnSDKLoginUser = vcnSDKLoginUser;
	}

	public String getVcnSDKLoginPwd() {
		return vcnSDKLoginPwd;
	}

	public void setVcnSDKLoginPwd(String vcnSDKLoginPwd) {
		this.vcnSDKLoginPwd = vcnSDKLoginPwd;
	}

	public String getVcnSDKCallBackURL() {
		return vcnSDKCallBackURL;
	}

	public void setVcnSDKCallBackURL(String vcnSDKCallBackURL) {
		this.vcnSDKCallBackURL = vcnSDKCallBackURL;
	}

	public String getVcnSDKLoginUserId() {
		return vcnSDKLoginUserId;
	}

	public void setVcnSDKLoginUserId(String vcnSDKLoginUserId) {
		this.vcnSDKLoginUserId = vcnSDKLoginUserId;
	}

	public String getVcnSDKDomainCode() {
		return vcnSDKDomainCode;
	}

	public void setVcnSDKDomainCode(String vcnSDKDomainCode) {
		this.vcnSDKDomainCode = vcnSDKDomainCode;
	}
	
}
