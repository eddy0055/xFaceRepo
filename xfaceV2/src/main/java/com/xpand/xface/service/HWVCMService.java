package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.entity.HWVCM;

public interface HWVCMService  extends CacheManageService {
	public HWVCM findByVcmId(String transactionId, Integer vcmId);
	public List<HWVCM> getAll(String transactionId);
}
