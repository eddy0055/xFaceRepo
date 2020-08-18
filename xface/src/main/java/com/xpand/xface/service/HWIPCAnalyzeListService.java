package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWIPCAnalyzeList;

public interface HWIPCAnalyzeListService {
	public HWIPCAnalyzeList findById(Integer ipcanalId, String className);	
	public HWIPCAnalyzeList findBySuspectId(String suspectId, String className);
	public List<HWIPCAnalyzeList> findAll(String className);
	public ResultStatus updateByService(HWIPCAnalyzeList hwIPCAnalyzeList);
}
