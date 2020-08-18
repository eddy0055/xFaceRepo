package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.ApplicationCfg;

@Repository
public interface ApplicationCfgDAO extends JpaRepository<ApplicationCfg, String>{
		
}
