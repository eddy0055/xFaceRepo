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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_person_info", 
	indexes = { @Index(name = "tbl_person_info_idx1", columnList = "certificationNo"),
			@Index(name = "tbl_person_info_idx2", columnList = "hwPeopleId")})
public class PersonInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SIZE_OF_ADDRESS = 500;

	@Id
	@Column(name = "personId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer personId;

	@Column(name = "personCode", length = 30)
	protected String personCode;
	
	@ManyToOne
	@JoinColumn(name="titleId", nullable=false)
	protected PersonTitle personTitle;	
	
	@ManyToOne
	@JoinColumn(name="certificationId", nullable=false)
	protected PersonCertification personCertification;
	
	@Column(name = "certificationNo", length = 100, nullable=false)
	protected String certificationNo;
	
	@Column(name = "firstName", length = 200, nullable=false)
	protected String firstName;

	@Column(name = "lastName", length = 200, nullable=true)
	protected String lastName;

	@Column(name = "addressInfo", length = PersonInfo.SIZE_OF_ADDRESS, nullable=true)
	protected String addressInfo;
	
	@ManyToOne
	@JoinColumn(name="categoryId", nullable=false)
	protected PersonCategory personCategory;

	@JsonIgnore
	@OneToMany(mappedBy = "personInfo", fetch=FetchType.LAZY)	
	protected Set<HWAlarmHist> hwAlarmHists;
	
			
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false, nullable=false)	
	protected Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", updatable=false, nullable=false)	
	protected Date dateUpdated;
			
	@Column(name="userCreated", updatable=false, nullable=false)
	String userCreated;
	@Column(name="userUpdated", nullable=false)
	String userUpdated;
	
	@Column(name="hwPeopleId",length=50)
	protected String hwPeopleId;
		
	@Column(name="personPhoto",length=50, nullable=false)
	@Lob	
	@Basic(fetch = FetchType.LAZY)
	protected String personPhoto;
	
	public PersonInfo() {}
	public PersonInfo(PersonInfo personInfo) {
		super();
		if (personInfo.personId!=null) {
			this.personId = personInfo.getPersonId();
		}
		this.personCode = personInfo.getPersonCode();
		this.personTitle = personInfo.getPersonTitle();
		this.personCertification = personInfo.getPersonCertification();
		this.certificationNo = personInfo.getCertificationNo();		
		this.firstName = personInfo.getFirstName();
		this.lastName = personInfo.getLastName();
		this.personCategory = personInfo.getPersonCategory();
		this.personPhoto = personInfo.getPersonPhoto();
		//this.hwIPCAnalyzeList = personInfo.getHwIPCAnalyzeList();
	}
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String getPersonCode() {
		return personCode;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	public String getFullName() {
		return (this.firstName + " " + this.lastName).trim();
	}
	
/*	public HWIPCAnalyzeList getHwIPCAnalyzeList() {
		return hwIPCAnalyzeList;
	}

	public void setHwIPCAnalyzeList(HWIPCAnalyzeList hwIPCAnalyzeList) {
		this.hwIPCAnalyzeList = hwIPCAnalyzeList;
	}*/

	public String getHwPeopleId() {
		return hwPeopleId;
	}

	public void setHwPeopleId(String hwPeopleId) {
		this.hwPeopleId = hwPeopleId;
	}

	public PersonTitle getPersonTitle() {
		return personTitle;
	}

	public void setPersonTitle(PersonTitle personTitle) {
		this.personTitle = personTitle;
	}

	public PersonCertification getPersonCertification() {
		return personCertification;
	}

	public void setPersonCertification(PersonCertification personCertification) {
		this.personCertification = personCertification;
	}

	public String getCertificationNo() {
		return certificationNo;
	}

	public void setCertificationNo(String certificationNo) {
		this.certificationNo = certificationNo;
	}

	public PersonCategory getPersonCategory() {
		return personCategory;
	}

	public void setPersonCategory(PersonCategory personCategory) {
		this.personCategory = personCategory;
	}

	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}

	public String getPersonPhoto() {
		return personPhoto;
	}

	public void setPersonPhoto(String personPhoto) {
		this.personPhoto = personPhoto;
	}

	@Override
	@JsonIgnore
	public String toString() {
		return "PersonInfo [personId=" + personId + ", personCode=" + personCode + ", personTitle=" + personTitle
				+ ", personCertification=" + personCertification + ", certificationNo=" + certificationNo
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", personCategory=" + personCategory
				+ ", hwPeopleId=" + hwPeopleId + "]";
	}
	public Set<HWAlarmHist> getHwAlarmHists() {
		return hwAlarmHists;
	}
	public void setHwAlarmHist(Set<HWAlarmHist> hwAlarmHists) {
		this.hwAlarmHists = hwAlarmHists;
	}
	public String getAddressInfo() {
		return addressInfo;
	}
	public void setAddressInfo(String addressInfo) {
		if (addressInfo!=null && addressInfo.length()>PersonInfo.SIZE_OF_ADDRESS) {
			addressInfo = addressInfo.substring(0, PersonInfo.SIZE_OF_ADDRESS-10)+" **cut**";
		}
		this.addressInfo = addressInfo;
	}
	
}
