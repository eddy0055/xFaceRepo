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
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.DailyPassengerChartField;
import com.xpand.xface.bean.report.google.GoogleChartResult;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;
import com.xpand.xface.dao.HWGateAccessInfoDAO;
import com.xpand.xface.entity.EquipmentDirection;
import com.xpand.xface.entity.HWGateAccessInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.EquipmentDirectionService;
import com.xpand.xface.service.HWGateAccessInfoService;
import com.xpand.xface.service.HWGateInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class HWGateAccessInfoServiceImpl implements HWGateAccessInfoService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	HWGateAccessInfoDAO hwGateAccessInfoDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	EquipmentDirectionService equipmentDirectionService;
	@Autowired
	HWGateInfoService hwGateInfoService;

	@Override	
	public HWGateAccessInfo findById(String transactionId, Long recordId) {
		return this.hwGateAccessInfoDAO.findOne(recordId);
	}			
	
	//first dashboard of daily stat
	@Override
	public GoogleChartResultList getDailyPassengerChart(String transactionId, ReportFEParam param) {
		String queryStmt = "SELECT DATE_FORMAT(event_time,'%Y-%m-%d'),eqdirection_id,COUNT(*) cntPassenger " 
				+ "FROM tbl_gate_access_info ga "				
				+ "WHERE event_time BETWEEN ? AND ? "
				+ "GROUP BY DATE_FORMAT(event_time,'%Y-%m-%d'),eqdirection_id "
				+ "ORDER BY DATE_FORMAT(event_time,'%Y-%m-%d'),eqdirection_id";
		return this.getDailyChart(transactionId, queryStmt, param, "Date", ConstUtil.UNKNOWN_VISIT_DATE_CODE);
	}
	
	@Override
	public GoogleChartResultList getDailyPassengerByGateChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyPassengerByGateChart"));
		String queryStmt = "SELECT gin.gate_name,equ.eqdirection_id, COUNT(*) noOfTran FROM tbl_gate_access_info ga " 
		+ "INNER JOIN tbl_equ_direction equ ON ga.eqdirection_id=equ.eqdirection_id "
		+ "INNER JOIN tbl_gate_info gin ON ga.gate_id=gin.gate_id "
		+ "WHERE event_time BETWEEN ? AND ? "
		+ "GROUP BY gin.gate_name,equ.eqdirection_id "
		+ "ORDER BY gin.gate_name,equ.eqdirection_id";
		return this.getDailyChart(transactionId, queryStmt, param, "Gate", ConstUtil.UNKNOWN_GATEINFO_CODE);
	}

	@Override
	public GoogleChartResultList getDailyPassengerByBoatChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyPassengerByBoatChart"));
		String queryStmt = "SELECT bot.boat_short_name,eqdirection_id,COUNT(*) " 				
				+ "FROM tbl_boat bot "
				+ "INNER JOIN tbl_boat_schedule bsc "
				+ "ON bot.boat_id=bsc.boat_id "
				+ "INNER JOIN tbl_gate_access_info gat " 
				+ "ON bsc.gate_id=gat.gate_id "
				+ "WHERE gat.event_time BETWEEN ? AND ? "
				+ "AND gat.event_time BETWEEN bsc.date_arrival AND bsc.date_departure "
				+ "GROUP BY bot.boat_name,eqdirection_id "
				+ "ORDER BY bot.boat_name,eqdirection_id";
		return this.getDailyChart(transactionId, queryStmt, param, "Boat", ConstUtil.UNKNOWN_BOAT_CODE);
	}

	@Override
	public GoogleMultiChartResult getDailyPassengerTimeChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyPassengerTimeChart"));
		String queryStmt = "SELECT DATE_FORMAT(event_time,'%H'),eqdirection_id,COUNT(*) cntPassenger, 'GROUP' AS group_name " 
				+ "FROM tbl_gate_access_info ga "				
				+ "WHERE event_time BETWEEN ? AND ? "
				+ "GROUP BY DATE_FORMAT(event_time,'%H'),eqdirection_id "
				+ "ORDER BY DATE_FORMAT(event_time,'%H'),eqdirection_id";
		return this.getDailyByTimeChart(transactionId, queryStmt, param, ConstUtil.UNKNOWN_VISIT_DATE_CODE);
	}

	@Override
	public GoogleMultiChartResult getDailyPassengerByGateTimeChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyPassengerByGateTimeChart"));
		String queryStmt = "SELECT DATE_FORMAT(event_time,'%H') AS event_time,equ.eqdirection_id, COUNT(*) noOfTran,gin.gate_name "
		+ "FROM tbl_gate_access_info ga " 
		+ "INNER JOIN tbl_equ_direction equ ON ga.eqdirection_id=equ.eqdirection_id "
		+ "INNER JOIN tbl_gate_info gin ON ga.gate_id=gin.gate_id "
		+ "WHERE event_time BETWEEN ? AND ? "
		+ "GROUP BY gin.gate_name,DATE_FORMAT(event_time,'%H'),equ.eqdirection_id "
		+ "ORDER BY gin.gate_name,DATE_FORMAT(event_time,'%H'),equ.eqdirection_id";		
		return this.getDailyByTimeChart(transactionId, queryStmt, param, ConstUtil.UNKNOWN_GATEINFO_CODE);
	}

	@Override
	public GoogleMultiChartResult getDailyPassengerByBoatTimeChart(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyPassengerByBoatTimeChart"));
		String queryStmt = "SELECT DATE_FORMAT(event_time,'%H') event_time,eqdirection_id,COUNT(*),bot.boat_short_name "
				+ "FROM tbl_boat bot INNER JOIN tbl_boat_schedule bsc ON bot.boat_id=bsc.boat_id " 
				+ "INNER JOIN tbl_gate_access_info gat ON bsc.gate_id=gat.gate_id "
				+ "INNER JOIN tbl_gate_info gin ON gat.gate_id=gin.gate_id "
				+ "WHERE event_time BETWEEN ? AND ? "
				+ "AND gat.event_time BETWEEN bsc.date_arrival AND bsc.date_departure "
				+ "GROUP BY bot.boat_name,DATE_FORMAT(event_time,'%H'),eqdirection_id "
				+ "ORDER BY bot.boat_name,DATE_FORMAT(event_time,'%H'),eqdirection_id";
		return this.getDailyByTimeChart(transactionId, queryStmt, param, ConstUtil.UNKNOWN_BOAT_CODE);
	}
	public GoogleChartResultList getDailyChart(String transactionId, String queryStmt, ReportFEParam param, String hAxisTitle, String notFoundTitle) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyChart:"+param.getStartDate()+","+param.getEndDate()));
		List<Object[]> queryResultList = null;
		DailyPassengerChartField passengerField = null;
		GoogleChartResultList resultList = new GoogleChartResultList();
		GoogleChartResult passenger = new GoogleChartResult();
		EquipmentDirection eqDirectIN = this.equipmentDirectionService.findByDirectionCode(transactionId, EquipmentDirection.DIRECTION_IN);
		resultList.sethAxisTitle(hAxisTitle);
		resultList.setvAxisTitle("No of passenger");		
		passenger.getResultList().add(hAxisTitle);
		passenger.getResultList().add(eqDirectIN.getDirectionDesc());
		EquipmentDirection eqDirectOut = this.equipmentDirectionService.findByDirectionCode(transactionId, EquipmentDirection.DIRECTION_OUT);
		passenger.getResultList().add(eqDirectOut.getDirectionDesc());
		resultList.addResult(passenger);
		passenger = null;
		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		int noOfIN = 0;
		int noOfOUT = 0;
		String prevGateName = null;
		try {					
			paramList.add(startDate);
			paramList.add(endDate);
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			//result will look like this
			//BOAT1, in, 100
			//BOAT1, out, 30
			//BOAT2, in, 500
			//BOAT2, out, 66
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					passengerField = new DailyPassengerChartField(queryResult);
					if (prevGateName==null||!prevGateName.equals(passengerField.getFieldLabel())) {
						if  (passenger!=null) {
							passenger.getResultList().add("0");
							//add to result list when we define value for OUT coz OUT is alwary last
							resultList.addResult(passenger);
						}
												
						passenger = new GoogleChartResult();
						passenger.getResultList().add(passengerField.getFieldLabel());
						if (passengerField.getEqdirectionId()==EquipmentDirection.DIRECTION_IN) {
							passenger.getResultList().add(passengerField.getNoOfPassenger()+"");
							noOfIN += passengerField.getNoOfPassenger();
						}else {
							//0 for IN coz if start with out which mean no IN any more (we order by IN, OUT)
							passenger.getResultList().add("0"); 
							passenger.getResultList().add(passengerField.getNoOfPassenger()+"");
							//add to result list when we define value for OUT coz OUT is alwary last
							resultList.addResult(passenger);
							noOfOUT += passengerField.getNoOfPassenger();
							passenger = null;
						}						
						prevGateName = passengerField.getFieldLabel();
					}else {
						passenger.getResultList().add(passengerField.getNoOfPassenger()+"");
						//add to result list when we define value for OUT coz OUT is alwary last
						resultList.addResult(passenger);
						noOfOUT += passengerField.getNoOfPassenger();
						passenger = null;
					}																			
				}	
				if (passenger!=null) {
					//no add to result list and transaction is out
					passenger.getResultList().add("0");
					//add to result list when we define value for OUT coz OUT is alwary last
					resultList.addResult(passenger);										
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
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwGateAccess get passenger IN-OUT", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwGateAccess get passenger IN-OUT");
		}							
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getDailyChart with result:"+resultStatus.toString()));
		resultList.setFooter("Number of "+eqDirectIN.getDirectionDesc()+" is "+noOfIN+", Number of "+eqDirectOut.getDirectionDesc()+" is "+noOfOUT+", Total is "+(noOfIN+noOfOUT));
		resultList.setResultStatus(resultStatus);		
		return resultList;
	}
	public GoogleMultiChartResult getDailyByTimeChart(String transactionId, String queryStmt, ReportFEParam param, String notFoundTitle) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyByTimeChart:"+param.getStartDate()+","+param.getEndDate()));
		List<Object[]> queryResultList = null;
		DailyPassengerChartField passengerField = null;
		GoogleMultiChartResult multiChartResult = new GoogleMultiChartResult();
		GoogleChartResultList resultList = null;
		GoogleChartResult passenger = null;
		EquipmentDirection eqDirectIN = this.equipmentDirectionService.findByDirectionCode(transactionId, EquipmentDirection.DIRECTION_IN);
		EquipmentDirection eqDirectOut = this.equipmentDirectionService.findByDirectionCode(transactionId, EquipmentDirection.DIRECTION_OUT);		
		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
		Date endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		int noOfIN = 0;
		int noOfOUT = 0;
		String prevGateName = null;
		String prevHour = null;
		try {					
			paramList.add(startDate);
			paramList.add(endDate);
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			//result will look like this
			//hour1, in, 100
			//hour1, out, 30
			//hour3, in, 500
			//hour3, out, 66
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					passengerField = new DailyPassengerChartField(queryResult);
					if (prevGateName==null||!prevGateName.equals(passengerField.getFieldGroup())) {
						if  (passenger!=null) {
							passenger.getResultList().add("0");
							//add to result list when we define value for OUT coz OUT is alwary last
							resultList.addResult(passenger);
							resultList.setFooter("Number of "+eqDirectIN.getDirectionDesc()+" is "+noOfIN+", Number of "+eqDirectOut.getDirectionDesc()+" is "+noOfOUT+", Total is "+(noOfIN+noOfOUT));
							resultList.setResultStatus(resultStatus);
							noOfIN = 0;
							noOfOUT = 0;
						}
						resultList = new GoogleChartResultList();
						multiChartResult.addGoogleChart(resultList);
						resultList.sethAxisTitle("Hour");
						resultList.setvAxisTitle("No of passenger");
						passenger = new GoogleChartResult();
						passenger.getResultList().add("Hour");
						passenger.getResultList().add(eqDirectIN.getDirectionDesc());						
						passenger.getResultList().add(eqDirectOut.getDirectionDesc());
						resultList.addResult(passenger);						
						resultList.setTitle(passengerField.getFieldGroup());																											
						prevGateName = passengerField.getFieldGroup();
						passenger = null;
					}
					if (prevHour==null||!prevHour.equals(passengerField.getFieldLabel())) {
						if  (passenger!=null) {
							passenger.getResultList().add("0");
							//add to result list when we define value for OUT coz OUT is alwary last
							resultList.addResult(passenger);							
						}												
						passenger = new GoogleChartResult();
						passenger.getResultList().add(passengerField.getFieldLabel());
						if (passengerField.getEqdirectionId()==EquipmentDirection.DIRECTION_IN) {
							passenger.getResultList().add(passengerField.getNoOfPassenger()+"");
							noOfIN += passengerField.getNoOfPassenger();
						}else {
							//0 for IN coz if start with out which mean no IN any more (we order by IN, OUT)
							passenger.getResultList().add("0"); 
							passenger.getResultList().add(passengerField.getNoOfPassenger()+"");
							//add to result list when we define value for OUT coz OUT is alwary last
							resultList.addResult(passenger);
							noOfOUT += passengerField.getNoOfPassenger();
							passenger = null;
						}						
						prevHour = passengerField.getFieldGroup()+"@@"+ passengerField.getFieldLabel();
					}else {
						passenger.getResultList().add(passengerField.getNoOfPassenger()+"");
						//add to result list when we define value for OUT coz OUT is alwary last
						resultList.addResult(passenger);
						noOfOUT += passengerField.getNoOfPassenger();
						passenger = null;
					}//end of }else if (prevHour==null||!prevHour.equals(passengerField.getFieldLabel())) {
				}//end of for (Object[] queryResult: queryResultList) {
				if (passenger!=null) {
					//no add to result list and transaction is out
					passenger.getResultList().add("0");
					//add to result list when we define value for OUT coz OUT is alwary last
					resultList.addResult(passenger);					
				}
				resultList.setFooter("Number of "+eqDirectIN.getDirectionDesc()+" is "+noOfIN+", Number of "+eqDirectOut.getDirectionDesc()+" is "+noOfOUT+", Total is "+(noOfIN+noOfOUT));
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
				passenger.getResultList().add(eqDirectIN.getDirectionDesc());						
				passenger.getResultList().add(eqDirectOut.getDirectionDesc());
				resultList.addResult(passenger);						
				resultList.setTitle(notFoundTitle);				
				passenger = new GoogleChartResult();
				passenger.getResultList().add(notFoundTitle);
				passenger.getResultList().add("0");
				passenger.getResultList().add("0");
				resultList = new GoogleChartResultList();				
				resultList.addResult(passenger);
				resultList.setFooter("Number of "+eqDirectIN.getDirectionDesc()+" is "+noOfIN+", Number of "+eqDirectOut.getDirectionDesc()+" is "+noOfOUT+", Total is "+(noOfIN+noOfOUT));
				resultList.setResultStatus(resultStatus);
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwGateAccess get passenger by time IN-OUT", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "hwGateAccess get passenger by time IN-OUT");
		}							
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getDailyByTimeChart with result:"+resultStatus.toString()));
		multiChartResult.setResult(resultStatus);
		return multiChartResult;
	}
	
//	@Override
//	public StatisticsResultList getGateStatisticsDMY(String transactionId, ReportFEParam param) {
//		if (param.getTimePortionType() == ReportFEParam.TIME_PORTION_TYPE_DAILY) {
//			return this.getGateStatisticsDaily(transactionId, param);
//		} else if (param.getTimePortionType() == ReportFEParam.TIME_PORTION_TYPE_MONTHLY) {
//			return this.getGateStatisticsMonthly(transactionId, param);
//		} else if (param.getTimePortionType() == ReportFEParam.TIME_PORTION_TYPE_YEARLY) {
//			return this.getGateStatisticsYearly(transactionId, param);
//		} else {
//			return null;
//		}		
//	}
//	private StatisticsResultList getGateStatisticsDaily(String transactionId, ReportFEParam param) {
//		//start and end group by day
//		Logger.info(this, LogUtil.getLogInfo(transactionId,"in getGateStatisticsDaily with startDate:" + param.getStartDate() + " end date:" + param.getEndDate()));
//		ResultStatus resultStatus = new ResultStatus();
//		StatisticsResultList statisticsResultList = new StatisticsResultList();		
//		String queryStmt = "SELECT DATE_FORMAT(ginfo.event_time,'%Y%m%d') AS event_time,"
//				+ "direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran "
//				+ "FROM tbl_gate_access_info ginfo INNER JOIN tbl_gate_direction direct "
//				+ "ON ginfo.record_in_or_out=direct.direction_code WHERE ginfo.event_time BETWEEN ? AND ? "
//				+ "GROUP BY DATE_FORMAT(ginfo.event_time,'%Y%m%d'),direct.direction_code,direct.direction_desc "
//				+ "ORDER BY DATE_FORMAT(ginfo.event_time,'%Y%m%d'),direct.direction_code,direct.direction_desc";
//		try {
//			Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//			Date endDate = StringUtil.stringToDate(param.getEndDate() + "235959", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt,paramList);
//			StatisticsResult statResult = null;
//			statisticsResultList.setStartDate(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			statisticsResultList.setEndDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			for (Object[] queryResult : queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.getGateStatisticsDMY(queryResult);
//				statisticsResultList.addStatisticsResultList(transactionId, statResult);				
//			}
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "getGateStatisticsDaily no of result:"+ statisticsResultList.getStatisticsResultList().size() + " record."));
//		} catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with startDate:"
//					+ param.getStartDate() + " endDate:" + param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE,
//					"hwAlarm with startDate:" + param.getStartDate() + " endDate:" + param.getEndDate());
//		}
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this,LogUtil.getLogInfo(transactionId, "out getGateStatisticsDaily with result:" + resultStatus.toString()));
//		return statisticsResultList;	
//	}
//	private StatisticsResultList getGateStatisticsMonthly(String transactionId, ReportFEParam param) {
//		//year group by month
//		Logger.info(this, LogUtil.getLogInfo(transactionId,"in getGateStatisticsMonthly with startDate:" + param.getStartDate() + " end date:" + param.getEndDate()));
//		ResultStatus resultStatus = new ResultStatus();
//		StatisticsResultList statisticsResultList = new StatisticsResultList();		
//		String queryStmt = "SELECT DATE_FORMAT(ginfo.event_time,'%Y%m%d') AS event_time,"
//				+ "direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran "
//				+ "FROM tbl_gate_access_info ginfo INNER JOIN tbl_gate_direction direct "
//				+ "ON ginfo.record_in_or_out=direct.direction_code WHERE ginfo.event_time BETWEEN ? AND ? "
//				+ "GROUP BY DATE_FORMAT(ginfo.event_time,'%Y%m%d'),direct.direction_code,direct.direction_desc "
//				+ "ORDER BY DATE_FORMAT(ginfo.event_time,'%Y%m%d'),direct.direction_code,direct.direction_desc";
//		try {
//			Date startDate = StringUtil.stringToDate(param.getStartYear()+"0101", StringUtil.DATE_FORMAT_YYYYMMDD);
//			Date endDate = StringUtil.stringToDate(param.getStartYear()+"1231235959", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt,paramList);
//			StatisticsResult statResult = null;
//			statisticsResultList.setStartDate(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			statisticsResultList.setEndDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			for (Object[] queryResult : queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.getGateStatisticsDMY(queryResult);
//				statisticsResultList.addStatisticsResultList(transactionId, statResult);				
//			}
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "getGateStatisticsMonthly no of result:"+ statisticsResultList.getStatisticsResultList().size() + " record."));
//		} catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with startDate:"
//					+ param.getStartDate() + " endDate:" + param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE,
//					"hwAlarm with startDate:" + param.getStartDate() + " endDate:" + param.getEndDate());
//		}
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this,LogUtil.getLogInfo(transactionId, "out getGateStatisticsMonthly with result:" + resultStatus.toString()));
//		return statisticsResultList;	
//	}
//	private StatisticsResultList getGateStatisticsYearly(String transactionId, ReportFEParam param) {
//		//group by year
//		return null;
//	}
	
//	@Override
//	public StatisticsResultList getGateStatistics(String transactionId, ReportFEParam param) {
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getGateStatistics with startDate:" + param.getStartDate() + " end date:" + param.getEndDate()));
//		ResultStatus resultStatus = new ResultStatus();
//		StatisticsResultList statisticsResultList = new StatisticsResultList();
//		// String queryStmt = "SELECT aa.gate_name,aa.direction_desc in_dir,aa.cnt_tran
//		// cnt_in, bb.direction_desc out_dir, bb.cnt_tran cnt_out " +
//		// "FROM (" +
//		// "SELECT gate.gate_name,direct.direction_desc,COUNT(record_id) cnt_tran " +
//		// "FROM tbl_gate_access_info ginfo " +
//		// "INNER JOIN tbl_gate_info gate " +
//		// "ON ginfo.gate_id = gate.gate_id " +
//		// "INNER JOIN tbl_gate_direction direct " +
//		// "ON ginfo.record_in_or_out=direct.direction_code " +
//		// "WHERE ginfo.record_in_or_out="+HWGateDirection.DIRECTION_IN +
//		// " AND ginfo.event_time BETWEEN ? AND ? "+
//		// " GROUP BY gate.gate_name,direct.direction_desc) AS aa," +
//		// "(SELECT gate.gate_name,direct.direction_desc,COUNT(record_id) cnt_tran " +
//		// "FROM tbl_gate_access_info ginfo " +
//		// "INNER JOIN tbl_gate_info gate " +
//		// "ON ginfo.gate_id = gate.gate_id " +
//		// "INNER JOIN tbl_gate_direction direct " +
//		// "ON ginfo.record_in_or_out=direct.direction_code " +
//		// "WHERE ginfo.record_in_or_out="+HWGateDirection.DIRECTION_IN+
//		// " AND ginfo.event_time BETWEEN ? AND ? "+
//		// " GROUP BY gate.gate_name,direct.direction_desc) AS bb";
//		String queryStmt = "SELECT gate.gate_name, direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran "
//				+ "FROM tbl_gate_access_info ginfo INNER JOIN tbl_gate_info gate "
//				+ "ON ginfo.gate_id = gate.gate_id INNER JOIN tbl_gate_direction direct "
//				+ "ON ginfo.record_in_or_out=direct.direction_code WHERE ginfo.event_time BETWEEN ? AND ? "
//				+ "GROUP BY gate.gate_name,direct.direction_code,direct.direction_desc "
//				+ "ORDER BY gate.gate_name,direct.direction_code";
//		try {
//			Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//			Date endDate = StringUtil.stringToDate(param.getEndDate() + "235959",
//					StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt,
//					paramList);
//			StatisticsResult statResult = null;
//			statisticsResultList.setStartDate(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			statisticsResultList.setEndDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			for (Object[] queryResult : queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.createGateStatistic(queryResult);
//				statisticsResultList.addStatisticsResultList(transactionId, statResult);				
//			}
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "getGateStatistics no of result:"
//					+ statisticsResultList.getStatisticsResultList().size() + " record."));
//		} catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with startDate:"
//					+ param.getStartDate() + " endDate:" + param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE,
//					"hwAlarm with startDate:" + param.getStartDate() + " endDate:" + param.getEndDate());
//		}
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this,
//				LogUtil.getLogInfo(transactionId, "out getGateStatistics with result:" + resultStatus.toString()));
//		return statisticsResultList;
//	}
//
//	@Override
//	public StatisticsResultList getGateStatisticsTimePortion(String transactionId, ReportFEParam param) {
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getARCStatisticsTimePortion"));
//		ResultStatus resultStatus = new ResultStatus();
//		StatisticsResultList statisticsResultList = new StatisticsResultList();
////		String queryStmt = "SELECT aa.gate_name,IFNULL(aa.event_time,bb.event_time) AS event_time,aa.direction_desc in_dir,aa.cnt_tran cnt_in, bb.direction_desc out_dir, bb.cnt_tran cnt_out "
////				+ "FROM (" + "SELECT gate.gate_name, DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("
////				+ param.getTimePortion() + "*60))*(" + param.getTimePortion()
////				+ "*60)),'%H:%i') AS event_time,direct.direction_desc,COUNT(record_id) cnt_tran "
////				+ "FROM tbl_gate_access_info ginfo " + "INNER JOIN tbl_gate_info gate "
////				+ "ON ginfo.gate_id = gate.gate_id " + "INNER JOIN tbl_gate_direction direct "
////				+ "ON ginfo.record_in_or_out=direct.direction_code " + "WHERE ginfo.record_in_or_out="
////				+ HWGateDirection.DIRECTION_IN + " AND ginfo.event_time BETWEEN ? AND ? "
////				+ " GROUP BY gate.gate_name,DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("
////				+ param.getTimePortion() + "*60))*(" + param.getTimePortion()
////				+ "*60)),'%H:%i'),direct.direction_desc) AS aa,"
////				+ "(SELECT gate.gate_name,DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("
////				+ param.getTimePortion() + "*60))*(" + param.getTimePortion()
////				+ "*60)),'%H:%i') AS event_time,direct.direction_desc,COUNT(record_id) cnt_tran "
////				+ "FROM tbl_gate_access_info ginfo " + "INNER JOIN tbl_gate_info gate "
////				+ "ON ginfo.gate_id = gate.gate_id " + "INNER JOIN tbl_gate_direction direct "
////				+ "ON ginfo.record_in_or_out=direct.direction_code " + "WHERE ginfo.record_in_or_out="
////				+ HWGateDirection.DIRECTION_IN + " AND ginfo.event_time BETWEEN ? AND ? "
////				+ " GROUP BY gate.gate_name,DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("
////				+ param.getTimePortion() + "*60))*(" + param.getTimePortion()
////				+ "*60)),'%H:%i'),direct.direction_desc) AS bb";
//		String queryStmt = "SELECT gate.gate_name,DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i') AS event_time,"
//				+ "direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran "
//				+ "FROM tbl_gate_access_info ginfo INNER JOIN tbl_gate_info gate "
//				+ "ON ginfo.gate_id = gate.gate_id INNER JOIN tbl_gate_direction direct "
//				+ "ON ginfo.record_in_or_out=direct.direction_code WHERE ginfo.event_time BETWEEN ? AND ? "
//				+ "GROUP BY gate.gate_name,DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i'),"
//				+ "direct.direction_code,direct.direction_desc "
//				+ "ORDER BY gate.gate_name,DATE_FORMAT(FROM_UNIXTIME(CEIL(UNIX_TIMESTAMP(ginfo.event_time)/("+param.getTimePortion()+"*60))*("+param.getTimePortion()+"*60)),'%H:%i'),"
//				+ "direct.direction_code";
//		try {
//			Date startDate = null;
//			Date endDate = null;
//			if (param.getDurationType() == ReportFEParam.TIME_DURATION_TYPE_NOW_MINUS_MINUTE) {
//				endDate = new Date();
//				endDate = DateTimeUtil.getTimeRoundDownToPortion(param.getTimePortion(), endDate);
//				startDate = DateTimeUtil.getTimeRoundDownToPortion(param.getTimePortion(),
//						new Date(endDate.getTime() - (param.getTimeMinusMinute() * 60 * 1000)));
//			} else if (param.getDurationType() == ReportFEParam.TIME_DURATION_TYPE_START_END) {
//				startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//				endDate = StringUtil.stringToDate(param.getEndDate(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//			} else {
//				startDate = new Date();
//				endDate = startDate;
//			}
//			Logger.info(this,
//					LogUtil.getLogInfo(transactionId, "in parameter startDate:" + startDate + " endDate:" + endDate));
//			ArrayList<Date> paramList = new ArrayList<>();
//			paramList.add(startDate);
//			paramList.add(endDate);
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt,
//					paramList);
//			StatisticsResult statResult = null;
//			statisticsResultList
//					.setStartDate(StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS));
//			statisticsResultList
//					.setEndDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS));
//			for (Object[] queryResult : queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.createGateStatisticTimePortion(queryResult);
//				statisticsResultList.addStatisticsResultList(transactionId, statResult);				
//			}
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "getARCStatisticsTimePortion no of result:"
//					+ statisticsResultList.getStatisticsResultList().size() + " record."));
//		} catch (Exception ex) {
//			Logger.error(this,LogUtil.getLogError(transactionId, "error while read data hwAlarm time portion with startDate:"+ param.getStartDate() + "endDate:" + param.getEndDate(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE,"hwAlarm time portion with startDate:" + param.getStartDate() + " endDate:" + param.getEndDate());
//		}
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this, LogUtil.getLogInfo(transactionId,"out getARCStatisticsTimePortion with result:" + resultStatus.toString()));
//		return statisticsResultList;
//	}
//
//	@Override
//	public StatisticsResultList getGateStatisticsDMY(String transactionId, ReportFEParam param) {
//		Logger.info(this, LogUtil.getLogInfo(transactionId,"in getGateStatisticsDMY duration type:"+param.getDurationType()));				
//		ResultStatus resultStatus = new ResultStatus();
//		ReportDMYParam dmyParam = null;
//		if (param.getDurationType() == ReportFEParam.TIME_DURATION_TYPE_DAILY) {
//			dmyParam = this.getGateStatisticsDaily(transactionId, param);
//		} else if (param.getDurationType() == ReportFEParam.TIME_DURATION_TYPE_MONTHLY) {
//			dmyParam = this.getGateStatisticsMonthly(transactionId, param);
//		} else if (param.getDurationType() == ReportFEParam.TIME_DURATION_TYPE_YEARLY) {
//			dmyParam = this.getGateStatisticsYearly(transactionId, param);
//		}
//		if (dmyParam==null) {
//			Logger.info(this, LogUtil.getLogInfo(transactionId,"out getGateStatisticsDMY invalid portion type"));
//			return null;
//		}else {
//			Logger.info(this, LogUtil.getLogInfo(transactionId,"dmyParam:"+dmyParam.toString()));
//		}
//		StatisticsResultList statisticsResultList = new StatisticsResultList();		
//		String queryStmt = "SELECT DATE_FORMAT(ginfo.event_time,?) AS event_time,"
//				+ "direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran "
//				+ "FROM tbl_gate_access_info ginfo INNER JOIN tbl_gate_direction direct "
//				+ "ON ginfo.record_in_or_out=direct.direction_code WHERE ginfo.event_time BETWEEN ? AND ? "
//				+ "GROUP BY DATE_FORMAT(ginfo.event_time,?),direct.direction_code,direct.direction_desc "
//				+ "ORDER BY DATE_FORMAT(ginfo.event_time,?),direct.direction_code,direct.direction_desc";
//		try {
//			ArrayList<Object> paramList = new ArrayList<>();
//			paramList.add(dmyParam.getDateFormat());
//			paramList.add(dmyParam.getStartDate());
//			paramList.add(dmyParam.getEndDate());
//			paramList.add(dmyParam.getDateFormat());
//			paramList.add(dmyParam.getDateFormat());
//			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt,paramList);
//			StatisticsResult statResult = null;
//			statisticsResultList.setStartDate(StringUtil.dateToString(dmyParam.getStartDate(), StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			statisticsResultList.setEndDate(StringUtil.dateToString(dmyParam.getEndDate(), StringUtil.DATE_FORMAT_DD_MM_YYYY));
//			for (Object[] queryResult : queryResultList) {
//				statResult = new StatisticsResult();
//				statResult.getGateStatisticsDMY(queryResult);
//				statisticsResultList.addStatisticsResultList(transactionId, statResult);				
//			}
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "getGateStatisticsDMY no of result:"+ statisticsResultList.getStatisticsResultList().size() + " record."));
//		} catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data hwAlarm with param:"+dmyParam.toString(), ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE,"hwAlarm with param:"+dmyParam.toString());
//		}
//		statisticsResultList.setResult(resultStatus);
//		Logger.info(this,LogUtil.getLogInfo(transactionId, "out getGateStatisticsDMY with result:" + resultStatus.toString()));
//		return statisticsResultList;
//	}
	
//	private ReportDMYParam getGateStatisticsDaily(String transactionId, ReportFEParam param) {
//		//start and end group by day
//		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
//		Date endDate = StringUtil.stringToDate(param.getEndDate()+"235959", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//		String dateFormat = "%d/%m/%Y";
//		return new ReportDMYParam(startDate, endDate, dateFormat);
//	}
//	private ReportDMYParam getGateStatisticsMonthly(String transactionId, ReportFEParam param) {
//		//year group by month
//		Date startDate = StringUtil.stringToDate(param.getStartYear()+"0101", StringUtil.DATE_FORMAT_YYYYMMDD);
//		Date endDate = StringUtil.stringToDate(param.getStartYear()+"1231235959", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//		String dateFormat = "%b %Y";
//		return new ReportDMYParam(startDate, endDate, dateFormat);
//	}
//	private ReportDMYParam getGateStatisticsYearly(String transactionId, ReportFEParam param) {
//		//group by year
//		Date startDate = StringUtil.stringToDate("20000101", StringUtil.DATE_FORMAT_YYYYMMDD);
//		Date endDate = StringUtil.stringToDate("20991231235959", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
//		String dateFormat = "%Y";
//		return new ReportDMYParam(startDate, endDate, dateFormat);		
//	}	
}
