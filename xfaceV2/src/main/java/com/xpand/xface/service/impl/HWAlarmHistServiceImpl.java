package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.report.QueryAlarmPortionResultList;
import com.xpand.xface.bean.report.QueryAlarmResult;
import com.xpand.xface.bean.report.QueryAlarmResultList;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.DailyFaceChartField;
import com.xpand.xface.bean.report.google.GoogleChartResult;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;
import com.xpand.xface.bean.report.google.PassengerRegisterField;
import com.xpand.xface.dao.HWAlarmHistDAO;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.EquipmentDirection;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.HWAlarmHistService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class HWAlarmHistServiceImpl implements HWAlarmHistService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager entityManager;		
	@Autowired
	HWAlarmHistDAO hwAlarmHistDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	
	@Override
	public ResultStatus updateAlarm(String transactionId, HWAlarmHist hwAlarmHist) {			
		this.hwAlarmHistDAO.save(hwAlarmHist);		
		return new ResultStatus();
	}
	@Override
	public ResultStatus removeRelationshipPersonInfo(String transactionId, PersonInfo personInfo) {
		this.hwAlarmHistDAO.removeRelationshipPersonInfo(personInfo);
		return new ResultStatus();
	}
		
	@Override
	public ArrayList<PassengerRegisterField> getPassengerVisited(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getPassengerVisited:"+param.getStartDate()+","+param.getEndDate()));
		List<Object[]> queryResultList = null;
		PassengerRegisterField passengerField = null;
		ArrayList<PassengerRegisterField> resultList = new ArrayList<>();
		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		ArrayList<Object> paramList = new ArrayList<>();
		String queryStmt = "SELECT IFNULL(alm2.nationality_name,?) AS nationality_name, SUM(noOfVisited) AS noOfVisited, 0 AS noOfRegistered FROM (" 
				+ "SELECT alm.nationality_name, alm.person_id, CASE WHEN SUM(noOfVisited)=0 THEN 1 ELSE SUM(noOfVisited) END AS noOfVisited "
				+ "FROM ("
				+ "SELECT nat.nationality_name, alm.person_id, CASE WHEN alm.person_id IS NULL THEN 1 ELSE 0 END noOfVisited FROM tbl_alarm_hist alm "
				+ "LEFT OUTER JOIN tbl_person_info per ON alm.person_id=per.person_id "
				+ "LEFT OUTER JOIN tbl_person_nationality nat ON per.nationality_id=nat.nationality_id "
				+ "INNER JOIN tbl_ipc ipc ON alm.ipc_id=ipc.ipc_id "
				+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alarm_time BETWEEN ? AND ? AND ipc.eqdirection_id=?) alm "
				+ "GROUP BY alm.nationality_name, alm.person_id) alm2 GROUP BY IFNULL(alm2.nationality_name,?)";
		ResultStatus resultStatus = new ResultStatus();
		try {					
			paramList.add(ConstUtil.UNKNOWN_PERSON_NATIONALITY);
			paramList.add(startDate);
			paramList.add(endDate);
			paramList.add(EquipmentDirection.DIRECTION_IN);
			paramList.add(ConstUtil.UNKNOWN_PERSON_NATIONALITY);
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);								
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					passengerField = new PassengerRegisterField(queryResult);
					resultList.add(passengerField);
				}						
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerVisited with result:"+resultList.size()+" record"));				
			}else {
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerVisited 0 record"));
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm get passenger visited", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm get passenger visited");
		}							
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getPassengerVisited with result:"+resultStatus.toString()));
		return resultList;
	}	
	
	@Override
	public QueryAlarmPortionResultList getAlarmListPortionList(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getAlarmListTimePortion with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()+" certNo:"+param.getCertificateNo()+", FullName:"+param.getFullName()+", ipcCode:"+param.getIpcCodeList()));
		Date startDate = null;
		Date endDate = null;
		long tmpDate = 0;
		//1. calculate start and end
		if (param.getDurationType() == ReportFEParam.TIME_DURATION_TYPE_NOW_MINUS_MINUTE) {
			//now - hour			
			//round up to minute
			//ex. portion = 5 minute, now is 10.16 we have to round down to 10.20
			//ex. portion = 15 minute, now is 10.16 we have to round down to 10.30
			//ex. portion = 30 minute, now is 10.16 we have to round down to 10.30
			//ex. portion = 60 minute, now is 10.16 we have to round down to 11.00
			//ex. portion = 720 minute, now is 10.16 we have to round down to 22.00
			//ex. portion = 1440 minute, now is 10.16 we have to round down to 10.00
			tmpDate = new Date().getTime();
			//time portion > 60 , set to 60
			int timePortion = param.getTimePortion()>60?60:param.getTimePortion();
			tmpDate = (tmpDate - tmpDate % (timePortion*60*1000))+(timePortion*60*1000);
			endDate = new Date(tmpDate);
			startDate = new Date(endDate.getTime()-(param.getTimeMinusMinute()*60*1000));					
		}else {
			//start end format is YYYYMMDDHHMM
			startDate = StringUtil.stringToDate(param.getStartDate()+"00", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
			endDate = StringUtil.stringToDate(param.getEndDate()+"00", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
			//endDate = new Date(endDate.getTime()+ (24*60*60*1000));
		}
		//2 loop from start to end and create time portion and query data from db
		long startDateLong = startDate.getTime();
		long endDateLong = endDate.getTime();		
		QueryAlarmPortionResultList alarmPortionResultList = new QueryAlarmPortionResultList();		
		QueryAlarmResultList alarmResultList = null;
		ResultStatus resultStatusTimePortion = new ResultStatus();
		Date queryStartDate = null;
		Date queryEndDate = null;
		boolean flagFirstTime = true;		
		ReportFEParam reportParam = new ReportFEParam();
		int noOfTimePortion = StringUtil.stringToInteger(this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_FACE_ALARM_HISTORY_TIME_PORTION_SIZE).getAppValue1(), 10);
		int facePageSize = StringUtil.stringToInteger(this.appCfgService.findByAppKey(transactionId,ApplicationCfg.KEY_FACE_ALARM_HISTORY_PAGE_SIZE).getAppValue1(), 12);
		try {
			//end to start
			while (endDateLong > startDateLong){
				//time portion in hour format then change to millisec before add to start
				//59 sec coz I want to start from 11.00.01 - 11.15.00 (time slop is 15 minute)
				tmpDate =  endDateLong - ((60*1000*param.getTimePortion()));
				if (tmpDate < startDateLong) {
					tmpDate = startDateLong;
				}
				queryStartDate = new Date(tmpDate);
				queryEndDate = new Date(endDateLong);
				if (flagFirstTime) {
					flagFirstTime = false;
					alarmPortionResultList.setMaximumStartDate(queryStartDate);
					alarmPortionResultList.setMaximumEndDate(queryEndDate);
				}
				reportParam.setStartDate(StringUtil.dateToString(queryStartDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));
				reportParam.setEndDate(StringUtil.dateToString(queryEndDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));
				reportParam.setCurrentPage(param.getCurrentPage());
				reportParam.setCertificateNo(param.getCertificateNo());
				reportParam.setFullName(param.getFullName());
				reportParam.setGateInfoCodeList(param.getGateInfoCodeList());
				reportParam.setIpcCodeList(param.getIpcCodeList());
				reportParam.setMatchCondition(param.getMatchCondition());
				reportParam.setBoatCodeList(param.getBoatCodeList());
				reportParam.setDirection(param.getDirection());
				alarmResultList = this.getAlarmListPortionPartList(transactionId, reportParam, facePageSize);
				if (alarmResultList.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
					if (alarmResultList.getQueryAlarmResultList().size() > 0) {
						alarmPortionResultList.getQueryAlarmResultList().add(alarmResultList);
						noOfTimePortion--;
						if (noOfTimePortion==0) {
							Logger.debug(this,  LogUtil.getLogDebug(transactionId, "no of time portion session reach maximum:"+alarmPortionResultList.getQueryAlarmResultList().size()+ " record then exit loop"));
							tmpDate = startDateLong;									
						}
					}
				}
				endDateLong = tmpDate;
			}
			Logger.info(this,  LogUtil.getLogInfo(transactionId, "alarmTimePortionResultList no of result:"+alarmPortionResultList.getQueryAlarmResultList().size()+ " record."));
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm time portion with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate(), ex));
			resultStatusTimePortion.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm time portion with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()); 
		}			
		alarmPortionResultList.setResult(resultStatusTimePortion);
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getAlarmList with result:"+resultStatusTimePortion.toString()));
		return alarmPortionResultList;
	}	
	@Override
	public QueryAlarmResultList getAlarmListPortionPartList(String transactionId, ReportFEParam param, int pageSize) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getAlarmListTimePortionPartList with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()));
		Date startDate = null;
		Date endDate = null;		
		//start end format is YYYYMMDDHHMISS
		startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);		
		//2 loop from start to end and create time portion and query data from db
		ArrayList<Object> paramList = new ArrayList<>();
		QueryAlarmResultList alarmResultList = null;
		ResultStatus resultStatus = null;
		List<Object[]> queryResultList = null;
		QueryAlarmResult alarmResult = null;
		String queryStmtCount = "SELECT COUNT(*)";
		String queryStmtSelect = "SELECT ah.alarmh_id,ah.live_photo,ah.alarm_time,per.person_id,'' AS person_photo,CONVERT(AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS certificate_no, CONVERT(AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS full_name,CONCAT(ipc.ipc_name,'[',equ.direction_desc,']') AS ipc_name,'' AS certificate_name,'' AS title_name,'' AS meta_scr, cat.category_name,gat.gate_name,bo.boat_short_name "; 
		String queryStmtCondition = "FROM tbl_alarm_hist ah INNER JOIN tbl_ipc ipc ON ah.ipc_id=ipc.ipc_id "
				+ "INNER JOIN tbl_gate_info gat ON ipc.gate_id=gat.gate_id "				
				+ "LEFT OUTER JOIN tbl_person_info per ON ah.person_id=per.person_id "
				+ "LEFT OUTER JOIN tbl_person_category cat ON per.category_id=cat.category_id "
				+ "LEFT OUTER JOIN tbl_boat_schedule sch ON (ah.alarm_time BETWEEN sch.date_arrival AND sch.date_departure AND gat.gate_id=sch.gate_id) "
				+ "LEFT OUTER JOIN tbl_boat bo ON sch.boat_id=bo.boat_id "
				+ "LEFT OUTER JOIN tbl_equ_direction equ ON ipc.eqdirection_id=equ.eqdirection_id "
				+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alarm_time >= ? AND alarm_time < ? ";				
		paramList.add(startDate);
		paramList.add(endDate);
		if (!StringUtil.checkNull(param.getDirection())) {
			queryStmtCondition += " AND equ.direction_code= ?";
			paramList.add(param.getDirection());
		}
		if (ConstUtil.ALARM_HISTORY_MATCH_CONDITION.equals(param.getMatchCondition())) {
			queryStmtCondition += " AND ah.person_id IS NOT NULL";
		}else if (ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION.equals(param.getMatchCondition())) {
			queryStmtCondition += " AND ah.person_id IS NULL";
		}		
		if (!StringUtil.checkNull(param.getCertificateNo())) {
			queryStmtCondition = queryStmtCondition+" AND AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"')=? ";
			paramList.add(param.getCertificateNo());
		}else if (!StringUtil.checkNull(param.getFullName())) {
			queryStmtCondition = queryStmtCondition+" AND AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') LIKE ? ";
			paramList.add("%"+param.getFullName()+"%");
		}
		
		//if already filter by ipc then no need to filter by gate anymore 
		if (!StringUtil.checkNull(param.getIpcCodeList())) {
			queryStmtCondition = queryStmtCondition+" AND ipc.ipc_code IN (";
			String ipcCodeList[] = param.getIpcCodeList().split(ConstUtil.STRING_ID_DELIMINATOR);
			for (String ipcCode: ipcCodeList) {
				queryStmtCondition = queryStmtCondition+"?,";
				paramList.add(ipcCode);
			}			
			queryStmtCondition = queryStmtCondition.substring(0, queryStmtCondition.length()-1)+")";
		}else if (!StringUtil.checkNull(param.getGateInfoCodeList())) {
			queryStmtCondition = queryStmtCondition+" AND gat.gate_code IN (";
			String gateInfoCodeList[] = param.getGateInfoCodeList().split(ConstUtil.STRING_ID_DELIMINATOR);
			for (String gateInfoCode: gateInfoCodeList) {
				queryStmtCondition = queryStmtCondition+"?,";
				paramList.add(gateInfoCode);
			}			
			queryStmtCondition = queryStmtCondition.substring(0, queryStmtCondition.length()-1)+")";
		}
		//if boat not null
		if (!StringUtil.checkNull(param.getBoatCodeList())) {
			if (param.getBoatCodeList().equals(Boat.BOAT_NONE_CODE)) {
				queryStmtCondition = queryStmtCondition+" AND bo.boat_code IS NULL";
			}else {
				boolean foundBoatNone = false;
				queryStmtCondition = queryStmtCondition+" AND (bo.boat_code IN (";
				String boatCodeList[] = param.getBoatCodeList().split(ConstUtil.STRING_ID_DELIMINATOR);
				for (String boatCode: boatCodeList) {
					if (boatCode.equals(Boat.BOAT_NONE_CODE)) {
						foundBoatNone = true;
					}else {
						queryStmtCondition = queryStmtCondition+"?,";
						paramList.add(boatCode);
					}
				}			
				queryStmtCondition = queryStmtCondition.substring(0, queryStmtCondition.length()-1)+")";
				if (foundBoatNone) {
					queryStmtCondition = queryStmtCondition +" OR bo.boat_code IS NULL)";
				}else {
					queryStmtCondition = queryStmtCondition +")";
				}
			}			
		}
		//////////////////////////////////////////////////
		
		String queryStmtOrderBy = "ORDER BY alarm_time DESC LIMIT "+((param.getCurrentPage()-1)*pageSize)+", "+pageSize;  //start 0 with 10 row
		try {			
			alarmResultList = new QueryAlarmResultList();
			alarmResultList.setTotalRecord(OtherUtil.doQueryCount(this, transactionId, this.entityManager, queryStmtCount+" "+queryStmtCondition, paramList));
			alarmResultList.setCurrentPage(param.getCurrentPage());
			alarmResultList.setPageSize(pageSize);
			if ((alarmResultList.getTotalRecord() % pageSize)==0) {
				alarmResultList.setMaximumPage((int)alarmResultList.getTotalRecord() / pageSize);
			}else {
				alarmResultList.setMaximumPage(((int)alarmResultList.getTotalRecord() / pageSize)+1);
			}
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmtSelect+" "+queryStmtCondition+" "+queryStmtOrderBy, paramList);								
			resultStatus = new ResultStatus();			
			alarmResultList.setStartDate(startDate);						
			alarmResultList.setStartDateDisplay(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM));
			alarmResultList.setEndDate(endDate);
			alarmResultList.setEndDateDisplay(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM));			
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					alarmResult = new QueryAlarmResult(queryResult);
					alarmResultList.getQueryAlarmResultList().add(alarmResult);				
				}						
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getAlarmList no of result:"+alarmResultList.getQueryAlarmResultList().size()+ " record for start date:"+StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) 
										+" end date:"+StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)));				
			}else {
				//record no found
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "no result found for alarmResultList with start date:"+StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)
							+" end date:"+StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)));
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with start date:"+StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS) 
							+" end date:"+StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS), ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate());
		}							
		alarmResultList.setResult(resultStatus);			
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getAlarmList with result:"+resultStatus.toString()));
		return alarmResultList;
	}
	
	public QueryAlarmResult getAlarm(String transactionId, ReportFEParam param, int getAlarmBy) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getAlarm with alarmId:"+param.getAlarmId()));
		List<Object[]> queryResultList = null;
		QueryAlarmResult alarmResult = null;
		String conditionParam = null;
		String queryStmtSelect = "SELECT ah.alarmh_id,ah.live_photo,ah.alarm_time,per.person_id,per.person_photo AS person_photo,CONVERT(AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS certificate_no, CONVERT(AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS full_name,ipc.ipc_name,cert.certificate_name,tit.title_name,ah.meta_scr,cat.category_name,gat.gate_name,bo.boat_short_name "; 
		String queryStmtCondition = "FROM tbl_alarm_hist ah INNER JOIN tbl_ipc ipc ON ah.ipc_id=ipc.ipc_id "
				+ "INNER JOIN tbl_gate_info gat ON ipc.gate_id=gat.gate_id "
				+ "LEFT OUTER JOIN tbl_person_info per ON ah.person_id=per.person_id "
				+ "LEFT OUTER JOIN tbl_person_certificate cert ON per.certificate_id=cert.certificate_id "
				+ "LEFT OUTER JOIN tbl_person_title tit ON per.title_id=tit.title_id "
				+ "LEFT OUTER JOIN tbl_person_category cat ON per.category_id=cat.category_id "
				+ "LEFT OUTER JOIN tbl_boat_schedule sch ON (ah.alarm_time BETWEEN sch.date_arrival AND sch.date_departure) "
				+ "LEFT OUTER JOIN tbl_boat bo ON sch.boat_id=bo.boat_id ";
				if (getAlarmBy == ConstUtil.GET_ALARM_BY_ID) {
					queryStmtCondition += "WHERE ah.alarmh_id = ?";
					conditionParam = ""+param.getAlarmId();
				}else {
					queryStmtCondition += "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND ah.alarm_code = ?";
					conditionParam = ""+param.getAlarmCode();
				}
				 
		ResultStatus resultStatus = new ResultStatus();
		try {
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmtSelect+" "+queryStmtCondition, conditionParam);								
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					alarmResult = new QueryAlarmResult(queryResult);
					break;				
				}						
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getAlarm with alarmId:"+param.getAlarmId()+" success."));				
			}else {
				//record no found
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getAlarm with alarmId:"+param.getAlarmId()+" not found."));
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with alarmId:"+param.getAlarmId(), ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm with alarmId:"+param.getAlarmId());
		}							
		alarmResult.setResultStatus(resultStatus);			
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getAlarmList with result:"+resultStatus.toString()));
		return alarmResult;
	}
	@Override
	public QueryAlarmResult getAlarmById(String transactionId, ReportFEParam param) {
		// TODO Auto-generated method stub
		return this.getAlarm(transactionId, param, ConstUtil.GET_ALARM_BY_ID);
	}
	@Override
	public QueryAlarmResult getAlarmByCode(String transactionId, ReportFEParam param) {
		// TODO Auto-generated method stub
		return this.getAlarm(transactionId, param, ConstUtil.GET_ALARM_BY_CODE);
	}
		
	@Override
	public GoogleChartResultList getDailyFaceChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyFace"));
		String queryStmt = "SELECT DATE_FORMAT(alarm_time,'%Y-%m-%d') alarm_time,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match  " 
		+ "FROM tbl_alarm_hist "
		+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alarm_time BETWEEN ? AND ? "
		+ "GROUP BY DATE_FORMAT(alarm_time,'%Y-%m-%d') "
		+ "ORDER BY DATE_FORMAT(alarm_time,'%Y-%m-%d')";
		return this.getDailyChart(transactionId, queryStmt, param, "Date", ConstUtil.UNKNOWN_VISIT_DATE_CODE);
	}
	@Override
	public GoogleChartResultList getDailyFaceByGateChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyFaceByGate"));
		String queryStmt = "SELECT gat.gate_name,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match " 
				+ "FROM tbl_gate_info gat INNER JOIN tbl_ipc ipc ON gat.gate_id=ipc.gate_id "
				+ "INNER JOIN tbl_alarm_hist alm ON ipc.ipc_id=alm.ipc_id "
				+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alm.alarm_time BETWEEN ? AND ? "
				+ "GROUP BY gat.gate_name "
				+ "ORDER BY gat.gate_name";
		return this.getDailyChart(transactionId, queryStmt, param, "Gate", ConstUtil.UNKNOWN_GATEINFO_CODE);
	}
	@Override
	public GoogleMultiChartResult getDailyFaceTimeChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyFaceByTimeChart"));
		String queryStmt = "SELECT DATE_FORMAT(alarm_time,'%H') alarm_time,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match,'GROUP' AS group_name  " 
		+ "FROM tbl_alarm_hist "
		+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alarm_time BETWEEN ? AND ? "
		+ "GROUP BY DATE_FORMAT(alarm_time,'%H') "
		+ "ORDER BY DATE_FORMAT(alarm_time,'%H')";
		return this.getDailyByTimeChart(transactionId, queryStmt, param, ConstUtil.UNKNOWN_VISIT_DATE_CODE);
	}
	@Override
	public GoogleMultiChartResult getDailyFaceByGateTimeChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyFaceByGateByTimeChart"));
		String queryStmt = "SELECT DATE_FORMAT(alarm_time,'%H'),SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match,gat.gate_name " 
				+ "FROM tbl_gate_info gat INNER JOIN tbl_ipc ipc ON gat.gate_id=ipc.gate_id "
				+ "INNER JOIN tbl_alarm_hist alm ON ipc.ipc_id=alm.ipc_id "
				+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alm.alarm_time BETWEEN ? AND ? "
				+ "GROUP BY gat.gate_name,DATE_FORMAT(alarm_time,'%H') "
				+ "ORDER BY gat.gate_name,DATE_FORMAT(alarm_time,'%H')";
		return this.getDailyByTimeChart(transactionId, queryStmt, param, ConstUtil.UNKNOWN_GATEINFO_CODE);		
	}
	
	public GoogleChartResultList getDailyChart(String transactionId, String queryStmt, ReportFEParam param, String hAxisTitle, String notFoundTitle) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyChart:"+param.getStartDate()+","+param.getEndDate()));
		List<Object[]> queryResultList = null;
		DailyFaceChartField passengerField = null;
		GoogleChartResultList resultList = new GoogleChartResultList();
		GoogleChartResult passenger = new GoogleChartResult();
		resultList.sethAxisTitle(hAxisTitle);
		resultList.setvAxisTitle("No of passenger");		
		passenger.getResultList().add(hAxisTitle);
		passenger.getResultList().add(ConstUtil.ALARM_HISTORY_MATCH_CONDITION);
		passenger.getResultList().add(ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION);
		resultList.addResult(passenger);
		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		int noOfMatch = 0;
		int noOfUnMatch = 0;
		try {					
			paramList.add(startDate);
			paramList.add(endDate);
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);								
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					passengerField = new DailyFaceChartField(queryResult);
					passenger = new GoogleChartResult();
					passenger.getResultList().add(passengerField.getFieldLabel());
					passenger.getResultList().add(passengerField.getNoOfMatch()+"");
					passenger.getResultList().add(passengerField.getNoOfUnMatch()+"");
					resultList.addResult(passenger);
					noOfMatch += passengerField.getNoOfMatch();
					noOfUnMatch += passengerField.getNoOfUnMatch();					
				}						
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getDailyChart with result:"+resultList.getChartResultList().size()+" record"));				
			}else {
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getDailyChart 0 record"));
				passenger = new GoogleChartResult();
				passenger.getResultList().add(notFoundTitle);
				passenger.getResultList().add("0");
				passenger.getResultList().add("0");
				resultList.addResult(passenger);
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm getDailyFaceByGate", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm getDailyFaceByGate");
		}							
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getDailyFaceByGate with result:"+resultStatus.toString()));
		resultList.setFooter("Number of "+ConstUtil.ALARM_HISTORY_MATCH_CONDITION+" is "+noOfMatch+", Number of "+ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION+" is "+noOfUnMatch+", Total is "+(noOfMatch+noOfUnMatch));
		resultList.setResultStatus(resultStatus);		
		return resultList;
	}
	
	public GoogleMultiChartResult getDailyByTimeChart(String transactionId, String queryStmt, ReportFEParam param, String notFoundTitle) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyByTimeChart:"+param.getStartDate()+","+param.getEndDate()));
		List<Object[]> queryResultList = null;
		DailyFaceChartField passengerField = null;
		GoogleMultiChartResult multiChartResult = new GoogleMultiChartResult();
		GoogleChartResultList resultList = null;
		GoogleChartResult passenger = null;
		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		int noOfMatch = 0;
		int noOfUnMatch = 0;
		String prevGateName = null;		
		try {					
			paramList.add(startDate);
			paramList.add(endDate);
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			//result will look like this
			//hour1, match, 100
			//hour1, unmatch, 30
			//hour3, match, 500
			//hour3, unmatch, 66
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					passengerField = new DailyFaceChartField(queryResult);
					if (prevGateName==null||!prevGateName.equals(passengerField.getFieldGroup())) {
						if  (resultList!=null) {
							resultList.setFooter("Number of "+ConstUtil.ALARM_HISTORY_MATCH_CONDITION+" is "+noOfMatch+", Number of "+ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION+" is "+noOfUnMatch+", Total is "+(noOfMatch+noOfUnMatch));
							resultList.setResultStatus(resultStatus);
							noOfMatch = 0;
							noOfUnMatch = 0;
						}
						resultList = new GoogleChartResultList();
						multiChartResult.addGoogleChart(resultList);
						resultList.sethAxisTitle("Hour");
						resultList.setvAxisTitle("No of passenger");
						passenger = new GoogleChartResult();
						passenger.getResultList().add("Hour");
						passenger.getResultList().add(ConstUtil.ALARM_HISTORY_MATCH_CONDITION);						
						passenger.getResultList().add(ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION);
						resultList.addResult(passenger);						
						resultList.setTitle(passengerField.getFieldGroup());																											
						prevGateName = passengerField.getFieldGroup();
						passenger = null;
					}																							
					passenger = new GoogleChartResult();
					passenger.getResultList().add(passengerField.getFieldLabel());
					passenger.getResultList().add(passengerField.getNoOfMatch()+"");
					passenger.getResultList().add(passengerField.getNoOfUnMatch()+"");
					resultList.addResult(passenger);
					noOfMatch += passengerField.getNoOfMatch();
					noOfUnMatch += passengerField.getNoOfUnMatch();																	
				}//end of for (Object[] queryResult: queryResultList) {
				resultList.setFooter("Number of "+ConstUtil.ALARM_HISTORY_MATCH_CONDITION+" is "+noOfMatch+", Number of "+ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION+" is "+noOfUnMatch+", Total is "+(noOfMatch+noOfUnMatch));
				resultList.setResultStatus(resultStatus);
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getDailyByTimeChart with result:"+multiChartResult.getChartResultList().size()+" record"));				
			}else {
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getDailyByTimeChart 0 record"));
				resultList = new GoogleChartResultList();
				multiChartResult.addGoogleChart(resultList);
				resultList.sethAxisTitle("Hour");
				resultList.setvAxisTitle("No of passenger");
				passenger = new GoogleChartResult();
				passenger.getResultList().add("Hour");
				passenger.getResultList().add(ConstUtil.ALARM_HISTORY_MATCH_CONDITION);						
				passenger.getResultList().add(ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION);
				resultList.addResult(passenger);						
				resultList.setTitle(notFoundTitle);				
				passenger = new GoogleChartResult();
				passenger.getResultList().add(notFoundTitle);
				passenger.getResultList().add("0");
				passenger.getResultList().add("0");
				resultList = new GoogleChartResultList();				
				resultList.addResult(passenger);
				resultList.setFooter("Number of "+ConstUtil.ALARM_HISTORY_MATCH_CONDITION+" is "+noOfMatch+", Number of "+ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION+" is "+noOfUnMatch+", Total is "+(noOfMatch+noOfUnMatch));
				resultList.setResultStatus(resultStatus);
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data alarm get face by time IN-OUT", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "alarm get face by time IN-OUT");
		}							
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getDailyByTimeChart with result:"+resultStatus.toString()));
		multiChartResult.setResult(resultStatus);
		return multiChartResult;
	}
	
//	@Override
//	public QueryAlarmResultList getAlarmList(String transactionId, ReportFEParam param) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getAlarmList with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()));
//		ResultStatus resultStatus = new ResultStatus();
//		//random start record
//		Random ran = new Random();
//		int randomRecNo = ran.nextInt(200000);
//		randomRecNo = 0;
//		QueryAlarmResultList queryAlarmResultList = new QueryAlarmResultList();
//		String queryStmt = "SELECT ah.alarmh_id,ah.live_photo,ah.alarm_time,per.person_id,per.person_photo,AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') AS certificate_no, AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') AS full_name,cat.category_name "
//				+"FROM tbl_alarm_hist ah "
//				+ "LEFT OUTER JOIN tbl_person_info per "
//				+ "ON ah.person_id=per.person_id "
//				+ "LEFT OUTER JOIN tbl_person_category cat "
//				+ "ON per.category_id=cat.category_id "
//				+ "WHERE alarm_time BETWEEN ? AND ? ORDER BY alarm_time DESC LIMIT "+randomRecNo+", 10";
//		try {
//			Date startDate = StringUtil.stringToDate(param.getStartDate()+"00", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			Date endDate = StringUtil.stringToDate(param.getEndDate()+"00", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);			
//			QueryAlarmResult alarmResult = null;
//			for (Object[] queryResult: queryResultList) {
//				alarmResult = new QueryAlarmResult(queryResult);
//				queryAlarmResultList.getQueryAlarmResultList().add(alarmResult);				
//			}
//			Logger.info(this,  LogUtil.getLogInfo(transactionId, "getAlarmList no of result:"+queryAlarmResultList.getQueryAlarmResultList().size()+ " record."));			
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()); 
//		}		
//		queryAlarmResultList.setResult(resultStatus);
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getAlarmList with result:"+resultStatus.toString()));
//		return queryAlarmResultList;
//	}
	
//	@Override
//	public StatisticsResultList getARCStatistics(String transactionId, ReportFEParam param) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getARCStatistics with startDate:"+param.getStartDate()+" end date:"+param.getEndDate()));
//		ResultStatus resultStatus = new ResultStatus();
//		StatisticsResultList statisticsResultList = new StatisticsResultList();
//		String queryStmt = "SELECT gate_name,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match "
//				+ "FROM ("
//				+ "SELECT DISTINCT person_id,rec_match,rec_un_match, ipc_id FROM tbl_alarm_hist WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND person_id IS NOT NULL AND alarm_time BETWEEN ? AND ? "
//				+ "UNION ALL "
//				+ "SELECT person_id,0 rec_match,1 rec_un_match, ipc_id FROM tbl_alarm_hist WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND person_id IS NULL AND alarm_time BETWEEN ? AND ?"
//				+ ") t1 "
//				+ "INNER JOIN tbl_ipc ic "
//				+ "ON t1.ipc_id=ic.ipc_id "
//				+ "INNER JOIN tbl_gate_info gate "
//				+ "ON ic.gate_id=gate.gate_id "
//				+ "GROUP BY gate_name";
//		try {
//			Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//			Date endDate = StringUtil.stringToDate(param.getEndDate()+"235959", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);			
//			StatisticsResult statResult = null;
//			statisticsResultList.setStartDate(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			statisticsResultList.setEndDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			for (Object[] queryResult: queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.createARCStatistic(queryResult);
//				statisticsResultList.getStatisticsResultList().add(statResult);				
//				statisticsResultList.increaseNoPassengerMatchUnMatch(statResult.getNoPassengerMatchUnMatch());
//			}
//			Logger.info(this,  LogUtil.getLogInfo(transactionId, "getARCStatistics no of result:"+statisticsResultList.getStatisticsResultList().size()+ " record."));			
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()); 
//		}		
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getARCStatistics with result:"+resultStatus.toString()));
//		return statisticsResultList;
//	}
//	@Override
//	public StatisticsResultList getARCStatisticsTimePortion(String transactionId, ReportFEParam param) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getARCStatisticsTimePortion"));
//		ResultStatus resultStatus = new ResultStatus();
//		StatisticsResultList statisticsResultList = new StatisticsResultList();
////		SELECT ROUND DOWN TO 15
////		String queryStmt = "SELECT gate_name,alarm_time,0 AS passenger_in, 0 AS passenger_out, SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match "
////				+ "FROM ("
////				+ "SELECT DISTINCT DATE_FORMAT(FROM_UNIXTIME(FLOOR(UNIX_TIMESTAMP(alarm_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i') AS alarm_time,person_id,rec_match, rec_un_match, ipc_id FROM  tbl_alarm_hist WHERE person_id IS NOT NULL AND alarm_time BETWEEN ? AND ? "
////				+ "UNION ALL "
////				+ "SELECT DATE_FORMAT(FROM_UNIXTIME(FLOOR(UNIX_TIMESTAMP(alarm_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i') AS alarm_time, person_id,0 rec_match, 1 rec_un_match, ipc_id FROM  tbl_alarm_hist WHERE person_id IS NULL AND alarm_time BETWEEN ? AND ?"
////				+ ") t1 "
////				+ "INNER JOIN tbl_ipc ic "
////				+ "ON t1.ipc_id=ic.ipc_id "
////				+ "INNER JOIN tbl_gate_info gate "
////				+ "ON ic.gate_id=gate.gate_id "
////				+ "GROUP BY gate_name,alarm_time "
////				+ "ORDER BY gate_name,alarm_time";
////		SELECT ROUND UP TO 15
//		String queryStmt = "SELECT gate_name,alarm_time,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match "
//				+ "FROM ("
//				+ "SELECT DISTINCT DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(alarm_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i') AS alarm_time,person_id,rec_match, rec_un_match, ipc_id FROM  tbl_alarm_hist WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND person_id IS NOT NULL AND alarm_time BETWEEN ? AND ? "
//				+ "UNION ALL "
//				+ "SELECT DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(alarm_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i') AS alarm_time, person_id,0 rec_match, 1 rec_un_match, ipc_id FROM  tbl_alarm_hist WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND person_id IS NULL AND alarm_time BETWEEN ? AND ?"
//				+ ") t1 "
//				+ "INNER JOIN tbl_ipc ic "
//				+ "ON t1.ipc_id=ic.ipc_id "
//				+ "INNER JOIN tbl_gate_info gate "
//				+ "ON ic.gate_id=gate.gate_id "
//				+ "GROUP BY gate_name,alarm_time "
//				+ "ORDER BY gate_name,alarm_time";
//		
//		try {			
//			Date startDate = null;
//			Date endDate = null;
//			if (param.getDurationType()==ReportFEParam.TIME_DURATION_TYPE_NOW_MINUS_MINUTE) {
//				endDate = new Date();
//				endDate = DateTimeUtil.getTimeRoundDownToPortion(param.getTimePortion(), endDate);
//				startDate = DateTimeUtil.getTimeRoundDownToPortion(param.getTimePortion(), 
//						new Date(endDate.getTime()-(param.getTimeMinusMinute()*60*1000)));
//			}else {
//				startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//				endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			}
//			Logger.info(this,  LogUtil.getLogInfo(transactionId, "in parameter startDate:"+startDate+" endDate:"+endDate));
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);			
//			StatisticsResult statResult = null;
//			statisticsResultList.setStartDate(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS));
//			statisticsResultList.setEndDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS));
//			for (Object[] queryResult: queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.createARCStatisticTimePortion(queryResult);
//				statisticsResultList.getStatisticsResultList().add(statResult);				
//				statisticsResultList.increaseNoPassengerMatchUnMatch(statResult.getNoPassengerMatchUnMatch());
//			}
//			Logger.info(this,  LogUtil.getLogInfo(transactionId, "getARCStatisticsTimePortion no of result:"+statisticsResultList.getStatisticsResultList().size()+ " record."));			
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm time portion with startDate:"+param.getStartDate()+"endDate:"+param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm time portion with startDate:"+param.getStartDate()+" endDate:"+param.getEndDate()); 
//		}		
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getARCStatisticsTimePortion with result:"+resultStatus.toString()));
//		return statisticsResultList;
//	}
	
//	@Override
//	public GoogleChartResultList getPassengerIPC(String transactionId, ReportFEParam param) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getPassengerIPC:"+param.getStartDate()+","+param.getEndDate()));
//		List<Object[]> queryResultList = null;
//		DailyFaceChartField passengerField = null;
//		GoogleChartResultList resultList = new GoogleChartResultList();
//		GoogleChartResult passenger = new GoogleChartResult();
//		resultList.sethAxisTitle("Camera");
//		resultList.setvAxisTitle("No of passenger");		
//		passenger.getResultList().add("Camera");
//		passenger.getResultList().add(ConstUtil.ALARM_HISTORY_MATCH_CONDITION);
//		passenger.getResultList().add(ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION);
//		resultList.addResult(passenger);
//		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
//		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//		ArrayList<Object> paramList = new ArrayList<>();
//		String queryStmt = "SELECT ipc.ipc_name,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match " 
//				+ "FROM tbl_alarm_hist alm "
//				+ "INNER JOIN tbl_ipc ipc ON alm.ipc_id=ipc.ipc_id "
//				+ "WHERE alarm_source='"+HWAlarmHist.HW_VCM_ALARM+"' AND alarm_time BETWEEN ? AND ? "
//				+ "GROUP BY ipc.ipc_name "
//				+ "ORDER BY ipc.ipc_name";
//		ResultStatus resultStatus = new ResultStatus();
//		int noOfMatch = 0;
//		int noOfUnMatch = 0;
//		try {					
//			paramList.add(startDate);
//			paramList.add(endDate);
//			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);								
//			if (queryResultList.size()>0) {
//				for (Object[] queryResult: queryResultList) {
//					passengerField = new DailyFaceChartField(queryResult);
//					passenger = new GoogleChartResult();
//					passenger.getResultList().add(passengerField.getFieldLabel());
//					passenger.getResultList().add(passengerField.getNoOfMatch()+"");
//					passenger.getResultList().add(passengerField.getNoOfUnMatch()+"");
//					resultList.addResult(passenger);
//					noOfMatch += passengerField.getNoOfMatch();
//					noOfUnMatch += passengerField.getNoOfUnMatch();					
//				}						
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerIPC with result:"+resultList.getChartResultList().size()+" record"));				
//			}else {
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerIPC 0 record"));
//				passenger = new GoogleChartResult();
//				passenger.getResultList().add(ConstUtil.UNKNOWN_IPC_CODE);
//				passenger.getResultList().add("0");
//				passenger.getResultList().add("0");
//				resultList.addResult(passenger);
//			}
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm get passenger ipc", ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm get passenger ipc");
//		}							
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getPassengerIPC with result:"+resultStatus.toString()));
//		resultList.setTitle("Report passenger on "+ StringUtil.dateToString(StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD), StringUtil.DATE_FORMAT_DD_MM_YYYY)
//						+" match["+noOfMatch+"]/unmatch["+noOfUnMatch+"]");
//		resultList.setResultStatus(resultStatus);		
//		return resultList;
//	}
	
	//get of of person register vs person visit	
//	public GoogleChartResultList getPassengerRegister(String transactionId, ReportFEParam param) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getPassengerRegister:"+param.getStartDate()));
//		List<Object[]> queryResultList = null;
//		PassengerRegisterField passengerField = null;
//		ArrayList<Object> paramList = new ArrayList<>();
//		GoogleChartResultList chartResultList = new GoogleChartResultList();
//		GoogleChartResult passenger = new GoogleChartResult();
//		chartResultList.sethAxisTitle("Nationality");
//		chartResultList.setvAxisTitle("No of passenger");
//		chartResultList.setTitle("Report passenger register/visit");
//		passenger.getResultList().add("Nationality");
//		passenger.getResultList().add("Register");
//		passenger.getResultList().add("Visit");
//		chartResultList.addResult(passenger);
//		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//		String queryStmt = "SELECT IFNULL(alm1.nationality_name,?) AS nationality_name, alm1.noOfVisited, IFNULL(per1.noOfRegistered,0) AS noOfRegistered " 
//				+ "FROM ("
//				+ "SELECT alm2.nationality_name, SUM(noOfVisited) AS noOfVisited FROM ("
//				+ "SELECT alm.nationality_name, alm.person_id, CASE WHEN SUM(noOfVisited)=0 THEN 1 ELSE SUM(noOfVisited) END AS noOfVisited "
//				+ "FROM ("
//				+ "SELECT nat.nationality_name, alm.person_id, CASE WHEN alm.person_id IS NULL THEN 1 ELSE 0 END noOfVisited FROM tbl_alarm_hist alm "
//				+ "LEFT OUTER JOIN tbl_person_info per ON alm.person_id=per.person_id "
//				+ "LEFT OUTER JOIN tbl_person_nationality nat ON per.nationality_id=nat.nationality_id "
//				+ "INNER JOIN tbl_ipc ipc ON alm.ipc_id=ipc.ipc_id WHERE alarm_time BETWEEN ? AND ? "
//				+ "AND ipc.eqdirection_id=?) alm GROUP BY alm.nationality_name,alm.person_id) alm2 GROUP BY alm2.nationality_name"
//				+ ") alm1 LEFT OUTER JOIN ("
//				+ "SELECT nat.nationality_name, COUNT(per.person_id) AS noOfRegistered FROM tbl_person_info per "
//				+ "INNER JOIN tbl_person_reg_date reg ON per.person_id=reg.person_id "
//				+ "INNER JOIN tbl_person_nationality nat ON per.nationality_id=nat.nationality_id "
//				+ "WHERE register_date = ? GROUP BY nat.nationality_name) per1 ON alm1.nationality_name=per1.nationality_name " 
//				+ "ORDER BY IFNULL(alm1.nationality_name,?)";
//
//		ResultStatus resultStatus = new ResultStatus();
//		try {
//			paramList.add(ConstUtil.UNKNOWN_PERSON_NATIONALITY);
//			paramList.add(startDate);
//			paramList.add(endDate);
//			paramList.add(EquipmentDirection.DIRECTION_IN);
//			paramList.add(startDate);
//			paramList.add(ConstUtil.UNKNOWN_PERSON_NATIONALITY);
//			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);								
//			if (queryResultList.size()>0) {
//				for (Object[] queryResult: queryResultList) {
//					passengerField = new PassengerRegisterField(queryResult);
//					passenger = new GoogleChartResult();
//					passenger.getResultList().add(passengerField.getNationality());
//					passenger.getResultList().add(""+passengerField.getNoOfRegistered());
//					passenger.getResultList().add(""+passengerField.getNoOfVisited());
//					chartResultList.addResult(passenger);
//				}						
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerRegister with result:"+chartResultList.getChartResultList().size()+" record"));				
//			}else {
//				//record no found
//				passenger = new GoogleChartResult();
//				passenger.getResultList().add(ConstUtil.UNKNOWN_PERSON_NATIONALITY);
//				passenger.getResultList().add("0");
//				passenger.getResultList().add("0");
//				chartResultList.addResult(passenger);
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerRegister 0 record"));
//			}
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm get passenger register", ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwAlarm get passenger register");
//		}							
//		chartResultList.setResultStatus(resultStatus);			
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getPassengerRegister with result:"+resultStatus.toString()));
//		return chartResultList;
//	}	
}
