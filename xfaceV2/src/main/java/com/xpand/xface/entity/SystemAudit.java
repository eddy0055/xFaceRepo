package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="tbl_system_audit")
public class SystemAudit implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SIZE_OF_DESC = 2048;
	
	//module name
	public static final String MOD_SECURITY = "security";	
	public static final String MOD_ALARM = "alarm";
	public static final String MOD_USER = "user";
	public static final String MOD_ROLE = "role";
	public static final String MOD_PERSON_INFO = "personInfo";
	public static final String MOD_PERSON_TITLE = "personTitle";
	public static final String MOD_PERSON_NATIONALITY = "personNationality";
	public static final String MOD_PERSON_CATEGORY = "personCategory";
	public static final String MOD_PERSON_CERTIFICATE = "PersonCertificate";
	public static final String MOD_LOCATION_MAP = "locationMap";
	public static final String MOD_APPLICATION_CFG = "applicationCfg";
	public static final String MOD_PAGE_HTML = "htmlPage";
	public static final String MOD_BOAT_INFO = "boatInfo";
	//sub module
	public static final String MOD_SUB_ALL = "all";
	public static final String MOD_SUB_PERSONINFO_VISA = "visa";
	public static final String MOD_SUB_ALARM = "alarm";
	public static final String MOD_SUB_ALARM_HIST = "alarm_hist";
	
	//result
	public static final String RES_SUCCESS = "success";
	public static final String RES_FAIL = "fail";

	
	@Id
	@Column(name="auditId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer auditId;

	@Column(name="moduleName",length=50, nullable=false)
	private String moduleName;
	
	@Column(name="subModuleName",length=50, nullable=false)
	private String subModuleName;
	
	@Column(name="description",length=SystemAudit.SIZE_OF_DESC, nullable=false)
	private String description;		
	
	@Column(name="result",length=20, nullable=false)
	private String result;			

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", nullable=false)	
	private Date dateCreated;
	
	@Column(name="userCreated", updatable=false)
	String userCreated;

	@PrePersist
	protected void onCreate() {
		this.dateCreated = new Date();
	}   

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getAuditId() {
		return auditId;
	}
	
	@Override
	public String toString() {
		return "SystemAudit [moduleName=" + moduleName + ", subModuleName=" + subModuleName + ", description="
				+ description + ", result=" + result + ", userCreated=" + this.userCreated +"]";
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}
}