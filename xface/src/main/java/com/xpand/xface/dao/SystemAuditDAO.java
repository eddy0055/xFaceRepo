package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.SystemAudit;

@Repository
public interface SystemAuditDAO extends JpaRepository<SystemAudit, Integer>{
	
}
