package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name="tbl_gate_access_info")
public class HWGateAccessInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "gateaccId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer gateAccId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "gateId")
	private HWGateInfo hwGateInfo;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "eqdirectionId")
	private EquipmentDirection equipmentDirection;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "personId")
	private PersonInfo personInfo;
	
	@Column(name="eventTime")
	private Date eventTime;
	
	@Column(name="cardNo",length=20)
	private String cardNo;

	public Integer getGateAccId() {
		return gateAccId;
	}

	public void setGateAccId(Integer gateAccId) {
		this.gateAccId = gateAccId;
	}

	public HWGateInfo getHwGateInfo() {
		return hwGateInfo;
	}

	public void setHwGateInfo(HWGateInfo hwGateInfo) {
		this.hwGateInfo = hwGateInfo;
	}

	public EquipmentDirection getEquipmentDirection() {
		return equipmentDirection;
	}

	public void setEquipmentDirection(EquipmentDirection equipmentDirection) {
		this.equipmentDirection = equipmentDirection;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
}
