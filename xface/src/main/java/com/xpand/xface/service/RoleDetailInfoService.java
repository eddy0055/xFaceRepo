package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;

public interface RoleDetailInfoService {
	public List<RoleDetailInfo> findByRoleId(Integer roleId);	
	public List<RoleDetailInfo> findByRoleInfoRoleName(String roleName);
	public RoleDetailInfo findByRoleDetailId(Integer roleDetailId);	
	
	//operation
	public ResultStatus deleteByRoleInfo(String transactionId, RoleInfo roleInfo);
	public RoleInfo resetRoleDetailId(RoleInfo roleInfo);
	public RoleInfo setPermissionObject(RoleInfo roleInfo);
}
