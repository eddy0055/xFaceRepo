package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.RoleInfo;

public interface RoleInfoService extends CacheManageService {
	public List<RoleInfo> findAll();	
	public TablePage getRoleInfoList(String transactionId, PaginationCriteria pc);
	public RoleInfo findByRoleId(String transactionId, Integer roleId);
	public RoleInfo findByRoleCode(String transactionId, String roleCode);
	
	//operation
	public ResultStatus update(String transactionId, String logonUserName, RoleInfo roleInfo);
	public ResultStatus delete(String transactionId, String logonUserName, RoleInfo roleInfo, String roleCode);
	
//	public void removeFromCache(String transactionId, Integer roleId, String roleCode, String logonUserName);
//	public void purgeCache(String transactionId,String logonUserName);
	
}
