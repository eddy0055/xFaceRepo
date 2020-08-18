package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
@Table(name = "tbl_person_category")
public class PersonCategory implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final Integer UNKNOWN_CATEGORY_ID = -1;
	public static final String UNKNOWN_CATEGORY_NAME = "Unknown";
	public static final String UNKNOWN_CATEGORY_DESC = "Unknown";
	public static final String UNKNOWN_CATEGORY_COLOR_CODE = "#FFFFFF";
	public PersonCategory() {
		
	}
	public PersonCategory(Integer Id, String name, String desc, String colorCode) {
		this.categoryId = Id;
		this.categoryName = name;
		this.categoryDesc = desc;
		this.categoryColorCode = colorCode;
	}
	
	@Id
	@Column(name = "categoryId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer categoryId;
	
	@ManyToOne
	@JoinColumn(name="ipcanalId")
	private HWIPCAnalyzeList hwIPCAnalyzeList;
			
	@Column(name = "categoryName", length = 100, nullable=false)
	private String categoryName;
	
	@Column(name = "categoryDesc", length = 100, nullable=true)
	private String categoryDesc;
	
	@Column(name = "categoryColorCode", length = 10, nullable=false)
	private String categoryColorCode;

	@JsonIgnore
	@OneToMany(mappedBy = "personCategory")
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
	
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryColorCode() {
		return categoryColorCode;
	}
	public void setCategoryColorCode(String categoryColorCode) {
		this.categoryColorCode = categoryColorCode;
	}
	public Set<PersonInfo> getPersonInfos() {
		return personInfos;
	}
	public void setPersonInfos(Set<PersonInfo> personInfos) {
		this.personInfos = personInfos;
	}
	public String getCategoryDesc() {
		return categoryDesc;
	}
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}
	public HWIPCAnalyzeList getHwIPCAnalyzeList() {
		return hwIPCAnalyzeList;
	}
	public void setHwIPCAnalyzeList(HWIPCAnalyzeList hwIPCAnalyzeList) {
		this.hwIPCAnalyzeList = hwIPCAnalyzeList;
	}	
	
}
