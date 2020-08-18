package com.xpand.xface.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.NetworkUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
	@Autowired
	UserInfoService userInfoService;
	
	@Autowired
	SystemAuditService sysAuditService; 
	
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = StringUtil.getSha256(authentication.getCredentials().toString());        
        UserInfo userInfo = this.validateCredential(username, password);
        String transactionId = LogUtil.getWebSessionId();
        if (userInfo==null) {
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "login with username "+username+" pwd "+password)+" is fail");
        	this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL
        			, "login for user "+username, SystemAudit.RES_FAIL, username);
        	throw new BadCredentialsException("Invalid username or password for user " + username);        	
        } else {
        	ArrayList<GrantedAuthority> listOfRole = this.userInfoService.getAuthority(userInfo);
        	this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL
        				, "login for user "+username, SystemAudit.RES_SUCCESS, username);
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "login with username "+username+" pwd "+password+" is pass with role "+StringUtil.arrayToString(listOfRole)));
        	userInfo.setLogInServer(NetworkUtil.getLocalIP());
        	this.userInfoService.updateLogInServer(transactionId, userInfo);        	
            return new UsernamePasswordAuthenticationToken(username, password, listOfRole);
        }
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
          UsernamePasswordAuthenticationToken.class);
    }
    
    private UserInfo validateCredential(String username, String password) {
    	if (StringUtil.checkNull(username) || StringUtil.checkNull(password)) {
    		return null;
    	}else {
    		return this.userInfoService.findByUserNameAndPassword(username, password);    			
    	}
    }
}