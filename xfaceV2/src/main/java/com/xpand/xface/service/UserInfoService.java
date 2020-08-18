package com.xpand.xface.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.UserInfo;

public interface UserInfoService  extends CacheManageService {
	public List<UserInfo> findAll(String transactionId);	
	public UserInfo findByUserName(String transactionId, String userName);
	//find by userId
	public UserInfo findByUserId(String transactionId, Integer userId);
	
	public UserInfo findByUserNameAndPassword(String transactionId, String userName, String password);	
	
	public TablePage getUserInfoList(String transactionId, PaginationCriteria pc);
	
	
	//operation
	public ArrayList<GrantedAuthority> getAuthority(String transactionId, UserInfo userInfo);
		
	public ResultStatus update(String transactionId, String logonUserName, UserInfo userInfo);
	public ResultStatus delete(String transactionId, String logonUserName, UserInfo userInfo, String userName);
	public void updateLogInServer(String transactionId, UserInfo userInfo);
	public ResultStatus changePwd(String transactionId, String logonUserName, WebFEParam webFEParam);
	public ResultStatus forgetPwd(String transactionId, WebFEParam webFEParam) throws Exception;
	
	public void clearAuthorityCache(String transactionId, UserInfo userInfo);
}
