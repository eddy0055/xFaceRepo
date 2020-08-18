package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.entity.HWGateInfo;

public interface HWGateInfoService extends CacheManageService {
	public HWGateInfo findById(String transactionId, Integer gateId);
	public HWGateInfo findByGateCode(String transactionId, String gateCode);
	public HWGateInfo findByGateName(String transactionId, String gateName);
	public List<HWGateInfo> findAll(String transactionId);
	public List<HWGateInfo> removeSomeObject(String transactionId, List<HWGateInfo> hwGateInfoList);
	public List<HWGateInfo> findByGateCodeList(String transactionId, List<String> gateCodeList);
	public List<HWGateInfo> findByGateCodeList(String transactionId, String gateCodeList);	
	
}
