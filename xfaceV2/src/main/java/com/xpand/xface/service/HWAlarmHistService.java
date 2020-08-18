package com.xpand.xface.service;

import java.util.ArrayList;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.report.QueryAlarmPortionResultList;
import com.xpand.xface.bean.report.QueryAlarmResult;
import com.xpand.xface.bean.report.QueryAlarmResultList;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;
import com.xpand.xface.bean.report.google.PassengerRegisterField;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.PersonInfo;

public interface HWAlarmHistService {
	public ResultStatus updateAlarm(String transactionId, HWAlarmHist hwAlarmHist);
	public ResultStatus removeRelationshipPersonInfo(String transactionId, PersonInfo personInfo);
		
	//call from alarm history
	public QueryAlarmPortionResultList getAlarmListPortionList(String transactionId, ReportFEParam param);
	public QueryAlarmResultList getAlarmListPortionPartList(String transactionId, ReportFEParam param, int pageSize);
	public QueryAlarmResult getAlarmById(String transactionId, ReportFEParam param);
	////////////////////////
	
	//call from alarm monitor, indv
	public QueryAlarmResult getAlarmByCode(String transactionId, ReportFEParam param);
	//////////////
		
	public ArrayList<PassengerRegisterField> getPassengerVisited(String transactionId, ReportFEParam param); 	

	
	
	//call from dailystatictis
	public GoogleChartResultList getDailyFaceChart(String transactionId, ReportFEParam param);
	public GoogleChartResultList getDailyFaceByGateChart(String transactionId, ReportFEParam param);
	////////////////////////
	//call from dailystatistic by time
	public GoogleMultiChartResult getDailyFaceTimeChart(String transactionId, ReportFEParam param);
	public GoogleMultiChartResult getDailyFaceByGateTimeChart(String transactionId, ReportFEParam param);
	///////////////////////////////
	
//	//no once call
//	public StatisticsResultList getARCStatistics(String transactionId, ReportFEParam param);
//	public StatisticsResultList getARCStatisticsTimePortion(String transactionId, ReportFEParam param);
	
	//call from old landing page 3 chart
//	public GoogleChartResultList getPassengerIPC(String transactionId, ReportFEParam param);
//	///////////////
}
