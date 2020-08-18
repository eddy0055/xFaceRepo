package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.PersonNationality;

public interface PersonNationalityService  extends CacheManageService {
	public List<PersonNationality> findAll(String transactionId);
	public Page<PersonNationality> getPersonNationalityList(String transactionId, Pageable pageable);
	public PersonNationality findByNationalityCode(String transactionId, String nationalityCode);
	public PersonNationality findByNationalityName(String transactionId, String nationalityName);
	public PersonNationality findById(String transactionId, Integer nationalityId);
	public List<PersonNationality> removeSomeObject(List<PersonNationality> personNationality);

	public TablePage getPersonNationalityInfoList(String transactionId, PaginationCriteria pc);
	
	public ResultStatus delete(String transactionId, String logonUserName, String nationalityCode);
	public ResultStatus update(String transactionId, String logonUserName, PersonNationality personNationality);

}
