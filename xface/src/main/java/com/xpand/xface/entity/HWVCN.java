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
@Entity
@Table(name="tbl_vcn")
public class HWVCN implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="vcnId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer vcnId;
	
	@Column(name="vcnIp",length=20, nullable=false)
	private String vcnIp;
	
	@Column(name="vcnName",length=50, nullable=false)
	private String vcnName;
			
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="vcmId")
	private HWVCM hwVCM;
		
	@OneToMany(mappedBy="hwVCN", cascade = CascadeType.REFRESH)
	private Set<HWIPC> hwIPCs;
	
	@Column(name="loginUserName",length=50, nullable=false)
	private String loginUserName;
	
	@Column(name="loginPassword",length=50, nullable=false)
	private String loginPassword;
	
	@Column(name="loginPort",length=10, nullable=false)
	private String loginPort;
	
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
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	@Override
	public String toString() {
		return "HWVCN [vcnId=" + vcnId + ", vcnIp=" + vcnIp + ", vcnName=" + vcnName + ", hwVCM=" + hwVCM.toString() + ", loginUserName=" + loginUserName + ", loginPassword=" + loginPassword + ", loginPort="
				+ loginPort + "]";
	}

	public Integer getVcnId() {
		return vcnId;
	}

	public void setVcnId(Integer vcnId) {
		this.vcnId = vcnId;
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

	public HWVCM getHwVCM() {
		return hwVCM;
	}

	public void setHwVCM(HWVCM hwVCM) {
		this.hwVCM = hwVCM;
	}

	public Set<HWIPC> getHwIPCs() {
		return hwIPCs;
	}

	public void setHwIPCs(Set<HWIPC> hwIPCs) {
		this.hwIPCs = hwIPCs;
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
	}

	public String getLoginPort() {
		return loginPort;
	}

	public void setLoginPort(String loginPort) {
		this.loginPort = loginPort;
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
}
