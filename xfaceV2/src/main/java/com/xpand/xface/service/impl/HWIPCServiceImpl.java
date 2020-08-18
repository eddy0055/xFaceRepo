package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.dao.HWIPCDAO;
import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.service.HWGateInfoService;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.LogUtil;

@SessionScope
@Component
public class HWIPCServiceImpl implements HWIPCService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	HWIPCDAO hwIPCDAO;
	@Autowired
	HWGateInfoService hwGateInfoService; 
	
	@Override
	public List<HWIPC> findAll(String transactionId) {
		// TODO Auto-generated method stub
		return this.hwIPCDAO.findAll();
	}

	@Override
	public Page<HWIPC> getHWIPCList(String transactionId, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Cacheable(value=CacheName.CACHE_HWIPC, key="'key_'+#ipcCode")
	public HWIPC findByIpcCode(String transactionId, String ipcCode) {
		return this.hwIPCDAO.findByIpcCode(ipcCode);
	}

	@Override
	public ResultStatus update(String transactionId, HWIPC hwIPC) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwIPCDAO.save(hwIPC);			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update camera name:"+hwIPC.getIpcName()+" code:"+hwIPC.getIpcCode()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;		
	}

	@Override
	public ResultStatus delete(String transactionId, String ipcCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HWIPC> removeSomeObject(String transactionId, List<HWIPC> hwIPCList) {
		// TODO Auto-generated method stub
		for (HWIPC hwIPC : hwIPCList) {
			hwIPC.setHwAlarmHist(null);
			hwIPC.setHwCheckPointLibrary(null);
			hwIPC.setHwGateInfo(null);
			hwIPC.setHwVCM(null);			
			hwIPC.setLocationMap(null);			
			hwIPC.getEquipmentDirection().setHWGateAccessInfoList(null);
			hwIPC.getEquipmentDirection().setHwIPCList(null);
		}
		return hwIPCList;
	}

	@Override
	public List<HWIPC> getHWIPCByGate(String transactionId, WebFEParam webFEParam) {
		List<HWGateInfo> hwGateInfoList = this.hwGateInfoService.findByGateCodeList(transactionId, webFEParam.getGateInfoCodeList());
		if (hwGateInfoList==null) {
			return null;
		}else {
			return this.hwIPCDAO.findByhwGateInfoIn(hwGateInfoList);
		}		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_HWIPC, key="'key_'+#ipcName")
	public HWIPC findByIpcName(String transactionId, String ipcName) {
		return this.hwIPCDAO.findByIpcName(ipcName);
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_ALL'")
	public List<HWIPC> getHWIPCWOGate(String transactionId) {
		// TODO Auto-generated method stub
		List<HWIPC> hwIPCList = this.hwIPCDAO.findAll();
		List<HWIPC> hwIPCReturn = new ArrayList<>();
		for (HWIPC hwIPC: hwIPCList) {
			if (hwIPC.getHwGateInfo()==null) {
				hwIPC.setHwAlarmHist(null);
				hwIPC.setHwCheckPointLibrary(null);
				hwIPC.setHwGateInfo(null);
				hwIPC.setHwVCM(null);							
				hwIPC.getEquipmentDirection().setHWGateAccessInfoList(null);
				hwIPC.getEquipmentDirection().setHwIPCList(null);				
				hwIPC.getLocationMap().setHwGateInfoList(null);
				hwIPC.getLocationMap().setHwIPCList(null);
				hwIPCReturn.add(hwIPC);				
			}
		}
		return hwIPCReturn;
	}
}
