package com.xpand.xface.bean.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.EquipmentDirection;

public class StatisticsResultList {
	private long noPassengerInOut;
	private long noPassengerMatchUnMatch;
	private String startDate;
	private String endDate;
	private ResultStatus result;
	private List<StatisticsResult> statisticsResultList;
	private HashMap<String, Integer> indexResultList;
	public long getNoPassengerInOut() {
		return noPassengerInOut;
	}
	public void setNoPassengerInOut(long noPassengerInOut) {
		this.noPassengerInOut = noPassengerInOut;
	}
	public void increaseNoPassengerInOut(long noPassengerInOut) {
		this.noPassengerInOut = this.noPassengerInOut+noPassengerInOut;
	}
	public long getNoPassengerMatchUnMatch() {
		return noPassengerMatchUnMatch;
	}
	public void setNoPassengerMatchUnMatch(long noPassengerMatchUnMatch) {
		this.noPassengerMatchUnMatch = noPassengerMatchUnMatch;
	}
	public void increaseNoPassengerMatchUnMatch(long noPassengerMatchUnMatch) {
		this.noPassengerMatchUnMatch = this.noPassengerMatchUnMatch+noPassengerMatchUnMatch;
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public List<StatisticsResult> getStatisticsResultList() {
		if (this.statisticsResultList==null) {
			this.statisticsResultList = new ArrayList<StatisticsResult>();			
		}
		return statisticsResultList;
	}
	public void setStatisticsResultList(List<StatisticsResult> statisticsResultList) {		
		this.statisticsResultList = statisticsResultList;
	}
	public void addStatisticsResultList(String transactionId, StatisticsResult statisticsResult) {
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "in addStatisticsResultList"));
		this.getStatisticsResultList();
		if (this.indexResultList==null) {
			this.indexResultList = new HashMap<>();
		}
		String keyIndex = statisticsResult.getGateDesc()+statisticsResult.getTimePortion();
		Integer existingIndex = this.indexResultList.get(keyIndex);
		StatisticsResult existingStat = null;
		if (existingIndex==null) {
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "keyIndex:"+keyIndex+" not found add new"));
			this.statisticsResultList.add(statisticsResult);			
			this.indexResultList.put(keyIndex, this.statisticsResultList.size()-1);			
		}else { 
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "found keyIndex:"+keyIndex+" on id:"+existingIndex));
			existingStat = this.statisticsResultList.get(existingIndex);
			if ((statisticsResult.getDirectionInId() != null) && (EquipmentDirection.DIRECTION_IN == statisticsResult.getDirectionInId())){
				//in
				existingStat.setDirectionInId(statisticsResult.getDirectionInId());
				existingStat.setDirectionInDesc(statisticsResult.getDirectionInDesc());
				existingStat.setNoPassengerIn(statisticsResult.getNoPassengerIn());
				existingStat.setNoPassengerInOut(existingStat.getNoPassengerInOut()+existingStat.getNoPassengerIn());
			}else {
				//out
				existingStat.setDirectionOutId(statisticsResult.getDirectionOutId());
				existingStat.setDirectionOutDesc(statisticsResult.getDirectionOutDesc());
				existingStat.setNoPassengerOut(statisticsResult.getNoPassengerOut());
				existingStat.setNoPassengerInOut(existingStat.getNoPassengerInOut()+existingStat.getNoPassengerOut());
			}			
			this.statisticsResultList.set(existingIndex, existingStat);
		}					
		this.increaseNoPassengerInOut(statisticsResult.getNoPassengerInOut());
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "out addStatisticsResultList"));
	}
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
}
