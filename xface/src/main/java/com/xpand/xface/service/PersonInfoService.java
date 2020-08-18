package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.entity.PersonInfo;

public interface PersonInfoService {
	public PersonInfo findByPersonId(Integer personId, String className);	
	public PersonInfo findByCertificationNo(String certificationNo, String className);		
	public PersonInfo findByHwPeopleId(String hwPeopleId, String className);
	public PersonInfo findOneByWebFEParam(WebFEParam webFEParam, String className);
	public List<PersonInfo> findAll(String className);
	public Page<PersonInfo> getPersonInfoList(Pageable pageable, String className);
	public List<PersonSummaryInfo> findPersonSummaryInfo();
	

	public ResultStatus update(String transactionId, String logonUserName, PersonInfo personInfo, MultipartFile image);
	public ResultStatus delete(String transactionId, String logonUserName, String personCertificationNo);
	
	public ResultStatus register(String transactionId, String logonUserName, CustomerRegister customerInfo);
}
