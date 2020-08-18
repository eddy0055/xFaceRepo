package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.PersonRegisterDateDAO;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonRegisterDate;
import com.xpand.xface.service.PersonRegDateService;
import com.xpand.xface.util.LogUtil;

@SessionScope
@Component
public class PersonRegDateServiceImpl implements PersonRegDateService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	PersonRegisterDateDAO personRegisterDateDAO;
	
	@Override
	public PersonInfo resetPersonRegDateId(PersonInfo personInfo) {
		for (PersonRegisterDate personRegDate: personInfo.getPersonRegisterDateList()) {
			personRegDate.setPersonInfo(personInfo);
			//personRegDate.setPregdId(null);
		}
		return personInfo;
	}
	
	@Override
	public ResultStatus deleteByPersonInfo(String transactionId, PersonInfo personInfo) {		
		ResultStatus result = new ResultStatus();
		try {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete person reg date under person cert:"+personInfo.getCertificateNo()));
			if (personInfo.getPersonId()==null) {
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "person id is null no need to delete person reg date"));
			}else {
				this.personRegisterDateDAO.deleteByPersonInfo(personInfo);
				Logger.info(this, LogUtil.getLogInfo(transactionId, "deletev person reg date under person cert: "+personInfo.getCertificateNo()+" success"));
			}			
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete person reg date under person cert: "+personInfo.getCertificateNo(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "reg date of person cert:"+personInfo.getCertificateNo());
		}	
		return result;		
	}

	@Override
	public PersonInfo addPersonRegDate(PersonInfo personInfo, Date regDate, String agentName, String logonUserName) {
		//check travel date already in register date or not
		Iterator<PersonRegisterDate> personRegDateList = personInfo.getPersonRegisterDateList().iterator();
		PersonRegisterDate personRegDate = null;
		boolean foundDate = false;
		while (personRegDateList.hasNext()) {
			personRegDate = personRegDateList.next();
			if (personRegDate.getRegisterDate().compareTo(regDate)==0) {
				personRegDate.setAgentName(agentName);				
				foundDate = true;
				break;
			}
		}
		if (!foundDate) {
			personRegDate = new PersonRegisterDate();
			personRegDate.setRegisterDate(regDate);
			personRegDate.setUserCreated(logonUserName);
			personRegDate.setAgentName(agentName);
			personInfo.getPersonRegisterDateList().add(personRegDate);
		}
		return personInfo;
	}

}
