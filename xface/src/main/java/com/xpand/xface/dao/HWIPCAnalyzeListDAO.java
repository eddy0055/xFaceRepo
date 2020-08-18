package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.HWIPCAnalyzeList;

@Repository
public interface HWIPCAnalyzeListDAO extends JpaRepository<HWIPCAnalyzeList, Integer>{
	public HWIPCAnalyzeList findBySuspectId(String suspectId);
}
