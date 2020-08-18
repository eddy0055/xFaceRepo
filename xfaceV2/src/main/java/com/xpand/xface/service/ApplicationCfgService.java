package com.xpand.xface.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.ApplicationCfg;

public interface ApplicationCfgService extends CacheManageService{
	public List<ApplicationCfg> getAll(String transactionId);
	public HashMap<String, ApplicationCfg> getAllInHashMap(String transactionId);
	public ApplicationCfg findByAppKey(String transactionId, String appKey);
	public TablePage getApplicationCfgInfoList(String id, PaginationCriteria treq);
	public ResultStatus update(String transactionId, String logonUserName, ApplicationCfg applicationCfgInfo, MultipartFile image);			
}
