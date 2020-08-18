package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.EmailInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.dao.UserInfoDAO;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.EmailEngine;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope

@Component
public class UserInfoServiceImpl implements UserInfoService, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	UserInfoDAO userInfoDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	RoleInfoService roleInfoService;
	
	
	@Override
	@Cacheable(value=CacheName.CACHE_USERINFO, key="'key_'+#userName")
	public UserInfo findByUserName(String transactionId, String userName) {				
		UserInfo userInfo = this.userInfoDAO.findByUserName(userName);		
		if (userInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "user name:"+userName+" not found"));
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found user name:"+userName));
		}
		return userInfo;		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_USERINFO, key="'key_'+#userId")
	public UserInfo findByUserId(String transactionId, Integer userId) {		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "find user by user Id:"+userId));
		UserInfo userInfo = this.userInfoDAO.findByUserId(userId);
		if (userInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "user id:"+userId+" not found"));
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found userId:"+userId));
		}
		return userInfo; 		
	}

	@Override
	public UserInfo findByUserNameAndPassword(String transactionId, String userName, String password) {
		UserInfo userInfo = this.findByUserName(transactionId, userName);
		if (userInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "user name:"+userName+" not found"));
			return null;
		}else if (userInfo.getPassword().equals(password)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "user name/pwd ok for user name:"+userName));
			return userInfo;
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "user name:"+userName+" found but wrong pwd"));
			return null;
		}			
	}

	@Override
	@Cacheable(value=CacheName.CACHE_USERINFO, key="'authority_'+#userInfo.userName")
	public ArrayList<GrantedAuthority> getAuthority(String transactionId, UserInfo userInfo) {
		ArrayList<GrantedAuthority> roleList = new ArrayList<GrantedAuthority>();
		if (userInfo.getPasswordExpire().getTime() < (new Date().getTime())) {
			roleList.add(new SimpleGrantedAuthority(ConstUtil.USER_ROLE_FORCE_CHANGE_PWD_USAGE));
		}else {				
			Set<RoleDetailInfo> roleDetails = userInfo.getRoleInfo().getRoleDetailInfoList();		
			for (RoleDetailInfo roleDetail : roleDetails){
				roleList.add(new SimpleGrantedAuthority(roleDetail.getPermissionList().getPermissionName()));
			}
		}		
		return roleList;		
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_USERINFO, key="'authority_'+#userInfo.userName")
	public void clearAuthorityCache(String transactionId, UserInfo userInfo) {		
	}
	
//	@Override
//	@Caching(
//		evict = { 
//			@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#userId"),
//			@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#userName")
//		}
//	)
//	public void removeFromCache(String transactionId, Integer userId, String userName, String logonUserName) {
//		// TODO Auto-generated method stub
//		Logger.debug(this, LogUtil.getLogDebug(transactionId, "remove cahce userId:"+userId+" userName:"+userName+" by user:"+logonUserName));
//	}
	
	
	public UserInfo createToDB(UserInfo userInfo) {
		this.userInfoDAO.save(userInfo);
		return userInfo;
	}

	public UserInfo updateToDB(UserInfo userInfo) {
		this.userInfoDAO.save(userInfo);
		return userInfo;
	}
	
//	@Override
//	@CacheEvict(value=CacheName.CACHE_USERINFO, allEntries=true)
//	public void purgeCache(String transactionId, String logonUserName) {
//		//clear cache		
//		Logger.debug(this, LogUtil.getLogDebug(transactionId, "clear cahce by user:"+logonUserName));
//	}	
	@Override
	public TablePage getUserInfoList(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getUserInfoList"));				
		TablePage page = new TablePage(transactionId, this.userInfoDAO, pc);
        Logger.info(this, LogUtil.getLogInfo(transactionId, "out getUserInfoList"));
		return page;		
	}	
	
	
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#userInfo.userId"),
			@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#userInfo.userName")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, UserInfo userInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "userInfo.getUsername :: " + userInfo.getUserName() ));
		if (StringUtil.checkNull(userInfo.getUserName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "username");
		}else if (StringUtil.checkNull(userInfo.getFirstName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "firstname");
		}else if (StringUtil.checkNull(userInfo.getLastName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "lastname");
		}else if (userInfo.getRoleInfo()==null || userInfo.getRoleInfo().getRoleId()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "roleInfo");
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
			//Set password 
			userInfo.setPassword(StringUtil.getSha256(userInfo.getFirstName() + ":" + userInfo.getLastName()));
			//Set password Expire 
			userInfo.setPasswordExpire(new Date(new Date().getTime()+(86400*6000)));
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
			userInfo.setPasswordExpire(new Date(new Date().getTime()+(86400*6000)));
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
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#userInfo.userId"),
			@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#userName")
		}
	)
	public ResultStatus delete(String transactionId, String logonUserName, UserInfo userInfo, String userName) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete userInfo start"));
		ResultStatus result = new ResultStatus();		
		if (userInfo.getUserId()==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "userName "+userName+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "username:"+userName);		
		}else {					
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found username "+userName+"["+userInfo.getUserName()+"] then delete"));
			this.userInfoDAO.delete(userInfo.getUserId());				
			String oldValue = userInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete userInfo oldValue:"+oldValue+" by userName:"+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete username "+userName+"["+userInfo.getUserId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
					, "delete userInfo oldValue:"+oldValue+" by userName:"+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
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

	@Override
	public List<UserInfo> findAll(String transactionId) {
		// TODO Auto-generated method stub
		return this.userInfoDAO.findAll();
	}	
	
	@Override
	@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_USERINFO, key="'key_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_USERINFO, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus changePwd(String transactionId, String logonUserName, WebFEParam webFEParam) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "change pwd start"));
		if (StringUtil.checkNull(webFEParam.getOldPwd())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "original password");
		}else if (StringUtil.checkNull(webFEParam.getNewPwd())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "new password");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "change pwd fail with result "+result.toString()));
			return result;
		}
//		result = StringUtil.passwordValidation(logonUserName, webFEParam.getNewPwd());
//		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "change pwd fail with result "+result.toString()));
//			return result;
//		}
		UserInfo userInfo = this.userInfoDAO.findByUserName(logonUserName);
		if (userInfo.getPassword().equals(StringUtil.getSha256(logonUserName+":"+webFEParam.getOldPwd()))) {
			//60 day						
			int expireWithIn = StringUtil.stringToInteger(this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_NEW_PWD_EXPIRE_DAY).getAppValue1(), 60) * 1000;
			this.userInfoDAO.changePwd(StringUtil.getSha256(logonUserName+":"+webFEParam.getNewPwd())
						, new Date(new Date().getTime()+(86400*expireWithIn)), userInfo.getUserId());
		}else {
			result.setStatusCode(ResultStatus.CHANGE_PWD_OLD_PASSWORD_NOT_MATCH_CODE, null);
		}
		return result;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus forgetPwd(String transactionId, WebFEParam webFEParam) throws Exception{		
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "forgetPwd start"));
		if (StringUtil.checkNull(webFEParam.getUserName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "user name");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "forgetPwd fail with result "+result.toString()));
			return result;
		}		
		UserInfo userInfo = this.userInfoDAO.findByUserName(webFEParam.getUserName());		
		if (userInfo==null) {
			result.setStatusCode(ResultStatus.FORGET_PWD_INVALID_USERNAME_CODE, webFEParam.getUserName());			
		}else {		
			String randomPwd = StringUtil.generateRandomPassword(1, 1, 1, 1, 8);
			this.userInfoDAO.changePwd(StringUtil.getSha256(webFEParam.getUserName()+":"+randomPwd)
						, new Date(), userInfo.getUserId());
			EmailInfo email = new EmailInfo();
			email.setMessage("You temporary password for user name "+webFEParam.getUserName()+" to access xFace is "+randomPwd);
			email.setSendFrom("mvne.support.prd@xpand.asia");
			email.setSendTo(webFEParam.getUserName());
			email.setSubject("xFace temporary password");
			email.setUserName("mvne.support.prd@xpand.asia");
			email.setUserPwd("5Uv14Adm!n");			
			new EmailEngine().sendEmail(transactionId, email);
			Logger.info(this, LogUtil.getLogInfo(transactionId, "send random pwd:"+randomPwd+" to user"+webFEParam.getUserName()));
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "out forgetPwd"));
		return result;		
	}

	
	
	
}
