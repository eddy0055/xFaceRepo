package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.RoleInfo;

public interface RoleInfoService {
	public List<RoleInfo> findAll();	
	public Page<RoleInfo> getRoleInfoList(Pageable pageable);
	public RoleInfo findByRoleId(Integer roleId);
	public RoleInfo findOneByRoleName(String roleName);
	
	//operation
	public ResultStatus update(String transactionId, String logonUserName, RoleInfo roleInfo);
	public ResultStatus delete(String transactionId, String logonUserName, String roleName);	
}
