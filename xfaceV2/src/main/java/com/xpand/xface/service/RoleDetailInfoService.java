package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;

public interface RoleDetailInfoService extends CacheManageService {
	public List<RoleDetailInfo> findByRoleId(String transactionId, Integer roleId);		
	public List<RoleDetailInfo> findByRoleInfoRoleCode(String transactionId, String roleCode);
	public RoleDetailInfo findByRoleDetailId(String transactionId, Integer roleDetailId);	
	
	//operation
	public ResultStatus deleteByRoleInfo(String transactionId, RoleInfo roleInfo);
	public RoleInfo resetRoleDetailId(String transactionId, RoleInfo roleInfo);
	public RoleInfo setPermissionObject(String transactionId, RoleInfo roleInfo);
	
}
