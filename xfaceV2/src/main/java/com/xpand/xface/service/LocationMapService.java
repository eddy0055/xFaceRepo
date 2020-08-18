package com.xpand.xface.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.LocationMap;

public interface LocationMapService  extends CacheManageService {
	public List<LocationMap> findAll(String transactionId);	
	public TablePage getLocationMapList(String transactionId, PaginationCriteria pc);
	public List<LocationMap> removeSomeObject(String transactionId, List<LocationMap> locationMapList);
	public LocationMap findByMapId(String transactionId, Integer mapId);
	public LocationMap findByMapCode(String transactionId, String mapCode);
	public ResultStatus update(String transactionId, String logonUserName, LocationMap mapInfo, MultipartFile mapPhoto);
	public ResultStatus delete(String transactionId, String logonUserName, String mapCode);
	
}
