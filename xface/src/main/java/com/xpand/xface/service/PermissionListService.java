package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.entity.PermissionList;

public interface PermissionListService {
	public List<PermissionList> findAll();
	public PermissionList findByPermissionId(Integer permissionId);
}
