
package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.dao.LocationMapDAO;
import com.xpand.xface.entity.LocationMap;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.LocationMapService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class LocationMapServiceImpl implements LocationMapService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	LocationMapDAO locationMapDAO;
	@Autowired
	SystemAuditService systemAuditService;
	@Override
	public List<LocationMap> findAll(String transactionId) {
		return this.locationMapDAO.findAll();
	}
		
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'locationMap_'+#mapId")
	public LocationMap findByMapId(String transactionId, Integer mapId) {
		return this.locationMapDAO.findOne(mapId);
	}	
	
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'locationMap_'+#mapCode")	
	public LocationMap findByMapCode(String transactionId, String mapCode) {
		return this.locationMapDAO.findOneByMapCode(mapCode);
	}
		
	
	@Override	
	public TablePage getLocationMapList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getLocationMapList"));		
		TablePage page = new TablePage(transactionId, this.locationMapDAO, pc);
        Logger.info(this, LogUtil.getLogInfo(transactionId, "out getLocationMapList"));
		return page;
	}		
	
	
	
	
	//Update Location map
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'locationMap_'+#mapId"),
					@CacheEvict(value=CacheName.CACHE_OTHER, key="'locationMap_'+#mapCode")
			}
		)
	public ResultStatus update(String transactionId, String logonUserName, LocationMap locationMap, MultipartFile mapPhoto) {	
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationMap start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "locationMap mapCode ::" + locationMap.getMapCode()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "locationMap mapName ::" + locationMap.getMapName()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "locationMap mapDesc ::" + locationMap.getMapDesc()));
		
		if (StringUtil.checkNull(locationMap.getMapCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "mapCode");
		}else if (StringUtil.checkNull(locationMap.getMapName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "mapName");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationMap fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;						
		LocationMap existingLocationMap = this.locationMapDAO.findOneByMapCode(locationMap.getMapCode());
		if (locationMap.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingLocationMap!=null) {
				//error mapCode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "mapCode="+locationMap.getMapCode());
			}
		}else if (existingLocationMap==null) { 
			//error cannot find mapCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "mapCode="+locationMap.getMapCode());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update lcoationMap fail with result "+result.toString()));
			return result;
		}
		if (existingLocationMap==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "mapCode "+locationMap.getMapCode()+" not found then create"));
			locationMap = new LocationMap(locationMap);
			locationMap.setMapId(null);
			locationMap.setUserCreated(logonUserName);
			locationMap.setUserUpdated(logonUserName);
			locationMap.setMapPhoto(ImageUtil.getImageFromMultipartFile(mapPhoto));
			this.createToDB(transactionId, locationMap);			
			oldValue = locationMap.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create locationMap:"+oldValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create mapCode "+locationMap.getMapCode()+"["+locationMap.getMapId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
					, "create locationMap:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);			
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found mapCode "+locationMap.getMapCode()+" then update"));
			locationMap.setUserUpdated(logonUserName);
			locationMap.setMapId(existingLocationMap.getMapId());
			locationMap.setMapPhoto(ImageUtil.getImageFromMultipartFile(mapPhoto));
			this.updateToDB(transactionId, locationMap);
			oldValue = existingLocationMap.toString();
			String newValue = locationMap.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update locationMap oldValue:"+oldValue+", newValue:"+newValue));			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update mapCode "+locationMap.getMapCode()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
					, "update locationMap oldValue:"+oldValue+", newValue:"+newValue, SystemAudit.RES_SUCCESS, logonUserName);
		}			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationMap done with result "+result.toString()));
		return result;
	}
	

	@Override
	//Delete Location Map
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'locationMap_'+#mapId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'locationMap_'+#mapCode")
			}
	)
	public ResultStatus delete(String transactionId, String logonUserName, String mapCode) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete Location Map start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete find location map code:: " + mapCode));
		ResultStatus result = new ResultStatus();
		LocationMap locationMap = this.locationMapDAO.findOneByMapCode(mapCode);
		Logger.info(this, LogUtil.getLogInfo(transactionId, "Location Map find mapCode:: " + mapCode));

		if (locationMap != null ) { 			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found mapCode "+locationMap.getMapCode()+"["+ locationMap.getMapId()+"] then delete"));
			this.locationMapDAO.deleteByMapId(locationMap.getMapId());
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete LocationMap :"+ locationMap.getMapId()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete LocationMap "+locationMap.getMapCode()+"["+ locationMap.getMapCode() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
				, "delete location Map :"+mapCode+" by "+logonUserName
				, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "Map code :"+mapCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "Map code :"+mapCode);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete mapCode done with result "+result.toString()));
		return result;	
	}
		
	public LocationMap createToDB(String transactionId, LocationMap locationMap) {
		this.locationMapDAO.save(locationMap);		
		return locationMap;
	}
	public LocationMap updateToDB(String transactionId, LocationMap locationMap) {					
		this.locationMapDAO.save(locationMap);
		return locationMap;
	}

	
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'locationMap_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'locationMap_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<LocationMap> removeSomeObject(String transactionId, List<LocationMap> locationMapList) {
		for (LocationMap map: locationMapList) {
			map.setHwIPCList(null);			
			map.setHwGateInfoList(null);
		}
		return locationMapList;
	}
	
	
				
}
