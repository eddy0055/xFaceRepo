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
import com.xpand.xface.dao.LocationFloorDAO;
import com.xpand.xface.entity.LocationBuilding;
import com.xpand.xface.entity.LocationFloor;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.LocationFloorService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class LocationFloorServiceImpl implements LocationFloorService{
	
	@Autowired
	LocationFloorDAO locationFloorDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	
	@Override
	public LocationFloor findByFloorName(String floorName, Integer buildingId, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find floor name : "+floorName + " Build id : "+buildingId +" class name : "+className));
		LocationFloor locationFloor = this.locationFloorDAO.findBylocationFloorNameAndBuildingId(floorName,buildingId);
		LocationBuilding locationBuilding = null;
		if (locationFloor!=null) {	
			locationFloor.setAreas(null);
			locationBuilding = locationFloor.getBuilding();
			locationFloor.setBuilding(locationBuilding);
			locationBuilding.setFloors(null);
		}
		return locationFloor;		
	}
	@Override
	public List<LocationFloor> findAll() {
		// TODO Auto-generated method stub
		List<LocationFloor> locationFloorList = this.locationFloorDAO.findAll();
		LocationBuilding locationBuilding = null;
		for(LocationFloor locationFloor:locationFloorList) {
			if (locationFloor!=null ) {	
				locationFloor.setAreas(null);
				locationBuilding = locationFloor.getBuilding();
				locationFloor.setBuilding(locationBuilding);
				locationBuilding.setFloors(null);
			}
		}		
		return locationFloorList;
	}
	@Override
	public Page<LocationFloor> getLocationFloorList(Pageable pageable, String className) {	
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get LocationFloor list:"+pageable.toString()+" class name: "+ className));
		Page<LocationFloor> pageLocationFloor = this.locationFloorDAO.findAll(pageable);
		LocationBuilding locationBuilding = null;
		if (RestAPIController.CLASS_NAME.equals(className)) {			
			List<LocationFloor> locationFloorList = pageLocationFloor.getContent();
			if (locationFloorList!=null && locationFloorList.size()>0) {
				for(LocationFloor locationFloor:locationFloorList) {
					locationFloor.setAreas(null);	
					locationBuilding = locationFloor.getBuilding();
					locationFloor.setBuilding(locationBuilding);
					locationBuilding.setFloors(null);
				}
			}		
		}			
		return pageLocationFloor;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, LocationFloor locationFloor) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update locationFloor start"));		
		if (StringUtil.checkNull(locationFloor.getFloorName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "FloorName");
		}
		else if (locationFloor.getBuilding()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Object Building");
		}
		else if (StringUtil.checkNull(locationFloor.getBuilding().getBuildingId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "BuildingId");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update locationFloor fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;
		LocationFloor existingLocationFloor = this.locationFloorDAO.findBylocationFloorNameAndBuildingId(locationFloor.getFloorName(),locationFloor.getBuilding().getBuildingId());
		if (existingLocationFloor == null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "FloorName : "+locationFloor.getFloorName()+" not found then create"));	
			locationFloor.setUserCreated(logonUserName);
			locationFloor.setUserUpdated(logonUserName);
			locationFloor.setAreas(null);
			this.createToDB(locationFloor);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create locationFloor By :"+logonUserName));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create locationFloor : "+locationFloor.getFloorName() +" is building id : " + locationFloor.getBuilding().getBuildingId() +" By ["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_FLOOR, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
		}else{				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found FloorName "+locationFloor.getFloorName()+" then update"));
			locationFloor.setFloorId(existingLocationFloor.getFloorId());
			locationFloor.setUserUpdated(logonUserName);
			locationFloor.setAreas(null);
			this.updateToDB(locationFloor);
			oldValue = existingLocationFloor.toString();
			String newValue = locationFloor.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update locationFloor oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationFloor "+locationFloor.getFloorName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_FLOOR, SystemAudit.MOD_SUB_ALL
						, "update locationFloor oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		} 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update locationFloor done with result "+result.toString()));
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)	
	public ResultStatus delete(String transactionId,String logonUserName, String floorName, Integer buildingId) {	
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationFloor start"));
		ResultStatus result = new ResultStatus();
		LocationFloor locationFloor = this.locationFloorDAO.findBylocationFloorNameAndBuildingId(floorName,buildingId);
		if (locationFloor != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found locationFloor "+locationFloor.getFloorName()+"["+ locationFloor.getBuilding().getBuildingId()+"] then delete"));
		    this.locationFloorDAO.deleteBylocationFloor(locationFloor.getFloorId());					
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete locationFloor :"+ locationFloor.getFloorName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationFloor "+locationFloor.getFloorName()+"["+  locationFloor.getBuilding().getBuildingId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_FLOOR, SystemAudit.MOD_SUB_ALL
					, "delete locationFloor :"+floorName+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "Floorname :"+floorName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "locationFloor name :"+floorName);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationFloor done with result "+result.toString()));
		return result;
	}
	public LocationFloor createToDB(LocationFloor locationFloor) {
		this.locationFloorDAO.save(locationFloor);
		return locationFloor;
	}
	public LocationFloor updateToDB(LocationFloor locationFloor) {	
		this.locationFloorDAO.save(locationFloor);
		return locationFloor;
	}
}
