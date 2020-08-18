package com.xpand.xface.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.bean.query.photo.ResultPersonRespList;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.PassengerRegisterField;
import com.xpand.xface.entity.PersonInfo;

public interface PersonInfoService  extends CacheManageService {
	public PersonInfo findByPersonId(String transactionId, Integer personId);	
	public PersonInfo findByCertificateNo(String transactionId, String certificateNo);		
	public PersonInfo findByHwPeopleId(String transactionId, String hwPeopleId);
	public PersonInfo findOneByWebFEParam(String transactionId, WebFEParam webFEParam);
	public List<PersonInfo> findAll(String transactionId);	
	
	public TablePage getPersonInfoList(String transactionId, PaginationCriteria pc);	
	
	
	public List<PersonSummaryInfo> findPersonSummaryInfo(String transactionId);	
	public ResultStatus update(String transactionId, String logonUserName, PersonInfo personInfo, MultipartFile image);
	public ResultStatus delete(String transactionId, String logonUserName, PersonInfo personInfo, String personCertificateNo);
	
	public ResultStatus register(String transactionId, String logonUserName, CustomerRegister customerInfo, PersonInfo existingPersonInfo);
	public ResultStatus registerV2(String transactionId, String logonUserName, CustomerRegister customerInfo, PersonInfo existingPersonInfo);
	public ResultPersonRespList personTrace(String transactionId, WebFEParam webFEParam, MultipartFile personPhoto);
	
	public List<PersonInfo> clearSomeObject(String transactionId, List<PersonInfo> personInfoList);
	public PersonInfo clearSomeObject(String transactionId, PersonInfo personInfo);
	public ArrayList<PassengerRegisterField> getPassengerRegistered(String transactionId, ReportFEParam param);
}
