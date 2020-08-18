package com.xpand.xface.service;

import java.util.Date;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.PersonInfo;

public interface PersonRegDateService {
	public PersonInfo resetPersonRegDateId(PersonInfo personInfo);
	public PersonInfo addPersonRegDate(PersonInfo personInfo, Date regDate, String agentName, String logonUserName);
	public ResultStatus deleteByPersonInfo(String transactionId,  PersonInfo personInfo);
}
