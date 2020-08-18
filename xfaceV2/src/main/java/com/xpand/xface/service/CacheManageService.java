package com.xpand.xface.service;

public interface CacheManageService {
	//cache management
	public void removeCacheByKey(String cacheKey);
	public void removeCacheById(Integer cacheId);
	public void purgeCache();
	
}
