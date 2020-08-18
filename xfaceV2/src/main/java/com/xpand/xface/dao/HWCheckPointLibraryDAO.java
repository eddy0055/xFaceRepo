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

import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWVCM;

@Repository
public interface HWCheckPointLibraryDAO extends JpaRepository<HWCheckPointLibrary, Integer>{	
	@Query("SELECT h FROM HWCheckPointLibrary h WHERE (h.libraryId IS NULL OR h.libraryId='') AND h.hwVCM = :hwVCM")
	public List<HWCheckPointLibrary> findNeverCreateLibrary(@Param("hwVCM") HWVCM hwVCM);
	@Query("SELECT h FROM HWCheckPointLibrary h WHERE (h.checkPointId IS NULL OR h.checkPointId='') AND h.hwVCM = :hwVCM")
	public List<HWCheckPointLibrary> findNeverCreateCheckPoint(@Param("hwVCM") HWVCM hwVCM);
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Modifying
	@Query("update HWCheckPointLibrary u set u.libraryId = :libraryId where u.chkponlibId=:chkponlibId")
	public void updateLibraryId(@Param("libraryId") String libraryId, @Param("chkponlibId") Integer chkponlibId);
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Modifying
	@Query("update HWCheckPointLibrary u set u.checkPointId = :checkPointId where u.chkponlibId=:chkponlibId")
	public void updateCheckPointId(@Param("checkPointId") String checkPointId, @Param("chkponlibId") Integer chkponlibId);
	
	@Query("SELECT h FROM HWCheckPointLibrary h ORDER BY h.chkponlibId ASC")
	public List<HWCheckPointLibrary> findAllByOrderBychkponlibIdAsc();
	
}
