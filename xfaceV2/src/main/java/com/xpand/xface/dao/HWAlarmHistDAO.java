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
	@Query("update HWAlarmHist hw set hw.personInfo = null where hw.personInfo = :personInfo")
	public void removeRelationshipPersonInfo(@Param("personInfo") PersonInfo personInfo);
	
	
//	@Query("SELECT new com.xpand.xface.bean.report.StatisticsResult("
//			+ "DATE_FORMAT(hh.alarmTime,'%d/%m/%Y') AS alarmTime, gt"
//			+ ",SUM(0) AS passengerIn, SUM(0) AS passengerOut"
//			+ ",SUM(hh.recMatch) AS passengerMatch, SUM(hh.recUnMatch) AS passengerUnMatch"
//			+ ") FROM HWAlarmHist hh JOIN hh.hwIPC ip JOIN ip.hwGateInfo gt "
//			+ "WHERE hh.alarmTime BETWEEN :startDate AND :endDate "
//			+ "GROUP BY DATE_FORMAT(hh.alarmTime,'%d/%m/%Y')"
//			+ "ORDER BY DATE_FORMAT(hh.alarmTime,'%d/%m/%Y')")
//	@Query(value = "SELECT alarm_time,gate_name,0 AS passenger_in, 0 AS passenger_out, 0 AS passenger_total,SUM(rec_match) AS rec_matchA, SUM(rec_un_match) AS rec_un_matchA, SUM(rec_match)+SUM(rec_un_match) AS rec_match_un_matchA "
//			+ "FROM ("
//			+ "SELECT DISTINCT DATE_FORMAT(alarm_time,'%Y/%m/%d') AS alarm_time,person_id,rec_match, rec_un_match, ipc_id FROM  tbl_alarm_hist WHERE person_id IS NOT NULL AND alarm_time BETWEEN :startDate AND :endDate "
//			+ "UNION ALL "
//			+ "SELECT DATE_FORMAT(alarm_time,'%Y/%m/%d') AS alarm_time,0 rec_match,person_id, 1 rec_un_match, ipc_id FROM  tbl_alarm_hist WHERE person_id IS NULL AND alarm_time BETWEEN :startDate AND :endDate"
//			+ ") t1 "
//			+ "INNER JOIN tbl_ipc ic "
//			+ "ON t1.ipc_id=ic.ipc_id "
//			+ "INNER JOIN tbl_gate_info gate "
//			+ "ON ic.gate_id=gate.gate_id "
//			+ "GROUP BY alarm_time, gate_name"
//			, nativeQuery = true)
//	public List<StatisticsResultInterface> findFRCStatisticsDaily(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
//	@Query("SELECT new com.xpand.xface.bean.report.StatisticsResult("
//			+ "DATE_FORMAT(hh.alarmTime,'%d/%m/%Y %H') AS alarmTime, gt"
//			+ ",SUM(0) AS passengerIn, SUM(0) AS passengerOut"
//			+ ",SUM(hh.recMatch) AS passengerMatch, SUM(hh.recUnMatch) AS passengerUnMatch"
//			+ ") FROM HWAlarmHist hh JOIN hh.hwIPC ip JOIN ip.hwGateInfo gt "
//			+ "WHERE hh.alarmTime BETWEEN :startDate AND :endDate "
//			+ "GROUP BY DATE_FORMAT(hh.alarmTime,'%d/%m/%Y %H')"
//			+ "ORDER BY DATE_FORMAT(hh.alarmTime,'%d/%m/%Y %H')")	        
//	public List<StatisticsResult> findFRCStatisticsHourly(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
