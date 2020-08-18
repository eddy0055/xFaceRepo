package com.xpand.xface.bean;

import java.util.Set;

import com.xpand.xface.entity.EntityBase;
import com.xpand.xface.entity.PersonRegisterDate;

public class CustomerRegister extends EntityBase {
	private String certificateId;	
	private String customerName;
	private String customerAddress;
	private String agentName;
	private String customerImage;
	private String nationality;
	private String contactNo;
	private String travelDate;
	private String hwPeopleId;
	private Integer personId;
	private String logonUserName;
	private Set<PersonRegisterDate> personRegisterDateList;
	public String getCertificateId() {
		return certificateId;
	}
	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getCustomerImage() {
		return customerImage;
	}
	public void setCustomerImage(String customerImage) {
		this.customerImage = customerImage;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getTravelDate() {
		return travelDate;
	}
	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate;
	}
	@Override
	public String toString() {
		return "CustomerRegister [certificateId=" + certificateId + ", customerName=" + customerName + ", customerAddress=" + customerAddress + ", agentName=" + agentName + ", customerImage=" + customerImage + ", nationality=" + nationality + ", contactNo=" + contactNo + ", travelDate=" + travelDate
				+ "]";
	}
	public String toStringNoImage() {
		return "CustomerRegister [certificateId=" + certificateId + ", customerName=" + customerName + ", customerAddress=" + customerAddress + ", agentName=" + agentName + ", nationality=" + nationality + ", contactNo=" + contactNo + ", travelDate=" + travelDate
				+ "]";
	}
	public String getHwPeopleId() {
		return hwPeopleId;
	}
	public void setHwPeopleId(String hwPeopleId) {
		this.hwPeopleId = hwPeopleId;
	}
	public Integer getPersonId() {
		return personId;
	}
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
	public String getLogonUserName() {
		return logonUserName;
	}
	public void setLogonUserName(String logonUserName) {
		this.logonUserName = logonUserName;
	}
	public Set<PersonRegisterDate> getPersonRegisterDateList() {
		return personRegisterDateList;
	}
	public void setPersonRegisterDateList(Set<PersonRegisterDate> personRegisterDateList) {
		this.personRegisterDateList = personRegisterDateList;
	}	
}
