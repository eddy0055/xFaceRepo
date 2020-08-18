package com.xpand.xface.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.report.QueryAlarmPortionResultList;
import com.xpand.xface.bean.report.QueryAlarmResult;
import com.xpand.xface.bean.report.QueryAlarmResultList;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.EquipmentDirectionService;
import com.xpand.xface.service.HWAlarmHistService;
import com.xpand.xface.service.HWGateAccessInfoService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.ReportService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class ReportServiceImpl implements ReportService , Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	HWAlarmHistService hwAlarmHistService;
	@Autowired
	PersonInfoService personInfoService;
	@Autowired
	HWGateAccessInfoService hwGateAccessInfoService;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	EquipmentDirectionService equipmentDirectionService;
	
	//call from alarm history
	@Override
	public QueryAlarmPortionResultList getAlarmListPortionList(String transactionId, ReportFEParam param) {
		return this.hwAlarmHistService.getAlarmListPortionList(transactionId, param);
	}
	@Override
	public QueryAlarmResultList getAlarmListPortionPartList(String transactionId, ReportFEParam param) {
		int facePageSize = StringUtil.stringToInteger(this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_FACE_ALARM_HISTORY_PAGE_SIZE).getAppValue1(), 12);
		return this.hwAlarmHistService.getAlarmListPortionPartList(transactionId, param, facePageSize);
	}

	@Override
	public QueryAlarmResult getAlarmById(String transactionId, ReportFEParam param) {
		return this.hwAlarmHistService.getAlarmById(transactionId, param);
	}
	////////////////////////////
	
	//call from alarm monitor, indv
	@Override
	public QueryAlarmResult getAlarmByCode(String transactionId, ReportFEParam param) {
		return this.hwAlarmHistService.getAlarmByCode(transactionId, param);
	}
	///////////////////
	
	//call from DailyStatistics.js			
	@Override
	public GoogleChartResultList getDailyPassenger(String transactionId, ReportFEParam param) {		
		return this.hwGateAccessInfoService.getDailyPassengerChart(transactionId, param);
	}
	@Override
	public GoogleChartResultList getDailyPassengerByGate(String transactionId, ReportFEParam param) {
		return this.hwGateAccessInfoService.getDailyPassengerByGateChart(transactionId, param);
	}
	@Override
	public GoogleChartResultList getDailyFace(String transactionId, ReportFEParam param) {
		return this.hwAlarmHistService.getDailyFaceChart(transactionId, param);
	}
	@Override
	public GoogleChartResultList getDailyFaceByGate(String transactionId, ReportFEParam param) {
		return this.hwAlarmHistService.getDailyFaceByGateChart(transactionId, param);	
	}
	@Override
	public GoogleChartResultList getDailyPassengerByBoat(String transactionId, ReportFEParam param) {
		return this.hwGateAccessInfoService.getDailyPassengerByBoatChart(transactionId, param);
	}
	//////////////////////////////////
	
	//call from DailyStatisticsByTime.js
	@Override
	public GoogleMultiChartResult getDailyByTime(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getDailyByTime with dashboard type:"+param.getDashBoardType()));
		GoogleMultiChartResult chartResult = null;
		//dev only prevent null pointer error
		chartResult = new GoogleMultiChartResult();
		chartResult.setResult(new ResultStatus(ResultStatus.INVALID_DASHBOARD_PARAMETER_CODE, param.getDashBoardType()));
		//////////
		if (ConstUtil.REPORT_NO_PASSENGER_BY_TIME_CODE.equals(param.getDashBoardType())) {
			//gate access info
			chartResult = this.hwGateAccessInfoService.getDailyPassengerTimeChart(transactionId, param);
		} else if (ConstUtil.REPORT_NO_PASSENGER_GATE_BY_TIME_CODE.equals(param.getDashBoardType())) {
			//gate access info
			chartResult = this.hwGateAccessInfoService.getDailyPassengerByGateTimeChart(transactionId, param);
		} else if (ConstUtil.REPORT_NO_FACE_BY_TIME_CODE.equals(param.getDashBoardType())) {
			//alarm
			chartResult = this.hwAlarmHistService.getDailyFaceTimeChart(transactionId, param);
		} else if (ConstUtil.REPORT_NO_FACE_GATE_BY_TIME_CODE.equals(param.getDashBoardType())) {
			//alarm
			chartResult = this.hwAlarmHistService.getDailyFaceByGateTimeChart(transactionId, param);
		} else if (ConstUtil.REPORT_NO_PASSENGER_BOAT_BY_TIME_CODE.equals(param.getDashBoardType())) {
			//gate access info
			chartResult = this.hwGateAccessInfoService.getDailyPassengerByBoatTimeChart(transactionId, param);
		}else {
			chartResult = new GoogleMultiChartResult();
			chartResult.setResult(new ResultStatus(ResultStatus.INVALID_DASHBOARD_PARAMETER_CODE, param.getDashBoardType()));
		}
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getDailyByTime with status:"+chartResult.getResult().toString()));
		return chartResult;
	}	
	/////////////////////////////////////
	
	//no once call
//	@Override
//	public StatisticsResultList getGateStatistics(String transactionId, ReportFEParam param) {		
//		return this.hwGateAccessInfoService.getGateStatistics(transactionId, param);
//	}
//
//	@Override
//	public StatisticsResultList getGateStatisticsTimePortion(String transactionId, ReportFEParam param) {
//		return this.hwGateAccessInfoService.getGateStatisticsTimePortion(transactionId, param);
//	}
//	
//	@Override
//	public StatisticsResultList getGateStatisticsDMY(String transactionId, ReportFEParam param) {		
//		return this.hwGateAccessInfoService.getGateStatisticsDMY(transactionId, param);
//	}
//
//	@Override
//	public StatisticsResultList getARCStatistics(String transactionId, ReportFEParam param) {
//		return this.hwAlarmHistService.getARCStatistics(transactionId, param);
//	}
//
//	@Override
//	public StatisticsResultList getARCStatisticsTimePortion(String transactionId, ReportFEParam param) {
//		return this.hwAlarmHistService.getARCStatisticsTimePortion(transactionId, param);
//	}
//
//	@Override
//	public GoogleChartResultList getPassengerRegister(String transactionId, ReportFEParam param) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getPassengerRegistered:"+param.getStartDate()+","+param.getEndDate()));
//		ArrayList<PassengerRegisterField> visitedList = this.hwAlarmHistService.getPassengerVisited(transactionId, param);
//		ArrayList<PassengerRegisterField> registeredList = this.personInfoService.getPassengerRegistered(transactionId, param);
//		ArrayList<PassengerRegisterField> finalResultList = new ArrayList<>(); 
//		GoogleChartResultList chartResultList = new GoogleChartResultList();
//		GoogleChartResult passenger = new GoogleChartResult();
//		chartResultList.sethAxisTitle("Nationality");
//		chartResultList.setvAxisTitle("No of passenger");		
//		passenger.getResultList().add("Nationality");
//		passenger.getResultList().add(ConstUtil.ALARM_HISTORY_PERSON_REGISTER);
//		passenger.getResultList().add(ConstUtil.ALARM_HISTORY_PERSON_VISIT);
//		chartResultList.addResult(passenger);
//		PassengerRegisterField finalResult = null;
//		int indexFound = -1;
//		//put all registered result to final result
//		for (PassengerRegisterField passengerField: registeredList) {			
//			finalResultList.add(passengerField);			
//		}
//		//if key is found then update visited value
//		//else add visited value to final result
//		for (PassengerRegisterField visitedField: visitedList) {
//			indexFound = -1;
//			for (PassengerRegisterField finalField: finalResultList) {
//				indexFound++;
//				if (finalField.getNationality().equals(visitedField.getNationality())) {
//					finalResult = finalField;
//					break;
//				}
//			}
//			if (finalResult==null) {
//				finalResultList.add(visitedField);				
//			}else {				
//				finalResult.setNoOfVisited(visitedField.getNoOfVisited());
//				//update result back to list
//				finalResultList.set(indexFound, finalResult);				
//				finalResult = null;
//			}
//		}
//		boolean foundData = false;
//		int noOfVisited = 0;
//		int noOfRegistered = 0;
//		//final result haspmap to google chart list
//		for (PassengerRegisterField finalField: finalResultList) {
//			passenger = new GoogleChartResult();
//			passenger.getResultList().add(finalField.getNationality());
//			passenger.getResultList().add(finalField.getNoOfRegistered()+"");
//			passenger.getResultList().add(finalField.getNoOfVisited()+"");
//			chartResultList.addResult(passenger);
//			if (!foundData) {
//				foundData = true;
//			}
//			noOfRegistered += finalField.getNoOfRegistered();
//			noOfVisited += finalField.getNoOfVisited();
//		}
//		if (!foundData) {
//			//add default
//			passenger = new GoogleChartResult();
//			passenger.getResultList().add(ConstUtil.UNKNOWN_PERSON_NATIONALITY);
//			passenger.getResultList().add("0");
//			passenger.getResultList().add("0");			
//			chartResultList.addResult(passenger);
//		}
//		chartResultList.setTitle("Report passenger on "+StringUtil.dateToString(StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD), StringUtil.DATE_FORMAT_DD_MM_YYYY)
//				+" register["+noOfRegistered+"]/visit["+noOfVisited+"]");
//		chartResultList.setResultStatus(new ResultStatus());
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getPassengerRegistered with result:"+chartResultList.getChartResultList().size()+" record"));
//		return chartResultList;		
//	}
//	@Override
//	public GoogleChartResultList getPassengerIPC(String transactionId, ReportFEParam param) {
//		return this.hwAlarmHistService.getPassengerIPC(transactionId, param);
//	}
}
