package com.xpand.xface.bean;

import java.util.Date;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.util.LogUtil;

public class LastAlarmPersonDateTime {
	private final Integer personId;
	private final long lastAlarmTime;
	//tmp
	private final long noOfRecord;
	///////
	public LastAlarmPersonDateTime(PersonInfo personInfo, Date lastAlarmTime, long noOfRecord) {
		//String transactionId = LogUtil.getWebSessionId();
		Logger.debug(this, LogUtil.getLogDebug("transactionId", "personId:"+personInfo+" lastAlarm:"+lastAlarmTime));
		this.personId = personInfo==null?null:personInfo.getPersonId();
		this.lastAlarmTime = lastAlarmTime==null?0:lastAlarmTime.getTime();
		this.noOfRecord = noOfRecord;
	}
	public Integer getPersonId() {
		return personId;
	}
	public long getLastAlarmTime() {
		return lastAlarmTime;
	}
	public long getNoOfRecord() {
		return noOfRecord;
	}
	
}
