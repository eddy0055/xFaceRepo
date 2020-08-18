package com.xpand.xface.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xpand.xface.entity.PersonInfo;

public class PersonNotification extends PersonInfo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer alarmId=1;
	private String livePhoto="base64";
	private String latestIPCCode="02117790000000000101#ab8df621bf3f4d91b61ce8cf5100c01a";	
	private String latestVCNIP="192.168.2.200";
	private String vcnUsername="xpandapp";
	private String vcnPassword="Xpand@456";
	private String vcnPort="9900";
	private int percentMatch;
	private Date alarmDate;
	private String alarmCode;
	public PersonNotification() {
		
	}
	public PersonNotification(PersonInfo personInfo) {
		this.personId = personInfo.getPersonId();
		this.personCode = personInfo.getPersonCode();
		this.personTitle = personInfo.getPersonTitle();
		this.personCertification = personInfo.getPersonCertification();		
		this.certificationNo = personInfo.getCertificationNo();
		this.firstName = personInfo.getFirstName();
		this.lastName = personInfo.getLastName();
		this.personCategory = personInfo.getPersonCategory();
		this.hwPeopleId = personInfo.getHwPeopleId();		
		this.personPhoto = personInfo.getPersonPhoto();
	}
	public Integer getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(Integer alarmId) {
		this.alarmId = alarmId;
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
	public int getPercentMatch() {
		return percentMatch;
	}
	public void setPercentMatch(int percentMatch) {
		this.percentMatch = percentMatch;
	}
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss(SSS)", timezone= "Asia/Bangkok")
	public Date getAlarmDate() {
		return alarmDate;
	}
	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}
	public String getAlarmCode() {
		return alarmCode;
	}
	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}
	
}
