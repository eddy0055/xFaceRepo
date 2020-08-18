package com.xpand.xface.bean;

import java.util.List;

public class WebFEParam {
	private String userName;
	private String roleName;
	private String buildingName;
	private String floorName;
	private Integer floorId;
	private String areaName;
	private Integer areaId;
	private Integer buildingId;
	private String personCode;
	private String personTitle;
	private String categoryName;
	private String certificationName;
	private String personCertificationNo;
	private List<SearchPersonCondition> searchPersonConditionList;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getFloorName() {
		return floorName;
	}
	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public Integer getFloorId() {
		return floorId;
	}
	public void setFloorId(Integer floorId) {
		this.floorId = floorId;
	}
	public Integer getAreaId() {
		return areaId;
	}
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	public Integer getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(Integer buildingId) {
		this.buildingId = buildingId;
	}
	public String getPersonCode() {
		return personCode;
	}
	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}
	public String getPersonTitle() {
		return personTitle;
	}
	public void setPersonTitle(String personTitle) {
		this.personTitle = personTitle;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCertificationName() {
		return certificationName;
	}
	public void setCertificationName(String certificationName) {
		this.certificationName = certificationName;
	}
	public List<SearchPersonCondition> getSearchPersonConditionList() {
		return searchPersonConditionList;
	}
	public void setSearchPersonConditionList(List<SearchPersonCondition> searchPersonConditionList) {
		this.searchPersonConditionList = searchPersonConditionList;
	}
	public String getPersonCertificationNo() {
		return personCertificationNo;
	}
	public void setPersonCertificationNo(String personCertificationNo) {
		this.personCertificationNo = personCertificationNo;
	}

}