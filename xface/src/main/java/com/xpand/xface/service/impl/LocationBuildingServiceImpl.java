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
import com.xpand.xface.dao.LocationBuildingDAO;
import com.xpand.xface.dao.LocationFloorDAO;
import com.xpand.xface.entity.LocationBuilding;
import com.xpand.xface.entity.LocationFloor;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.LocationBuildingService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class LocationBuildingServiceImpl implements LocationBuildingService{

	@Autowired
	LocationBuildingDAO locationBuildingDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	LocationFloorDAO locationFloorDAO;
	
	@Override
	public List<LocationBuilding> findAll() {
		// TODO Auto-generated method stub
		List<LocationBuilding> locationBuildingList = this.locationBuildingDAO.findAll();
		for(LocationBuilding locationBuilding:locationBuildingList)
		if (locationBuilding!=null ) {	
			locationBuilding.setFloors(null);
			locationBuilding.setHwVCMs(null);
		}
		return locationBuildingList;
	}
	@Override
	public Page<LocationBuilding> getLocationBuildingList(Pageable pageable, String className) {	
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get LocationBuilding list:"+pageable.toString()+" class name:"+className));
		Page<LocationBuilding> pageLocationBuilding = this.locationBuildingDAO.findAll(pageable);
		if (RestAPIController.CLASS_NAME.equals(className)) {			
			List<LocationBuilding> locationBuildingList = pageLocationBuilding.getContent();
			if (locationBuildingList!=null && locationBuildingList.size()>0) {
				for(LocationBuilding locationBuilding:locationBuildingList) {
					locationBuilding.setFloors(null);
					locationBuilding.setHwVCMs(null);
				}
			}		
		}			
		return pageLocationBuilding;
	}
	@Override
	public LocationBuilding findByBuildingName(String buildingName, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find building name:"+buildingName+" class name:"+className));
		LocationBuilding locationBuilding = this.locationBuildingDAO.findByBuildingName(buildingName);
		if (locationBuilding!=null ) {	
				locationBuilding.setFloors(null);
				locationBuilding.setHwVCMs(null);
		}	
		return locationBuilding;		
	}
	@Override
	public LocationBuilding findByBuildingNameAndFloor(String buildingName, String className) {
		LocationBuilding locationBuilding = this.locationBuildingDAO.findByBuildingName(buildingName);
		if (locationBuilding!=null) {
			if (RestAPIController.CLASS_NAME.equals(className)) {
				locationBuilding.setHwVCMs(null);
				if (locationBuilding.getFloors()!=null) {
					for (LocationFloor lf:locationBuilding.getFloors()) {
						if (lf!=null) {
							lf.setBuilding(null);
						}
					}
				}
			}
		}	
		return locationBuilding;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)	
	public ResultStatus delete(String transactionId,String logonUserName, String buildingName) {	
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationBuilding start"));
		ResultStatus result = new ResultStatus();
		LocationBuilding locationBuilding  = this.locationBuildingDAO.findByBuildingName(buildingName); 
		if (locationBuilding!=null ) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found Building id : "+locationBuilding.getBuildingId()+"["+ locationBuilding.getBuildingName() +"] then delete"));
			if(locationBuilding.getBuildingId()!= null) {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "Start Delete Tablel Floor : Building id " + locationBuilding.getBuildingId()));
				this.locationFloorDAO.deleteBylocationFloorFilterBuilding(locationBuilding.getBuildingId());	
				Logger.info(this, LogUtil.getLogInfo(transactionId, "Start Building Tablel Building : Building Name" + buildingName));
				this.locationBuildingDAO.delete(buildingName);	
			}
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete locationBuilding :"+ locationBuilding.getBuildingName()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationBuilding "+locationBuilding.getBuildingId()+"["+ locationBuilding.getBuildingName() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BUILDING, SystemAudit.MOD_SUB_ALL
					, "delete locationBuilding :"+locationBuilding.getBuildingId()+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "Building name :"+buildingName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "locationBuilding name :"+buildingName);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete locationBuilding done with result "+result.toString()));
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, LocationBuilding locationBuilding) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationBuilding start"));
		if (StringUtil.checkNull(locationBuilding.getBuildingName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "BuildingName");
		}else if (StringUtil.checkNull(locationBuilding.getLocation())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Location");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationBuilding fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;		
		LocationBuilding existingLocationBuilding = this.locationBuildingDAO.findByBuildingName(locationBuilding.getBuildingName());
		if (existingLocationBuilding==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "BuildingName "+locationBuilding.getBuildingName()+" not found then create"));			
			locationBuilding.setUserCreated(logonUserName);
			locationBuilding.setUserUpdated(logonUserName);
			locationBuilding.setHwVCMs(null);
			this.createToDB(locationBuilding);	
			for (int i = 1; i <= locationBuilding.getNumberOfFloors(); i++) {			
				String NumberOfFloorsString = Integer.toString(i);
				LocationFloor locationFloor = this.locationFloorDAO.findBylocationFloorNameAndBuildingId(NumberOfFloorsString,locationBuilding.getBuildingId());
				if (locationFloor == null) {
					locationFloor = new LocationFloor();
					locationFloor.setUserCreated(logonUserName);
					locationFloor.setUserUpdated(logonUserName);
					locationFloor.setFloorName(NumberOfFloorsString);
					locationFloor.setBuilding(locationBuilding);
					locationFloor.setAreas(null);
					this.createToDBForLocationFloor(locationFloor);					
				} 	
			}			
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create locationBuilding By :"+logonUserName));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create locationBuilding : "+locationBuilding.getBuildingName()+"["+logonUserName+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BUILDING, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found LocationBuilding "+locationBuilding.getBuildingName()+" then update"));
			locationBuilding.setBuildingId(existingLocationBuilding.getBuildingId());			
			locationBuilding.setUserUpdated(logonUserName);
			locationBuilding.setFloors(null);
			locationBuilding.setHwVCMs(null);
			this.updateToDB(locationBuilding);
			oldValue = existingLocationBuilding.toString();
			String newValue = locationBuilding.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update locationBuilding oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationBuilding "+locationBuilding.getBuildingName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BUILDING, SystemAudit.MOD_SUB_ALL
						, "update LocationBuilding oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update locationBuilding done with result "+result.toString()));
		return result;
	}
	public LocationBuilding createToDB(LocationBuilding locationBuilding) {
		this.locationBuildingDAO.save(locationBuilding);
		return locationBuilding;
	}
	public LocationBuilding updateToDB(LocationBuilding locationBuilding) {
		this.locationBuildingDAO.save(locationBuilding);
		return locationBuilding;
	}
	public LocationFloor createToDBForLocationFloor(LocationFloor locationFloor) {
		this.locationFloorDAO.save(locationFloor);
		return locationFloor;
	}
}



