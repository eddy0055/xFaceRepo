package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Entity
@Table(name="tbl_building")
public class LocationBuilding implements Serializable {	
	private static final long serialVersionUID = 1L;	
	@Id
	@Column(name="buildingId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer buildingId;
	
	@Column(name="buildingName",length=200, nullable=false)
	private String buildingName;
	
	@Column(name="location",length=300, nullable=false)
	private String location;		
	
	@Column(name="buildingDesc",length=100)
	private String buildingDesc;
	
	@Column(name="numberOfFloors")
	private Integer numberOfFloors;
	
	//@JsonIgnore
	@OneToMany(mappedBy="building")
	private Set<LocationFloor> floors;
	
	@JsonIgnore
	@OneToMany(mappedBy="building")
	private Set<HWVCM> hwVCMs;	
	
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

    @Override
	public String toString() {
		return "LocationBuilding [buildingId=" + buildingId + ", buildingName=" + buildingName + ", location="
				+ location + "]";
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

	public String getBuildingDesc() {
		return buildingDesc;
	}

	public void setBuildingDesc(String buildingDesc) {
		this.buildingDesc = buildingDesc;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public Integer getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(Integer buildingId) {
		this.buildingId = buildingId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public Set<LocationFloor> getFloors() {
		return floors;
	}

	public void setFloors(Set<LocationFloor> floors) {
		this.floors = floors;
	}
	

	public Set<HWVCM> getHwVCMs() {
		return hwVCMs;
	}

	public Integer getNumberOfFloors() {
		return numberOfFloors;
	}

	public void setNumberOfFloors(Integer numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	public void setHwVCMs(Set<HWVCM> hwVCMs) {
		this.hwVCMs = hwVCMs;
	}
}
