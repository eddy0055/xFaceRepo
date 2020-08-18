package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.dao.HtmlPageInfoDAO;
import com.xpand.xface.entity.HtmlPageInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.HtmlPageInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;


@SessionScope
@Component
public class HtmlPageInfoServiceImpl implements HtmlPageInfoService , Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	HtmlPageInfoDAO htmlPageInfoDAO; 
	@Autowired
	RoleInfoService roleInfoService;
	
	@Autowired
	HtmlPageInfoService htmlPageInfoService;
	
	@Autowired
	SystemAuditService systemAuditService;

	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'page_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'page_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}

	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'page_all'")
	public List<HtmlPageInfo> findAll(String transactionId) {
		return this.htmlPageInfoDAO.findAll();		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'page_'+#pageId")
	public HtmlPageInfo findById(String transactionId, Integer pageId) {
		return this.htmlPageInfoDAO.findByPageId(pageId);		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'page_one'")
	public HtmlPageInfo getOne(String transactionId) {
		List<HtmlPageInfo> pageInfoList = this.htmlPageInfoDAO.findAll();
		if (pageInfoList==null || pageInfoList.size()==0) {
			return null;
		}else {
			return pageInfoList.get(0);
		}
		
	}
	@Override
	public List<HtmlPageInfo> removeSomeObject(List<HtmlPageInfo> htmlPageInfoList) {
		for (HtmlPageInfo page: htmlPageInfoList) {
			page.setRoleInfoList(null);
		}
		return htmlPageInfoList;
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'page_'+#Code")
	public HtmlPageInfo findByCode(String transactionId, String pageCode) {
		return this.htmlPageInfoDAO.findByPageCode(pageCode);		
	}
	//set for TablePage
	@Override	
	public TablePage getHtmlPageInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getHtmlPage InfoList"));		
		TablePage page = new TablePage(transactionId, this.htmlPageInfoDAO, pc);
	    Logger.info(this, LogUtil.getLogInfo(transactionId, "out getHtmlPage InfoList"));
		return page;
	}
	
	@Cacheable(value=CacheName.CACHE_OTHER, key="'page_'+#pageCode")
	@Override
	public HtmlPageInfo findByPageCode(String transactionid, String pageCode) {
		return this.htmlPageInfoDAO.findByPageCode(pageCode);		
	}	
	
	@Cacheable(value=CacheName.CACHE_OTHER,key="'page2' + #pageCode")
	@Override
	public HtmlPageInfo findOneByPageCode(String transactionid,String pageCode) {
		return this.htmlPageInfoDAO.findOneByPageCode(transactionid, pageCode);
	}
		
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'key_'+#htmlPageInfo.pageCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'key_all'")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, HtmlPageInfo htmlPageInfo) {
		ResultStatus result = new ResultStatus();	
		Logger.info(this, LogUtil.getLogInfo(transactionId, "actionCommand ::" + htmlPageInfo.getActionCommand()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "pageCode ::" + htmlPageInfo.getPageCode()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "pageDesc ::" + htmlPageInfo.getPageDesc()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "pageURL ::" + htmlPageInfo.getPageURL()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "pageId ::" + htmlPageInfo.getPageId()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "roleInfoList ::" + htmlPageInfo.getRoleInfoList()));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update HtmlPageInfo start"));
		
		if (StringUtil.checkNull(htmlPageInfo.getPageCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "PageCode");
		}else if(StringUtil.checkNull(htmlPageInfo.getPageDesc())){
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "PageDesc");			
		}else if (StringUtil.checkNull(htmlPageInfo.getPageURL())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "PageURL");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update HtmlPageInfo fail with result "+result.toString()));
			return result;
		}		
		
		String oldValue = null;						
		HtmlPageInfo existingHtmlPageInfo = this.htmlPageInfoDAO.findByPageCode(htmlPageInfo.getPageCode());
		if (htmlPageInfo.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingHtmlPageInfo!=null) {
				//error pageCode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "pageCode="+htmlPageInfo.getPageCode());
			}
		}else if (existingHtmlPageInfo==null) { 
			//error cannot find pageCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "pageCode="+htmlPageInfo.getPageCode());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update htmlPageInfo fail with result "+result.toString()));
			return result;
		}
		if (existingHtmlPageInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "pageCode "+htmlPageInfo.getPageCode()+" not found then create"));
			htmlPageInfo = new HtmlPageInfo(htmlPageInfo);
			htmlPageInfo.setPageId(null);
			htmlPageInfo.setPageCode(htmlPageInfo.getPageCode());
			htmlPageInfo.setPageDesc(htmlPageInfo.getPageDesc());
			//set for pageType fix to 0
			htmlPageInfo.setPageType(0);
			htmlPageInfo.setPageURL(htmlPageInfo.getPageURL());
			Logger.info(this, LogUtil.getLogInfo(transactionId, "insert actionCommand ::" + htmlPageInfo.getActionCommand()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "insert pageCode ::" + htmlPageInfo.getPageCode()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "insert pageDesc ::" + htmlPageInfo.getPageDesc()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "insert pageType ::" + htmlPageInfo.getPageType()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "insert pageURL ::" + htmlPageInfo.getPageURL()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "insert pageId ::" + htmlPageInfo.getPageId()));
			
			this.createToDB(transactionId, htmlPageInfo);			
			oldValue = htmlPageInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create htmlPageInfo:"+oldValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create htmlPage "+htmlPageInfo.getPageCode()+"["+htmlPageInfo.getPageId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PAGE_HTML, SystemAudit.MOD_SUB_ALL
					, "create roleInfo:"+oldValue
					, SystemAudit.RES_SUCCESS, logonUserName);			
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found pageCode "+htmlPageInfo.getPageCode()+" then update"));
			htmlPageInfo.setPageId(existingHtmlPageInfo.getPageId());
			htmlPageInfo.setPageCode(htmlPageInfo.getPageCode());
			htmlPageInfo.setPageDesc(htmlPageInfo.getPageDesc());
			htmlPageInfo.setPageURL(htmlPageInfo.getPageURL());
			htmlPageInfo.setPageType(existingHtmlPageInfo.getPageType());
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update actionCommand ::" + htmlPageInfo.getActionCommand()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update pageCode ::" + htmlPageInfo.getPageCode()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update pageDesc ::" + htmlPageInfo.getPageDesc()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update pageType ::" + htmlPageInfo.getPageType()));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update pageId ::" + htmlPageInfo.getPageId()));
			
			this.updateToDB(transactionId, htmlPageInfo);
			oldValue = existingHtmlPageInfo.toString();
			String newValue = htmlPageInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update htmlPageInfo oldValue:"+oldValue+", newValue:"+newValue));			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update pageCode "+htmlPageInfo.getPageCode()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PAGE_HTML, SystemAudit.MOD_SUB_ALL
					, "update roleInfo oldValue:"+oldValue+", newValue:"+newValue
					, SystemAudit.RES_SUCCESS, logonUserName);
		}	
		if (ResultStatus.SUCCESS_CODE.equals(result.getStatusCode())) {
			//clear cache of pageCode to make sure that next time once request page Code
			//of pageCode will get updated data
			this.htmlPageInfoService.removeCacheByKey(htmlPageInfo.getPageCode());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update htmlPageInfo done with result "+result.toString()));
		return result;
	}
		
	public HtmlPageInfo createToDB(String transactionId,HtmlPageInfo htmlPageInfo) {
		//this.roleDetailInfoService.resetRoleDetailId(transactionId, roleInfo);
		//this.roleDetailInfoService.resetRoleDetailId(transactionId, htmlPageInfo);
		this.htmlPageInfoDAO.save(htmlPageInfo);
		return htmlPageInfo;
	}
	public HtmlPageInfo updateToDB(String transactionId,HtmlPageInfo htmlPageInfo) {	
		this.htmlPageInfoDAO.save(htmlPageInfo);
		return htmlPageInfo;
	}
		
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus delete(String transactionId, String logonUserName, String pageCode) {
		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete htmlPageTitle start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete find htmlPageInfo pageCode:: " + pageCode));
		ResultStatus result = new ResultStatus();
		HtmlPageInfo htmlPageInfo = this.htmlPageInfoDAO.findByPageCode(pageCode);
		Logger.info(this, LogUtil.getLogInfo(transactionId, " htmlPageInfo find  pageCode:: " + pageCode));

		if (htmlPageInfo != null ) { 			
		    Logger.info(this, LogUtil.getLogInfo(transactionId, "found htmlPageInfo "+htmlPageInfo.getPageCode()+"["+ htmlPageInfo.getPageId()+"] then delete"));
		    this.htmlPageInfoDAO.deleteByHtmlPageInfo(htmlPageInfo.getPageId());
		    Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete htmlPageInfo :"+ htmlPageInfo.getPageId()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete htmlPageInfo "+htmlPageInfo.getPageCode()+"["+  htmlPageInfo.getPageId() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, "delete htmlPageInfo :"+htmlPageInfo.getPageCode()+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else{
			Logger.info(this, LogUtil.getLogInfo(transactionId, "page code :"+pageCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "page code :"+pageCode);
		}			 
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete htmlPageInfo done with result "+result.toString()));
		return result;	
	}
	
	
	
	
	
	
	
}
