package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_location_map")
public class LocationMap extends EntityBase implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="mapId",nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer mapId;
	
	@Column(name="mapCode",length=50)
	private String mapCode;
	
	@Column(name="mapName",length=50)
	private String mapName;
	
	@Column(name="mapDesc",length=200)
	private String mapDesc;
			
	@Column(name="mapPhoto",length=50, nullable=false)
	@Lob	
	@Basic(fetch = FetchType.LAZY)
	protected String mapPhoto;
			
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", nullable=false)	
	private Date dateUpdated;
	
	@Column(name="userCreated", updatable=false)
	String userCreated;
	@Column(name="userUpdated", nullable=false)
	String userUpdated;
	
	@OneToMany(mappedBy="locationMap", fetch=FetchType.LAZY)
	private Set<HWIPC> hwIPCList;
	@OneToMany(mappedBy="locationMap", fetch=FetchType.LAZY)         
	private Set<HWGateInfo> hwGateInfoList;
	
	public LocationMap() {
		
	}
	
	public LocationMap(LocationMap locationMap) {
		if (locationMap.getMapId()!=null) {
			this.mapId = locationMap.getMapId();
		}
		this.mapCode = locationMap.getMapCode();
		this.mapName = locationMap.getMapName();
		this.mapDesc = locationMap.getMapDesc();		
		this.mapPhoto = locationMap.getMapPhoto();
	}
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	public Integer getMapId() {
		return mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	public String getMapCode() {
		return mapCode;
	}

	public void setMapCode(String mapCode) {
		this.mapCode = mapCode;
	}

	public String getMapDesc() {
		return mapDesc;
	}

	public void setMapDesc(String mapDesc) {
		this.mapDesc = mapDesc;
	}

	public String getMapPhoto() {
		return mapPhoto;
	}

	public void setMapPhoto(String mapPhoto) {
		this.mapPhoto = mapPhoto;
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

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
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
	public Set<HWIPC> getHwIPCList() {
		return hwIPCList;
	}
	public void setHwIPCList(Set<HWIPC> hwIPCList) {
		this.hwIPCList = hwIPCList;
	}

	public Set<HWGateInfo> getHwGateInfoList() {
		return hwGateInfoList;
	}

	public void setHwGateInfoList(Set<HWGateInfo> hwGateInfoList) {
		this.hwGateInfoList = hwGateInfoList;
	}	
}
