package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.PersonCertificate;

public interface PersonCertificateService  extends CacheManageService {
	public List<PersonCertificate> findAll(String transactionId);
	public Page<PersonCertificate> getPersonCertificateList(String transactionId, Pageable pageable);
	public PersonCertificate findByCertificateCode(String transactionId, String certificateName);
	public PersonCertificate findById(String transactionId, Integer certificateId);
	
	public TablePage getPersonCertificateInfoList(String transactionId, PaginationCriteria pc);
	public ResultStatus delete(String transactionId, String logonUserName, String certificateCode);
	public ResultStatus update(String transactionId, String logonUserName, PersonCertificate personCertificate);

}
