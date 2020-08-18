package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xpand.xface.dao.PermissionListDAO;
import com.xpand.xface.entity.PermissionList;
import com.xpand.xface.service.PermissionListService;

@Component
public class PermissionListServiceImpl implements PermissionListService{

	@Autowired
	PermissionListDAO permissionListDAO;
	
	@Override
	public List<PermissionList> findAll() {
		return this.permissionListDAO.findAll();
	}

	@Override
	public PermissionList findByPermissionId(Integer permissionId) {
		return this.permissionListDAO.findOne(permissionId);
	}

}
