package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xpand.xface.entity.LocationArea;

@Repository
public interface LocationAreaDAO extends JpaRepository<LocationArea, Integer>{
	
	@Query(value ="select b from LocationArea b where b.areaName =:areaName and b.floor.floorId = :floorId ") 
	public LocationArea findByAreaNameAndFloorId(@Param("areaName") String areaName,@Param("floorId") Integer floorId);
	
	@Modifying
	@Query("delete from LocationArea b where b.areaId = :areaId")
	public void deleteBylocationArea (@Param("areaId") Integer areaId);
	
}
