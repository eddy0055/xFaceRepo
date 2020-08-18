package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="tbl_hrms_log", indexes = { @Index(name = "tbl_hrms_log_idx1", columnList = "dateCreated") })
public class HRMSLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="hrmslId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer hrmsl_id;
	
	@Column(name="fileName",length=200, nullable=false)
	private String firstName;
	
	@Column(name="noRecord", nullable=false)
	private Integer noRecord;
	
	@Column(name="noSuccess", nullable=false)
	private Integer noSuccess;
	
	@Column(name="noFail", nullable=false)
	private Integer noFail;
			
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false, nullable=false)	
	private Date dateCreated;
	
	@Column(name="userCreated", updatable=false, nullable=false)
	String userCreated;
	

	@PrePersist
	protected void onCreate() {
		this.dateCreated = new Date();
	}


	public String getUserCreated() {
		return userCreated;
	}


	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}   
}
