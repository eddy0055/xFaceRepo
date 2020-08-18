package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.StringUtil;

@Entity
@Table(name = "tbl_person_info", 
	indexes = { @Index(name = "tbl_person_info_idx1", columnList = "certificateNo"),
			@Index(name = "tbl_person_info_idx2", columnList = "hwPeopleId")})
public class PersonInfo extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SIZE_OF_ADDRESS = 500;
	public static final byte STATUS_NEW = 0; //need to add to vcn
	public static final byte STATUS_UPDATE_TO_VCM_ZK = 1; //already update to vcm, zk
	public static final byte STATUS_REMOVE_FROM_VCM_ZK = 2; //already remove from vcm,zk and may need to re-update to vcn,zk
	public static final byte STATUS_UPDATE_ON_XFACE = 3; //update person on xFace need to check do we need to remove/add to VCM, ZK
	
	public static final int UNKNONW_PERSON_ID = -1;

	@Id
	@Column(name = "personId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer personId;

	@Column(name = "personCode", length = 30)
	protected String personCode;
	
	@ManyToOne
	@JoinColumn(name="titleId", nullable=false)
	protected PersonTitle personTitle;	
	
	@Transient
	protected String personTitleCode;
	
	@ManyToOne
	@JoinColumn(name="certificateId", nullable=false)
	protected PersonCertificate personCertificate;
	
	@Transient
	protected String personCertificateCode;
	
	@Column(name = "certificateNo", length = 100, nullable=false)
	//@Convert(converter = StringEncryptDecryptConverter.class)
	@ColumnTransformer(read = "AES_DECRYPT(FROM_BASE64(certificate_no), '"+ConstUtil.MY_VALUE_TEST+"')", write = "TO_BASE64(AES_ENCRYPT(?, '"+ConstUtil.MY_VALUE_TEST+"'))")
	protected String certificateNo;
	
	@Column(name = "fullName", length = 200, nullable=false)
	//@Convert(converter = StringEncryptDecryptConverter.class)
	@ColumnTransformer(read = "AES_DECRYPT(FROM_BASE64(full_name), '"+ConstUtil.MY_VALUE_TEST+"')", write = "TO_BASE64(AES_ENCRYPT(?, '"+ConstUtil.MY_VALUE_TEST+"'))")
	protected String fullName;

	
	@Column(name = "addressInfo", length = PersonInfo.SIZE_OF_ADDRESS, nullable=true)
	//@Convert(converter = StringEncryptDecryptConverter.class)
	@ColumnTransformer(read = "AES_DECRYPT(FROM_BASE64(address_info), '"+ConstUtil.MY_VALUE_TEST+"')", write = "TO_BASE64(AES_ENCRYPT(?, '"+ConstUtil.MY_VALUE_TEST+"'))")
	protected String addressInfo;
	
	@ManyToOne
	@JoinColumn(name="categoryId", nullable=false)
	protected PersonCategory personCategory;
	
	@Transient
	protected String personCategoryCode;
	
	@Transient
	protected String personNationalityCode;

	@JsonIgnore
	@OneToMany(mappedBy = "personInfo", fetch=FetchType.LAZY)	
	protected Set<HWAlarmHist> hwAlarmHistList;
	
			
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false, nullable=false)	
	protected Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", updatable=false, nullable=false)	
	protected Date dateUpdated;
			
	@Column(name="userCreated", updatable=false, nullable=false, length=50)
	String userCreated;
	@Column(name="userUpdated", nullable=false, length=50)
	String userUpdated;
	
	@Column(name="hwPeopleId",length=50, updatable= false)
	protected String hwPeopleId;
		
	@Column(name="personPhoto",length=50, nullable=false)
	@Lob	
	@Basic(fetch = FetchType.LAZY)
	protected String personPhoto;
	
	@Column(name="contactNo",length=50)
	@ColumnTransformer(read = "AES_DECRYPT(FROM_BASE64(contact_no), '"+ConstUtil.MY_VALUE_TEST+"')", write = "TO_BASE64(AES_ENCRYPT(?, '"+ConstUtil.MY_VALUE_TEST+"'))")
	protected String contactNo;
	
	@ManyToOne
	@JoinColumn(name="nationalityId", nullable=false)
	protected PersonNationality nationality;

	
	@JsonIgnore
	@OneToMany(mappedBy = "personInfo", cascade = CascadeType.ALL, fetch=FetchType.EAGER)	
	@OnDelete(action = OnDeleteAction.CASCADE)
	protected Set<PersonRegisterDate> personRegisterDateList;
	
	@Column(name="personVCMStatus")
	protected byte personVCMStatus;
		
	public PersonInfo() {}
	public PersonInfo(PersonInfo personInfo) {
		super();
		if (personInfo.personId!=null) {
			this.personId = personInfo.getPersonId();
		}
		this.personCode = personInfo.getPersonCode();
		this.personTitle = personInfo.getPersonTitle();
		this.personCertificate = personInfo.getPersonCertificate();
		this.nationality = personInfo.getNationality();
		this.certificateNo = personInfo.getCertificateNo();		
		this.fullName = personInfo.getFullName();		
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
		return this.fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public PersonCertificate getPersonCertificate() {
		return personCertificate;
	}

	public void setPersonCertificate(PersonCertificate personCertificate) {
		this.personCertificate = personCertificate;
	}

	public String getCertificateNo() {
		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
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
				+ ", personCertificate=" + personCertificate + ", certificateNo=" + certificateNo
				+ ", fullName=" + fullName + ", personCategory=" + personCategory
				+ ", hwPeopleId=" + hwPeopleId + "]";
	}
	public Set<HWAlarmHist> getHwAlarmHistList() {
		return hwAlarmHistList;
	}
	public void setHwAlarmHistList(Set<HWAlarmHist> hwAlarmHistList) {
		this.hwAlarmHistList = hwAlarmHistList;
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
	public Set<PersonRegisterDate> getPersonRegisterDateList() {
		if (this.personRegisterDateList==null) {
			this.personRegisterDateList = new HashSet<PersonRegisterDate>();
		}
		return personRegisterDateList;
	}
	public void setPersonRegisterDateList(Set<PersonRegisterDate> personRegisterDateList) {
		this.personRegisterDateList = personRegisterDateList;
	}
	public String getPersonTitleCode() {
		return personTitleCode;
	}
	public void setPersonTitleCode(String personTitleCode) {
		this.personTitleCode = personTitleCode;
	}
	public String getPersonCertificateCode() {
		return personCertificateCode;
	}
	public void setPersonCertificateCode(String personCertificateCode) {
		this.personCertificateCode = personCertificateCode;
	}
	public String getPersonCategoryCode() {
		return personCategoryCode;
	}
	public void setPersonCategoryCode(String personCategoryCode) {
		this.personCategoryCode = personCategoryCode;
	}
	
	public void createPersonInfo(Object[] columns) {
		//0=per.person_id, 1=per.certificate_no, 2=per.full_name, 3=per.person_code
		//,4=per.hw_people_id, 5=per.person_photo
		this.personId = StringUtil.stringToInteger(columns[0]+"",-1);
		this.certificateNo = columns[1]+"";
		this.fullName = columns[2]+"";
		this.personCode = columns[3]==null? "":columns[3]+"";
		this.hwPeopleId = columns[4]==null? "":columns[4]+"";
		this.personPhoto = columns[5]==null? ConstUtil.NO_IMAGE_DISPLAY:columns[5]+"";
		this.nationality = new PersonNationality();
		this.nationality.setNationalityName(columns[6]+"");
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public PersonNationality getNationality() {
		return nationality;
	}
	public void setNationality(PersonNationality nationality) {
		this.nationality = nationality;
	}
	public String getPersonNationalityCode() {
		return personNationalityCode;
	}
	public void setPersonNationalityCode(String personNationalityCode) {
		this.personNationalityCode = personNationalityCode;
	}
	public byte getPersonVCMStatus() {
		return personVCMStatus;
	}
	public void setPersonVCMStatus(byte personVCMStatus) {
		this.personVCMStatus = personVCMStatus;
	}
	
}
