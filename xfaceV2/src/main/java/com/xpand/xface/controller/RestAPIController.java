package com.xpand.xface.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xpand.xface.bean.PersonNotification;
import com.xpand.xface.bean.SearchPersonCondition;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.PersonNotificationService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.util.StringUtil;

@RestController
//@RequestMapping(com.xpand.xface.util.ConstUtil.WEB_CONTEXT_PATH+"/rest")
@RequestMapping("/rest")
public class RestAPIController {
	
	public static String CLASS_NAME=RestAPIController.class.getName();
	@Autowired
	PersonNotificationService personNotificationService;
	@Autowired
	PersonInfoService personInfoService;
	@Autowired
	SystemAuditService sysAuditService;
	@Autowired
	XFaceServerService xfaceServerService;
	
	@RequestMapping("/getPersonNotificationByAlarm/{alarmId}")
	@ResponseBody
	public PersonNotification getPersonNotificationByAlarm(@PathVariable("alarmId") String alarmId) {
		return this.personNotificationService.getPersonByAlarmId(StringUtil.stringToInteger(alarmId,0));
	}
	@RequestMapping("/getListPersonNotificationByDate/{inquiryDate}")
	@ResponseBody
	public ArrayList<PersonNotification> getListPersonNotificationByDate(@PathVariable("inquiryDate") String inquiryDate) {
		return this.personNotificationService.getListPersonByDate(inquiryDate);
	}
	@RequestMapping("/getListPersonNotificationByDate")
	@ResponseBody
	public ArrayList<PersonNotification> getListPersonNotification() {
		return this.personNotificationService.getListPersonByDate(null);
	}
	@RequestMapping("/getPersonNotificationByPersonId/{personOrPassport}")
	@ResponseBody
	public PersonNotification getPersonNotificationByPersonPassport(@PathVariable("personOrPassport") String personOrPassport) {
		return this.personNotificationService.getPersonByAlarmId(StringUtil.stringToInteger(personOrPassport,0));
	}
				
	@RequestMapping("/getTest")	
	@PostMapping
	public WebFEParam getTest() {
		//return this.personInfoService.findByCertificateNo(webFEParam.getSearchPersonConsitionList().get(0).getSearchValue(), RestAPIController.CLASS_NAME);				
//		return this.personInfoService.findOneByWebFEParam(webFEParam, RestAPIController.CLASS_NAME);
		List<SearchPersonCondition> x = new ArrayList<>();
		SearchPersonCondition y = new SearchPersonCondition();
		y.setSearchField("xxxx");
		y.setSearchValue("xxxxxx");
		y.setSearchOperation(SearchPersonCondition.OPERATION_EQUAL);
		x.add(y);
		y = new SearchPersonCondition();
		y.setSearchField("yyyyy");
		y.setSearchValue("yyyyy");
		y.setSearchOperation(SearchPersonCondition.OPERATION_EQUAL);
		x.add(y);
		WebFEParam result = new WebFEParam();
		result.setSearchPersonConditionList(x);
		return result;
	}								
}
