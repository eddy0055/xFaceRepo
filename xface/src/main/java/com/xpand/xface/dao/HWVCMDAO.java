package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.HWVCM;

@Repository
public interface HWVCMDAO extends JpaRepository<HWVCM, Integer>{
//	public HWVCM findByVcmId(Integer vcmId);
//	public ArrayList<HWVCM> findByAll();	
}
