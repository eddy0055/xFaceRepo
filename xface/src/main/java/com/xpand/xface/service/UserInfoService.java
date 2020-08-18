package com.xpand.xface.service;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.UserInfo;

public interface UserInfoService {
	public UserInfo findByUserName(String userName, String className);
	public UserInfo findByUserId(Integer userId);
	public UserInfo findByUserNameAndPassword(String userName, String password);
	public Page<UserInfo> getUserInfoList(Pageable pageable, String className);
	//operation
	public ArrayList<GrantedAuthority> getAuthority(UserInfo userInfo);
	public void removeUserFromCacheByUserId(Integer userId);
	public ResultStatus update(String transactionId, String logonUserName, UserInfo userInfo);
	public ResultStatus delete(String transactionId, String logonUserName, String userName);
	public void updateLogInServer(String transactionId, UserInfo userInfo);
	public void purgeCache();		
}
