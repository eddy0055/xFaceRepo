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

import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;

@Repository
public interface HWTaskListDAO extends JpaRepository<HWTaskList, Integer>{
	@Query("SELECT h FROM HWTaskList h JOIN h.hwCheckPointLibrary c WHERE (h.taskId IS NULL OR h.taskId='') AND c.hwVCM = :hwVCM")
	public List<HWTaskList> findNeverCreateTaskList(@Param("hwVCM") HWVCM hwVCM);
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Modifying
	@Query("update HWTaskList u set u.taskId = :taskId where u.taskListId=:taskListId")
	public void updateTaskId(@Param("taskId") String taskId, @Param("taskListId") Integer taskListId);

}
