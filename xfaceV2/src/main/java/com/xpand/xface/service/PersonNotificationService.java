package com.xpand.xface.service;

import java.util.ArrayList;

import com.xpand.xface.bean.PersonNotification;

public interface PersonNotificationService {
	public PersonNotification getPersonByAlarmId(Integer alarmId);
	public ArrayList<PersonNotification> getListPersonByDate(String inqueryDate);
}
