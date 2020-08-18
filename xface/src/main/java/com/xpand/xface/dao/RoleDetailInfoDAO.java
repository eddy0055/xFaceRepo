package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;

@Repository
public interface RoleDetailInfoDAO extends JpaRepository<RoleDetailInfo, Integer>{
	@Modifying
	@Query("delete from RoleDetailInfo r where r.roleDetailId = ?1")
	public void delete(Integer roleDetailId);
	
	@Modifying
	@Query("delete from RoleDetailInfo r where r.roleInfo.roleId = ?1")
//	@Query("DELETE FROM RoleDetailInfo r " + 
//			"WHERE r.roleInfo.roleId IN (SELECT roleId FROM RoleInfo WHERE roleId=?1)")
	public void deleteByRoleInfoRoleId(Integer roleId);
	
	@Modifying
	@Query("delete from RoleDetailInfo r where r.roleInfo = ?1")
//	@Query("DELETE FROM RoleDetailInfo r " + 
//			"WHERE r.roleInfo.roleId IN (SELECT roleId FROM RoleInfo WHERE roleId=?1)")
	public void deleteByRoleInfo(RoleInfo roleInfo);
	
	
	
	public List<RoleDetailInfo> findByRoleInfo(RoleInfo roleInfo);
}
