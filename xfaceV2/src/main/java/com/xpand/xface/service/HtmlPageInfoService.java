package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.HtmlPageInfo;

public interface HtmlPageInfoService extends CacheManageService {
	public List<HtmlPageInfo> findAll(String transactionId);
	public HtmlPageInfo findById(String transactionId, Integer pageId);
	public HtmlPageInfo findByCode(String transactionId, String pageCode);
	public HtmlPageInfo getOne(String transactionId);
	public TablePage getHtmlPageInfoList(String transactionId,PaginationCriteria pc);
	public List<HtmlPageInfo> removeSomeObject(List<HtmlPageInfo> htmlPageInfoList);
	public HtmlPageInfo findByPageCode(String id, String pageCode);
	public HtmlPageInfo findOneByPageCode(String transactionid, String pageCode);
	public ResultStatus delete(String transactionId, String logonUserName, String pageCode);
	public ResultStatus update(String transactionId, String logonUserName, HtmlPageInfo htmlPageInfo);
}
