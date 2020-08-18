package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.LocationMap;

@Repository
public interface LocationMapDAO extends JpaRepository<LocationMap, Integer>{
	@Override
	@Modifying
	@Query("DELETE FROM LocationMap lo WHERE lo.mapId = :mapId")
	public void delete(@Param("mapId") Integer mapId);
	public LocationMap findOneByMapCode(String mapCode);
	public void deleteByMapId(Integer mapId);
}
