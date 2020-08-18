package com.xpand.xface.service;

import java.util.List;

import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.Boat;

public interface BoatService extends CacheManageService {
	public Boat findById(String transactionId, Integer boatId);
	public Boat findByShortName(String transactionId, String boatShortName);
	public List<Boat> findAll(String transactionId);
	public List<Boat> removeSomeObject(String transactionId, List<Boat> boatList);
	public TablePage getBoatInfoList(String id, PaginationCriteria pc);
	public ResultStatus update(String transactionId, String logonUserName, Boat boatInfo);
	public ResultStatus delete(String transactionId, String logonUserName, String boatCode);
	public Boat findByBoatCode(String transactionId, String boatCode);
	public TablePage getBoatInfo(String transactionId, PaginationCriteria pc);		
}
