package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.entity.IPCGroup;

public interface IPCGroupService {
	public List<IPCGroup> findAll();	
	public IPCGroup findById(int ipcgId);
}
