package com.xpand.xface.bean;

import java.util.ArrayList;
import java.util.List;

public class PersonRegisterResultList {
	private ResultStatus result;
	private List<PersonRegisterResult> personRegisterResultList;
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public List<PersonRegisterResult> getPersonRegisterResultList() {
		if (this.personRegisterResultList==null) {
			this.personRegisterResultList = new ArrayList<PersonRegisterResult>();
		}
		return personRegisterResultList;
	}
	public void setPersonRegisterResultList(List<PersonRegisterResult> personRegisterResultList) {
		this.personRegisterResultList = personRegisterResultList;
	}		
}
