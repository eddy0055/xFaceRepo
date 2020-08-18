package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name="tbl_boat")
public class Boat extends EntityBase implements Serializable{
	private static final long serialVersionUID = 1L;	
	public static final String BOAT_NONE_CODE = "none";
	public static final String BOAT_NONE_SHORT_NAME = "-";
	
	@Id
	@Column(name="boatId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer boatId;
			
	@Column(name="boatCode", length = 50, nullable=false)
	private String boatCode;	
	
	@Column(name="boatName", length = 100, nullable=false)
	private String boatName;
	
	@Column(name="boatShortName", length = 20, nullable=false)
	private String boatShortName;
	
	@Column(name="cardNo", length = 20, nullable=false)
	private String cardNo;
	
	@Column(name="zkPin", length = 20, nullable=false)
	private String zkPin;
						
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", nullable=false)	
	private Date dateUpdated;
	
	@JsonIgnore
	@Column(name="userCreated", updatable=false)
	private String userCreated;
	
	@JsonIgnore
	@Column(name="userUpdated", nullable=false)
	private String userUpdated;
	
	@OneToMany(mappedBy="boat", cascade = CascadeType.REFRESH, fetch=FetchType.LAZY)
	private Set<BoatSchedule> boatScheduleList;
					
	public Integer getBoatId() {
		return boatId;
	}

	public void setBoatId(Integer boatId) {
		this.boatId = boatId;
	}

	public String getBoatCode() {
		return boatCode;
	}

	public void setBoatCode(String boatCode) {
		this.boatCode = boatCode;
	}

	public String getBoatName() {
		return boatName;
	}

	public void setBoatName(String boatName) {
		this.boatName = boatName;
	}

	public String getBoatShortName() {
		return boatShortName;
	}

	public void setBoatShortName(String boatShortName) {
		this.boatShortName = boatShortName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	

	public Set<BoatSchedule> getBoatScheduleList() {
		return boatScheduleList;
	}

	public void setBoatScheduleList(Set<BoatSchedule> boatScheduleList) {
		this.boatScheduleList = boatScheduleList;
	}
		
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
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

	public String getZkPin() {
		return zkPin;
	}

	public void setZkPin(String zkPin) {
		this.zkPin = zkPin;
	}	
}
