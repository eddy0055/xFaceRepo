package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.PersonCertification;

public interface PersonCertificationService {
	public List<PersonCertification> findAll();
	public Page<PersonCertification> getPersonCertificationList(Pageable pageable, String className);
	public PersonCertification findByCertificationName(String certificationName, String className);
	public PersonCertification findById(Integer certificationId, String className);
	
	public ResultStatus delete(String transactionId,String logonUserName,String certificationName);
	public ResultStatus update(String transactionId, String logonUserName, PersonCertification personCertification);

}
