package com.xpand.xface.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.xpand.xface.bean.CustomUserDetails;
import com.xpand.xface.entity.UserInfo;

@Component
public class CustomUserDetailsService  implements UserDetailsService{
	@Autowired
	UserInfoService userInfoService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {		
		UserInfo userInfo = this.userInfoService.findByUserName(username, null);
        if (userInfo==null) {
        	throw new UsernameNotFoundException("Username not found");        	
        }else {
        	return new CustomUserDetails(userInfo);
        }                
	}

}
