package com.xpand.xface.service;

import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;
import com.xpand.xface.entity.HWGateAccessInfo;

public interface HWGateAccessInfoService {
	public HWGateAccessInfo findById(String transactionId, Long recordId);
	
	//call from dailystatistic
	//daily passenger in-out
	public GoogleChartResultList getDailyPassengerChart(String transactionId, ReportFEParam param);
	//daily passenger in-out by gate
	public GoogleChartResultList getDailyPassengerByGateChart(String transactionId, ReportFEParam param);
	//daily passenger in-out by boat
	public GoogleChartResultList getDailyPassengerByBoatChart(String transactionId, ReportFEParam param);
	/////////////////////////
	
	//call from dailystatistic time
	//daily passenger in-out by time
	public GoogleMultiChartResult getDailyPassengerTimeChart(String transactionId, ReportFEParam param);	
	//daily passenger by gate in-out by time
	public GoogleMultiChartResult getDailyPassengerByGateTimeChart(String transactionId, ReportFEParam param);
	//daily passenger by boat in-out by time
	public GoogleMultiChartResult getDailyPassengerByBoatTimeChart(String transactionId, ReportFEParam param);
	///////////////////
	
	//no one call
//	public StatisticsResultList getGateStatistics(String transactionId, ReportFEParam param);	
//	public StatisticsResultList getGateStatisticsTimePortion(String transactionId, ReportFEParam param);
//	public StatisticsResultList getGateStatisticsDMY(String transactionId, ReportFEParam param);
	
}
