package com.xpand.xface.service;

import com.xpand.xface.entity.SystemAudit;

public interface SystemAuditService {
	public void createAudit(String transactionId, SystemAudit sysAudit);
	public void createAudit(String transactionId, String module, String subModule, String description, String result, String userName);
}
