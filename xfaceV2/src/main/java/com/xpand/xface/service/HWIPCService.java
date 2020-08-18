
package com.xpand.xface.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.entity.HWIPC;

public interface HWIPCService  extends CacheManageService {
	public List<HWIPC> findAll(String transactionId);	
	public Page<HWIPC> getHWIPCList(String transactionId, Pageable pageable);
	public HWIPC findByIpcCode(String transactionId, String ipcCode);
	public HWIPC findByIpcName(String transactionId, String ipcName);
	public List<HWIPC> removeSomeObject(String transactionId, List<HWIPC> hwIPCList);
	public List<HWIPC> getHWIPCByGate(String transactionId, WebFEParam webFEParam);
	
	//operation
	public ResultStatus update(String transactionId, HWIPC hwIPC);
	public ResultStatus delete(String transactionId, String ipcCode);
	public List<HWIPC> getHWIPCWOGate(String transactionId);
	
}
