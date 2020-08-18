package com.xpand.xface.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;


public class CustomUserDetails extends UserInfo implements UserDetails{
	private static final long serialVersionUID = 1L;
//	@Autowired
//	UserInfoService userInfoService;
	@Autowired
	SystemAuditService sysAuditService; 

	String transactionId = null;
	public CustomUserDetails(final UserInfo userInfo) {
        super(userInfo);
        this.transactionId = LogUtil.getWebSessionId();
    }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "getAuthorities is fire"));
		ArrayList<GrantedAuthority> listOfRole = this.getAuthority();    	
    	//this.sysAuditService.createAudit(this.transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL, "login for user "+super.getUserName(), SystemAudit.RES_SUCCESS);
    	Logger.info(this, LogUtil.getLogInfo(this.transactionId, "login with username "+super.getUserName()+" pwd "+super.getUserName()+" is pass with role "+StringUtil.getJson(listOfRole, null)));    	
    	return listOfRole;    	
	}

	@Override
	public String getUsername() {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "get username is fire"));
		return super.getUserName();
	}
	
	@Override
    public String getPassword() {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "get password is fire"));
        return super.getPassword();
    }
	
	@Override
	public boolean isAccountNonExpired() {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "isAccNonExpired is fire"));
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "isAccNonLock is fire"));
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "isCredentialsNonExpired is fire"));
		return true;
	}

	@Override
	public boolean isEnabled() { 
		Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "isEnable is fire"));
		return true;
	}
	
	public ArrayList<GrantedAuthority> getAuthority() {
		ArrayList<GrantedAuthority> roleList = new ArrayList<GrantedAuthority>();
		Set<RoleDetailInfo> roleDetails = super.getRoleInfo().getRoleDetailInfoList();		
		for (RoleDetailInfo roleDetail : roleDetails){
			roleList.add(new SimpleGrantedAuthority(roleDetail.getPermissionList().getPermissionName()));
		}
		return roleList;		
	}

}
