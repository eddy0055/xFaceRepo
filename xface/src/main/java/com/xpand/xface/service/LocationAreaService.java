package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.LocationArea;

public interface LocationAreaService {
	public List<LocationArea> findAll();
	public Page<LocationArea> getLocationAreaList(Pageable pageable, String className);
	public LocationArea findByAreaName(String areaName, Integer floorId, String className);

	public ResultStatus delete(String transactionId, String logonUserName,String areaName,Integer floorId);
	public ResultStatus update(String transactionId, String logonUserName, LocationArea locationArea);
}