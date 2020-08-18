package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.bean.PersonRegisterResult;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.dao.BoatDAO;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.BoatService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.service.hwapi.HWAPISessionService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;
import com.jcabi.log.Logger;


@SessionScope
@Component
public class BoatServiceImpl implements BoatService, Serializable{	
	private static final long serialVersionUID = 1L;
	@Autowired
	BoatDAO boatDAO;
	@Autowired 
	SystemAuditService systemAuditService;
	@Autowired
	HWAPISessionService hwAPIService;
	@Autowired
	XFaceBatchService xFaceBatchService;
	
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'boat_'+#boatId")
	public Boat findById(String transactionId, Integer boatId) {
		return this.boatDAO.findOne(boatId);
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'boat_'+#boatCode")
	public Boat findByBoatCode(String transactionId, String boatCode) {
		return this.boatDAO.findByBoatCode(boatCode);
	}
	@Override
	public List<Boat> findAll(String transactionId) {
		return this.boatDAO.findNormalBoat(Boat.BOAT_NONE_CODE);
	}
	@Override
	public List<Boat> removeSomeObject(String transactionId, List<Boat> boatList) {
		for (Boat boat : boatList) {
			boat.setBoatScheduleList(null);			
		}
		return boatList;
	}	
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub		
	}
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'boat_'+#boatShortName")
	public Boat findByShortName(String transactionId, String boatShortName) {
		return this.boatDAO.findByBoatShortName(boatShortName);
	}
	//TablePage
	@Override
	public TablePage getBoatInfo(String transactionId, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in get BoatInfo"));			
		TablePage page = new TablePage(transactionId, this.boatDAO, pc);
		Logger.info(this, LogUtil.getLogInfo(transactionId, "out get BoatInfo"));		
		return page;
	}

	//Update Boat
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
		evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#boatId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#boatCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#boatShortName")
		}
	)
	public ResultStatus update(String transactionId, String logonUserName, Boat boatInfo) {		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update Boat start"));
		Logger.debug(this,LogUtil.getLogDebug(transactionId, "BoatCode Impl::" + boatInfo.getBoatCode()));
		Logger.debug(this,LogUtil.getLogDebug(transactionId, "ActionCommand Impl :" + boatInfo.getActionCommand()));
		Logger.debug(this, LogUtil.getLogInfo(transactionId, "BoatName Impl :" + boatInfo.getBoatName()));
		//Status success
		ResultStatus result = new ResultStatus();
		
		if (StringUtil.checkNull(boatInfo.getBoatCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "boatCode not null");
		}else if (StringUtil.checkNull(boatInfo.getBoatName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "boatName not null");
		}else if (StringUtil.checkNull(boatInfo.getBoatShortName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "boatShortName not null");
		}else if (StringUtil.checkNull(boatInfo.getCardNo())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "cardNo not null");
		}else if (StringUtil.checkNull(boatInfo.getZkPin())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "ZkPin not null");
		}
		
		if(!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update Boat fail with result" + result.toString()));
			return result;
		}
		
		String oldValue = null;
		Boat existingBoatInfo = this.boatDAO.findByBoatCode(boatInfo.getBoatCode());
		if (boatInfo.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingBoatInfo!=null) {
			//error boatInfo already exist				
			result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "boatCode"+ boatInfo.getBoatCode());
			}
		}else if (existingBoatInfo == null) { 
			//error cannot find boatCode for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "boatCode :"+ boatInfo.getBoatCode());
		}
		if(!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update Boat fail with result" + result.toString()));
			return result;
		}
		
		if (existingBoatInfo == null) {
			//create boatInfo
			Logger.info(this, LogUtil.getLogInfo(transactionId, "boatCode : "+boatInfo.getBoatCode()+" not found in db then create"));	
			boatInfo.setUserCreated(logonUserName);
			boatInfo.setUserUpdated(logonUserName);
			//create data for ZK
			PersonRegisterResult personRegister = new PersonRegisterResult();
			personRegister.setCertificateNo(boatInfo.getZkPin());
			personRegister.setFullName(boatInfo.getBoatName());
			personRegister.setCardNo(boatInfo.getCardNo());
			//call initialClass hwAPIService 
			this.hwAPIService.initialClass(transactionId);
			//call hwAPIService addPersonZK
			ResultStatus zkResult = this.hwAPIService.addPersonZK(transactionId, personRegister).getResult();
		
			if(zkResult.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.createToDB(boatInfo);
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create BoatName By :"+logonUserName));					
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create BoatInfo : "+boatInfo.getBoatName() +" By ["+logonUserName+"] success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL, "create boatInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName); 
			}else{
				result.setStatusCode(ResultStatus.FAIL_TO_ADD_BOAT_TO_ZK_DESC, "Fail to consume web service ::",boatInfo.getBoatCode());
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create BaotName By: "+ boatInfo.getBoatName()+"["+boatInfo.getBoatName()+"] to ZK fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
		}else{				
			//update boatInfo
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found BoatName "+boatInfo.getBoatName()+" then update"));
			boatInfo.setBoatId(existingBoatInfo.getBoatId());
			boatInfo.setUserUpdated(logonUserName);
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update BoatId "+boatInfo.getBoatId()));
			//create data for ZKBIOSecurity
			PersonRegisterResult personRegister = new PersonRegisterResult();
			personRegister.setCertificateNo(boatInfo.getZkPin());
			personRegister.setFullName(boatInfo.getBoatName());
			personRegister.setCardNo(boatInfo.getCardNo());
			//call initialClass hwAPIService ZKBIOSecurity
			this.hwAPIService.initialClass(transactionId);
			//call hwAPIService addPersonZK  
			ResultStatus zkResult = this.hwAPIService.addPersonZK(transactionId, personRegister).getResult();
			Logger.info(this,LogUtil.getLogInfo(transactionId +"_"+ zkResult , "out executeAddVCM_ZK"));
			
			if(zkResult.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.updateToDB(boatInfo);
				oldValue = existingBoatInfo.toString();
				String newValue = boatInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "update boatInfo oldValue:"+oldValue+", newValue:"+newValue));
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update personNationality "+boatInfo.getBoatName()+" success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL
						, "update boatInfo oldValue:"+oldValue+", newValue:"+newValue, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				result.setStatusCode(ResultStatus.FAIL_TO_ADD_BOAT_TO_ZK_DESC, "Fail to consume web service ::",boatInfo.getBoatCode());
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update BaotName By: "+ boatInfo.getBoatName()+"["+boatInfo.getBoatName()+"] to ZK fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create or update boatInfo done with result "+result.toString()));
		return result;
	}		
	
	public Boat createToDB(Boat boatInfo) {
		this.boatDAO.save(boatInfo);
		return boatInfo;
	}
	public Boat updateToDB(Boat boatInfo) {	
		this.boatDAO.save(boatInfo);
		return boatInfo;
	}
	
	//Delete BoatInfo
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#boatId"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#boatCode"),
				@CacheEvict(value=CacheName.CACHE_OTHER, key="'boat_'+#boatShortName")
			}
	)
	public ResultStatus delete(String transactionId, String logonUserName, String boatCode) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete boat code start"));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete find boat code:: " + boatCode));
		ResultStatus result = new ResultStatus();
		Boat boatInfo = this.boatDAO.findByBoatCode(boatCode);
		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "Boat find boatCode:: " + boatCode));
		//set pin for sent to ZKBIOSecurity
		String zkPin = boatInfo.getZkPin();
		//call initialClass hwAPIService ZKBIOSecurity
		this.hwAPIService.initialClass(transactionId);
		//call hwAPIService remove PersonZK   
		ResultStatus zkResult = this.hwAPIService.removePersonZK(transactionId, zkPin).getResult();
		Logger.info(this,LogUtil.getLogInfo(transactionId +"_"+ zkResult , "out executeAddVCM_ZK"));
		if(boatInfo.getBoatCode().equals(null)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "boat code :"+boatCode+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "boat code :"+boatCode);
		}
		
		if(zkResult.getStatusCode().equals(ResultStatus.SUCCESS_CODE)){ 			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found boatCode "+boatInfo.getBoatCode()+"["+ boatInfo.getBoatId()+"] then delete"));
			this.boatDAO.delete(boatInfo.getBoatId());
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete boatCode :"+ boatInfo.getBoatCode()+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete boatCode "+ boatInfo.getBoatCode()+"["+ boatInfo.getBoatCode() +"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL
					, "delete boatInfo :"+boatCode+" by "+logonUserName, SystemAudit.RES_SUCCESS, logonUserName);
		}else {
			result.setStatusCode(ResultStatus.FAIL_TO_ADD_BOAT_TO_ZK_DESC, "Connect ZK Fail to consume web service ::",boatInfo.getBoatCode());
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete Boat fail "));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL
					, "delete boatInfo :"+boatCode+" by "+logonUserName, SystemAudit.RES_FAIL, logonUserName);
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete boatCode done with result "+result.toString()));
		return result;	
	}
	
	@Override
	public TablePage getBoatInfoList(String id, PaginationCriteria pc) {
		// TODO Auto-generated method stub
		return null;
	}	
}
