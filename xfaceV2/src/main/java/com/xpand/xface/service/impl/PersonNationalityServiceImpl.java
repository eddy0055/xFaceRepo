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
import com.xpand.xface.dao.PersonNationalityDAO;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonNationalityService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class PersonNationalityServiceImpl implements PersonNationalityService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonNationalityDAO personNationalityDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityId")
	public PersonNationality findById(String transactionId, Integer nationalityId) {
		return this.personNationalityDAO.findOne(nationalityId);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityName")
	public PersonNationality findByNationalityName(String transactionId, String nationalityName) {
		// TODO Auto-generated method stub
		return this.personNationalityDAO.findByNationalityName(nationalityName);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityCode")
	public PersonNationality findByNationalityCode(String transactionId, String nationalityCode) {				
		PersonNationality personNationality = this.personNationalityDAO.findByNationalityCode(nationalityCode);
		if (personNationality==null) {			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "Nationality code : "+nationalityCode+" not found"));
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found Nationality code : "+nationalityCode));
		}
		return personNationality;		
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityAll")
	public List<PersonNationality> findAll(String transactionId) {
		List<PersonNationality> personNationalityList = this.personNationalityDAO.findAll();	
		return personNationalityList;
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityFindAll")
	public Page<PersonNationality> getPersonNationalityList(String transactionId, Pageable pageable) {	
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personNationality list:"+pageable.toString()));
		Page<PersonNationality> pagePersonNationality = this.personNationalityDAO.findAll(pageable);		
		return pagePersonNationality;
	}
	

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityFindAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityName")
		}
	)
	public ResultStatus delete(String transactionId, String logonUserName, String personNationalityCode) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personNationality start"));
		ResultStatus result = new ResultStatus();
		PersonNationality personNationality = this.personNationalityDAO.findByNationalityCode(personNationalityCode);
		if (personNationality != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personNationality "+personNationality.getNationalityName()+"["+ personNationality.getNationalityId()+"] then delete"));
		    this.personNationalityDAO.deleteByPersonNationality(personNationality.getNationalityId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personNationality :"+ personNationality.getNationalityName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personNationality "+personNationality.getNationalityName()+"["+  personNationality.getNationalityId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_NATIONALITY, SystemAudit.MOD_SUB_ALL
					, "delete personNationality :"+personNationalityCode+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "nationality code :"+personNationalityCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personNationality code :"+personNationalityCode);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personNationality done with result "+result.toString()));
		return result;	
	}
	
	//Update Person Nationality
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityFindAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityName")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, PersonNationality personNationality) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personNationality start"));	
		if (StringUtil.checkNull(personNationality.getNationalityCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "nationalityCode");
		}else if (StringUtil.checkNull(personNationality.getNationalityName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "nationalityName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personNationality fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		PersonNationality existingpersonNationality = this.personNationalityDAO.findByNationalityCode(personNationality.getNationalityCode());
		if (personNationality.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingpersonNationality!=null) {
				//error NationalityCode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "NationalityCode="+personNationality.getNationalityCode());
			}
		}else if (existingpersonNationality==null) { 
			//error cannot find NationalityCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "NationalityCode="+personNationality.getNationalityCode());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update getNationalityCode fail with result "+result.toString()));
			return result;
		}
		if (existingpersonNationality == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "nationalityName : "+personNationality.getNationalityName()+" not found then create"));	
			personNationality.setUserCreated(logonUserName);
			personNationality.setUserUpdated(logonUserName);
			this.createToDB(personNationality);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personNationality By :"+logonUserName));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personNationality : "+personNationality.getNationalityName() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_NATIONALITY, SystemAudit.MOD_SUB_ALL
					, "create personNationality:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found nationalityName "+personNationality.getNationalityName()+" then update"));
			personNationality.setNationalityId(existingpersonNationality.getNationalityId());
			personNationality.setUserUpdated(logonUserName);
			this.updateToDB(personNationality);
			oldValue = existingpersonNationality.toString();
			String newValue = personNationality.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personNationality oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personNationality "+personNationality.getNationalityName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
						, "update personNationality oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personNationality done with result "+result.toString()));
		return result;
	}	
	public PersonNationality createToDB(PersonNationality personNationality) {
		this.personNationalityDAO.save(personNationality);
		return personNationality;
	}
	public PersonNationality updateToDB(PersonNationality personNationality) {	
		this.personNationalityDAO.save(personNationality);
		return personNationality;
	}
	
	@Override
	public List<PersonNationality> removeSomeObject(List<PersonNationality> personNationalityList) {
		for (PersonNationality nationality: personNationalityList) {
			nationality.setPersonInfoList(null);
		}
		return personNationalityList;
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}
	@Override
	public TablePage getPersonNationalityInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in get Person Nationality InfoList"));		
		TablePage page = new TablePage(transactionId, this.personNationalityDAO, pc);
		Logger.info(this, LogUtil.getLogInfo(transactionId, "out get Person Nationality InfoList"));
	return page;
	}
}
