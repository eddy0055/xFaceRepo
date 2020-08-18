package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.PersonCategoryDAO;
import com.xpand.xface.dao.PersonCertificationDAO;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertification;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.PersonCertificationService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class PersonCertificationServiceImpl implements PersonCertificationService{
	@Autowired
	PersonCertificationDAO personCertificationDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	public List<PersonCertification> findAll() {
		return this.personCertificationDAO.findAll();
	}
	@Override
	public Page<PersonCertification> getPersonCertificationList(Pageable pageable, String className){
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personCertification list:"+pageable.toString()+" class name: "+ className));
		Page<PersonCertification> pagePersonCertification = this.personCertificationDAO.findAll(pageable);		
		return pagePersonCertification;
	}
	@Override
	public PersonCertification findByCertificationName(String certificationName, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find certification name : "+certificationName +" class name : "+className));
		PersonCertification personCertification = this.personCertificationDAO.findByCertificationName(certificationName);
		return personCertification;		
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus delete(String transactionId,String logonUserName,String certificationName) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCertification start"));
		ResultStatus result = new ResultStatus();
		PersonCertification personCertification = this.personCertificationDAO.findByCertificationName(certificationName);
		if (personCertification != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personCertification "+personCertification.getCertificationName()+"["+ personCertification.getCertificationId()+"] then delete"));
		    this.personCertificationDAO.deleteBypersonCertification(personCertification.getCertificationId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personCertification :"+ personCertification.getCertificationName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCertification "+personCertification.getCertificationName()+"["+  personCertification.getCertificationId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATION, SystemAudit.MOD_SUB_ALL
					, "delete personCertification :"+certificationName+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificationName :"+certificationName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personCertification name :"+certificationName);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCertification done with result "+result.toString()));
		return result;	
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, PersonCertification personCertification) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCertification start"));	
		if (StringUtil.checkNull(personCertification.getCertificationName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CertificationName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCertification fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		PersonCertification existingpersonCertification = this.personCertificationDAO.findByCertificationName(personCertification.getCertificationName());
		if (existingpersonCertification == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificationName : "+personCertification.getCertificationName()+" not found then create"));	
			personCertification.setUserCreated(logonUserName);
			personCertification.setUserUpdated(logonUserName);
			this.createToDB(personCertification);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personCertification By :"+logonUserName));				
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertification : "+personCertification.getCertificationName() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATION, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found CertificationName "+personCertification.getCertificationName()+" then update"));
			personCertification.setCertificationId(existingpersonCertification.getCertificationId());
			personCertification.setUserUpdated(logonUserName);
			this.updateToDB(personCertification);
			oldValue = existingpersonCertification.toString();
			String newValue = personCertification.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personCertification oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personCertification "+personCertification.getCertificationName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATION, SystemAudit.MOD_SUB_ALL
						, "update personCertification oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCertification done with result "+result.toString()));
		return result;
	}
	public PersonCertification createToDB(PersonCertification personCertification) {
		this.personCertificationDAO.save(personCertification);
		return personCertification;
	}
	public PersonCertification updateToDB(PersonCertification personCertification) {	
		this.personCertificationDAO.save(personCertification);
		return personCertification;
	}
	@Override
	public PersonCertification findById(Integer certificationId, String className) {
		return this.personCertificationDAO.findOne(certificationId);
	}
}
