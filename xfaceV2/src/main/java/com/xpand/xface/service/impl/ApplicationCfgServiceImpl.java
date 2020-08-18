package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.dao.ApplicationCfgDAO;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;




@SessionScope
@Component
public class ApplicationCfgServiceImpl implements ApplicationCfgService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	ApplicationCfgDAO applicationCfgDAO;
	
	@Autowired
	SystemAuditService systemAuditService;

	@Override
	public List<ApplicationCfg> getAll(String transactionId) {
		return this.applicationCfgDAO.findAll();
	}

	@Override
	@Cacheable(value=CacheName.CACHE_APPCFG, key="'key_'+#appKey")
	public ApplicationCfg findByAppKey(String transactionId, String appKey) {
		Logger.debug(this, LogUtil.getLogDebug("transactionId", "get app config with key "+appKey));		
		return this.applicationCfgDAO.findOne(appKey);
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, key="'key_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		//remove cache
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, key="'key_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		//remove cache
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, allEntries=true)
	public void purgeCache() {
		//clear cache
	}
	

	@Override
	@Cacheable(value=CacheName.CACHE_APPCFG, key="'key_all'")
	public HashMap<String, ApplicationCfg> getAllInHashMap(String transactionId) {
		List<ApplicationCfg> appCfgList = this.applicationCfgDAO.findAll();
		HashMap<String, ApplicationCfg> appCfgHash = new HashMap<String, ApplicationCfg>();
		for (ApplicationCfg appCfg:appCfgList) {
			appCfgHash.put(appCfg.getAppKey(), appCfg);
		}
		return appCfgHash;
	}
	
	@Override	
	public TablePage getApplicationCfgInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getApplicationCfgInfoList"));		
		TablePage page = new TablePage(transactionId, this.applicationCfgDAO, pc);
        Logger.info(this, LogUtil.getLogInfo(transactionId, "out getApplicationCfgInfoList"));
		return page;
	}	
	
	//@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	//@CacheEvict(value=CacheName.CACHE_APPCFG, key="'key_'+#applicationCfg.appKey")
	@Override
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_APPCFG, key="'key_'+#appKey"),
					@CacheEvict(value=CacheName.CACHE_APPCFG, key="'key_all'")
			}
		)
	public ResultStatus update(String transactionId, String logonUserName, ApplicationCfg applicationCfgInfo, MultipartFile personPhoto) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update applicationCfg start"));
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "in update Impl applicationCfgInfo.getAppDesc()"+ applicationCfgInfo.getAppDesc()));
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "in update Impl applicationCfgInfo.getAppKey()"+ applicationCfgInfo.getAppKey()));
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "in update Impl applicationCfgInfo.getAppAppValue1()"+ applicationCfgInfo.getAppValue1()));
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "in update Impl applicationCfgInfo.getAppAppValue2()"+ applicationCfgInfo.getAppValue2()));
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "in update Impl applicationCfgInfo.getAppAppValue3()"+ applicationCfgInfo.getAppValue3()));
		
		if (applicationCfgInfo.getAppKey() == null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "appKey");
		}else{
			applicationCfgInfo.setAppKey(applicationCfgInfo.getAppKey());
			applicationCfgInfo.setAppDesc(applicationCfgInfo.getAppDesc());
			applicationCfgInfo.setAppValue1(applicationCfgInfo.getAppValue1());
			applicationCfgInfo.setAppValue2(applicationCfgInfo.getAppValue2());
			applicationCfgInfo.setAppValue3(applicationCfgInfo.getAppValue3());
			applicationCfgInfo.setUserUpdated(logonUserName);
			applicationCfgInfo.setAppLobValue(ImageUtil.getImageFromMultipartFile(personPhoto));
			this.updateToDB(transactionId, applicationCfgInfo);
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_APPLICATION_CFG, SystemAudit.MOD_SUB_ALL
					, "update ApplicationCfg :"+applicationCfgInfo, SystemAudit.RES_SUCCESS, logonUserName);
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update applicationCfg fail with result "+result.toString()));
			return result;
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update applicationCfgInfo done with result "+result.toString()));
		return result;
	}

	public ApplicationCfg createToDB(ApplicationCfg applicationCfgInfo) {
		this.applicationCfgDAO.save(applicationCfgInfo);
		return applicationCfgInfo;
	}
	public ApplicationCfg updateToDB(String transactionId, ApplicationCfg applicationCfgInfo) {	
		this.applicationCfgDAO.save(applicationCfgInfo);
		return applicationCfgInfo;
	}


	
	
}
