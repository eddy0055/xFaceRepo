package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.ApplicationCfg;

@Repository
public interface ApplicationCfgDAO extends JpaRepository<ApplicationCfg, String>{
	public ApplicationCfg findByAppKey(String transactionId, String appKey);
	@Override
	public List<ApplicationCfg> findAll();
	public ApplicationCfg findOneByAppKey(String appKey);
	@Override
	public ApplicationCfg findOne(String appKey);
	
}
