package com.xpand.xface.bean;

import java.util.List;

public class WebFEParam {
	private String userName;
	private String userCode;
	private String roleCode;
	private Integer roleId;
	private String personCode;
	private String personTitleCode;
	private String personNationalityCode;
	private String personNationalityName;
	private String categoryCode;
	private String pageCode;
	private String certificateCode;
	//for persontrace screen certificate may contain csv aa,bb,ccc
	private String personCertificateNo;
	private String personPhoto;
	private String startDate;
	private String endDate;
	private String fullName;
	private String ipcCodeList;
	private String gateInfoCodeList;
	private int confidenceThreshold;
	private List<SearchPersonCondition> searchPersonConditionList;
	
	private String oldPwd;
	private String newPwd;
	private String appKey;
	private String mapCode;
	private String boatCode;
	//getUserId
	private int userId; 
	
	public String getBoatCode() {
		return boatCode;
	}
	public void setBoatCode(String boatCode) {
		this.boatCode = boatCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
	public String getMapCode() {
		return mapCode;
	}
	public void setMapCode(String mapCode) {
		this.mapCode = mapCode;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getPageCode() {
		return pageCode;
	}
	public void setPagecode(String pageCode) {
		this.pageCode = pageCode;
	}
	
	public String getPersonNationalityCode() {
		return personNationalityCode;
	}
	public void setPersonNationalityCode(String personNationalityCode) {
		this.personNationalityCode = personNationalityCode;
	}
	public String getPersonNationalityName() {
		return personNationalityName;
	}
	public void setPersonNationalityName(String personNationalityName) {
		this.personNationalityName = personNationalityName;
	}
	//Add AppKey
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	
	//persontrace, realtime monitor
	private int webSocketModule;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}	
	public String getPersonCode() {
		return personCode;
	}
	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}	
	public List<SearchPersonCondition> getSearchPersonConditionList() {
		return searchPersonConditionList;
	}
	public void setSearchPersonConditionList(List<SearchPersonCondition> searchPersonConditionList) {
		this.searchPersonConditionList = searchPersonConditionList;
	}
	public String getPersonCertificateNo() {
		return personCertificateNo;
	}
	public void setPersonCertificateNo(String personCertificateNo) {
		this.personCertificateNo = personCertificateNo;
	}
	public String getPersonPhoto() {
		return personPhoto;
	}
	public void setPersonPhoto(String personPhoto) {
		this.personPhoto = personPhoto;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getConfidenceThreshold() {
		return confidenceThreshold;
	}
	public void setConfidenceThreshold(int confidenceThreshold) {
		this.confidenceThreshold = confidenceThreshold;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getPersonTitleCode() {
		return personTitleCode;
	}
	public void setPersonTitleCode(String personTitleCode) {
		this.personTitleCode = personTitleCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCertificateCode() {
		return certificateCode;
	}
	public void setCertificateCode(String certificateCode) {
		this.certificateCode = certificateCode;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public int getWebSocketModule() {
		return webSocketModule;
	}
	public void setWebSocketModule(int webSocketModule) {
		this.webSocketModule = webSocketModule;
	}
	public String getIpcCodeList() {
		return ipcCodeList;
	}
	public void setIpcCodeList(String ipcCodeList) {
		this.ipcCodeList = ipcCodeList;
	}
	public String getGateInfoCodeList() {
		return gateInfoCodeList;
	}
	public void setGateInfoCodeList(String gateInfoCodeList) {
		this.gateInfoCodeList = gateInfoCodeList;
	}
	public String getOldPwd() {
		return oldPwd;
	}
	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}
	public String getNewPwd() {
		return newPwd;
	}
	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	
}