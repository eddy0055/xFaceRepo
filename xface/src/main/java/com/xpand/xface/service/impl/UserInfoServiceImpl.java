package com.xpand.xface.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.controller.RestAPIController;
import com.xpand.xface.controller.UserController;
import com.xpand.xface.dao.UserInfoDAO;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.IPCGroupService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class UserInfoServiceImpl implements UserInfoService{

	@Autowired
	UserInfoDAO userInfoDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	IPCGroupService ipcGroupService;
	
	@Override
	public UserInfo findByUserName(String userName, String className) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find user by user name:"+userName+" class name:"+className));
		UserInfo userInfo = this.userInfoDAO.findByUserName(userName);
		if (RestAPIController.CLASS_NAME.equals(className)||UserController.CLASS_NAME.equals(className)) {
			userInfo.getRoleInfo().setRoleDetailInfos(null);
			if (userInfo.getIpcGroup()!=null) {
				userInfo.getIpcGroup().setIpcGroupDetails(null);
			}
		}
		return userInfo;		
	}

	@Override
//	@Cacheable(value=CacheName.CACHE_USERINFO, key="#root.methodName+'_'+#userId")
	public UserInfo findByUserId(Integer userId) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find user by user Id:"+userId));
		return this.userInfoDAO.findByUserId(userId);		
	}

	@Override
	public UserInfo findByUserNameAndPassword(String userName, String password) {
		return this.userInfoDAO.findByUserNameAndPassword(userName, password);		
	}

	@Override
	public ArrayList<GrantedAuthority> getAuthority(UserInfo userInfo) {
		ArrayList<GrantedAuthority> roleList = new ArrayList<GrantedAuthority>();
		Set<RoleDetailInfo> roleDetails = userInfo.getRoleInfo().getRoleDetailInfos();		
		for (RoleDetailInfo roleDetail : roleDetails){
			roleList.add(new SimpleGrantedAuthority(roleDetail.getPermissionList().getPermissionName()));
		}
		return roleList;		
	}
	
	@Override	
//	@CacheEvict(value=CacheName.CACHE_USERINFO, key="#root.methodName+'_'+#userId")
	public void removeUserFromCacheByUserId(Integer userId) {
		//remove cache
	}
	
//	@CachePut(value=CacheName.CACHE_USERINFO, key="#root.methodName+'_'+#result.userId")
	public UserInfo createToDB(UserInfo userInfo) {
		this.userInfoDAO.save(userInfo);
		return userInfo;
	}

	
//	@CachePut(value=CacheName.CACHE_USERINFO, key="#root.methodName+'_'+#userInfo.userId")
	public UserInfo updateToDB(UserInfo userInfo) {
		this.userInfoDAO.save(userInfo);
		return userInfo;
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_USERINFO, allEntries=true)
	public void purgeCache() {
		//clear cache		
	}	
	
	@Override
	public Page<UserInfo> getUserInfoList(Pageable pageable, String className) {	
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "get userInfo list:"+pageable.toString()+" class name:"+className));
		Page<UserInfo> pageUserInfo = this.userInfoDAO.findAll(pageable);
		if (RestAPIController.CLASS_NAME.equals(className)) {
			//remove roleDetailInfos, ipcGroupDetails			
			List<UserInfo> userInfoList = pageUserInfo.getContent();
			if (userInfoList!=null && userInfoList.size()>0) {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "remove roleDetailInfos, ipcGroupDetails from result"));
				for(UserInfo userInfo:userInfoList) {
					userInfo.getRoleInfo().setRoleDetailInfos(null);
					if (userInfo.getIpcGroup()!=null) {
						userInfo.getIpcGroup().setIpcGroupDetails(null);
					}					
				}
			}		
		}			
		return pageUserInfo;
	}	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus update(String transactionId, String logonUserName, UserInfo userInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo start"));
		if (StringUtil.checkNull(userInfo.getUserName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "username");
		}else if (StringUtil.checkNull(userInfo.getFirstName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "firstname");
		}else if (StringUtil.checkNull(userInfo.getLastName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "lastname");
		}else if (userInfo.getRoleInfo()==null || userInfo.getRoleInfo().getRoleId()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleInfo");
		}else if (userInfo.getIpcGroup()!=null && userInfo.getIpcGroup().getIpcgId()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "ipcGroupId");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;		
		UserInfo existingUserInfo = this.userInfoDAO.findByUserName(userInfo.getUserName());
		if (existingUserInfo==null) {
			if (userInfo.getPassword()==null) {
				result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "password");
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
				return result;
			}
		}
		if (existingUserInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "userName "+userInfo.getUserName()+" not found then create"));
			userInfo = new UserInfo(userInfo);
			userInfo.setPassword(StringUtil.getSha256(userInfo.getPassword()));
			userInfo.setUserCreated(logonUserName);	
			userInfo.setUserId(null);
			this.createToDB(userInfo);
			oldValue = userInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "create userInfo:"+oldValue));					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create userId "+userInfo.getUserName()+"["+userInfo.getUserId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
					, "create userInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);
		}else {				
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found userName "+userInfo.getUserName()+" then update"));
			userInfo.setUserId(existingUserInfo.getUserId());
			userInfo.setPassword(existingUserInfo.getPassword());
			userInfo.setUserUpdated(logonUserName);					
			this.updateToDB(userInfo);
			oldValue = existingUserInfo.toString();
			String newValue = userInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update userInfo oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update userName "+userInfo.getUserName()+" success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
						, "update userInfo oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, logonUserName);
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo done with result "+result.toString()));
		return result;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Override
//	@CacheEvict(value=CacheName.CACHE_USERINFO, key="#root.methodName+'_'+#userId")
	public ResultStatus delete(String transactionId, String logonUserName, String userName) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete userInfo start"));
		ResultStatus result = new ResultStatus();
		UserInfo userInfo = this.userInfoDAO.findByUserName(userName.replace("=", ""));
		if (userInfo!=null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found username "+userName+"["+userInfo.getUserId()+"] then delete"));
			this.userInfoDAO.delete(userInfo.getUserId());				
			String oldValue = userInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete userInfo oldValue:"+oldValue+" by userName:"+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete username "+userName+"["+userInfo.getUserId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
					, "delete userInfo oldValue:"+oldValue+" by userName:"+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "userName "+userName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "username:"+userName);
		}						
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete userInfo done with result "+result.toString()));
		return result;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Override
	public void updateLogInServer(String transactionId, UserInfo userInfo) {
		//call by login module only
		this.userInfoDAO.updateLogInServer(userInfo.getLogInServer(), userInfo.getUserId());		
	}	
}
