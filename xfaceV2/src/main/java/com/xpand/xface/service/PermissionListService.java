package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.entity.PermissionList;

public interface PermissionListService  extends CacheManageService {
	public List<PermissionList> findAll(String transactionId);
	public PermissionList findByPermissionName(String transactionId, String permissionName);
}
