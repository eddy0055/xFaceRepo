package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.dao.HWVCMDAO;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.HWVCMService;
import com.xpand.xface.util.CacheName;

@SessionScope
@Component
public class HWVCMServiceImpl implements HWVCMService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	HWVCMDAO hwVCMDAO;
	 
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'vcm_'+#vcmId")
	public HWVCM findByVcmId(String transactionId, Integer vcmId) {
		return this.hwVCMDAO.findOne(vcmId);
	}

	@Override
	public List<HWVCM> getAll(String transactionId) {
		return this.hwVCMDAO.findAll();
	}

	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'vcm_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'vcm_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
		
	}
}
