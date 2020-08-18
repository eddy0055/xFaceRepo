package com.xpand.xface.bean.queue;

import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.entity.PersonInfo;

public class QueueCustomerRegister {
	private CustomerRegister customerRegister;
	private PersonInfo existingPersonInfo;
	public QueueCustomerRegister(CustomerRegister customerRegister, PersonInfo existingPersonInfo) {
		this.customerRegister = customerRegister;
		this.existingPersonInfo = existingPersonInfo;
	}
	public CustomerRegister getCustomerRegister() {
		return customerRegister;
	}
	public void setCustomerRegister(CustomerRegister customerRegister) {
		this.customerRegister = customerRegister;
	}
	public PersonInfo getExistingPersonInfo() {
		return existingPersonInfo;
	}
	public void setExistingPersonInfo(PersonInfo existingPersonInfo) {
		this.existingPersonInfo = existingPersonInfo;
	}
	
}
