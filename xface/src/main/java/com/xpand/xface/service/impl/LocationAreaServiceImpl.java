package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.controller.RestAPIController;
import com.xpand.xface.dao.LocationAreaDAO;
import com.xpand.xface.entity.LocationArea;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.LocationAreaService;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class LocationAreaServiceImpl implements LocationAreaService{
	
	@Autowired
	LocationAreaDAO locationAreaDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	public List<LocationArea> findAll() {
		// TODO Auto-generated method stub
		List<LocationArea> locationAreaList = this.locationAreaDAO.findAll();
		for(LocationArea locationArea:locationAreaList) {
			if (locationArea!=null ) {
				locationArea.getFloor().setBuilding(null);
			}
		}		
		return locationAreaList;
	}
	@Override
	public LocationArea findByAreaName(String areaName, Integer floorId, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find area name :"+ areaName + " Floor id :"+floorId +" class name:"+className));
		LocationArea locationArea = this.locationAreaDAO.findByAreaNameAndFloorId(areaName,floorId);
		if(locationArea != null) {
			locationArea.getFloor().setBuilding(null);
		}
		return locationArea;
	}
	@Override
	public Page<LocationArea> getLocationAreaList(Pageable pageable, String className){
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get LocationArea list:"+pageable.toString()+" class name:"+className));
		Page<LocationArea> pageLocationArea = this.locationAreaDAO.findAll(pageable);
		if (RestAPIController.CLASS_NAME.equals(className)) {			
			List<LocationArea> locationAreaList = pageLocationArea.getContent();
			if (locationAreaList!=null && locationAreaList.size()>0) {
				for(LocationArea locationArea:locationAreaList) {
					locationArea.getFloor().setBuilding(null);
				}
			}		
		}			
		return pageLocationArea;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, LocationArea locationArea) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update locationArea start"));
		if (StringUtil.checkNull(locationArea.getAreaName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "AreaName");
		}else if (locationArea.getFloor() == null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Object Area");
		}else if (StringUtil.checkNull(locationArea.getFloor().getFloorId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "FloorId");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update locationArea fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		LocationArea existinglocationArea =this.locationAreaDAO.findByAreaNameAndFloorId(locationArea.getAreaName(),locationArea.getFloor().getFloorId());
		if (existinglocationArea == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "AreaName : "+locationArea.getAreaName()+" not found then create"));
			locationArea.setUserCreated(logonUserName);
			locationArea.setUserUpdated(logonUserName);
			this.createToDB(locationArea);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create locationArea By :"+logonUserName));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create locationArea : "+locationArea.getAreaName() +" is floor id : " + locationArea.getFloor().getFloorId() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_AREA, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found AreaName : "+locationArea.getAreaName()+" then update"));
			locationArea.setAreaId(existinglocationArea.getAreaId());
			locationArea.setUserUpdated(logonUserName);
			this.updateToDB(locationArea);
			oldValue = existinglocationArea.getAreaDesc();
			String newValue = locationArea.getAreaDesc();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update Area id : "+ existinglocationArea.getAreaId().toString() +" oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationArea "+existinglocationArea.getAreaName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_AREA, SystemAudit.MOD_SUB_ALL
						, "update locationArea oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update locationArea done with result "+result.toString()));
		return result;
	}	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)	
	public ResultStatus delete(String transactionId, String logonUserName,String areaName,Integer floorId) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationArea start"));
		ResultStatus result = new ResultStatus();
		LocationArea locationArea = this.locationAreaDAO.findByAreaNameAndFloorId(areaName,floorId);
		if (locationArea != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found AreaName "+locationArea.getAreaName()+"["+ locationArea.getFloor().getFloorId()+"] then delete"));
		    this.locationAreaDAO.deleteBylocationArea(locationArea.getAreaId());					
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete locationArea :"+ locationArea.getAreaName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationArea "+locationArea.getAreaName()+"["+  locationArea.getFloor().getFloorId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_AREA, SystemAudit.MOD_SUB_ALL
					, "delete locationArea :"+locationArea.getAreaName()+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "locationArea name :"+areaName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "locationArea name :"+areaName);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationArea done with result "+result.toString()));
		return result;
	}	
	public LocationArea createToDB(LocationArea locationArea) {
		this.locationAreaDAO.save(locationArea);
		return locationArea;
	}
	public LocationArea updateToDB(LocationArea locationArea) {	
		this.locationAreaDAO.save(locationArea);
		return locationArea;
	}
}
