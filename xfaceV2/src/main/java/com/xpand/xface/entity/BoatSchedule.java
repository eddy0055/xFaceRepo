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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_boat_schedule")
public class BoatSchedule extends EntityBase implements Serializable{
	private static final long serialVersionUID = 1L;	
	
	@Id
	@Column(name="bscheduleId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer bscheduleId;
				
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateArrival", updatable=false)	
	private Date dateArrival;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateDeparture", nullable=false)	
	private Date dateDeparture;
	
	@ManyToOne
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="boatId")
	private Boat boat;
	
	@ManyToOne
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="gateId")
	private HWGateInfo hwGateInfo;

	public Integer getBscheduleId() {
		return bscheduleId;
	}

	public void setBscheduleId(Integer bscheduleId) {
		this.bscheduleId = bscheduleId;
	}

	public Date getDateArrival() {
		return dateArrival;
	}

	public void setDateArrival(Date dateArrival) {
		this.dateArrival = dateArrival;
	}

	public Date getDateDeparture() {
		return dateDeparture;
	}

	public void setDateDeparture(Date dateDeparture) {
		this.dateDeparture = dateDeparture;
	}

	public Boat getBoat() {
		return boat;
	}

	public void setBoat(Boat boat) {
		this.boat = boat;
	}

	public HWGateInfo getHwGateInfo() {
		return hwGateInfo;
	}

	public void setHwGateInfo(HWGateInfo hwGateInfo) {
		this.hwGateInfo = hwGateInfo;
	}
	
}
