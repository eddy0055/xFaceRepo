package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.PermissionList;

@Repository
public interface PermissionListDAO extends JpaRepository<PermissionList, Integer>{
	public PermissionList findByPermissionName(String permissionName);
}
