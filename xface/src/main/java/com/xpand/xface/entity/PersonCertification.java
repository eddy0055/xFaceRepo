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
@Table(name = "tbl_person_certification")
public class PersonCertification implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Id
	@Column(name = "certificationId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer certificationId;
		
	@Column(name = "certificationName", length = 100, nullable=false)
	private String certificationName;
	@Column(name = "certificationDesc", length = 100)
	private String certificationDesc;
	@Column(name = "thirdPartyCode", length = 30)
	private String thirdPartyCode;
	

	@JsonIgnore
	@OneToMany(mappedBy = "personCertification", cascade = CascadeType.REFRESH)
	private Set<PersonInfo> personInfos;
			
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
	public Integer getCertificationId() {
		return certificationId;
	}
	public void setCertificationId(Integer certificationId) {
		this.certificationId = certificationId;
	}
	public String getCertificationName() {
		return certificationName;
	}
	public void setCertificationName(String certificationName) {
		this.certificationName = certificationName;
	}
	public Set<PersonInfo> getPersonInfos() {
		return personInfos;
	}
	public void setPersonInfos(Set<PersonInfo> personInfos) {
		this.personInfos = personInfos;
	}
	public String getCertificationDesc() {
		return certificationDesc;
	}
	public void setCertificationDesc(String certificationDesc) {
		this.certificationDesc = certificationDesc;
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
	public String getThirdPartyCode() {
		return thirdPartyCode;
	}
	public void setThirdPartyCode(String thirdPartyCode) {
		this.thirdPartyCode = thirdPartyCode;
	}	
}
