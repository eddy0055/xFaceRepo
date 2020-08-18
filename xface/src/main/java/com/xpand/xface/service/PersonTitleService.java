package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.PersonTitle;

public interface PersonTitleService {
	public List<PersonTitle> findAll();
	public Page<PersonTitle> getPersonTitleList(Pageable pageable, String className);
	public PersonTitle findByTitleName(String titleName, String className);
	public PersonTitle findById(Integer titleId, String className);

	public ResultStatus delete(String transactionId,String logonUserName,String titleName);
	public ResultStatus update(String transactionId, String logonUserName, PersonTitle personTitle);

}
