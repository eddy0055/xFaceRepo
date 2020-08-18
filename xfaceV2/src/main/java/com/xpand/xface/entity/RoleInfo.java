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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xpand.xface.entity.EntityBase;

@Entity
@Table(name="tbl_role")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RoleInfo extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="roleId", nullable=false)
	private Integer roleId;
	
	@Column(name="roleCode",length=50,nullable=false)
	private String roleCode;
	
	@Column(name="roleName",length=50,nullable=false)
	private String roleName;	
	
	@OneToMany(mappedBy="roleInfo", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<RoleDetailInfo> roleDetailInfoList;
	
	@OneToMany(mappedBy="roleInfo", cascade = CascadeType.REFRESH, fetch=FetchType.LAZY)
	private Set<UserInfo> userInfoList;

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
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="pageId")
	private HtmlPageInfo htmlPageInfo;
	
	public RoleInfo() {}
		
	public RoleInfo(RoleInfo roleInfo) {
		if (roleInfo.getRoleId()!=null) {
			this.roleId = roleInfo.getRoleId();
		}
		this.roleCode = roleInfo.getRoleCode();
		this.roleName = roleInfo.getRoleName();
		this.htmlPageInfo = roleInfo.getHtmlPageInfo();
		this.roleDetailInfoList = roleInfo.getRoleDetailInfoList();		
		this.userInfoList = roleInfo.getUserInfoList();
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

	public Set<RoleDetailInfo> getRoleDetailInfoList() {
		return roleDetailInfoList;
	}
	@JsonIgnore
	public Set<UserInfo> getUserInfoList() {
		return userInfoList;
	}
	
	public void setUserInfoList(Set<UserInfo> userInfoList) {
		this.userInfoList = userInfoList;
	}
			
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
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

	public void setRoleDetailInfoList(Set<RoleDetailInfo> roleDetailInfoList) {
		this.roleDetailInfoList = roleDetailInfoList;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public HtmlPageInfo getHtmlPageInfo() {
		return htmlPageInfo;
	}

	public void setHtmlPageInfo(HtmlPageInfo htmlPageInfo) {
		this.htmlPageInfo = htmlPageInfo;
	}
}
