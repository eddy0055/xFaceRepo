package com.xpand.xface.service;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWGateAccessInfo;

public interface HWGateAccessInfoService {
	public HWGateAccessInfo findById(Long recordId, String className);
	public ResultStatus update(String transactionId, HWGateAccessInfo hwGateAccessInfo);
}
