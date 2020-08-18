package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.LocationFloor;

public interface LocationFloorService {
	public List<LocationFloor> findAll();
	public Page<LocationFloor> getLocationFloorList(Pageable pageable, String className);
	public LocationFloor findByFloorName(String floorName, Integer buildingId, String className);
	
	public ResultStatus delete(String transactionId, String logonUserName, String floorName, Integer buildingId);
	public ResultStatus update(String transactionId, String logonUserName, LocationFloor locationFloor);
}
