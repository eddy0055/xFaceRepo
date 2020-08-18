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
import com.xpand.xface.dao.PersonTitleDAO;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class PersonTitleServiceImpl implements PersonTitleService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonTitleDAO personTitleDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleId")
	public PersonTitle findById(String transactionId, Integer titleId) {
		return this.personTitleDAO.findOne(titleId);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleCode")
	public PersonTitle findByTitleCode(String transactionId, String titleCode) {				
		PersonTitle personTitle = this.personTitleDAO.findByTitleCode(titleCode);
		if (personTitle==null) {			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "Title code : "+titleCode+" not found"));
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found Title code : "+titleCode));
		}
		return personTitle;		
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER,key="'ptit_'+#titleAll")
	public List<PersonTitle> findAll(String transactionId) {
		List<PersonTitle> personTitleList = this.personTitleDAO.findAll();	
		return personTitleList;
	}
	@Override
	public Page<PersonTitle> getPersonTitleList(String transactionId, Pageable pageable) {			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personTitle list:"+pageable.toString()));
		Page<PersonTitle> pagePersonTitle = this.personTitleDAO.findAll(pageable);		
		return pagePersonTitle;
	}
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleCode"),
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleId"),
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleAll")
			}
	)
	public ResultStatus delete(String transactionId, String logonUserName, String personTitleCode) {
		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personTitle start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete find person titleCode:: " + personTitleCode));
		ResultStatus result = new ResultStatus();
		PersonTitle personTitle = this.personTitleDAO.findByTitleCode(personTitleCode);
		Logger.info(this, LogUtil.getLogInfo(transactionId, "personTitle find person titleCode:: " + personTitleCode));

		if (personTitle != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personTitle "+personTitle.getTitleName()+"["+ personTitle.getTitleId()+"] then delete"));
		    this.personTitleDAO.deleteByPersonTitle(personTitle.getTitleId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personTitle :"+ personTitle.getTitleName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personTitle "+personTitle.getTitleName()+"["+  personTitle.getTitleId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, "delete personTitle :"+personTitleCode+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "title code :"+personTitleCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personTitle code :"+personTitleCode);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personTitle done with result "+result.toString()));
		return result;	
	}
	
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleCode"),
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleId"),
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleAll")
			}
		)
	public ResultStatus update(String transactionId, String logonUserName, PersonTitle personTitleInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personTitleInfo start"));
		
		if (StringUtil.checkNull(personTitleInfo.getTitleCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "TitleCode");
			Logger.info(this, LogUtil.getLogInfo(transactionId, "check Null personTitle.getTitleCode ::" + personTitleInfo.getTitleCode()));
		}else if (StringUtil.checkNull(personTitleInfo.getTitleName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "TitleName");
			Logger.info(this, LogUtil.getLogInfo(transactionId, "check Null personTitle.getTitleName ::" + personTitleInfo.getTitleName()));
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personTitle fail with result "+result.toString()));
			return result;
		}
		
		String oldValue = null;
		PersonTitle existingpersonTitle = this.personTitleDAO.findByTitleCode(personTitleInfo.getTitleCode());
		Logger.info(this, LogUtil.getLogInfo(transactionId, "existingPersonTitle personTitle.getTitleCode ::" + personTitleInfo.getTitleCode()));
		if (personTitleInfo.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingpersonTitle!=null) {
				//error titleCode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "titleCode="+personTitleInfo.getTitleCode());
			}
		}else if (existingpersonTitle==null) { 
			//error cannot find titleCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "titleCode="+personTitleInfo.getTitleCode());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personTitleInfo fail with result "+result.toString()));
			return result;
		}
		if (existingpersonTitle == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personTitleInfo start"));	
			personTitleInfo.setUserCreated(logonUserName);
			personTitleInfo.setUserUpdated(logonUserName);
			this.createToDB(personTitleInfo);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personTitleInfo By :"+logonUserName));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personTitleInfo : "+personTitleInfo.getTitleName() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, "create personTitleInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personTitleInfo start"));	
			personTitleInfo.setTitleId(existingpersonTitle.getTitleId());
			personTitleInfo.setUserUpdated(logonUserName);
			this.updateToDB(personTitleInfo);
			oldValue = existingpersonTitle.toString();
			String newValue = personTitleInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personTitleInfo oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personTitleInfo "+personTitleInfo.getTitleName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
						, "update personTitleInfo oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personTitleInfo done with result "+result.toString()));
		return result;
	}
	
		
		
	public PersonTitle createToDB(PersonTitle personTitleInfo) {
		this.personTitleDAO.save(personTitleInfo);
		return personTitleInfo;
	}
	public PersonTitle updateToDB(PersonTitle personTitleInfo) {	
		this.personTitleDAO.save(personTitleInfo);
		return personTitleInfo;
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'ptit_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}
	
	//set for TablePage PersonTitle
	@Override	
	public TablePage getPersonTitleInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getPerson TitleList"));		
		TablePage page = new TablePage(transactionId, this.personTitleDAO, pc);
	    Logger.info(this, LogUtil.getLogInfo(transactionId, "out getPerson TitleList"));
	    return page;
	}

	
}
