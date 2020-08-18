package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.HWIPCDAO;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.util.CacheName;

@Component
public class HWIPCServiceImpl implements HWIPCService{

	@Autowired
	HWIPCDAO hwIPCDAO;
	
	@Override
	public List<HWIPC> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<HWIPC> getHWIPCList(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Cacheable(value=CacheName.CACHE_HWIPC, key="#root.methodName+'_'+#ipcCode")
	public HWIPC findByIpcCode(String ipcCode) {
		return this.hwIPCDAO.findByIpcCode(ipcCode);
	}

	@Override
	public ResultStatus update(HWIPC hwIPC) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultStatus delete(String ipcCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, key="#root.methodName+'_'+#ipcCode")
	public void removeFromCacheByIPCCode(String ipcCode) {
		//remove cache
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, allEntries=true)
	public void purgeCache() {
		//clear cache
	}

}
