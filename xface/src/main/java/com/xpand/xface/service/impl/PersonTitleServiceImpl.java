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
import com.xpand.xface.controller.RestAPIController;
import com.xpand.xface.dao.PersonTitleDAO;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class PersonTitleServiceImpl implements PersonTitleService {

	@Autowired
	PersonTitleDAO personTitleDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	public PersonTitle findByTitleName(String titleName, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find Title name : "+titleName +" class name : "+className));
		PersonTitle personTitle = this.personTitleDAO.findByTitleName(titleName);
		return personTitle;		
	}
	@Override
	public List<PersonTitle> findAll() {
		// TODO Auto-generated method stub
		List<PersonTitle> personTitleList = this.personTitleDAO.findAll();	
		return personTitleList;
	}
	@Override
	public Page<PersonTitle> getPersonTitleList(Pageable pageable, String className) {	
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personTitle list:"+pageable.toString()+" class name: "+ className));
		Page<PersonTitle> pagePersonTitle = this.personTitleDAO.findAll(pageable);		
		return pagePersonTitle;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus delete(String transactionId,String logonUserName,String titleName)
	{
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personTitle start"));
		ResultStatus result = new ResultStatus();
		PersonTitle personTitle = this.personTitleDAO.findByTitleName(titleName);
		if (personTitle != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personTitle "+personTitle.getTitleName()+"["+ personTitle.getTitleId()+"] then delete"));
		    this.personTitleDAO.deleteBypersonTitle(personTitle.getTitleId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personTitle :"+ personTitle.getTitleName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personTitle "+personTitle.getTitleName()+"["+  personTitle.getTitleId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, "delete personTitle :"+titleName+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "titlename :"+titleName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personTitle name :"+titleName);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personTitle done with result "+result.toString()));
		return result;	
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, PersonTitle personTitle) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personTitle start"));	
		if (StringUtil.checkNull(personTitle.getTitleName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "TitleName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personTitle fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		PersonTitle existingpersonTitle = this.personTitleDAO.findByTitleName(personTitle.getTitleName());
		if (existingpersonTitle == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "titleName : "+personTitle.getTitleName()+" not found then create"));	
			personTitle.setUserCreated(logonUserName);
			personTitle.setUserUpdated(logonUserName);
			this.createToDB(personTitle);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personTitle By :"+logonUserName));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personTitle : "+personTitle.getTitleName() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found TitleName "+personTitle.getTitleName()+" then update"));
			personTitle.setTitleId(existingpersonTitle.getTitleId());
			personTitle.setUserUpdated(logonUserName);
			this.updateToDB(personTitle);
			oldValue = existingpersonTitle.toString();
			String newValue = personTitle.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personTitle oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personTitle "+personTitle.getTitleName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
						, "update personTitle oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personTitle done with result "+result.toString()));
		return result;
	}	
	public PersonTitle createToDB(PersonTitle personTitle) {
		this.personTitleDAO.save(personTitle);
		return personTitle;
	}
	public PersonTitle updateToDB(PersonTitle personTitle) {	
		this.personTitleDAO.save(personTitle);
		return personTitle;
	}
	@Override
	public PersonTitle findById(Integer titleId, String className) {
		return this.personTitleDAO.findOne(titleId);
	}
}
