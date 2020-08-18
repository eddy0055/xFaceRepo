package com.xpand.xface.bean.landing;

import com.xpand.xface.bean.report.google.DailyFaceChartField;

public class LandingPageInfo {
	//show alarm button list
	DailyAlarmInfoList alarmInfoList;
	//show boat check in list
	DailyBoatInfoList boatInfoList;
	//show ipc match, unmatch
	DailyIPCInfoList ipcInfoList;
	//create IPCStatudInfoList 
	DailyIPCStatusInfoList ipcStatusInfoList;
	//show gate in, out
	DailyGateInfoList gateInfoList;
	private int noOfPassengerIN;
	private int noOfPassengerOUT;
	private int noOfFaceMatch;
	private int noOfFaceUnMatch;
	
	public LandingPageInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public DailyAlarmInfoList getAlarmInfoList() {
		return alarmInfoList;
	}
	public void setAlarmInfoList(DailyAlarmInfoList alarmInfoList) {
		this.alarmInfoList = alarmInfoList;
	}
	
	public DailyBoatInfoList getBoatInfoList() {
		return boatInfoList;
	}
	public void setBoatInfoList(DailyBoatInfoList boatInfoList) {
		this.boatInfoList = boatInfoList;
	}
	
	public DailyGateInfoList getGateInfoList() {
		return gateInfoList;
	}
	
	public void setGateInfoList(DailyGateInfoList gateInfoList) {
		this.gateInfoList = gateInfoList;
		//calculate no of passenger in, out
		this.noOfPassengerIN = 0;
		this.noOfPassengerOUT = 0;
		if (this.gateInfoList!=null) {
			for (DailyGateInfoField field: this.gateInfoList.getGateInfoList()) {
				this.noOfPassengerIN += field.getNoOfIN()==null ? 0 : field.getNoOfIN();
				this.noOfPassengerOUT += field.getNoOfOut()==null ? 0 : field.getNoOfOut();
			}
		}
	}
	public DailyIPCInfoList getIpcInfoList() {
		return ipcInfoList;
	}
	
	public void setIpcInfoList(DailyIPCInfoList ipcInfoList) {
		this.ipcInfoList = ipcInfoList;
		//calculate no of face match, unmatch
		this.noOfFaceMatch = 0;
		this.noOfFaceUnMatch = 0;
		if (this.ipcInfoList!=null) {
			for (DailyFaceChartField field: this.ipcInfoList.getIPCInfoList()) {
				this.noOfFaceMatch += field.getNoOfMatch()==null ? 0 : field.getNoOfMatch();
				this.noOfFaceMatch += field.getNoOfUnMatch()==null ? 0 : field.getNoOfUnMatch();
			}
		}
	}
	

	public DailyIPCStatusInfoList getIpcStatusInfoList() {
		return ipcStatusInfoList;
	}

	public void setIpcStatusInfoList(DailyIPCStatusInfoList ipcStatusInfoList) {
		this.ipcStatusInfoList = ipcStatusInfoList;
	}
	
	public int getNoOfPassengerIN() {
		return noOfPassengerIN;
	}
	public void setNoOfPassengerIN(int noOfPassengerIN) {
		this.noOfPassengerIN = noOfPassengerIN;
	}
	public int getNoOfPassengerOUT() {
		return noOfPassengerOUT;
	}
	public void setNoOfPassengerOUT(int noOfPassengerOUT) {
		this.noOfPassengerOUT = noOfPassengerOUT;
	}
	public int getNoOfFaceMatch() {
		return noOfFaceMatch;
	}
	public void setNoOfFaceMatch(int noOfFaceMatch) {
		this.noOfFaceMatch = noOfFaceMatch;
	}
	public int getNoOfFaceUnMatch() {
		return noOfFaceUnMatch;
	}
	public void setNoOfFaceUnMatch(int noOfFaceUnMatch) {
		this.noOfFaceUnMatch = noOfFaceUnMatch;
	}

}
