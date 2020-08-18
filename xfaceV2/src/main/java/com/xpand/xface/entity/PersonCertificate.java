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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_person_certificate")
public class PersonCertificate extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Id
	@Column(name = "certificateId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer certificateId;
		
	@Column(name = "certificateCode", length = 50, nullable=false)
	private String certificateCode;
	
	@Column(name = "certificateName", length = 100, nullable=false)
	private String certificateName;
	@Column(name = "certificateDesc", length = 100)
	private String certificateDesc;
	@Column(name = "thirdPartyCode", length = 30)
	private String thirdPartyCode;
	

	@JsonIgnore
	@OneToMany(mappedBy = "personCertificate", cascade = CascadeType.REFRESH)
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
	public Integer getCertificateId() {
		return certificateId;
	}
	public void setCertificateId(Integer certificateId) {
		this.certificateId = certificateId;
	}
	public String getCertificateName() {
		return certificateName;
	}
	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}
	public Set<PersonInfo> getPersonInfoList() {
		return personInfoList;
	}
	public void setPersonInfoList(Set<PersonInfo> personInfoList) {
		this.personInfoList = personInfoList;
	}
	public String getCertificateDesc() {
		return certificateDesc;
	}
	public void setCertificateDesc(String certificateDesc) {
		this.certificateDesc = certificateDesc;
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
	public String getCertificateCode() {
		return certificateCode;
	}
	public void setCertificateCode(String certificateCode) {
		this.certificateCode = certificateCode;
	}	
}
