package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.RoleInfo;

@Repository
public interface RoleInfoDAO extends JpaRepository<RoleInfo, Integer>{
	@Override
	@Modifying
	@Query("DELETE FROM RoleInfo r WHERE r.roleId = :roleId")
	public void delete(@Param("roleId") Integer roleId);
	
	public RoleInfo findOneByRoleCode(String roleCode);
}
