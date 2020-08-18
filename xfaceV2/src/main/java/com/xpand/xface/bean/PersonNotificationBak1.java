package com.xpand.xface.bean;

import java.util.Date;

import com.xpand.xface.util.StringUtil;

public class PersonNotificationBak1 {
	private Integer alarmId=1;
	private String personCode="1";	
	private String firstName="fname";
	private String lastName="lname";
	private String visaType="business"; // Status: Residence or Business Trip
	private String visaValidity="20181201";
	private String hrPhoto="base64";
	private String livePhoto="base64";
	private String latestIPCCode="02117790000000000101#ab8df621bf3f4d91b61ce8cf5100c01a";	
	private String alarmColor="red";
	private String alarmStatus="ok";
	private String thumbnailImage = "";	
	private String latestVCNIP="192.168.2.200";
	private String vcnUsername="xpandapp";
	private String vcnPassword="Xpand@456";
	private String vcnPort="9900";
	private String passportNo="passportNo";
	private String visa="visa";
	private String location="location";
	private String department="Department";
	
	public Integer getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(Integer alarmId) {
		this.alarmId = alarmId;
	}
	public String getPersonCode() {
		return personCode;
	}
	public void setPersonCode(String personCode) {
		this.personCode = personCode;
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
	public String getVisaType() {
		return visaType;
	}
	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}
	public String getVisaValidity() {
		return visaValidity;
	}
	public void setVisaValidity(String visaValidity) {
		this.visaValidity = visaValidity;
	}
	public String getHrPhoto() {
		return hrPhoto;
	}
	public void setHrPhoto(String hrPhoto) {
		this.hrPhoto = hrPhoto;
	}
	public String getLivePhoto() {
		return livePhoto;
	}
	public void setLivePhoto(String livePhoto) {
		this.livePhoto = livePhoto;
	}
	public String getLatestIPCCode() {
		return latestIPCCode;
	}
	public void setLatestIPCCode(String latestIPCCode) {
		this.latestIPCCode = latestIPCCode;
	}
	public String getAlarmColor() {
		return alarmColor;
	}
	public void setAlarmColor(String alarmColor) {
		this.alarmColor = alarmColor;
	}
	public String getAlarmStatus() {
		return alarmStatus;
	}
	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	public String getThumbnailImage() {
		return thumbnailImage;
	}
	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}
	public String getLatestVCNIP() {
		return latestVCNIP;
	}
	public void setLatestVCNIP(String latestVCNIP) {
		this.latestVCNIP = latestVCNIP;
	}
	public String getVcnUsername() {
		return vcnUsername;
	}
	public void setVcnUsername(String vcnUsername) {
		this.vcnUsername = vcnUsername;
	}
	public String getVcnPassword() {
		return vcnPassword;
	}
	public void setVcnPassword(String vcnPassword) {
		this.vcnPassword = vcnPassword;
	}
	public String getVcnPort() {
		return vcnPort;
	}
	public void setVcnPort(String vcnPort) {
		this.vcnPort = vcnPort;
	}
	public String getPassportNo() {
		return passportNo;
	}
	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}
	public Date visaValidityToDate() {
		return StringUtil.stringToDate(this.visaValidity, StringUtil.DATE_FORMAT_YYYYMMDD);
	}
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
	public String getVisa() {
		return visa;
	}
	public void setVisa(String visa) {
		this.visa = visa;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	@Override
	public String toString() {
		return "PersonInfo [alarmId=" + alarmId + ", personCode=" + personCode + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", visaType=" + visaType + ", visaValidity=" + visaValidity + ", hrPhoto="
				+ hrPhoto + ", livePhoto=" + livePhoto + ", latestIPCCode=" + latestIPCCode + ", alarmColor="
				+ alarmColor + ", alarmStatus=" + alarmStatus + ", thumbnailImage=" + thumbnailImage + ", latestVCNIP="
				+ latestVCNIP + ", vcnUsername=" + vcnUsername + ", vcnPassword=" + vcnPassword + ", vcnPort=" + vcnPort
				+ ", passportNo=" + passportNo + ", visa=" + visa + ", location=" + location + ", department="
				+ department + "]";
	}
}
