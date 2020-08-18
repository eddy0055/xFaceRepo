package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.HWIPC;

@Repository
public interface HWIPCDAO extends JpaRepository<HWIPC, Integer>{
	@Modifying
	@Query("delete from HWIPC h where h.ipcId = ?1")
	public void delete(Integer ipcId);
	
	public HWIPC findByIpcCode(String ipcCode);
}
