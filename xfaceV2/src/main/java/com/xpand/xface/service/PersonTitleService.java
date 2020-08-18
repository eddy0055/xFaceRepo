package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.PersonTitle;

public interface PersonTitleService  extends CacheManageService {
	public List<PersonTitle> findAll(String transactionId);
	public Page<PersonTitle> getPersonTitleList(String transactionId, Pageable pageable);
	public PersonTitle findByTitleCode(String transactionId, String titleCode);
	public PersonTitle findById(String transactionId, Integer titleId);
	//TablePage personTitle
	public TablePage getPersonTitleInfoList(String transactionId, PaginationCriteria trep);
	public ResultStatus delete(String transactionId, String logonUserName, String titleCode);
	public ResultStatus update(String transactionId, String logonUserName, PersonTitle personTitle);

}
