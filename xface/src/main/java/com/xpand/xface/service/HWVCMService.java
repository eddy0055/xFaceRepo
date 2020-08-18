package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.entity.HWVCM;

public interface HWVCMService {
	public HWVCM findByVcmId(Integer vcmId);
	public List<HWVCM> getAll();
}
