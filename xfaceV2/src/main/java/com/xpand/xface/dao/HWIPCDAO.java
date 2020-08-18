package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWVCM;

@Repository
public interface HWIPCDAO extends JpaRepository<HWIPC, Integer>{
	@Override
	@Modifying
	@Query("DELETE FROM HWIPC h WHERE h.ipcId = :ipcId")
	public void delete(@Param("ipcId") Integer ipcId);
	
	public HWIPC findByIpcCode(String ipcCode);
	public HWIPC findByIpcName(String ipcName);
	
	@Query("SELECT i FROM HWIPC i WHERE (i.ipcTaskId IS NULL OR i.ipcTaskId='') AND i.hwVCM = :hwVCM")
	public List<HWIPC> findNeverCreateTaskList(@Param("hwVCM") HWVCM hwVCM);
	
	@Query("SELECT i FROM HWIPC i WHERE (i.checkPointId IS NULL OR i.checkPointId='') AND i.hwVCM = :hwVCM")
	public List<HWIPC> findNeverAddToCheckPoint(@Param("hwVCM") HWVCM hwVCM);
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Modifying
	@Query("UPDATE HWIPC u SET u.ipcTaskId = :ipcTaskId WHERE u.ipcId=:ipcId")
	public void updateTaskId(@Param("ipcTaskId") String ipcTaskId, @Param("ipcId") Integer ipcId);
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Modifying
	@Query("UPDATE HWIPC u SET u.checkPointId = :checkPointId WHERE u.ipcId=:ipcId")
	public void updateCheckPointId(@Param("checkPointId") String checkPointId, @Param("ipcId") Integer ipcId);
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Modifying
	@Query("UPDATE HWIPC u SET u.ipcStatus = :ipcStatus WHERE u.ipcId=:ipcId")
	public void updateStatus(@Param("ipcStatus") Integer ipcStatus, @Param("ipcId") Integer ipcId);
	
	public List<HWIPC> findByhwGateInfoIn(List<HWGateInfo> hwGateInfoList);
	
//	Query( "select o from MyObject o where inventoryId in :ids" )
//	List<MyObject> findByInventoryIds(@Param("ids") List<Long> inventoryIdList);
}
