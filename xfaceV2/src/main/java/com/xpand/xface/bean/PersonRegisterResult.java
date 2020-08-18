package com.xpand.xface.bean;

import java.util.Date;

import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.util.StringUtil;

public class PersonRegisterResult {
	private int personId;
	private String certificateNo;
	private String certificateThirdPartyCode;
	private String fullName;
	private String hwPeopleId;
	private String zkPin;
	private String base64Image;
	private String cardNo;
	private Date registerDate;
	private byte personVCMStatus;
	private ResultStatus result;
	public PersonRegisterResult() {				
	}		
	public void createPersonRegister(Object[] columns) {
		//SELECT per.person_id,certificate_no,full_name,person_photo,hw_people_id,third_party_code,personvcmstatus 
		this.personId = StringUtil.checkNull(""+columns[0])?PersonInfo.UNKNONW_PERSON_ID:StringUtil.stringToInteger(""+columns[0], PersonInfo.UNKNONW_PERSON_ID);
		this.certificateNo = ""+columns[1];		
		this.fullName = ""+columns[2];
		this.base64Image = ""+columns[3];
		this.hwPeopleId = ""+columns[4];	
		this.certificateThirdPartyCode = ""+columns[5];
		this.personVCMStatus = StringUtil.stringToByte(""+columns[6], PersonInfo.STATUS_NEW);
		
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getCertificateThirdPartyCode() {
		return certificateThirdPartyCode;
	}
	public void setCertificateThirdPartyCode(String certificateThirdPartyCode) {
		this.certificateThirdPartyCode = certificateThirdPartyCode;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getBase64Image() {
		return base64Image;
	}
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public String getHwPeopleId() {
		return hwPeopleId;
	}
	public void setHwPeopleId(String hwPeopleId) {
		this.hwPeopleId = hwPeopleId;
	}	
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public byte getPersonVCMStatus() {
		return personVCMStatus;
	}
	public void setPersonVCMStatus(byte personVCMStatus) {
		this.personVCMStatus = personVCMStatus;
	}
	public String getZkPin() {
		return zkPin;
	}
	public void setZkPin(String zkPin) {
		this.zkPin = zkPin;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	
}
