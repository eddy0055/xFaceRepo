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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpand.xface.util.StringUtil;
@Entity
@Table(name="tbl_ipc_group")
public class IPCGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ipcgId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer ipcgId;
	
	@Column(name="groupName",length=50,nullable=false)
	private String groupName;
	
	@OneToMany(mappedBy="ipcGroup", cascade = CascadeType.ALL)
	private Set<IPCGroupDetail> ipcGroupDetails;
	
	@OneToMany(mappedBy="ipcGroup", cascade = CascadeType.REFRESH, fetch=FetchType.EAGER)
	private Set<UserInfo> userInfos;
	
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", nullable=false)	
	private Date dateUpdated;
			
	@JsonIgnore
	@Column(name="userCreated", updatable=false)
	String userCreated;
	@JsonIgnore
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

	public Integer getIpcgId() {
		return ipcgId;
	}

	public void setIpcgId(Integer ipcgId) {
		this.ipcgId = ipcgId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public Set<IPCGroupDetail> getIpcGroupDetails() {
		return ipcGroupDetails;
	}

	
	@JsonIgnore
	public Set<UserInfo> getUserInfos() {
		return userInfos;
	}
	
	public void setUserInfos(Set<UserInfo> userInfos) {
		this.userInfos = userInfos;
	}

	@Override
	public String toString() {		
		return "IPCGroup [ipcgId=" + ipcgId + ", groupName=" + groupName + ", ipcGroupDetail=" + "]";
	}
	public String toStringLevel2() {
		return "IPCGroup [ipcgId=" + ipcgId + ", groupName=" + groupName + ", ipcGroupDetail=" + StringUtil.arrayToString(this.ipcGroupDetails) + "]";
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

	public void setIpcGroupDetails(Set<IPCGroupDetail> ipcGroupDetails) {
		this.ipcGroupDetails = ipcGroupDetails;
	}
}
