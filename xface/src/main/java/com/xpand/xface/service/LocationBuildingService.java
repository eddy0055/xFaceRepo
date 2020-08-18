package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.xpand.xface.bean.ResultStatus;

import com.xpand.xface.entity.LocationBuilding;

public interface LocationBuildingService {
	public List<LocationBuilding> findAll();
	public Page<LocationBuilding> getLocationBuildingList(Pageable pageable, String className);
	public LocationBuilding findByBuildingName(String buildingName, String className);
	public LocationBuilding findByBuildingNameAndFloor(String buildingName, String className);

	public ResultStatus delete(String transactionId,String logonUserName,String buildingName);
	public ResultStatus update(String transactionId, String logonUserName, LocationBuilding locationBuilding);
	
}