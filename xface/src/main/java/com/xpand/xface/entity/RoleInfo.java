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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xpand.xface.util.StringUtil;
@Entity
@Table(name="tbl_role")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RoleInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="roleId", nullable=false)
	private Integer roleId;
	
	@Column(name="roleName",length=50,nullable=false)
	private String roleName;
	
	@Column(name="defaultPage",length=200, nullable=false)	
	private String defaultPage;
	
	@OneToMany(mappedBy="roleInfo", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<RoleDetailInfo> roleDetailInfos;
	
	@OneToMany(mappedBy="roleInfo", cascade = CascadeType.REFRESH, fetch=FetchType.EAGER)
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
	private String userCreated;
	
	@JsonIgnore
	@Column(name="userUpdated", nullable=false)
	private String userUpdated;
	
	public RoleInfo() {}
		
	public RoleInfo(RoleInfo roleInfo) {
		if (roleInfo.getRoleId()!=null) {
			this.roleId = roleInfo.getRoleId();
		}
		this.roleName = roleInfo.getRoleName();
		this.defaultPage = roleInfo.getDefaultPage();
		this.roleDetailInfos = roleInfo.getRoleDetailInfos();		
		this.userInfos = roleInfo.getUserInfos();
	}
	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDefaultPage() {
		return defaultPage;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}
	
	public Set<RoleDetailInfo> getRoleDetailInfos() {
		return roleDetailInfos;
	}
	@JsonIgnore
	public Set<UserInfo> getUserInfos() {
		return userInfos;
	}
	
	public void setUserInfos(Set<UserInfo> userInfo) {
		this.userInfos = userInfo;
	}
			
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
		return "RoleInfo [roleId=" + roleId + ", roleName=" + roleName + ", defaultPage=" + defaultPage + ", roleDetails=" + StringUtil.arrayToString(this.roleDetailInfos) + ", userCreated=" + this.userCreated + ", userUpdated=" + this.userUpdated + "]";
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
		this.userUpdated = userCreated;
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

	public void setRoleDetailInfos(Set<RoleDetailInfo> roleDetailInfos) {
		this.roleDetailInfos = roleDetailInfos;
	}
}
