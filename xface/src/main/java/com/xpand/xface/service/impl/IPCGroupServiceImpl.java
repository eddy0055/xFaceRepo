
package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.dao.IPCGroupDAO;
import com.xpand.xface.entity.IPCGroup;
import com.xpand.xface.service.IPCGroupService;
import com.xpand.xface.util.LogUtil;

@Component
public class IPCGroupServiceImpl implements IPCGroupService{
	
	@Autowired
	IPCGroupDAO ipcGroupDAO;
	@Override
	public List<IPCGroup> findAll() {
		// TODO Auto-generated method stub
		return this.ipcGroupDAO.findAll();
	}
	@Override
	public IPCGroup findById(int ipcgId) {
		String transactionId = LogUtil.getWebSessionId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "search ipcgroup by id "+ipcgId));
		IPCGroup result = this.ipcGroupDAO.findOne(ipcgId);
		if (result==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "search ipcgroup by id "+ipcgId+" not found"));
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "search ipcgroup by id "+ipcgId+" found group name "+result.getGroupName()));
		}
		return result;
	}
	
}
