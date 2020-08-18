package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.LocationFloor;

@Repository
public interface LocationFloorDAO extends JpaRepository<LocationFloor, Integer>{
	public LocationFloor findByFloorId(Integer buildingId,String floorName);

	@Query(value ="select b from LocationFloor b where b.floorName =:floorName and b.building.buildingId = :buildingId  ") 
	public LocationFloor findBylocationFloorNameAndBuildingId(@Param("floorName") String floorName,@Param("buildingId") Integer buildingId);
	
	@Query(value ="select b from LocationFloor b where b.building.buildingId = :buildingId  ") 
	public LocationFloor findlocationFloorAndBuildingId(@Param("buildingId") Integer buildingId);

	@Modifying
	@Query("delete from LocationFloor where floorId = :floorId ") 
	public void deleteBylocationFloor(@Param("floorId") Integer floorId);
	
	@Modifying
	@Query("delete from LocationFloor where building.buildingId = :buildingId ") 
	public void deleteBylocationFloorFilterBuilding(@Param("buildingId") Integer buildingId);
		
}
