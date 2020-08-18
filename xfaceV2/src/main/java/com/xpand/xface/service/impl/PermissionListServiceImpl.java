package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.dao.PermissionListDAO;
import com.xpand.xface.entity.PermissionList;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.util.CacheName;

@SessionScope
@Component
public class PermissionListServiceImpl implements PermissionListService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	PermissionListDAO permissionListDAO;
	
	@Override
	public List<PermissionList> findAll(String transactionId) {
		return this.permissionListDAO.findAll();
	}

	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pms_'+#permissionName")
	public PermissionList findByPermissionName(String transactionId, String permissionName) {
		return this.permissionListDAO.findByPermissionName(permissionName);
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pms_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pms_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}

}
