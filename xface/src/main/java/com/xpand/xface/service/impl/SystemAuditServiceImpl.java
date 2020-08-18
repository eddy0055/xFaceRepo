package com.xpand.xface.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xpand.xface.dao.SystemAuditDAO;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.SystemAuditService;

@Component
public class SystemAuditServiceImpl implements SystemAuditService{
	
	@Autowired
	SystemAuditDAO sysAuditDAO;
		
	@Override
	public void createAudit(String transactionId, SystemAudit sysAudit){
		if (sysAudit.getDescription()!=null && sysAudit.getDescription().length()>SystemAudit.SIZE_OF_DESC) {
			sysAudit.setDescription(sysAudit.getDescription().substring(0, SystemAudit.SIZE_OF_DESC-10)+" **cut**");
		}
		this.sysAuditDAO.save(sysAudit);		
	}

	@Override
	public void createAudit(String transactionId, String module, String subModule, String description, String result, String userName){
		SystemAudit audit = new SystemAudit();
		audit.setModuleName(module);
		audit.setSubModuleName(subModule);
		audit.setDescription(description);
		if (description!=null && description.length()>SystemAudit.SIZE_OF_DESC) {
			description = description.substring(0, SystemAudit.SIZE_OF_DESC-10)+" **cut**";
		}
		audit.setResult(result);		
		this.sysAuditDAO.save(audit);
	}	
}
