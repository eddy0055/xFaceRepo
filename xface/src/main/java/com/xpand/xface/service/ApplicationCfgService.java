package com.xpand.xface.service;

import java.util.HashMap;
import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;

public interface ApplicationCfgService {
	public List<ApplicationCfg> getAll();
	public HashMap<String, ApplicationCfg> getAllInHashMap();
	public ApplicationCfg findByAppKey(String appKey);
	public ResultStatus update(ApplicationCfg applicationCfg);	
	
	//cache management
	public void removeCfgFromCacheByKey(String appKey);
	public void purgeCache();	
}
