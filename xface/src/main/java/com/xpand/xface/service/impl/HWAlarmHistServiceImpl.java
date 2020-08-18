package com.xpand.xface.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.HWAlarmHistDAO;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.service.HWAlarmHistService;

@Component
public class HWAlarmHistServiceImpl implements HWAlarmHistService{

	@Autowired
	HWAlarmHistDAO hwAlarmHistDAO;
	@Override
	public ResultStatus updateAlarm(HWAlarmHist hwAlarmHist) {
		this.hwAlarmHistDAO.save(hwAlarmHist);		
		return new ResultStatus();
	}
	@Override
	public List<LastAlarmPersonDateTime> findMaxAlarmGroupByPerson(Date startDate) {
		return this.hwAlarmHistDAO.findMaxDateCreatedGroupByPerson(startDate);
	}
	@Override
	public ResultStatus removeRelationshipPersonInfo(PersonInfo personInfo) {
		this.hwAlarmHistDAO.removeRelationshipPersonInfo(personInfo);
		return new ResultStatus();
	}

}
