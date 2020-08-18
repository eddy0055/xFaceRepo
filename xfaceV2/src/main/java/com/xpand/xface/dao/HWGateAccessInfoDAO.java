package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.HWGateAccessInfo;

@Repository
public interface HWGateAccessInfoDAO extends JpaRepository<HWGateAccessInfo, Long>{
	
}
