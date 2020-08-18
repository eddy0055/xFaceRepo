package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
@Table(name="tbl_floor")
public class LocationFloor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="floorId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer floorId;
	
	@Column(name="floorName",length=200, nullable=false)
	private String floorName;
	
	@Column(name="floorDesc",length=100)
	private String floorDesc;
		
	@ManyToOne
	@JoinColumn(name="buildingId")
	private LocationBuilding building;
	
	@JsonIgnore
	@OneToMany(mappedBy="floor")
	private Set<LocationArea> areas;
	
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
		 return "LocationFloor [floorId=" + floorId + ", floorName=" + floorName + ", building=" + building.toString() + "]";
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

	public String getFloorDesc() {
		return floorDesc;
	}

	public void setFloorDesc(String floorDesc) {
		this.floorDesc = floorDesc;
	}

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public Integer getFloorId() {
		return floorId;
	}

	public void setFloorId(Integer floorId) {
		this.floorId = floorId;
	}

	public Set<LocationArea> getAreas() {
		return areas;
	}

	public void setAreas(Set<LocationArea> areas) {
		this.areas = areas;
	}

	public LocationBuilding getBuilding() {
		return building;
	}

	public LocationBuilding setBuilding(LocationBuilding building) {
		return this.building = building;
	}
}
