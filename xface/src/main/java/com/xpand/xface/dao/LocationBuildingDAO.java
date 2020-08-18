package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xpand.xface.entity.LocationBuilding;

@Repository
public interface LocationBuildingDAO extends JpaRepository<LocationBuilding, Integer>{
	public LocationBuilding findByBuildingName(String buildingName);
	
	@Modifying
	@Query("delete from LocationBuilding b where b.buildingName = :buildingName") 
	public void delete(@Param("buildingName") String buildingName);
	
}