
package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.RoleInfoDAO;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class RoleInfoServiceImpl implements RoleInfoService{
	
	@Autowired
	RoleInfoDAO roleInfoDAO;
	@Autowired
	RoleDetailInfoService roleDetailInfoService;
	@Autowired
	SystemAuditService systemAuditService;
	@Override
	public List<RoleInfo> findAll() {
		return this.roleInfoDAO.findAll();
	}
		
	@Override
	public RoleInfo findByRoleId(Integer roleId) {
		return this.roleInfoDAO.findOne(roleId);
	}
	@Override
	public RoleInfo findOneByRoleName(String roleName) {
		RoleInfo roleInfo = this.roleInfoDAO.findOneByRoleName(roleName); 
		if (roleInfo==null) {
			return null;
		}else {
			return roleInfo;
		}
	}

	@Override
	public Page<RoleInfo> getRoleInfoList(Pageable pageable) {		
		return this.roleInfoDAO.findAll(pageable);
	}
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)	
	public ResultStatus delete(String transactionId, String logonUserName, String roleName) {		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "deletev2 roleInfo start"));
		ResultStatus result = new ResultStatus();
		RoleInfo roleInfo = this.roleInfoDAO.findOneByRoleName(roleName);
		if (roleInfo!=null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found roleName "+roleInfo.getRoleName()+"["+roleInfo.getRoleId()+"] then delete"));
			String oldValue = roleInfo.toString();
			this.roleDetailInfoService.deleteByRoleInfo(transactionId, roleInfo);			
			this.roleInfoDAO.delete(roleInfo.getRoleId());										
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete roleInfo oldValue:"+oldValue+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete role "+roleInfo.getRoleName()+"["+roleInfo.getRoleId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
					, "delete roleInfo oldValue:"+oldValue+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "role name "+roleName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "roleName:"+roleName);
		}			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete roleInfo done with result "+result.toString()));
		return result;
	}
		
	public RoleInfo createToDB(String transactionId, RoleInfo roleInfo) {
		this.roleDetailInfoService.resetRoleDetailId(roleInfo);
		this.roleInfoDAO.save(roleInfo);		
		return roleInfo;
	}
	
	public RoleInfo updateToDB(String transactionId, RoleInfo roleInfo) {
		//delete insert					
		this.roleDetailInfoService.deleteByRoleInfo(transactionId, roleInfo);
		this.roleDetailInfoService.resetRoleDetailId(roleInfo);
		this.roleInfoDAO.save(roleInfo);
		return roleInfo;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Override
	public ResultStatus update(String transactionId, String logonUserName, RoleInfo roleInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo start"));
		if (StringUtil.checkNull(roleInfo.getRoleName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleName");
		}else if (StringUtil.checkNull(roleInfo.getDefaultPage())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "defaultPage");
		}else if (roleInfo.getRoleDetailInfos()==null ||roleInfo.getRoleDetailInfos().size()==0) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleDetailInfo");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;						
		RoleInfo existingRoleInfo = this.roleInfoDAO.findOneByRoleName(roleInfo.getRoleName());
		if (existingRoleInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "roleName "+roleInfo.getRoleName()+" not found then create"));
			roleInfo = new RoleInfo(roleInfo);
			roleInfo.setRoleId(null);
			roleInfo.setUserCreated(logonUserName);
			this.roleDetailInfoService.setPermissionObject(roleInfo);					
			this.createToDB(transactionId, roleInfo);			
			oldValue = roleInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create roleInfo:"+oldValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create roleName "+roleInfo.getRoleName()+"["+roleInfo.getRoleId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
					, "create roleInfo:"+oldValue
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found roleName "+roleInfo.getRoleName()+" then update"));
			roleInfo.setUserUpdated(logonUserName);
			this.roleDetailInfoService.setPermissionObject(roleInfo);
			roleInfo.setRoleId(existingRoleInfo.getRoleId());
			this.updateToDB(transactionId, roleInfo);
			oldValue = existingRoleInfo.toString();
			String newValue = roleInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update roleInfo oldValue:"+oldValue+", newValue:"+newValue));			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleName "+roleInfo.getRoleName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
					, "update roleInfo oldValue:"+oldValue+", newValue:"+newValue
					, SystemAudit.RES_SUCCESS, logonUserName);
		}			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleInfo done with result "+result.toString()));
		return result;
	}			
}
