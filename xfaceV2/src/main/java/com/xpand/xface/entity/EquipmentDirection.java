package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_equ_direction")
public class EquipmentDirection implements Serializable {
	public static final int DIRECTION_IN = 1;
	public static final int DIRECTION_OUT = 2;
	public static final int DIRECTION_UNKNOWN = 3;
	
	private static final long serialVersionUID = 1L;	
	
	@Id
	@Column(name="eqdirectionId", nullable=false)	
	private Integer eqdirectionId;
	@Column(name="directionCode",nullable=false)
	private Integer directionCode;
	@Column(name="directionDesc", length=20 ,nullable=false)
	private String directionDesc;	
	@OneToMany(mappedBy = "equipmentDirection", fetch=FetchType.LAZY)
	private Set<HWGateAccessInfo> hwGateAccessInfoList;
	@OneToMany(mappedBy = "equipmentDirection", fetch=FetchType.LAZY)
	private Set<HWIPC> hwIPCList;
	
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateCreated", updatable = false)
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dateUpdated", nullable = false)
	private Date dateUpdated;

	@JsonIgnore
	@Column(name = "userCreated", updatable = false)
	private String userCreated;

	@JsonIgnore
	@Column(name = "userUpdated", nullable = false)
	private String userUpdated;
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.dateUpdated = new Date();
	}

	public Integer getEqdirectionId() {
		return eqdirectionId;
	}

	public void setEqdirectionId(Integer eqdirectionId) {
		this.eqdirectionId = eqdirectionId;
	}

	public Integer getDirectionCode() {
		return directionCode;
	}

	public void setDirectionCode(Integer directionCode) {
		this.directionCode = directionCode;
	}

	public String getDirectionDesc() {
		return directionDesc;
	}

	public void setDirectionDesc(String directionDesc) {
		this.directionDesc = directionDesc;
	}

	public Set<HWGateAccessInfo> getHWGateAccessInfoList() {
		return hwGateAccessInfoList;
	}

	public void setHWGateAccessInfoList(Set<HWGateAccessInfo> hwGateAccessInfoList) {
		this.hwGateAccessInfoList = hwGateAccessInfoList;
	}

	public Set<HWIPC> getHwIPCList() {
		return hwIPCList;
	}

	public void setHwIPCList(Set<HWIPC> hwIPCList) {
		this.hwIPCList = hwIPCList;
	}
}
