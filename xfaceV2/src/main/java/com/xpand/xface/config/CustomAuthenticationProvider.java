package com.xpand.xface.config;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.NetworkUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider, Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	UserInfoService userInfoService;	
	@Autowired
	SystemAuditService sysAuditService; 
	@Autowired	
	GlobalVarService globalVarService;	
	
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {    	
        String userName = authentication.getName();
        String password = StringUtil.getSha256(authentication.getName()+":"+authentication.getCredentials().toString());
        String transactionId = LogUtil.getWebSessionId();
        UserInfo userInfo = this.validateCredential(transactionId, userName, password);        
        if (userInfo==null) {
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "login with username "+userName+" pwd "+password)+" is fail");
        	this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL
        			, "login for user "+userName, SystemAudit.RES_FAIL, userName);
        	throw new BadCredentialsException("Invalid username or password for user " + userName);        	
        } else {
        	ArrayList<GrantedAuthority> listOfRole = this.userInfoService.getAuthority(transactionId, userInfo);
        	this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL
        				, "login for user "+userName, SystemAudit.RES_SUCCESS, userName);
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "login with username "+userName+" pwd "+password+" is pass with role "+StringUtil.arrayToString(listOfRole)));
        	userInfo.setLogInServer(NetworkUtil.getLocalIP());
        	WebSocketHolder wsh = this.globalVarService.getWebSocketHolderMatchUserName(userName);
        	if (wsh!=null) {
        		wsh.setMarkDelete(true);
        	}        	
        	this.userInfoService.updateLogInServer(transactionId, userInfo);
            return new UsernamePasswordAuthenticationToken(userName, password, listOfRole);
        }
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
          UsernamePasswordAuthenticationToken.class);
    }
    
    private UserInfo validateCredential(String transactionId, String username, String password) {
    	if (StringUtil.checkNull(username) || StringUtil.checkNull(password)) {
    		return null;
    	}else {
    		return this.userInfoService.findByUserNameAndPassword(transactionId, username, password);    			
    	}
    }
}