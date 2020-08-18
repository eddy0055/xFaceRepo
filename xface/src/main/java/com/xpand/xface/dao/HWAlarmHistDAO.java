package com.xpand.xface.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.PersonInfo;

@Repository
public interface HWAlarmHistDAO extends JpaRepository<HWAlarmHist, Integer>{
//	@Query("SELECT new org.magnum.mobilecloud.video.model.AggregateResults(
//	        AVG(rating) as rating, 
//	        COUNT(rating) as TotalRatings) 
//	    FROM UserVideoRating
//	    WHERE videoId=:videoId")
	@Query("SELECT new com.xpand.xface.bean.LastAlarmPersonDateTime(hh.personInfo, MAX(hh.dateCreated) as alarmTime, COUNT(hh.alarmhId) as noOfRecord) FROM HWAlarmHist hh WHERE hh.dateCreated>:startDate GROUP BY hh.personInfo")	        
	public List<LastAlarmPersonDateTime> findMaxDateCreatedGroupByPerson(@Param("startDate") Date startDate); //findAvgRatingByVideoId(@Param("videoId") long videoId);
	
	@Modifying
	@Query("update HWAlarmHist hw set hw.personInfo = null where hw.personInfo = ?1")
	public void removeRelationshipPersonInfo(PersonInfo personInfo);
}
