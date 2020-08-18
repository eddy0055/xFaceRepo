package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_area")
public class LocationArea implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="areaId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer areaId;
	
	@Column(name="areaName",length=200, nullable=false)
	private String areaName;
	
	@Column(name="areaDesc",length=200)
	private String areaDesc;
		
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="floorId")
	private LocationFloor floor;
		
	@JsonIgnore
	@OneToMany(mappedBy="area", cascade = CascadeType.REFRESH)
	private Set<HWIPC> hwIPCs;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", updatable=false)	
	private Date dateUpdated;
	
	@Column(name="userCreated", updatable=false)
	String userCreated;
	@Column(name="userUpdated", updatable=false)
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
		return "LocationArea [areaId=" + areaId + ", areaName=" + areaName + ", floor=" + floor.toString() 
				+ "]";
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

	public String getAreaDesc() {
		return areaDesc;
	}

	public void setAreaDesc(String areaDesc) {
		this.areaDesc = areaDesc;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
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

	public LocationFloor getFloor() {
		return floor;
	}

	public void setFloor(LocationFloor floor) {
		this.floor = floor;
	}

	public Set<HWIPC> getHwIPCs() {
		return hwIPCs;
	}

	public void setHwIPCs(Set<HWIPC> hwIPCs) {
		this.hwIPCs = hwIPCs;
	}
}
