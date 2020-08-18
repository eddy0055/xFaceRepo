
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
import com.xpand.xface.dao.RoleInfoDAO;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.HtmlPageInfoService;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class RoleInfoServiceImpl implements RoleInfoService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	RoleInfoDAO roleInfoDAO;
	@Autowired
	RoleDetailInfoService roleDetailInfoService;
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	HtmlPageInfoService htmlPageInfoService;
	@Override
	public List<RoleInfo> findAll() {
		return this.roleInfoDAO.findAll();
	}
		
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'role_'+#roleId")
	public RoleInfo findByRoleId(String transactionId, Integer roleId) {
		return this.roleInfoDAO.findOne(roleId);
	}	
	
	@Override	
	public TablePage getRoleInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getRoleInfoList"));		
		TablePage page = new TablePage(transactionId, this.roleInfoDAO, pc);
        Logger.info(this, LogUtil.getLogInfo(transactionId, "out getRoleInfoList"));
		return page;
	}		
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'role_'+#roleInfo.roleId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'role_'+#roleCode")
			}
		)
	public ResultStatus delete(String transactionId, String logonUserName, RoleInfo roleInfo, String roleCode) {		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "deletev2 roleInfo start"));
		ResultStatus result = new ResultStatus();		
		if (roleInfo.getRoleId()==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "role code "+roleCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "roleCode:"+roleCode);
		}else {					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found roleCode "+roleInfo.getRoleCode()+"["+roleInfo.getRoleId()+"] then delete"));
			String oldValue = roleInfo.toString();
			this.roleDetailInfoService.deleteByRoleInfo(transactionId, roleInfo);			
			this.roleInfoDAO.delete(roleInfo.getRoleId());										
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete roleInfo oldValue:"+oldValue+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete role "+roleInfo.getRoleName()+"["+roleInfo.getRoleId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
					, "delete roleInfo oldValue:"+oldValue+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);					
		}			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete roleInfo done with result "+result.toString()));
		return result;
	}
			
	public RoleInfo createToDB(String transactionId, RoleInfo roleInfo) {
		this.roleDetailInfoService.resetRoleDetailId(transactionId, roleInfo);
		this.roleInfoDAO.save(roleInfo);		
		return roleInfo;
	}
	public RoleInfo updateToDB(String transactionId, RoleInfo roleInfo) {
		//delete insert					
		this.roleDetailInfoService.deleteByRoleInfo(transactionId, roleInfo);
		this.roleDetailInfoService.resetRoleDetailId(transactionId,roleInfo);
		this.roleInfoDAO.save(roleInfo);
		return roleInfo;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_OTHER, key="'role_'+#roleInfo.roleId"),
			@CacheEvict(value=CacheName.CACHE_OTHER, key="'role_'+#roleInfo.roleCode")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, RoleInfo roleInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo start"));
		if (StringUtil.checkNull(roleInfo.getRoleName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleName");
		}else if (roleInfo.getHtmlPageInfo()==null || StringUtil.checkNull(roleInfo.getHtmlPageInfo().getPageCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "defaultPage");			
		}else if (StringUtil.checkNull(roleInfo.getRoleCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleCode");
		}else if (roleInfo.getRoleDetailInfoList()==null ||roleInfo.getRoleDetailInfoList().size()==0) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleDetailInfo");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo fail with result "+result.toString()));
			return result;
		}		
		String oldValue = null;						
		RoleInfo existingRoleInfo = this.roleInfoDAO.findOneByRoleCode(roleInfo.getRoleCode());
		if (roleInfo.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingRoleInfo!=null) {
				//error rolecode already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "roleCode="+roleInfo.getRoleCode());
			}
		}else if (existingRoleInfo==null) { 
			//error cannot find rolecode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "roleCode="+roleInfo.getRoleCode());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo fail with result "+result.toString()));
			return result;
		}
		if (existingRoleInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "roleCode "+roleInfo.getRoleCode()+" not found then create"));
			roleInfo = new RoleInfo(roleInfo);
			roleInfo.setRoleId(null);
			roleInfo.setUserCreated(logonUserName);
			roleInfo.setHtmlPageInfo(this.htmlPageInfoService.findByCode(transactionId, roleInfo.getHtmlPageInfo().getPageCode()));
			this.roleDetailInfoService.setPermissionObject(transactionId, roleInfo);					
			this.createToDB(transactionId, roleInfo);			
			oldValue = roleInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create roleInfo:"+oldValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create roleCode "+roleInfo.getRoleCode()+"["+roleInfo.getRoleId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
					, "create roleInfo:"+oldValue
					, SystemAudit.RES_SUCCESS, logonUserName);			
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found roleCode "+roleInfo.getRoleCode()+" then update"));
			roleInfo.setUserUpdated(logonUserName);
			this.roleDetailInfoService.setPermissionObject(transactionId, roleInfo);
			roleInfo.setRoleId(existingRoleInfo.getRoleId());
			roleInfo.setHtmlPageInfo(this.htmlPageInfoService.findByCode(transactionId, roleInfo.getHtmlPageInfo().getPageCode()));
			this.updateToDB(transactionId, roleInfo);
			oldValue = existingRoleInfo.toString();
			String newValue = roleInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update roleInfo oldValue:"+oldValue+", newValue:"+newValue));			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleCode "+roleInfo.getRoleCode()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
					, "update roleInfo oldValue:"+oldValue+", newValue:"+newValue
					, SystemAudit.RES_SUCCESS, logonUserName);
		}	
		if (ResultStatus.SUCCESS_CODE.equals(result.getStatusCode())) {
			//clear cache of roledetail to make sure that next time once request role detail
			//of roleCode will get updated data
			this.roleDetailInfoService.removeCacheByKey(roleInfo.getRoleCode());
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo done with result "+result.toString()));
		return result;
	}

	@Override	
	@Cacheable(value=CacheName.CACHE_OTHER, key="'role_'+#roleCode")	
	public RoleInfo findByRoleCode(String transactionId, String roleCode) {
		return this.roleInfoDAO.findOneByRoleCode(roleCode);
	}

	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'role_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'role_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}

		
//	@Override
//	@Caching(
//		evict = { 
//			@CacheEvict(value=CacheName.CACHE_OTHER, key="'roleInfo_'+#roleId"),
//			@CacheEvict(value=CacheName.CACHE_OTHER, key="'roleInfo_'+#roleCode")
//		}
//	)
//	public void removeFromCache(String transactionId, Integer roleId, String roleCode, String logonUserName) {
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "remove cahce roleId:"+roleId+" roldCode:"+roleCode+" by user:"+logonUserName));
//	}
//
//	@Override
//	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
//	public void purgeCache(String transactionId,String logonUserName) {
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "clear other cache by user:"+logonUserName));
//	}

}
