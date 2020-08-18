package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.bean.PersonNotification;
import com.xpand.xface.service.PersonNotificationService;

@SessionScope
@Component
public class PersonNotificationServiceImpl implements PersonNotificationService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	public static String MAN_IMAGE = "";
//	public static String WOMAN_IMAGE = "";
	
	@Override
	public PersonNotification getPersonByAlarmId(Integer alarmId) {				
		PersonNotification person = new PersonNotification();
//		person.setAlarmId(alarmId);
//		person.setFullName("fullname"+alarmId.intValue());		
//		person.setPersonCode(alarmId.toString());		
		// TODO Auto-generated method stub
		return person;
	}

	@Override
	public ArrayList<PersonNotification> getListPersonByDate(String inquiryDate) {
		ArrayList<PersonNotification> personNotificationList = new ArrayList<>();
		PersonNotification person = new PersonNotification();
//		person.setAlarmId(1);
//		personNotificationList.add(person);
//		person = new PersonNotification();
//		person.setAlarmId(2);
		personNotificationList.add(person);
		return personNotificationList;
	}
}
