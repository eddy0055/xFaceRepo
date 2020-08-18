package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.BoatSchedule;
import com.xpand.xface.entity.HWGateInfo;

@Repository
public interface BoatScheduleDAO extends JpaRepository<BoatSchedule, Integer>{
//	public Boat findByBoatCode(String boatCode);
//	public Boat findByBoatShortName(String boatShortName);
	
//	@Query("SELECT bo FROM Boat bo where boatCode <> :unknownBoatCode ")
//	public List<Boat> findNormalBoat(@Param("unknownBoatCode")  String unknownBoatCode);
//	
//	@Modifying
//	@Query("delete from Boat where boatId = :boatId ") 
//	public void deleteById(@Param("boatId") Integer boatId);
	
	@Query("SELECT bs FROM BoatSchedule bs WHERE boat=:boat AND hwGateInfo=:hwGateInfo AND dateDeparture IS NULL")
	public BoatSchedule findPendingDeparture(@Param("boat") Boat boat, @Param("hwGateInfo") HWGateInfo hwGateInfo);
}
