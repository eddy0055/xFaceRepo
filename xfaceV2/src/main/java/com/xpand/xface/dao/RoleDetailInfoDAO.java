package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;

@Repository
public interface RoleDetailInfoDAO extends JpaRepository<RoleDetailInfo, Integer>{
	@Override
	@Modifying
	@Query("delete from RoleDetailInfo r where r.roleDetailId = :roleDetailId")
	public void delete(@Param("roleDetailId") Integer roleDetailId);
	
	@Modifying
	@Query("delete from RoleDetailInfo r where r.roleInfo.roleId = :roleId")
//	@Query("DELETE FROM RoleDetailInfo r " + 
//			"WHERE r.roleInfo.roleId IN (SELECT roleId FROM RoleInfo WHERE roleId=?1)")
	public void deleteByRoleInfoRoleId(@Param("roleId") Integer roleId);
	
	@Modifying
	@Query("delete from RoleDetailInfo r where r.roleInfo = :roleInfo")
//	@Query("DELETE FROM RoleDetailInfo r " + 
//			"WHERE r.roleInfo.roleId IN (SELECT roleId FROM RoleInfo WHERE roleId=?1)")
	public void deleteByRoleInfo(@Param("roleInfo") RoleInfo roleInfo);
	
	
	
	public List<RoleDetailInfo> findByRoleInfo(RoleInfo roleInfo);
}
