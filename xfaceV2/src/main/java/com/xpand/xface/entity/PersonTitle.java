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
@Table(name = "tbl_person_title")
public class PersonTitle extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "titleId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer titleId;
		
	@Column(name = "titleCode", length = 50, nullable=false)
	private String titleCode;
	
	@Column(name = "titleName", length = 100, nullable=false)
	private String titleName;
	
	@Column(name = "titleDesc", length = 100, nullable=true)
	private String titleDesc;

	@JsonIgnore
	@OneToMany(mappedBy = "personTitle", cascade = CascadeType.REFRESH)
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

	public Integer getTitleId() {
		return titleId;
	}

	public void setTitleId(Integer titleId) {
		this.titleId = titleId;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public Set<PersonInfo> getPersonInfoList() {
		return personInfoList;
	}
	public void setPersonInfoList(Set<PersonInfo> personInfos) {
		this.personInfoList = personInfoList;
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
		
	public String getTitleDesc() {
		return titleDesc;
	}
	public void setTitleDesc(String titleDesc) {
		this.titleDesc = titleDesc;

	}
	public String getTitleCode() {
		return titleCode;
	}
	public void setTitleCode(String titleCode) {
		this.titleCode = titleCode;
	}	
}
