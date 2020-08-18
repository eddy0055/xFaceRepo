package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xpand.xface.bean.report.QueryAlarmPortionResultList;
import com.xpand.xface.bean.report.QueryAlarmResult;
import com.xpand.xface.bean.report.QueryAlarmResultList;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.GoogleChartResultList;
import com.xpand.xface.bean.report.google.GoogleMultiChartResult;
import com.xpand.xface.service.ReportService;

@RestController
@RequestMapping("/rest/rep")
public class RestReportController {
	
	public static String CLASS_NAME=RestReportController.class.getName();
	@Autowired
	ReportService reportService;	
	
	//call from AlarmMonitor.js, AlarmMonitorIDV.js -> reloadAlarmInfo ///
	@RequestMapping("/getAlarmByCode")
	@PostMapping
	public QueryAlarmResult getAlarmByCode(HttpServletRequest request, @RequestBody ReportFEParam param) {		
		return this.reportService.getAlarmByCode(request.getSession().getId(), param);
	}	
	
	/////////////////////////////////////
	
	//call from AlarmHistory.js ///
	// AlarmHistory.js -> reloadPageData
	@RequestMapping("/getAlarmPortionList")
	@PostMapping
	public QueryAlarmPortionResultList getAlarmPortionList(HttpServletRequest request, @RequestBody ReportFEParam param) {		
		return this.reportService.getAlarmListPortionList(request.getSession().getId(), param);
	}
	// AlarmHistory.js -> reloadPartData
	@RequestMapping("/getAlarmPortionPartList")
	@PostMapping
	public QueryAlarmResultList getAlarmPortionPartList(HttpServletRequest request, @RequestBody ReportFEParam param) {		
		return this.reportService.getAlarmListPortionPartList(request.getSession().getId(), param);
	}
	// AlarmHistory.js -> reloadAlarmInfo
	@RequestMapping("/getAlarmById")
	@PostMapping
	public QueryAlarmResult getAlarmById(HttpServletRequest request, @RequestBody ReportFEParam param) {		
		return this.reportService.getAlarmById(request.getSession().getId(), param);
	}
	/////////////////////////////////////
	
	//call from DailyStatistics.js //////////
	//call from DailyStatistics.js -> reloadPassengerChart
	@RequestMapping("/daily/getDailyPassenger")
	@PostMapping
	public GoogleChartResultList getDailyPassenger(HttpServletRequest request, @RequestBody ReportFEParam param) {
		return this.reportService.getDailyPassenger(request.getSession().getId(), param);		
	}
	//call from DailyStatistics.js -> reloadPassengerByGateChart
	@RequestMapping("/daily/getDailyPassengerByGate")
	@PostMapping
	public GoogleChartResultList getDailyPassengerByGate(HttpServletRequest request, @RequestBody ReportFEParam param) {
		return this.reportService.getDailyPassengerByGate(request.getSession().getId(), param);		
	}
	//call from DailyStatistics.js -> reloadFaceChart
	@RequestMapping("/daily/getDailyFace")
	@PostMapping
	public GoogleChartResultList getDailyFace(HttpServletRequest request, @RequestBody ReportFEParam param) {
		return this.reportService.getDailyFace(request.getSession().getId(), param);		
	}
	//call from DailyStatistics.js -> reloadFaceByGateChart
	@RequestMapping("/daily/getDailyFaceByGate")
	@PostMapping
	public GoogleChartResultList getDailyFaceByGate(HttpServletRequest request, @RequestBody ReportFEParam param) {
		return this.reportService.getDailyFaceByGate(request.getSession().getId(), param);		
	}
	//call from DailyStatistics.js -> reloadPassengerByBoatChart
	@RequestMapping("/daily/getDailyPassengerByBoat")
	@PostMapping
	public GoogleChartResultList getDailyPassengerByBoat(HttpServletRequest request, @RequestBody ReportFEParam param) {
		return this.reportService.getDailyPassengerByBoat(request.getSession().getId(), param);		
	}
	////////////////////////
	//call from DailyStatisticsByTime.js ////////////
	@RequestMapping("/daily/getDailyByTime")
	@PostMapping
	public GoogleMultiChartResult getDailyByTime(HttpServletRequest request, @RequestBody ReportFEParam param) {
		return this.reportService.getDailyByTime(request.getSession().getId(), param);		
	}
	////////////////////////////////////////////////
	
//	//call from LandingPage3Chart.js
//	//LandingPage3Chart.js -> reloadRegisterChart
//	@RequestMapping("/getPassengerRegisterList")
//	@PostMapping
//	public GoogleChartResultList getPassengerRegisterList(HttpServletRequest request, @RequestBody ReportFEParam param) {		
//		return this.reportService.getPassengerRegister(request.getSession().getId(), param);		
//	}
//	//LandingPage3Chart.js -> reloadIPCChart
//	@RequestMapping("/getPassengerIPCList")
//	@PostMapping
//	public GoogleChartResultList getPassengerIPCList(HttpServletRequest request, @RequestBody ReportFEParam param) {
//		return this.reportService.getPassengerIPC(request.getSession().getId(), param);		
//	}
//	//LandingPage3Chart.js -> reloadGateInfoChart
//	@RequestMapping("/getPassengerGateInfoList")
//	@PostMapping
//	public GoogleChartResultList getPassengerGateInfoList(HttpServletRequest request, @RequestBody ReportFEParam param) {
//		return this.reportService.getDailyPassengerByGate(request.getSession().getId(), param);		
//	}
//	//////////////////////////////////

////	ARC = alarm recognition not on call
//	@RequestMapping("/getGateStatistics")
//	@PostMapping
//	public StatisticsResultList getGateStatistics(HttpServletRequest request, @RequestBody ReportFEParam param) {
//		return this.reportService.getGateStatistics(request.getSession().getId(), param);
//	}
//	@RequestMapping("/getGateStatisticsTimePortion")
//	@PostMapping
//	public StatisticsResultList getGateStatisticsTimePortion(HttpServletRequest request, @RequestBody ReportFEParam param) {
//		return this.reportService.getGateStatisticsTimePortion(request.getSession().getId(), param);
//	}
//	@RequestMapping("/getGateStatisticsDMY")
//	@PostMapping
//	public StatisticsResultList getGateStatisticsDMY(HttpServletRequest request, @RequestBody ReportFEParam param) {		
//		return this.reportService.getGateStatisticsDMY(request.getSession().getId(), param);
//	}
//	@RequestMapping("/getARCStatistics")
//	@PostMapping
//	public StatisticsResultList getARCStatistics(HttpServletRequest request, @RequestBody ReportFEParam param) {
//		return this.reportService.getARCStatistics(request.getSession().getId(), param);
//	}
//	@RequestMapping("/getARCStatisticsTimePortion")
//	@PostMapping
//	public StatisticsResultList getARCStatisticsTimePortion(HttpServletRequest request, @RequestBody ReportFEParam param) {
//		return this.reportService.getARCStatisticsTimePortion(request.getSession().getId(), param);
//	}
}
