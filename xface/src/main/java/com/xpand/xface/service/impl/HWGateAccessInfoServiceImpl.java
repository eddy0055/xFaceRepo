package com.xpand.xface.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.HWGateAccessInfoDAO;
import com.xpand.xface.entity.HWGateAccessInfo;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.HWGateAccessInfoService;
import com.xpand.xface.service.SystemAuditService;

@Component
public class HWGateAccessInfoServiceImpl implements HWGateAccessInfoService {

	@Autowired
	HWGateAccessInfoDAO hwGateAccessInfoDAO;
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	SystemAuditService systemAuditService;
	@Override
	public HWGateAccessInfo findById(Long recordId, String className) {
		return this.hwGateAccessInfoDAO.findOne(recordId);
	}
	@Override
	public ResultStatus update(String transactionId, HWGateAccessInfo hwGateAccessInfo) {
		// TODO Auto-generated method stub
		HWGateAccessInfo hwGateAccessInfoTmp = this.hwGateAccessInfoDAO.findOne(hwGateAccessInfo.getRecordId());
		ResultStatus result = new ResultStatus();
		if (hwGateAccessInfoTmp==null) {
			this.hwGateAccessInfoDAO.save(hwGateAccessInfo);
		}else {
			result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "recordId:"+hwGateAccessInfo.getRecordId());
		}
		return result;
	}	
}
