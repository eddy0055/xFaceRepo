package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.IPCGroup;

@Repository
public interface IPCGroupDAO extends JpaRepository<IPCGroup, Integer>{
		
}
