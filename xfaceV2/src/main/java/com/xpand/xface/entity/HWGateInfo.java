package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name="tbl_gate_info")
public class HWGateInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="gateId", nullable=false)	
	private Integer gateId;	
	@Column(name="gateName", length=50,nullable=false)
	private String gateName;	
	@Column(name="gateDesc", length=200,nullable=false)
	private String gateDesc;
	@Column(name="gateShortName", length=10,nullable=false)
	private String gateShortName;
	@Column(name="gateCode", length=20,nullable=false)
	private String gateCode;
	
	@OneToMany(mappedBy = "hwGateInfo", fetch=FetchType.LAZY)
	private Set<HWGateAccessInfo> HWGateAccessInfoList;
	
	@OneToMany(mappedBy="hwGateInfo", cascade = CascadeType.REFRESH, fetch=FetchType.LAZY)
	private Set<BoatSchedule> boatScheduleList;
	
	@OneToMany(mappedBy="hwGateInfo")
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
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "mapId")
	private LocationMap locationMap;
	
	@Column(name = "gateLocationX", length=10)
	private String gateLocationX;
	
	@Column(name = "gateLocationY", length=10)
	private String gateLocationY;
	
	@Column(name = "gateNameColor", length=20)
	private String gateNameColor;
	
	@Column(name = "gateIconWidth", length=10)
	private String gateIconWidth;
	
	@Column(name = "gateIconHeight", length=10)
	private String gateIconHeight;
	
	@Column(name = "gateNameLocationX", length=10)
	private String gateNameLocationX;
	
	@Column(name = "gateNameLocationY", length=10)
	private String gateNameLocationY;
	
	@Column(name = "gateIconTransformX", length=10)
	private String gateIconTransformX;
	
	@Column(name = "gateIconTransformY", length=10)
	private String gateIconTransformY;
	
	@Column(name = "gateNameSize", length=10)
	private String gateNameSize;
	
	//location for boat icon which tie to gate
	@Column(name = "boatLocationX", length=10)
	private String boatLocationX;
	
	@Column(name = "boatLocationY", length=10)
	private String boatLocationY;
	
	@Column(name = "boatNameColor", length=20)
	private String boatNameColor;
	
	@Column(name = "boatIconWidth", length=10)
	private String boatIconWidth;
	
	@Column(name = "boatIconHeight", length=10)
	private String boatIconHeight;
	
	@Column(name = "boatNameLocationX", length=10)
	private String boatNameLocationX;
	
	@Column(name = "boatNameLocationY", length=10)
	private String boatNameLocationY;
	
	@Column(name = "boatIconTransformX", length=10)
	private String boatIconTransformX;
	
	@Column(name = "boatIconTransformY", length=10)
	private String boatIconTransformY;
	
	@Column(name = "boatNameSize", length=10)
	private String boatNameSize;
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		this.dateUpdated = new Date();
	}

	public Integer getGateId() {
		return gateId;
	}

	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}
	
	public String getGateDesc() {
		return gateDesc;
	}

	public void setGateDesc(String gateDesc) {
		this.gateDesc = gateDesc;
	}

	public Set<HWGateAccessInfo> getHWGateAccessInfoList() {
		return HWGateAccessInfoList;
	}

	public void setHWGateAccessInfoList(Set<HWGateAccessInfo> hWGateAccessInfoList) {
		HWGateAccessInfoList = hWGateAccessInfoList;
	}

	public Set<HWIPC> getHwIPCList() {
		return hwIPCList;
	}

	public void setHwIPCList(Set<HWIPC> hwIPCList) {
		this.hwIPCList = hwIPCList;
	}

	public Set<BoatSchedule> getBoatScheduleList() {
		return boatScheduleList;
	}

	public void setBoatScheduleList(Set<BoatSchedule> boatScheduleList) {
		this.boatScheduleList = boatScheduleList;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	public LocationMap getLocationMap() {
		return locationMap;
	}

	public void setLocationMap(LocationMap locationMap) {
		this.locationMap = locationMap;
	}

	public String getGateShortName() {
		return gateShortName;
	}

	public void setGateShortName(String gateShortName) {
		this.gateShortName = gateShortName;
	}

	public String getGateCode() {
		return gateCode;
	}

	public void setGateCode(String gateCode) {
		this.gateCode = gateCode;
	}

	public String getGateLocationX() {
		return gateLocationX;
	}

	public void setGateLocationX(String gateLocationX) {
		this.gateLocationX = gateLocationX;
	}

	public String getGateLocationY() {
		return gateLocationY;
	}

	public void setGateLocationY(String gateLocationY) {
		this.gateLocationY = gateLocationY;
	}

	public String getGateNameColor() {
		return gateNameColor;
	}

	public void setGateNameColor(String gateNameColor) {
		this.gateNameColor = gateNameColor;
	}

	public String getGateIconWidth() {
		return gateIconWidth;
	}

	public void setGateIconWidth(String gateIconWidth) {
		this.gateIconWidth = gateIconWidth;
	}

	public String getGateIconHeight() {
		return gateIconHeight;
	}

	public void setGateIconHeight(String gateIconHeight) {
		this.gateIconHeight = gateIconHeight;
	}

	public String getGateNameLocationX() {
		return gateNameLocationX;
	}

	public void setGateNameLocationX(String gateNameLocationX) {
		this.gateNameLocationX = gateNameLocationX;
	}

	public String getGateNameLocationY() {
		return gateNameLocationY;
	}

	public void setGateNameLocationY(String gateNameLocationY) {
		this.gateNameLocationY = gateNameLocationY;
	}

	public String getGateIconTransformX() {
		return gateIconTransformX;
	}

	public void setGateIconTransformX(String gateIconTransformX) {
		this.gateIconTransformX = gateIconTransformX;
	}

	public String getGateIconTransformY() {
		return gateIconTransformY;
	}

	public void setGateIconTransformY(String gateIconTransformY) {
		this.gateIconTransformY = gateIconTransformY;
	}

	public String getGateNameSize() {
		return gateNameSize;
	}

	public void setGateNameSize(String gateNameSize) {
		this.gateNameSize = gateNameSize;
	}

	public String getBoatLocationX() {
		return boatLocationX;
	}

	public void setBoatLocationX(String boatLocationX) {
		this.boatLocationX = boatLocationX;
	}

	public String getBoatLocationY() {
		return boatLocationY;
	}

	public void setBoatLocationY(String boatLocationY) {
		this.boatLocationY = boatLocationY;
	}

	public String getBoatNameColor() {
		return boatNameColor;
	}

	public void setBoatNameColor(String boatNameColor) {
		this.boatNameColor = boatNameColor;
	}

	public String getBoatIconWidth() {
		return boatIconWidth;
	}

	public void setBoatIconWidth(String boatIconWidth) {
		this.boatIconWidth = boatIconWidth;
	}

	public String getBoatIconHeight() {
		return boatIconHeight;
	}

	public void setBoatIconHeight(String boatIconHeight) {
		this.boatIconHeight = boatIconHeight;
	}

	public String getBoatNameLocationX() {
		return boatNameLocationX;
	}

	public void setBoatNameLocationX(String boatNameLocationX) {
		this.boatNameLocationX = boatNameLocationX;
	}

	public String getBoatNameLocationY() {
		return boatNameLocationY;
	}

	public void setBoatNameLocationY(String boatNameLocationY) {
		this.boatNameLocationY = boatNameLocationY;
	}

	public String getBoatIconTransformX() {
		return boatIconTransformX;
	}

	public void setBoatIconTransformX(String boatIconTransformX) {
		this.boatIconTransformX = boatIconTransformX;
	}

	public String getBoatIconTransformY() {
		return boatIconTransformY;
	}

	public void setBoatIconTransformY(String boatIconTransformY) {
		this.boatIconTransformY = boatIconTransformY;
	}

	public String getBoatNameSize() {
		return boatNameSize;
	}

	public void setBoatNameSize(String boatNameSize) {
		this.boatNameSize = boatNameSize;
	}
}
