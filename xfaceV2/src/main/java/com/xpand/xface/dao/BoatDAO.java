package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.Boat;

@Repository
public interface BoatDAO extends JpaRepository<Boat, Integer>{
	public Boat findByBoatCode(String boatCode);
	public Boat findByBoatShortName(String boatShortName);
	public Boat findByCardNo(String cardNo);
	@Override
	public List<Boat> findAll();
	
	@Query("SELECT bo FROM Boat bo where boatCode <> :unknownBoatCode ")
	public List<Boat> findNormalBoat(@Param("unknownBoatCode")  String unknownBoatCode);
	
	@Modifying
	@Query("delete from Boat where boatId = :boatId ") 
	public void deleteById(@Param("boatId") Integer boatId);
	public Boat findOneByBoatCode(String boatCode);
	public void deleteByBoatCode(String boatCode);
	
}
