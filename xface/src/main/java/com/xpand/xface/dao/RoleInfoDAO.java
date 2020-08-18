package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.RoleInfo;

@Repository
public interface RoleInfoDAO extends JpaRepository<RoleInfo, Integer>{
	@Modifying
	@Query("delete from RoleInfo r where r.roleId = ?1")
	public void delete(Integer roleId);
	
	public RoleInfo findOneByRoleName(String roleName);
}
