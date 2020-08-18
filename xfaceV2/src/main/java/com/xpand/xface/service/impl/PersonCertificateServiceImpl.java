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
import com.xpand.xface.dao.PersonCertificateDAO;
import com.xpand.xface.entity.PersonCertificate;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.PersonCertificateService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class PersonCertificateServiceImpl implements PersonCertificateService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	PersonCertificateDAO personCertificateDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateId")
	public PersonCertificate findById(String transactionId, Integer certificateId) {
		return this.personCertificateDAO.findOne(certificateId);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateFindAll")
	public List<PersonCertificate> findAll(String transactionId) {
		return this.personCertificateDAO.findAll();
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateAll")
	public Page<PersonCertificate> getPersonCertificateList(String transactionId, Pageable pageable){
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get personCertificate list:"+pageable.toString()));
		Page<PersonCertificate> pagePersonCertificate = this.personCertificateDAO.findAll(pageable);		
		return pagePersonCertificate;
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateCode")
	public PersonCertificate findByCertificateCode(String transactionId, String certificateCode) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find certificate code : "+certificateCode ));
		PersonCertificate personCertificate = this.personCertificateDAO.findByCertificateCode(certificateCode);
		return personCertificate;		
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateFindAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateCode")
		}
	)
	public ResultStatus delete(String transactionId, String logonUserName, String certificateCode) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCertificate start"));
		ResultStatus result = new ResultStatus();
		PersonCertificate personCertificate = this.personCertificateDAO.findByCertificateCode(certificateCode);
		if (personCertificate != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found personCertificate "+personCertificate.getCertificateName()+"["+ personCertificate.getCertificateId()+"] then delete"));
		    this.personCertificateDAO.deleteBypersonCertificate(personCertificate.getCertificateId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personCertificate :"+ personCertificate.getCertificateName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCertificate "+personCertificate.getCertificateName()+"["+  personCertificate.getCertificateId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
					, "delete personCertificate :"+certificateCode+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificateCode:"+certificateCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "personCertificate name :"+certificateCode);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personCertificate done with result "+result.toString()));
		return result;	
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateFindAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateAll"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateCode")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, PersonCertificate personCertificate) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCertificate start"));	
		if (StringUtil.checkNull(personCertificate.getCertificateCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CertificateCode");
		}else if (StringUtil.checkNull(personCertificate.getCertificateName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CertificateName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCertificate fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		PersonCertificate existingpersonCertificate = this.personCertificateDAO.findByCertificateCode(personCertificate.getCertificateCode());
		if (personCertificate.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingpersonCertificate!=null) {
				//error titleCode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "CertificateCode="+personCertificate.getCertificateCode());
			}
		}else if (existingpersonCertificate==null) { 
			//error cannot find titleCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "CertificateCode="+personCertificate.getCertificateCode());
		}
		
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update getCertificateCode fail with result "+result.toString()));
			return result;
		}
		
		if (existingpersonCertificate == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificateCode : "+personCertificate.getCertificateCode()+" not found then create"));	
			personCertificate.setUserCreated(logonUserName);
			personCertificate.setUserUpdated(logonUserName);
			this.createToDB(personCertificate);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personCertificate By :"+logonUserName));				
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificate : "+personCertificate.getCertificateName() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found CertificateCode "+personCertificate.getCertificateCode()+" then update"));
			personCertificate.setCertificateId(existingpersonCertificate.getCertificateId());
			personCertificate.setUserUpdated(logonUserName);
			this.updateToDB(personCertificate);
			oldValue = existingpersonCertificate.toString();
			String newValue = personCertificate.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personCertificate oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personCertificate "+personCertificate.getCertificateName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
						, "update personCertificate oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update personCertificate done with result "+result.toString()));
		return result;
	}
	public PersonCertificate createToDB(PersonCertificate personCertificate) {
		this.personCertificateDAO.save(personCertificate);
		return personCertificate;
	}
	public PersonCertificate updateToDB(PersonCertificate personCertificate) {	
		this.personCertificateDAO.save(personCertificate);
		return personCertificate;
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pcert_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}
	@Override
	public TablePage getPersonCertificateInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getPersonTitleInfoList"));		
		TablePage page = new TablePage(transactionId, this.personCertificateDAO, pc);
	    Logger.info(this, LogUtil.getLogInfo(transactionId, "out getPersonTitleInfoList"));
	    return page;
	}
}
