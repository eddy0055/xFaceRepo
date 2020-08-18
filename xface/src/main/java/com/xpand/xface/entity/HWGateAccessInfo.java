package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="tbl_gate_access_info")
public class HWGateAccessInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="recordId", nullable=false)	
	private Long recordId;
	@Column(name="recordType",nullable=false)
	private Integer recordType;
	@Column(name="recordDoorNo",nullable=false)
	private Integer recordDoorNo;
	@Column(name="recordInOrOut",nullable=false)
	private Integer recordInOrOut;
	@Column(name="recordCardNo",nullable=false)
	private Long recordCardNo;
	@Column(name="recordValid",nullable=false)
	private Integer recordValid;
	@Column(name="doorSN",nullable=false)
	private Long doorSN;
	@Column(name="reasonCode",nullable=false)
	private Integer reasonCode;
	@Column(name="reasonDesc", length=200, nullable=false)
	private String reasonDesc;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="recordTime",nullable=false)
	private Date recordTime;
	public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public Integer getRecordType() {
		return recordType;
	}
	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}
	public Integer getRecordDoorNo() {
		return recordDoorNo;
	}
	public void setRecordDoorNo(Integer recordDoorNo) {
		this.recordDoorNo = recordDoorNo;
	}
	public Integer getRecordInOrOut() {
		return recordInOrOut;
	}
	public void setRecordInOrOut(Integer recordInOrOut) {
		this.recordInOrOut = recordInOrOut;
	}
	public Long getRecordCardNo() {
		return recordCardNo;
	}
	public void setRecordCardNo(Long recordCardNo) {
		this.recordCardNo = recordCardNo;
	}
	public Integer getRecordValid() {
		return recordValid;
	}
	public void setRecordValid(Integer recordValid) {
		this.recordValid = recordValid;
	}
	public Long getDoorSN() {
		return doorSN;
	}
	public void setDoorSN(Long doorSN) {
		this.doorSN = doorSN;
	}
	public Integer getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getReasonDesc() {
		return reasonDesc;
	}
	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}
	public Date getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}
	

}
