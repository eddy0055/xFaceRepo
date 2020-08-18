package com.xpand.xface.bean;

import java.util.Date;

import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.util.ConstUtil;

public class PersonNotification {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private Integer alarmId=1;
	private String livePhoto="base64";
//	private String latestIPCCode="02117790000000000101#ab8df621bf3f4d91b61ce8cf5100c01a";	
//	private String latestVCNIP="192.168.2.200";
//	private String vcnUsername="xpandapp";
//	private String vcnPassword="Xpand@456";
//	private String vcnPort="9900";
//	private int percentMatch;
	private Date alarmDate;
	private String alarmCode;
	private int personId;
	private String fullName;
	private String gateInfoCode;
	private String gateInfoName;
	private String ipcCode;
	private String ipcName;	
	private String category;
	private String certificateNo;
	private String contactNo;
	private String personPhoto;
	private String nationalityName;
	private int metaScr;
	public PersonNotification() {
		
	}
	public PersonNotification(HWAlarmHist hwAlarm) {
		if (hwAlarm.getPersonInfo()==null) {
			this.personId = -1;
			this.fullName = ConstUtil.UNKNOWN_PERSON_FULLNAME;
			this.category = ConstUtil.UNKNOWN_PERSON_CATEGORY;
			this.certificateNo = ConstUtil.UNKNOWN_PERSON_CERTIFICATE_NO;
		}else {
			this.personId = hwAlarm.getPersonInfo().getPersonId();
			this.fullName = hwAlarm.getPersonInfo().getFullName();
			this.category = hwAlarm.getPersonInfo().getPersonCategory().getCategoryName();
			this.certificateNo = hwAlarm.getPersonInfo().getCertificateNo();
		}
		this.alarmCode = hwAlarm.getAlarmCode();
		this.alarmDate = hwAlarm.getAlarmTime();
		this.gateInfoCode = hwAlarm.getHwIPC().getHwGateInfo().getGateCode()+"";
		this.gateInfoName = hwAlarm.getHwIPC().getHwGateInfo().getGateName();
		this.ipcCode = hwAlarm.getHwIPC().getIpcCode();
		this.ipcName = hwAlarm.getHwIPC().getIpcName();
		this.livePhoto = hwAlarm.getLivePhoto();
		//Add for Alarm monitor IDV
		this.contactNo = hwAlarm.getPersonInfo().getContactNo();
		this.personPhoto = hwAlarm.getPersonInfo().getPersonPhoto();
		this.nationalityName = hwAlarm.getPersonInfo().getNationality().getNationalityName();
		this.metaScr = hwAlarm.getMetaScr();
	}	
	
	public int getMeteScr() {
		return metaScr;
	}
	public void setMetaScr(int metaScr) {
		this.metaScr = metaScr;
	}
	
	
	public String getNationalityName() {
		return nationalityName;
	}
	public void setNationalityName(String nationalityName) {
		this.nationalityName = nationalityName;
	}
	public String getPersonPhoto() {
		return personPhoto;
	}
	public void setPersonPhoto(String personPhoto) {
		this.personPhoto = personPhoto;
	}
	
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	
	
	
	public String getLivePhoto() {
		return livePhoto;
	}
	public void setLivePhoto(String livePhoto) {
		this.livePhoto = livePhoto;
	}		
	//@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss(SSS)", timezone= "Asia/Bangkok")
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
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getIpcName() {
		return ipcName;
	}
	public void setIpcName(String ipcName) {
		this.ipcName = ipcName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getIpcCode() {
		return ipcCode;
	}
	public void setIpcCode(String ipcCode) {
		this.ipcCode = ipcCode;
	}
	public String getGateInfoCode() {
		return gateInfoCode;
	}
	public void setGateInfoCode(String gateInfoCode) {
		this.gateInfoCode = gateInfoCode;
	}
	public String getGateInfoName() {
		return gateInfoName;
	}
	public void setGateInfoName(String gateInfoName) {
		this.gateInfoName = gateInfoName;
	}
	
}
