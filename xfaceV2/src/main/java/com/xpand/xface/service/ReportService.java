package com.xpand.xface.service;

import com.xpand.xface.bean.report.QueryAlarmPortionResultList;
import com.xpand.xface.bean.report.QueryAlarmResult;
import com.xpand.xface.bean.report.QueryAlarmResultList;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;

public interface ReportService {			
	
	//call from alarm monitor, indv
	public QueryAlarmResult getAlarmByCode(String transactionId, ReportFEParam param);
	
	//call from alarm history
	public QueryAlarmResult getAlarmById(String transactionId, ReportFEParam param);	
	public QueryAlarmPortionResultList getAlarmListPortionList(String transactionId, ReportFEParam param);
	public QueryAlarmResultList getAlarmListPortionPartList(String transactionId, ReportFEParam param);
	////////////////////////////	

	//call from DailyStatistics.js
	//get daily passenger	
	public GoogleChartResultList getDailyPassenger(String transactionId, ReportFEParam param);
	//get daily passenger by gate
	public GoogleChartResultList getDailyPassengerByGate(String transactionId, ReportFEParam param);
	/////////////////
	//get daily face match/unmatch statistic	
	public GoogleChartResultList getDailyFace(String transactionId, ReportFEParam param);
	//get daily face match/unmatch statistic by gate
	public GoogleChartResultList getDailyFaceByGate(String transactionId, ReportFEParam param);
	//get daily face visitor statistic by boat
	public GoogleChartResultList getDailyPassengerByBoat(String transactionId, ReportFEParam param);
	//cal from DailyStatisticsByTime.js
	public GoogleMultiChartResult getDailyByTime(String transactionId, ReportFEParam param);

	
	//no one call
//	public StatisticsResultList getGateStatistics(String transactionId, ReportFEParam param);
//	public StatisticsResultList getGateStatisticsTimePortion(String transactionId, ReportFEParam param);
//	public StatisticsResultList getGateStatisticsDMY(String transactionId, ReportFEParam param);
//	public StatisticsResultList getARCStatistics(String transactionId, ReportFEParam param);
//	public StatisticsResultList getARCStatisticsTimePortion(String transactionId, ReportFEParam param);
	//////////////
	
	//call by landing page 3 graph
//	public GoogleChartResultList getPassengerRegister(String transactionId, ReportFEParam param);	
//	public GoogleChartResultList getPassengerIPC(String transactionId, ReportFEParam param);
}
