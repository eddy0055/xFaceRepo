package com.xpand.xface.bean.report;

import com.xpand.xface.entity.EquipmentDirection;
import com.xpand.xface.util.StringUtil;

public class StatisticsResult {
	private String gateDesc;
	private String timePortion;
	private long noPassengerIn;
	private long noPassengerOut;
	private long noPassengerMatch;
	private long noPassengerUnMatch;
	private long noPassengerInOut;
	private long noPassengerMatchUnMatch;
	private Integer directionInId;
	private String directionInDesc;
	private Integer directionOutId;
	private String directionOutDesc;
	public StatisticsResult() {				
	}		
	public void createARCStatistic(Object[] columns) {
		//SELECT gate_name,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match
		this.gateDesc = ""+columns[0];
		this.noPassengerIn = 0;
		this.noPassengerOut = 0;
		this.noPassengerMatch = StringUtil.stringToLong(columns[1], 0);
		this.noPassengerUnMatch = StringUtil.stringToLong(columns[2], 0);
		this.noPassengerMatchUnMatch = this.noPassengerMatch+this.noPassengerUnMatch;
		this.noPassengerInOut = 0;
		this.timePortion = "";
	}
	public void createARCStatisticTimePortion(Object[] columns) {
		//SELECT gate_name,alarm_time,SUM(rec_match) AS rec_match, SUM(rec_un_match) AS rec_un_match
		this.gateDesc = ""+columns[0];
		this.timePortion = ""+columns[1];
		this.noPassengerIn = 0;
		this.noPassengerOut = 0;
		this.noPassengerMatch = StringUtil.stringToLong(columns[2], 0);
		this.noPassengerUnMatch = StringUtil.stringToLong(columns[3], 0);
		this.noPassengerMatchUnMatch = this.noPassengerMatch+this.noPassengerUnMatch;
		this.noPassengerInOut = 0;		
	}
	public void createGateStatistic(Object[] columns) {
		//0 gateDesc,1 = direction code, 2 = direction desc, 3 = direction cnt
		this.gateDesc = ""+columns[0];
		int direction_code = StringUtil.stringToInteger(""+columns[1], EquipmentDirection.DIRECTION_IN);
		if (EquipmentDirection.DIRECTION_IN == direction_code) {
			this.noPassengerIn = StringUtil.stringToLong(columns[3], 0);
			this.noPassengerOut = 0;
			this.directionInId = direction_code;
			this.directionInDesc = ""+columns[2];
			this.noPassengerInOut = this.noPassengerIn; 
		}else {
			this.noPassengerIn = 0;
			this.noPassengerOut = StringUtil.stringToLong(columns[3], 0);
			this.directionOutId = direction_code;
			this.directionOutDesc = ""+columns[2];
			this.noPassengerInOut = this.noPassengerOut;
		}				
		this.noPassengerMatch = 0;
		this.noPassengerUnMatch = 0;
		this.noPassengerMatchUnMatch = 0;		
		this.timePortion = "";
	}
	public void createGateStatisticTimePortion(Object[] columns) {
//		SELECT gate.gate_name,record_time,direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran 
		this.gateDesc = ""+columns[0];
		int direction_code = StringUtil.stringToInteger(""+columns[2], EquipmentDirection.DIRECTION_IN);
		if (EquipmentDirection.DIRECTION_IN == direction_code) {
			this.noPassengerIn = StringUtil.stringToLong(columns[4], 0);
			this.noPassengerOut = 0;
			this.directionInId = direction_code;
			this.directionInDesc = ""+columns[3];
			this.noPassengerInOut = this.noPassengerIn; 
		}else {
			this.noPassengerIn = 0;
			this.noPassengerOut = StringUtil.stringToLong(columns[4], 0);
			this.directionOutId = direction_code;
			this.directionOutDesc = ""+columns[3];
			this.noPassengerInOut = this.noPassengerOut;
		}				
		this.noPassengerMatch = 0;
		this.noPassengerUnMatch = 0;
		this.noPassengerMatchUnMatch = 0;		
		this.timePortion = ""+columns[1];
	}
	public void getGateStatisticsDMY(Object[] columns) {
		//"SELECT record_time,direct.direction_code,direct.direction_desc,COUNT(record_id) cnt_tran "
		this.gateDesc = "";
		this.timePortion = ""+columns[0];
		int direction_code = StringUtil.stringToInteger(""+columns[1], EquipmentDirection.DIRECTION_IN);
		if (EquipmentDirection.DIRECTION_IN == direction_code) {
			this.noPassengerIn = StringUtil.stringToLong(columns[3], 0);
			this.noPassengerOut = 0;
			this.directionInId = direction_code;
			this.directionInDesc = ""+columns[2];
			this.noPassengerInOut = this.noPassengerIn; 
		}else {
			this.noPassengerIn = 0;
			this.noPassengerOut = StringUtil.stringToLong(columns[3], 0);
			this.directionOutId = direction_code;
			this.directionOutDesc = ""+columns[2];
			this.noPassengerInOut = this.noPassengerOut;
		}				
		this.noPassengerMatch = 0;
		this.noPassengerUnMatch = 0;
		this.noPassengerMatchUnMatch = 0;		
		
	}
	public String getGateDesc() {
		return gateDesc;
	}
	public void setGateDesc(String gateDesc) {
		this.gateDesc = gateDesc;
	}
	public long getNoPassengerIn() {
		return noPassengerIn;
	}
	public void setNoPassengerIn(long noPassengerIn) {
		this.noPassengerIn = noPassengerIn;
	}
	public long getNoPassengerOut() {
		return noPassengerOut;
	}
	public void setNoPassengerOut(long noPassengerOut) {
		this.noPassengerOut = noPassengerOut;
	}
	public long getNoPassengerMatch() {
		return noPassengerMatch;
	}
	public void setNoPassengerMatch(long noPassengerMatch) {
		this.noPassengerMatch = noPassengerMatch;
	}
	public long getNoPassengerUnMatch() {
		return noPassengerUnMatch;
	}
	public void setNoPassengerUnMatch(long noPassengerUnMatch) {
		this.noPassengerUnMatch = noPassengerUnMatch;
	}
	public long getNoPassengerInOut() {
		return noPassengerInOut;
	}
	public void setNoPassengerInOut(long noPassengerInOut) {
		this.noPassengerInOut = noPassengerInOut;
	}
	public long getNoPassengerMatchUnMatch() {
		return noPassengerMatchUnMatch;
	}
	public void setNoPassengerMatchUnMatch(long noPassengerMatchUnMatch) {
		this.noPassengerMatchUnMatch = noPassengerMatchUnMatch;
	}
	public String getTimePortion() {
		return timePortion;
	}
	public void setTimePortion(String timePortion) {
		this.timePortion = timePortion;
	}
	public Integer getDirectionInId() {
		return directionInId;
	}
	public void setDirectionInId(Integer directionInId) {
		this.directionInId = directionInId;
	}
	public String getDirectionInDesc() {
		return directionInDesc;
	}
	public void setDirectionInDesc(String directionInDesc) {
		this.directionInDesc = directionInDesc;
	}
	public Integer getDirectionOutId() {
		return directionOutId;
	}
	public void setDirectionOutId(Integer directionOutId) {
		this.directionOutId = directionOutId;
	}
	public String getDirectionOutDesc() {
		return directionOutDesc;
	}
	public void setDirectionOutDesc(String directionOutDesc) {
		this.directionOutDesc = directionOutDesc;
	}
}
