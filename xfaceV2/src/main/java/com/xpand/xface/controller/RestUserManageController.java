package com.xpand.xface.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.PermissionList;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.util.LogUtil;

@RestController
@RequestMapping("/rest/userManage")
public class RestUserManageController {
	
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	SystemAuditService sysAuditService;
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	RoleDetailInfoService roleDetailInfoService;
	@Autowired
	PermissionListService permissionListService;
	
	@RequestMapping("/user/getAllUserInfo")	
	@GetMapping
	public List<UserInfo> getAllUserInfo(HttpServletRequest request) {
		return this.userInfoService.findAll(request.getSession().getId());				
	}	
	
	
	@RequestMapping("/user/getByUserId")	
	@PostMapping
	public UserInfo getUserId(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {		
		UserInfo userInfo = this.userInfoService.findByUserId(request.getSession().getId(), webFEParam.getUserId());
		if (userInfo!=null) {
			userInfo.getRoleInfo().setHtmlPageInfo(null);
			userInfo.getRoleInfo().setRoleDetailInfoList(null);
			userInfo.getRoleInfo().setUserInfoList(null);
			//userInfo.setRoleInfo(null);
			//userInfo.getRoleInfo().setRoleDetailInfoList(null);
		}
		return userInfo;				
	}
	
	@RequestMapping("/user/getUserInfoList")	
	@PostMapping
	public @ResponseBody TablePage getUserInfoList(HttpServletRequest request, @RequestBody PaginationCriteria treq) throws Exception{									
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in getUserInfoList"));		
		return this.userInfoService.getUserInfoList(request.getSession().getId(), treq);
	}
	
	@RequestMapping("/user/getUserInfo")	
	@PostMapping
	public UserInfo getUserInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {		
		UserInfo userInfo = this.userInfoService.findByUserName(request.getSession().getId(), webFEParam.getUserName());
		if (userInfo!=null) {
			userInfo.getRoleInfo().setRoleDetailInfoList(null);
			userInfo.getRoleInfo().setHtmlPageInfo(null);			
			userInfo.getRoleInfo().setUserInfoList(null);
		}
		return userInfo;				
	}
	
	//getAllRole and setting  for protect Stack OverFlow
		@RequestMapping("/user/getAllRole")	
		@GetMapping
		public List<RoleInfo> getAllRole(HttpServletRequest request) {
			List<RoleInfo> roleInfoList = this.roleInfoService.findAll();
			for (int i = 0; i < roleInfoList.size(); i++)  {			
				RoleInfo roleInfo = roleInfoList.get(i);
				roleInfo.setRoleDetailInfoList(null);
				roleInfo.setUserInfoList(null);
				roleInfo.getHtmlPageInfo().setRoleInfoList(null);
			}
			return roleInfoList;				
		}	
		
	
	@RequestMapping("/user/updateUserInfo")	
	@PostMapping
	public ResultStatus updateUserInfo(HttpServletRequest request, @RequestBody UserInfo userInfo) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.userInfoService.update(transactionId, logonUserName, userInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update userInfo:"+userInfo.getUserName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "username:"+userInfo.getUserName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;
	}
	
	@RequestMapping("/role/deleteRoleInfo")	
	@PostMapping
	public ResultStatus deleteRoleInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();
		try {
			RoleInfo roleInfo = this.roleInfoService.findByRoleCode(transactionId, webFEParam.getRoleCode());
			if (roleInfo==null) {
				roleInfo = new RoleInfo();
			}
			result = this.roleInfoService.delete(transactionId, logonUserName, roleInfo, webFEParam.getRoleCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete role code "+webFEParam.getRoleCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete roleCode:"+webFEParam.getRoleCode());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}	

	
	@RequestMapping("/user/deleteUserInfo")	
	@PostMapping
	public ResultStatus deleteUserInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();
		try {
			UserInfo userInfo = this.userInfoService.findByUserName(transactionId, webFEParam.getUserName());
			if (userInfo==null) {
				userInfo = new UserInfo();
			}
			result = this.userInfoService.delete(transactionId, logonUserName, userInfo, webFEParam.getUserName());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete user name "+webFEParam.getUserName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete user name:"+webFEParam.getUserName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	
	//forceChangePwd
		@RequestMapping("/user/changePwd")	
		@PostMapping
		public ResultStatus changePwd(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
			ResultStatus result = null;
			String transactionId = request.getSession().getId();
			String logonUserName =  request.getUserPrincipal().getName();
			UserInfo userInfo = this.userInfoService.findByUserName(transactionId, logonUserName);
			try {			
				Logger.info(this, LogUtil.getLogInfo(transactionId, "receive request to change pwd for user:"+logonUserName));
				result = this.userInfoService.changePwd(transactionId, logonUserName, webFEParam);			
			}catch(Exception ex) {
				Logger.error(this, LogUtil.getLogError(transactionId, "error while change password :"+logonUserName, ex));
				result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "user name:"+logonUserName);
			}	
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				userInfo.setPasswordExpire(new Date(new Date().getTime()+(86400*6000)));
				this.userInfoService.removeCacheByKey(logonUserName);			
				this.userInfoService.clearAuthorityCache(transactionId, userInfo);							
				result.setStatusParam(userInfo.getRoleInfo().getHtmlPageInfo().getPageURL());
				Logger.info(this, LogUtil.getLogInfo(transactionId, "change pwd for user:"+logonUserName+" success then redirect url:"+result.getStatusParam()));
			}else {			
				//not success then log to audit
				this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
				Logger.info(this, LogUtil.getLogInfo(transactionId, "change pwd for user:"+logonUserName+" fail:"+result.toString()));
			}
			return result;
		}
		
	//forceChangePwd
	@RequestMapping("/user/forceChangePwd")	
	@PostMapping
	public ResultStatus forceChangePwd(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();
		UserInfo userInfo = this.userInfoService.findByUserName(transactionId, logonUserName);
		try {			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "receive request to force change pwd for user:"+logonUserName));
			result = this.userInfoService.changePwd(transactionId, logonUserName, webFEParam);			
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while change password :"+logonUserName, ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "user name:"+logonUserName);
		}	
		if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			userInfo.setPasswordExpire(new Date(new Date().getTime()+(86400*6000)));
			this.userInfoService.removeCacheByKey(logonUserName);			
			this.userInfoService.clearAuthorityCache(transactionId, userInfo);			
			ArrayList<GrantedAuthority> authorityList = this.userInfoService.getAuthority(transactionId, userInfo);
			SecurityContextHolder.getContext().setAuthentication(
        	        new UsernamePasswordAuthenticationToken(
        	            SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
        	            SecurityContextHolder.getContext().getAuthentication().getCredentials(),
        	            authorityList)
        	        );
			result.setStatusParam(userInfo.getRoleInfo().getHtmlPageInfo().getPageURL());
			Logger.info(this, LogUtil.getLogInfo(transactionId, "for change pwd for user:"+logonUserName+" success then redirect url:"+result.getStatusParam()));
		}else {			
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL , logonUserName);
			Logger.info(this, LogUtil.getLogInfo(transactionId, "for change pwd for user:"+logonUserName+" fail:"+result.toString()));
		}
		return result;
	}
	@RequestMapping("/user/forgetPwd")	
	@PostMapping
	public ResultStatus forgetPwd(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		try {			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "receive request to forget pwd for user:"+webFEParam.getUserName()));
			result = this.userInfoService.forgetPwd(transactionId, webFEParam);			
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while reset password :"+webFEParam.getUserName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "user name:"+webFEParam.getUserName());
		}			
		return result;
	}
	
	
	
	
	@RequestMapping("/role/getRoleInfoList")	
	@PostMapping
	public @ResponseBody TablePage getRoleInfoListV2(HttpServletRequest request, @RequestBody PaginationCriteria treq) throws Exception{									
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in getRoleInfoListV2"));		
		return this.roleInfoService.getRoleInfoList(request.getSession().getId(), treq);
	}
	
	@RequestMapping("/role/getRoleInfo")	
	@PostMapping
	public RoleInfo getRoleInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		return this.roleInfoService.findByRoleCode(request.getSession().getId(), webFEParam.getRoleCode());				
	}
	@RequestMapping("/role/getRoleInfoById")	
	@PostMapping
	public RoleInfo getRoleInfoById(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		return this.roleInfoService.findByRoleId(request.getSession().getId(), webFEParam.getRoleId());				
	}
	@RequestMapping("/role/rmvRoleInfo")	
	@PostMapping
	@ResponseBody
	public String rmvRoleInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		this.roleInfoService.removeCacheById(webFEParam.getRoleId());
		this.roleInfoService.removeCacheByKey(webFEParam.getRoleCode());
		return "OK";
	}
	@RequestMapping("/role/rmvRoleInfoById")	
	@PostMapping
	@ResponseBody
	public String rmvRoleInfoById(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {		
		this.roleInfoService.removeCacheById(webFEParam.getRoleId());
		this.roleInfoService.removeCacheByKey(webFEParam.getRoleCode());
		return "OK";
	}
	
	
	@RequestMapping("/role/getRoleDetailInfoList")	
	@PostMapping
	public List<RoleDetailInfo> getRoleDetailInfoList(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		return this.roleDetailInfoService.findByRoleInfoRoleCode(request.getSession().getId(), webFEParam.getRoleCode());				
	}
	@RequestMapping("/role/getAllPermissionList")	
	@GetMapping
	public List<PermissionList> getAllPermissionList(HttpServletRequest request) {
		return this.permissionListService.findAll(request.getSession().getId());				
	}
	
	//@RequestMapping("/role/getAllRole")
	//@GetMapping
	//public List<RoleInfo> getAllRole(HttpServletRequest request){
		//return 
		
	//}
	
	
	@RequestMapping("/role/updateRoleInfo")	
	@PostMapping
	public ResultStatus updateRoleInfo(HttpServletRequest request, @RequestBody RoleInfo roleInfo) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.roleInfoService.update(transactionId, logonUserName, roleInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update RoleInfo:"+roleInfo.getRoleCode(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "role name:"+roleInfo.getRoleName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;
	}
}	
