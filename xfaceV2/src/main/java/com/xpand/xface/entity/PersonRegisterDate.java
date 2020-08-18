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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_person_reg_date")	
public class PersonRegisterDate implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SIZE_OF_ADDRESS = 500;

	@Id
	@Column(name = "pregdId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer pregdId;	
	
	@Column(name="registerDate", updatable=false, nullable=false)
	@Temporal(TemporalType.DATE)
	protected Date registerDate;
			
	@Column(name="userCreated", updatable=false, nullable=false, length=50)
	String userCreated;
		
	
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false, nullable=false)
	protected Date dateCreated;
	
	
	@ManyToOne
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="personId")
	protected PersonInfo personInfo;
	
	@Column(name="agentName", length=50)
	String agentName;
	
	public PersonRegisterDate() {}	
	
	@PrePersist
	protected void onCreate() {
		this.dateCreated = new Date();
	}
	public String getUserCreated() {
		return userCreated;
	}

	public Integer getPregdId() {
		return pregdId;
	}

	public void setPregdId(Integer pregdId) {
		this.pregdId = pregdId;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
}
