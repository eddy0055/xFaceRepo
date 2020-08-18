package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name="tbl_user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static String DEFAULT_LOGON_USERNAME="fr_system@xpand.asia";
	
	@Id
	@Column(name="userId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer userId;
		
	@Column(name="userName",length=50,nullable=false,unique=true)
	private String userName;
	
	@Column(name="password",length=800,nullable=false)
	private String password;
	
	@Column(name="firstName",length=100, nullable=false)	
	private String firstName;
	
	@Column(name="lastName",length=100, nullable=false)	
	private String lastName;
		
	@Column(name="enabled")	
	private short enabled;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="roleId")
	private RoleInfo roleInfo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ipcgId")
	private IPCGroup ipcGroup;

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
	
	@Column(name="logInServer",length=20)	
	private String logInServer;
			
	public UserInfo() {}

	public UserInfo(UserInfo userInfo) {
		super();
		if (userInfo.userId!=null) {
			this.userId = userInfo.userId;
		}		
		this.userName = userInfo.userName;
		this.password = userInfo.password;
		this.firstName = userInfo.firstName;
		this.lastName = userInfo.lastName;
		this.enabled = userInfo.enabled;
		this.roleInfo = userInfo.roleInfo;
		this.ipcGroup = userInfo.ipcGroup;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public short getEnabled() {
		return enabled;
	}

	public void setEnabled(short enabled) {
		this.enabled = enabled;
	}

	public RoleInfo getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(RoleInfo roleInfo) {
		this.roleInfo = roleInfo;
	}
	
	public IPCGroup getIpcGroup() {
		return ipcGroup;
	}

	public void setIpcGroup(IPCGroup ipcGroup) {
		this.ipcGroup = ipcGroup;
	}

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userName=" + userName + ", password=" + password + ", firstName="
				+ firstName + ", lastName=" + lastName + ", enabled=" + enabled + ", roleInfo=" + roleInfo.toString()
				+ ", ipcGroup=" + (ipcGroup==null?"":ipcGroup.toString()) + ", userCreated=" + this.userCreated + ", userUpdated=" + this.userUpdated + "]";
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

	public Date getDateUpdated() {
		return dateUpdated;
	}
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	public String getLogInServer() {
		return logInServer;
	}

	public void setLogInServer(String logInServer) {
		this.logInServer = logInServer;
	}


}
