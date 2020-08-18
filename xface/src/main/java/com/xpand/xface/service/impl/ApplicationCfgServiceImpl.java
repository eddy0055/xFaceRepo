package com.xpand.xface.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.ApplicationCfgDAO;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.LogUtil;

@Component
public class ApplicationCfgServiceImpl implements ApplicationCfgService{

	@Autowired
	ApplicationCfgDAO applicationCfgDAO;

	@Override
	public List<ApplicationCfg> getAll() {
		return this.applicationCfgDAO.findAll();
	}

	@Override
	@Cacheable(value=CacheName.CACHE_APPCFG, key="#root.methodName+'_'+#appKey")
	public ApplicationCfg findByAppKey(String appKey) {
		Logger.debug(this, LogUtil.getLogDebug("transactionId", "get app config with key "+appKey));		
		return this.applicationCfgDAO.findOne(appKey);
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, key="#root.methodName+'_'+#appKey")
	public void removeCfgFromCacheByKey(String appKey) {
		//remove cache
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, allEntries=true)
	public void purgeCache() {
		//clear cache
	}
	
	@Override
	@CachePut(value=CacheName.CACHE_APPCFG, key="#root.methodName+'_'+#applicationCfg.appKey")
	public ResultStatus update(ApplicationCfg applicationCfg) {
		return null;
	}

	@Override
	public HashMap<String, ApplicationCfg> getAllInHashMap() {
		List<ApplicationCfg> appCfgList = this.applicationCfgDAO.findAll();
		HashMap<String, ApplicationCfg> appCfgHash = new HashMap<String, ApplicationCfg>();
		for (ApplicationCfg appCfg:appCfgList) {
			appCfgHash.put(appCfg.getAppKey(), appCfg);
		}
		return appCfgHash;
	}
}
