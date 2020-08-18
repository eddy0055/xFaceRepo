package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.PersonCategory;

public interface PersonCategoryService {
	public List<PersonCategory> findAll(String className);
	public Page<PersonCategory> getPersonCategoryList(Pageable pageable, String className);
	public PersonCategory findByCategoryName(String categoryName, String className);
	public PersonCategory findById(Integer categoryId, String className);

	public ResultStatus delete(String transactionId,String logonUserName,String categoryName);
	public ResultStatus update(String transactionId, String logonUserName, PersonCategory personCategory);
}
