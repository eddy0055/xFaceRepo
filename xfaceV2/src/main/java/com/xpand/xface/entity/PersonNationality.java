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
@Table(name = "tbl_person_nationality")
public class PersonNationality extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "nationalityId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer nationalityId;
		
	@Column(name = "nationalityCode", length = 100, nullable=false)
	private String nationalityCode;
	
	@Column(name = "nationalityName", length = 100, nullable=false)
	private String nationalityName;
	
	@JsonIgnore
	@OneToMany(mappedBy = "nationality", cascade = CascadeType.REFRESH, fetch=FetchType.LAZY)	
	private Set<PersonInfo> personInfoList;
	
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false, nullable=false)	
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", updatable=false, nullable=false)	
	private Date dateUpdated;
			
	@Column(name="userCreated", updatable=false, nullable=false)
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
	public Integer getNationalityId() {
		return nationalityId;
	}
	public void setNationalityId(Integer nationalityId) {
		this.nationalityId = nationalityId;
	}
	public String getNationalityCode() {
		return nationalityCode;
	}
	public void setNationalityCode(String nationalityCode) {
		this.nationalityCode = nationalityCode;
	}
	public String getNationalityName() {
		return nationalityName;
	}
	public void setNationalityName(String nationalityName) {
		this.nationalityName = nationalityName;
	}
	public Set<PersonInfo> getPersonInfoList() {
		return personInfoList;
	}
	public void setPersonInfoList(Set<PersonInfo> personInfoList) {
		this.personInfoList = personInfoList;
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
	
}
