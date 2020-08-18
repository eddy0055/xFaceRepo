package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.dao.HWGateInfoDAO;
import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.service.HWGateInfoService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class HWGateInfoServiceImpl implements HWGateInfoService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	HWGateInfoDAO hwGateInfoDAO;
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'gateinfo_'+#gateId")
	public HWGateInfo findById(String transactionId, Integer gateId) {
		return this.hwGateInfoDAO.findOne(gateId);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'gateinfo_'+#gateCode")
	public HWGateInfo findByGateCode(String transactionId, String gateCode) {		
		return this.hwGateInfoDAO.findByGateCode(gateCode);
	}
	@Override
	public List<HWGateInfo> findAll(String transactionId) {
		// TODO Auto-generated method stub
		return this.hwGateInfoDAO.findAll();
	}
	@Override
	public List<HWGateInfo> removeSomeObject(String transactionId, List<HWGateInfo> hwGateInfoList) {
		for (HWGateInfo hwGate : hwGateInfoList) {
			hwGate.setHWGateAccessInfoList(null);
			hwGate.setHwIPCList(null);
			hwGate.setBoatScheduleList(null);			
			hwGate.getLocationMap().setHwGateInfoList(null);
			hwGate.getLocationMap().setHwIPCList(null);
		}
		return hwGateInfoList;
	}
	@Override
	public List<HWGateInfo> findByGateCodeList(String transactionId, List<String> gateCodeList) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<HWGateInfo> findByGateCodeList(String transactionId, String gateCodeList) {
		List<HWGateInfo> hwGateInfoList = null;
		List<String> arrayGateCodeList = null;
		if (!StringUtil.checkNull(gateCodeList)) {
			String arrayGateCode[] = gateCodeList.split(ConstUtil.STRING_ID_DELIMINATOR);
			arrayGateCodeList = new ArrayList<>();
			for(String gateCode: arrayGateCode) {
				if (!StringUtil.checkNull(gateCode)) {
					arrayGateCodeList.add(gateCode);
				}
			}
			if (arrayGateCodeList.size()>0) {
				hwGateInfoList = this.hwGateInfoDAO.findByGateCodeIn(arrayGateCodeList);
			}
		}
		return hwGateInfoList;
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'gateinfo_'+#gateName")
	public HWGateInfo findByGateName(String transactionId, String gateName) {
		// TODO Auto-generated method stub
		return this.hwGateInfoDAO.findByGateName(gateName);
	}		
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'gateinfo_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'gateinfo_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
		
	}
}
