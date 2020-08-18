package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.HWIPCAnalyzeListDAO;
import com.xpand.xface.entity.HWIPCAnalyzeList;
import com.xpand.xface.service.HWIPCAnalyzeListService;

@Component
public class HWIPCAnalyzeListServiceImpl implements HWIPCAnalyzeListService{
	@Autowired
	HWIPCAnalyzeListDAO hwIPCAnalyzeListDAO;
	@Override
	public HWIPCAnalyzeList findById(Integer ipcanalId, String className) {
		return this.clearSomeObject(className, this.hwIPCAnalyzeListDAO.findOne(ipcanalId));
	}

	@Override
	public List<HWIPCAnalyzeList> findAll(String className) {		
		return this.clearSomeObject(className, this.hwIPCAnalyzeListDAO.findAll());
	}

	@Override
	public HWIPCAnalyzeList findBySuspectId(String suspectId, String className) {
		return this.clearSomeObject(className, this.hwIPCAnalyzeListDAO.findBySuspectId(suspectId));
	}
	
	public List<HWIPCAnalyzeList> clearSomeObject(String className, List<HWIPCAnalyzeList> hwIPCAnalyzeListList) {
		if (hwIPCAnalyzeListList==null) {
			return null;
		}		
		for (int i=0;i<hwIPCAnalyzeListList.size();i++) {
			hwIPCAnalyzeListList.get(i).setHwIPCs(null);
			hwIPCAnalyzeListList.get(i).setHwVCM(null);					
		}
		return hwIPCAnalyzeListList;
	}	

	public HWIPCAnalyzeList clearSomeObject(String className, HWIPCAnalyzeList hwIPCAnalyzeList) {
		if (hwIPCAnalyzeList==null) {
			return null;
		}				
		hwIPCAnalyzeList.setHwIPCs(null);
		hwIPCAnalyzeList.setHwVCM(null);							
		return hwIPCAnalyzeList;
	}

	@Override
	public ResultStatus updateByService(HWIPCAnalyzeList hwIPCAnalyzeList) {
		this.hwIPCAnalyzeListDAO.save(hwIPCAnalyzeList);
		return new ResultStatus();
	}
}
