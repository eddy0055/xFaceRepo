package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWIPC;

public interface HWIPCService {
	public List<HWIPC> findAll();	
	public Page<HWIPC> getHWIPCList(Pageable pageable);
	public HWIPC findByIpcCode(String ipcCode);	
	
	//operation
	public ResultStatus update(HWIPC hwIPC);
	public ResultStatus delete(String ipcCode);	
	
	public void removeFromCacheByIPCCode(String ipcCode);
	public void purgeCache();
}
