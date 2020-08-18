package com.xpand.xface.bean.report;

public class ReportFEParam {
	public static final int TIME_DURATION_TYPE_NOW_MINUS_MINUTE=0;
	public static final int TIME_DURATION_TYPE_START_END=1;
	public static final int TIME_DURATION_TYPE_DAILY=2;
	public static final int TIME_DURATION_TYPE_MONTHLY=3;
	public static final int TIME_DURATION_TYPE_YEARLY=4;
	private String startDate;
	private String endDate;	
	private String startYear; // for time_portion_type_monthly
	private int timePortion; //15, 30, 60
	private int durationType; 
	private int timeMinusMinute;
	private int currentPage;
	private int alarmId;
	private String alarmCode;
	private String certificateNo;
	private String fullName;
	private String matchCondition;
	//comma sperate
	private String ipcCodeList;
	private String gateInfoCodeList;
	private String boatCodeList;
	private String direction;
	private String dashBoardType;
	
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public int getTimePortion() {
		return timePortion;
	}
	public void setTimePortion(int timePortion) {
		this.timePortion = timePortion;
	}
	public int getDurationType() {
		return durationType;
	}
	public void setDurationType(int durationType) {
		this.durationType = durationType;
	}
	public int getTimeMinusMinute() {
		return timeMinusMinute;
	}
	public void setTimeMinusMinute(int timeMinusMinute) {
		this.timeMinusMinute = timeMinusMinute;
	}
	public String getStartYear() {
		return startYear;
	}
	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getIpcCodeList() {
		return ipcCodeList;
	}
	public void setIpcCodeList(String ipcCodeList) {
		this.ipcCodeList = ipcCodeList;
	}
	public String getAlarmCode() {
		return alarmCode;
	}
	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}
	public String getGateInfoCodeList() {
		return gateInfoCodeList;
	}
	public void setGateInfoCodeList(String gateInfoCodeList) {
		this.gateInfoCodeList = gateInfoCodeList;
	}
	public String getMatchCondition() {
		return matchCondition;
	}
	public void setMatchCondition(String matchCondition) {
		this.matchCondition = matchCondition;
	}
	public String getBoatCodeList() {
		return boatCodeList;
	}
	public void setBoatCodeList(String boatCodeList) {
		this.boatCodeList = boatCodeList;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getDashBoardType() {
		return dashBoardType;
	}
	public void setDashBoardType(String dashBoardType) {
		this.dashBoardType = dashBoardType;
	}
}