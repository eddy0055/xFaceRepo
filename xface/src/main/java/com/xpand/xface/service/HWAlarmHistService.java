package com.xpand.xface.service;

import java.util.Date;
import java.util.List;

import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.PersonInfo;

public interface HWAlarmHistService {
	public ResultStatus updateAlarm(HWAlarmHist hwAlarmHist);
	public List<LastAlarmPersonDateTime> findMaxAlarmGroupByPerson(Date startDate);
	public ResultStatus removeRelationshipPersonInfo(PersonInfo personInfo);
}
