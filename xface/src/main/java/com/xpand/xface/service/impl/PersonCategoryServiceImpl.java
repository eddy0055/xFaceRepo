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
import com.xpand.xface.controller.RestAlarmController;
import com.xpand.xface.dao.PersonCategoryDAO;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class PersonCategoryServiceImpl implements PersonCategoryService{
	@Autowired
	PersonCategoryDAO personCategoryDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	public List<PersonCategory> findAll(String className) {				
		return this.removeSome(this.personCategoryDAO.findAll(), className);
	}
	@Override
	public Page<PersonCategory> getPersonCategoryList(Pageable pageable, String className){
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personCategory list:"+pageable.toString()+" class name: "+ className));
		Page<PersonCategory> pagePersonCategory = this.personCategoryDAO.findAll(pageable);		
		this.removeSome(pagePersonCategory.getContent(), className);
		return pagePersonCategory;
	}
	@Override
	public PersonCategory findByCategoryName(String categoryName, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find Category name : "+categoryName +" class name : "+className));
		PersonCategory personCategory = this.personCategoryDAO.findByCategoryName(categoryName);
		personCategory = this.removeSome(personCategory, className);
		return personCategory;		
	}
	public List<PersonCategory> removeSome(List<PersonCategory> personCategoryList, String className) {
		if (personCategoryList==null || personCategoryList.size()==0) {
			return personCategoryList;
		}
		if (RestAPIController.CLASS_NAME.equals(className)) {
			for (PersonCategory pc:personCategoryList) {
				pc = this.removeSome(pc, className);
			}
		}
		return personCategoryList;
	}
	public PersonCategory removeSome(PersonCategory personCategory, String className) {
		if (personCategory==null) {
			return null;
		}else if (RestAPIController.CLASS_NAME.equals(className)) {
			personCategory.setHwIPCAnalyzeList(null);			
		}
		return personCategory;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus delete(String transactionId,String logonUserName,String categoryName) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCategory start"));
		ResultStatus result = new ResultStatus();
		PersonCategory personCategory = this.personCategoryDAO.findByCategoryName(categoryName);
		if (personCategory != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personCategory "+personCategory.getCategoryName()+"["+ personCategory.getCategoryId()+"] then delete"));
		    this.personCategoryDAO.deleteBypersonCategory(personCategory.getCategoryId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personCategory :"+ personCategory.getCategoryName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCategory "+personCategory.getCategoryName()+"["+  personCategory.getCategoryId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
					, "delete personCategory :"+categoryName+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "categoryname :"+categoryName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personCategory name :"+categoryName);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCategory done with result "+result.toString()));
		return result;	
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, PersonCategory personCategory) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCategory start"));	
		if (StringUtil.checkNull(personCategory.getCategoryName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CategoryName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCategory fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		PersonCategory existingpersonCategory = this.personCategoryDAO.findByCategoryName(personCategory.getCategoryName());
		if (existingpersonCategory == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "categoryName : "+personCategory.getCategoryName()+" not found then create"));	
			personCategory.setUserCreated(logonUserName);
			personCategory.setUserUpdated(logonUserName);			
			this.createToDB(personCategory);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personCategory By :"+logonUserName));				
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCategory : "+personCategory.getCategoryName() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found TitleName "+personCategory.getCategoryName()+" then update"));
			personCategory.setCategoryId(existingpersonCategory.getCategoryId());
			personCategory.setUserUpdated(logonUserName);
			this.updateToDB(personCategory);
			oldValue = existingpersonCategory.toString();
			String newValue = personCategory.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personCategory oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personCategory "+personCategory.getCategoryName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
						, "update personCategory oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCategory done with result "+result.toString()));
		return result;
	}
	public PersonCategory createToDB(PersonCategory personCategory) {
		this.personCategoryDAO.save(personCategory);
		return personCategory;
	}
	public PersonCategory updateToDB(PersonCategory personCategory) {	
		this.personCategoryDAO.save(personCategory);
		return personCategory;
	}
	@Override
	public PersonCategory findById(Integer categoryId, String className) {
		// TODO Auto-generated method stub
		return this.personCategoryDAO.findOne(categoryId);
	}
}
