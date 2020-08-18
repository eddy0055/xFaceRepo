package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Entity
@Table(name="tbl_task_list")
public class HWTaskList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="taskListId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer taskListId;
	
	@Column(name="taskType", nullable=false)
	private Integer taskType;
	
	@Column(name="hitType", nullable=false)
	private Integer hitType;
	
	@Column(name="taskName",length=50, nullable=false)
	private String taskName;
	
	@Column(name="taskStartDate",length=10, nullable=false)
	private String taskStartDate;
	
	@Column(name="taskEndDate",length=10, nullable=false)
	private String taskEndDate;
	
	@Column(name="taskId",length=50)
	private String taskId;
	
	@Column(name="confidenceThreshold", nullable=false)
	private Integer confidenceThreshold;

	@Column(name="callbackURLMaster",length=100, nullable=false)
	private String callbackURLMaster;
	
	@Column(name="callbackURLSlave",length=100, nullable=false)
	private String callbackURLSlave;
				
	@Column(name="alarmDataType",length=2, nullable=false)
	private String alarmDataType;
	
	@ManyToOne
	@JoinColumn(name="chkponlibId")
	private HWCheckPointLibrary hwCheckPointLibrary;
	
	@JsonIgnore				
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

	public Integer getTaskListId() {
		return taskListId;
	}

	public void setTaskListId(Integer taskListId) {
		this.taskListId = taskListId;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getHitType() {
		return hitType;
	}

	public void setHitType(Integer hitType) {
		this.hitType = hitType;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskStartDate() {
		return taskStartDate;
	}

	public void setTaskStartDate(String taskStartDate) {
		this.taskStartDate = taskStartDate;
	}

	public String getTaskEndDate() {
		return taskEndDate;
	}

	public void setTaskEndDate(String taskEndDate) {
		this.taskEndDate = taskEndDate;
	}

	public Integer getConfidenceThreshold() {
		return confidenceThreshold;
	}

	public void setConfidenceThreshold(Integer confidenceThreshold) {
		this.confidenceThreshold = confidenceThreshold;
	}

	public String getCallbackURLMaster() {
		return callbackURLMaster;
	}

	public void setCallbackURLMaster(String callbackURLMaster) {
		this.callbackURLMaster = callbackURLMaster;
	}

	public String getCallbackURLSlave() {
		return callbackURLSlave;
	}

	public void setCallbackURLSlave(String callbackURLSlave) {
		this.callbackURLSlave = callbackURLSlave;
	}

	public String getAlarmDataType() {
		return alarmDataType;
	}

	public void setAlarmDataType(String alarmDataType) {
		this.alarmDataType = alarmDataType;
	}

	public HWCheckPointLibrary getHwCheckPointLibrary() {
		return hwCheckPointLibrary;
	}

	public void setHwCheckPointLibrary(HWCheckPointLibrary hwCheckPointLibrary) {
		this.hwCheckPointLibrary = hwCheckPointLibrary;
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

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
			
}
