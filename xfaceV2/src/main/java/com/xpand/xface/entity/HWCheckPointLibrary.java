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
@Table(name="tbl_checkpoint_library")	
public class HWCheckPointLibrary implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="chkponlibId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer chkponlibId;
	
	@Column(name="libraryType", nullable=false)
	private Integer libraryType;
	
	@Column(name="libraryName",length=50, nullable=false)
	private String libraryName;
	
	@Column(name="libraryId",length=50)
	private String libraryId;
	
	@Column(name="checkPointName",length=50, nullable=false)
	private String checkPointName;
	
	@Column(name="checkPointId",length=50)
	private String checkPointId;
		
	//1 = license plate, 2 = face image
	@Column(name="checkPointType",length=20, nullable=false)
	private String checkPointType;	
	
//	@JsonIgnore	
	@OneToMany(mappedBy="hwCheckPointLibrary", cascade = CascadeType.ALL, fetch=FetchType.LAZY)	
	private Set<PersonCategory> personCategoryList;
	
	@JsonIgnore
	@OneToMany(mappedBy="hwCheckPointLibrary")	
	private Set<HWTaskList> hwTaskListList;
	
	@JsonIgnore
	@OneToMany(mappedBy="hwCheckPointLibrary")	
	private Set<HWIPC> hwIPCList;
	
	@JsonIgnore				
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", nullable=false)	
	private Date dateUpdated;
	
	@Column(name="userCreated", updatable=false)
	String userCreated;
	
	@Column(name="userUpdated", nullable=false)
	String userUpdated;
	
	@ManyToOne
	@JoinColumn(name="vcmId")
	private HWVCM hwVCM;
	
	//value > 0 mean incase alarm from same person  
	@Column(name="ignoreSamePersonTime")
	private Integer ignoreSamePersonTime;
	
	@ManyToOne
	@JoinColumn(name="ignSMPTuId", referencedColumnName="tuId")
	private TimeUnit ignoreSamePersonTimeUnit;
	
	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	public Integer getChkponlibId() {
		return chkponlibId;
	}

	public void setChkponlibId(Integer chkponlibId) {
		this.chkponlibId = chkponlibId;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public String getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

	public String getCheckPointName() {
		return checkPointName;
	}

	public void setCheckPointName(String checkPointName) {
		this.checkPointName = checkPointName;
	}

	public String getCheckPointId() {
		return checkPointId;
	}

	public void setCheckPointId(String checkPointId) {
		this.checkPointId = checkPointId;
	}

	public String getCheckPointType() {
		return checkPointType;
	}

	public void setCheckPointType(String checkPointType) {
		this.checkPointType = checkPointType;
	}

	public Set<PersonCategory> getPersonCategoryList() {
		return personCategoryList;
	}

	public void setPersonCategoryList(Set<PersonCategory> personCategoryList) {
		this.personCategoryList = personCategoryList;
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

	public HWVCM getHwVCM() {
		return hwVCM;
	}

	public void setHwVCM(HWVCM hwVCM) {
		this.hwVCM = hwVCM;
	}

	public Integer getIgnoreSamePersonTime() {
		return ignoreSamePersonTime;
	}

	public void setIgnoreSamePersonTime(Integer ignoreSamePersonTime) {
		this.ignoreSamePersonTime = ignoreSamePersonTime;
	}

	public TimeUnit getIgnoreSamePersonTimeUnit() {
		return ignoreSamePersonTimeUnit;
	}

	public void setIgnoreSamePersonTimeUnit(TimeUnit ignoreSamePersonTimeUnit) {
		this.ignoreSamePersonTimeUnit = ignoreSamePersonTimeUnit;
	}

	public Integer getLibraryType() {
		return libraryType;
	}

	public void setLibraryType(Integer libraryType) {
		this.libraryType = libraryType;
	}

	public Set<HWTaskList> getHwTaskListList() {
		return hwTaskListList;
	}

	public void setHwTaskListList(Set<HWTaskList> hwTaskListList) {
		this.hwTaskListList = hwTaskListList;
	}

	public Set<HWIPC> getHwIPCList() {
		return hwIPCList;
	}

	public void setHwIPCList(Set<HWIPC> hwIPCList) {
		this.hwIPCList = hwIPCList;
	}			

}
