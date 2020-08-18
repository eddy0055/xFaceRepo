package com.xpand.xface.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.dao.EquipmentDirectionDAO;
import com.xpand.xface.entity.EquipmentDirection;
import com.xpand.xface.service.EquipmentDirectionService;
import com.xpand.xface.util.CacheName;

@SessionScope
@Component
public class EquipmentDirectionServiceImpl implements EquipmentDirectionService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	EquipmentDirectionDAO equipmentDirectionDAO;
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'EQkey_'+#gateId")
	public EquipmentDirection findById(String transactionId, Integer gateId) {
		return this.equipmentDirectionDAO.findOne(gateId);
		
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'EQkey_'+#directionCode")
	public EquipmentDirection findByDirectionCode(String transactionId, Integer directionCode) {
		return this.equipmentDirectionDAO.findByDirectionCode(directionCode);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'EQkey_'+#directionDesc")
	public EquipmentDirection findByDirectionDesc(String transactionId, String directionDesc) {
		return this.equipmentDirectionDAO.findByDirectionDesc(directionDesc);
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'EQkey_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'EQkey_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
		
	}

}
