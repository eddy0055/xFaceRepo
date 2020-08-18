package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.dao.PersonCategoryDAO;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class PersonCategoryServiceImpl implements PersonCategoryService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	PersonCategoryDAO personCategoryDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryId")
	public PersonCategory findById(String transactionId, Integer categoryId) {
		PersonCategory pc = this.personCategoryDAO.findOneByCategoryId(categoryId); 
		return pc;
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryCode")
	public PersonCategory findByCategoryCode(String transactionId, String categoryCode) {		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find Category code : "+categoryCode));
		PersonCategory personCategory = this.personCategoryDAO.findByCategoryCode(categoryCode);
		personCategory = this.removeSome(personCategory);
		return personCategory;		
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER,key="'pcat_'+#categoryAll")
	public Page<PersonCategory> getPersonCategoryList(String transactionId, Pageable pageable){		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personCategory list:"+pageable.toString()));
		Page<PersonCategory> pagePersonCategory = this.personCategoryDAO.findAll(pageable);		
		this.removeSome(pagePersonCategory.getContent());
		return pagePersonCategory;
	}
	public List<PersonCategory> removeSome(List<PersonCategory> personCategoryList) {
		if (personCategoryList==null || personCategoryList.size()==0) {
			return personCategoryList;
		}		
		for (PersonCategory pc:personCategoryList) {
			pc = this.removeSome(pc);
		}		
		return personCategoryList;
	}
	public PersonCategory removeSome(PersonCategory personCategory) {
		if (personCategory==null) {
			return null;
		}else {
			personCategory.setHwCheckPointLibrary(null);			
		}
		return personCategory;
	}
	@Override
	public List<PersonCategory> findAll(String transactionId) {				
		return this.removeSome(this.personCategoryDAO.findAll());
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryAll")
		}
	)
	public ResultStatus delete(String transactionId,String logonUserName, String categoryCode) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCategory start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "personCategory == " + categoryCode));

		ResultStatus result = new ResultStatus();
		PersonCategory personCategory = this.personCategoryDAO.findByCategoryCode(categoryCode);
		Logger.info(this, LogUtil.getLogInfo(transactionId, "personCategory" + personCategory));
		if (personCategory != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personCategory "+personCategory.getCategoryName()+"["+ personCategory.getCategoryId()+"] then delete"));
		    this.personCategoryDAO.deleteBypersonCategory(personCategory.getCategoryId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personCategory :"+ personCategory.getCategoryName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCategory "+personCategory.getCategoryName()+"["+  personCategory.getCategoryId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
					, "delete personCategory :"+categoryCode+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "category code :"+categoryCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personCategory code :"+categoryCode);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCategory done with result "+result.toString()));
		return result;	
	}
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryAll")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, PersonCategory personCategory) {
		ResultStatus result = new ResultStatus();				
		if (StringUtil.checkNull(personCategory.getCategoryCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CategoryCode");
		}else if (StringUtil.checkNull(personCategory.getCategoryName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CategoryName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCategory fail with result "+result.toString()));
			return result;
		}
		//String oldValue = null;
		//PersonCategory existingpersonCategory = this.personCategoryDAO.findByCategoryCode(personCategory.getCategoryCode());
		
		String oldValue = null;
		PersonCategory existingpersonCategory = this.personCategoryDAO.findByCategoryCode(personCategory.getCategoryCode());
		if (personCategory.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingpersonCategory!=null) {
				//error CategoryCode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "CategoryCode="+personCategory.getCategoryCode());
			}
		}else if (existingpersonCategory==null) { 
			//error cannot find CtegoryCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "CategoryCode="+personCategory.getCategoryCode());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update getCategoryCode fail with result "+result.toString()));
			return result;
		}
		
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
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}
	
	//Set for tablePage PersonCategory 
	@Override
	public TablePage getPersonCategoryInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getPersonTitleInfoList"));		
		TablePage page = new TablePage(transactionId, this.personCategoryDAO, pc);
	    Logger.info(this, LogUtil.getLogInfo(transactionId, "out getPersonTitleInfoList"));
	    return page;
	}
	
}
