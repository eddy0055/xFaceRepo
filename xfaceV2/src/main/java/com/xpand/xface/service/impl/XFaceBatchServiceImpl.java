package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.bean.PassengerBoatActivity;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.landing.DailyAlarmInfoField;
import com.xpand.xface.bean.landing.DailyAlarmInfoList;
import com.xpand.xface.bean.landing.DailyBoatInfoField;
import com.xpand.xface.bean.landing.DailyBoatInfoFieldSort;
import com.xpand.xface.bean.landing.DailyBoatInfoList;
import com.xpand.xface.bean.landing.DailyGateInfoField;
import com.xpand.xface.bean.landing.DailyGateInfoFieldSort;
import com.xpand.xface.bean.landing.DailyGateInfoList;
import com.xpand.xface.bean.landing.DailyIPCInfoList;
import com.xpand.xface.bean.landing.DailyIPCStatusInfoField;
import com.xpand.xface.bean.landing.DailyIPCStatusInfoList;
import com.xpand.xface.bean.landing.LandingPageInfo;
import com.xpand.xface.bean.report.google.DailyFaceChartField;
import com.xpand.xface.dao.ApplicationCfgDAO;
import com.xpand.xface.dao.BoatDAO;
import com.xpand.xface.dao.BoatScheduleDAO;
import com.xpand.xface.dao.EquipmentDirectionDAO;
import com.xpand.xface.dao.HWAlarmHistDAO;
import com.xpand.xface.dao.HWCheckPointLibraryDAO;
import com.xpand.xface.dao.HWGateAccessInfoDAO;
import com.xpand.xface.dao.HWGateInfoDAO;
import com.xpand.xface.dao.HWIPCDAO;
import com.xpand.xface.dao.HWTaskListDAO;
import com.xpand.xface.dao.HWVCMDAO;
import com.xpand.xface.dao.PersonCategoryDAO;
import com.xpand.xface.dao.PersonCertificateDAO;
import com.xpand.xface.dao.PersonInfoDAO;
import com.xpand.xface.dao.PersonNationalityDAO;
import com.xpand.xface.dao.PersonTitleDAO;
import com.xpand.xface.dao.SystemAuditDAO;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.BoatSchedule;
import com.xpand.xface.entity.EquipmentDirection;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWGateAccessInfo;
import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertificate;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.entity.PersonRegisterDate;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.exception.DoQueryErrorException;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;

@ApplicationScope
@Component
public class XFaceBatchServiceImpl implements XFaceBatchService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	EquipmentDirectionDAO equipmentDirectionDAO;
	@Autowired
	HWGateAccessInfoDAO hwGateAccessInfoDAO;
	@Autowired
	HWGateInfoDAO hwGateInfoDAO;
	@Autowired
	ApplicationCfgDAO applicationCfgDAO;
	@Autowired
	HWAlarmHistDAO hwAlarmHistDAO;
	@Autowired
	HWIPCDAO hwIPCDAO;
	@Autowired
	HWCheckPointLibraryDAO hwCheckPointLibraryDAO;
	@Autowired
	HWTaskListDAO hwTaskListDAO;
	@Autowired
	HWVCMDAO hwVCMDAO;
	@Autowired
	PersonTitleDAO personTitleDAO;
	@Autowired
	PersonCertificateDAO personCertificateDAO;
	@Autowired
	PersonNationalityDAO personNationalityDAO;
	@Autowired
	PersonCategoryDAO personCategoryDAO;
	@Autowired
	PersonInfoDAO personInfoDAO;
	@Autowired
	SystemAuditDAO sysAuditDAO;
	@Autowired
	BoatDAO boatDAO;
	@Autowired
	BoatScheduleDAO boatScheduleDAO;
	/*
	 * update gateAccessInfo once door is IN-OUT (require cache)
	 * 
	 */
	@Override
	public ResultStatus updateHWGateAccessInfo(String transactionId, PassengerBoatActivity passengerBoatActivity) {
		ResultStatus result = new ResultStatus();
		HWGateAccessInfo hwGateAccessInfo = new HWGateAccessInfo();
		hwGateAccessInfo.setCardNo(passengerBoatActivity.getBoatDriverCardId());
		if (PassengerBoatActivity.ACTIVITY_BOAT_DIRECTION_IN.equals(passengerBoatActivity.getDirection())) {
			hwGateAccessInfo.setEquipmentDirection(this.equipmentDirectionDAO.findByDirectionCode(EquipmentDirection.DIRECTION_IN));
		}else {
			hwGateAccessInfo.setEquipmentDirection(this.equipmentDirectionDAO.findByDirectionCode(EquipmentDirection.DIRECTION_OUT));
		}
		hwGateAccessInfo.setEventTime(StringUtil.stringToDate(passengerBoatActivity.getEventDT(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));
		hwGateAccessInfo.setHwGateInfo(this.hwGateInfoDAO.findByGateCode(passengerBoatActivity.getGateId()));
		hwGateAccessInfo.setPersonInfo(this.personInfoDAO.findByCertificateNo(passengerBoatActivity.getPassengerCertId()));
		try {
			this.hwGateAccessInfoDAO.save(hwGateAccessInfo);
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update hwGateAccessInfo error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}

	/*
	 * get config from database
	 * 
	 */
	@Override
	@Cacheable(value=CacheName.CACHE_APPCFG, key="'key_'+#appKey")
	public ApplicationCfg findACByAppKey(String transactionId, String appKey) {
		Logger.debug(this, LogUtil.getLogDebug("transactionId", "get app config with key "+appKey));		
		return this.applicationCfgDAO.findOne(appKey);
	}	
	///////////////////////////////
	//app cfg
	@Override
	@CacheEvict(value=CacheName.CACHE_APPCFG, allEntries=true)
	public void purgeACCache() {
		//clear cache
	}
	/*
	 * get all config into hashmap format
	 * 
	 */
	@Override
	@Cacheable(value=CacheName.CACHE_APPCFG, key="'key_all'")
	public HashMap<String, ApplicationCfg> getAllACHashMap(String transactionId) {
		List<ApplicationCfg> appCfgList = this.applicationCfgDAO.findAll();
		HashMap<String, ApplicationCfg> appCfgHash = new HashMap<String, ApplicationCfg>();
		for (ApplicationCfg appCfg:appCfgList) {
			appCfgHash.put(appCfg.getAppKey(), appCfg);
		}
		return appCfgHash;
	}
	
	/*
	 * update alarm history
	 * 
	 */
	@Override
	public ResultStatus updateAlarmHistory(String transactionId, HWAlarmHist hwAlarmHist) {								
		ResultStatus result = new ResultStatus();
		try {			
			this.hwAlarmHistDAO.save(hwAlarmHist);
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while hwAlarmHistory error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}
	
	/*
	 * get max alarm history group by person
	 * 
	 */
	@Override
	public List<LastAlarmPersonDateTime> findMaxAlarmHistoryGroupByPerson(Date startDate) {
		return this.hwAlarmHistDAO.findMaxDateCreatedGroupByPerson(startDate);
	}

	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="'key_'+#hwPeopleId")
	public PersonInfo findPersonByHwPeopleId(String transactionId, String hwPeopleId) {
		PersonInfo personInfo = this.personInfoDAO.findByHwPeopleId(hwPeopleId);
		return personInfo;
	}
	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="'key_'+#certificateNo")
	public PersonInfo findPersonByCertificateNo(String transactionId, String certificateNo) {
		PersonInfo personInfo = this.personInfoDAO.findByCertificateNo(certificateNo);
		return personInfo;
	}
	
	//HWIPC
	@Override
	@Cacheable(value=CacheName.CACHE_HWIPC, key="'key_'+#ipcCode")
	public HWIPC findIPCByCode(String ipcCode) {
		return this.hwIPCDAO.findByIpcCode(ipcCode);
	}
	
	@Override
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcCode"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcName"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_ALL'")
		}
	)
	public ResultStatus updateIPC(String transactionId, HWIPC hwIPC) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwIPCDAO.save(hwIPC);			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update camera name:"+hwIPC.getIpcName()+" code:"+hwIPC.getIpcCode()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}
	
	@Override
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcCode"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcName"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_ALL'")
		}
	)
	public ResultStatus updateIPCStatus(String transactionId, HWIPC hwIPC) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwIPCDAO.updateStatus(hwIPC.getIpcStatus(), hwIPC.getIpcId());			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update camera name:"+hwIPC.getIpcName()+" code:"+hwIPC.getIpcCode()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}
	
	@Override
	@CacheEvict(value=CacheName.CACHE_HWIPC, allEntries=true)
	public void purgeIPCCache() {
		
	}
	@Override
	public List<HWIPC> getIPCNeverCreateTaskList(HWVCM hwVCM) {
		return this.hwIPCDAO.findNeverCreateTaskList(hwVCM);
	}

	@Override
	public List<HWIPC> getIPCNeverAddToCheckPoint(HWVCM hwVCM) {
		return this.hwIPCDAO.findNeverAddToCheckPoint(hwVCM);
	}
	
	
	@Override	
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcCode"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcName"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_ALL'")
		}
	)
	public ResultStatus updateIPCTaskId(String transactionId, HWIPC hwIPC) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwIPCDAO.updateTaskId(hwIPC.getIpcTaskId(), hwIPC.getIpcId());			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update taskId:"+hwIPC.getIpcTaskId()+" id:"+hwIPC.getIpcId()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}
	
		
	@Override
	@Caching(
		evict = { 
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcCode"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_'+#hwIPC.ipcName"),
			@CacheEvict(value=CacheName.CACHE_HWIPC, key="'key_ALL'")
		}
	)
	public ResultStatus updateIPCCheckPointId(String transactionId, HWIPC hwIPC) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwIPCDAO.updateCheckPointId(hwIPC.getCheckPointId(), hwIPC.getIpcId());			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update checkPointId:"+hwIPC.getCheckPointId()+" id:"+hwIPC.getIpcId()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}	
	
	//checkpointlibrary
	@Override
	public HWCheckPointLibrary getCPLOneObject() {
		List<HWCheckPointLibrary> checkPointLib = this.hwCheckPointLibraryDAO.findAllByOrderBychkponlibIdAsc();
		if (checkPointLib.size()>0) {
			return checkPointLib.get(0);
		}else {
			return null;
		}
	}
	@Override
	public List<HWCheckPointLibrary> getCPLNeverCreateLibrary(HWVCM hwVCM) {
		return this.hwCheckPointLibraryDAO.findNeverCreateLibrary(hwVCM);
	}

	@Override
	public List<HWCheckPointLibrary> getCPLNeverCreateCheckPoint(HWVCM hwVCM) {
		return this.hwCheckPointLibraryDAO.findNeverCreateCheckPoint(hwVCM);
	}
	
	//not require to clear cache of check point library coz call on first time only
	@Override
	public ResultStatus updateCPLLibraryId(String transactionId, HWCheckPointLibrary hwCheckPointLibrary) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwCheckPointLibraryDAO.updateLibraryId(hwCheckPointLibrary.getLibraryId(), hwCheckPointLibrary.getChkponlibId());			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update libraryId:"+hwCheckPointLibrary.getLibraryId()+" id:"+hwCheckPointLibrary.getChkponlibId()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "hwCheckPointLibrary.libraryId:"+hwCheckPointLibrary.getLibraryId());
		}
		return result;
	}

	//not require to clear cache of coz call on first time only
	@Override
	public ResultStatus updateCPLCheckPointId(String transactionId, HWCheckPointLibrary hwCheckPointLibrary) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwCheckPointLibraryDAO.updateCheckPointId(hwCheckPointLibrary.getCheckPointId(), hwCheckPointLibrary.getChkponlibId());			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update checkPointId:"+hwCheckPointLibrary.getCheckPointId()+" id:"+hwCheckPointLibrary.getChkponlibId()+" error:"+ex.toString(), ex));			
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}		

	//tasklist
	@Override
	public List<HWTaskList> getTLNeverCreateTaskList(HWVCM hwVCM) {
		return this.hwTaskListDAO.findNeverCreateTaskList(hwVCM);
	}
	
	//not require to clear cache of coz call on first time only
	@Override
	public ResultStatus updateTLTaskId(String transactionId, HWTaskList hwTaskList) {
		ResultStatus result = new ResultStatus();
		try {
			this.hwTaskListDAO.updateTaskId(hwTaskList.getTaskId(), hwTaskList.getTaskListId());			
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update taskId:"+hwTaskList.getTaskId()+" id:"+hwTaskList.getTaskListId()+" error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}
	
	//vcm
	@Override
	public List<HWVCM> getVCMAll() {
		return this.hwVCMDAO.findAll();
	}

	@Override
	public List<Object[]> doQuery(Object source, String transactionId, String queryStmt, ArrayList<?> paramList) throws DoQueryErrorException {
		// TODO Auto-generated method stub
		return OtherUtil.doQuery(source, transactionId, this.entityManager, queryStmt, paramList);
	}
	
	//require to clear all cache of personInfo
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@CacheEvict(value=CacheName.CACHE_PERSONINFO, allEntries=true)
	public ResultStatus dbRemovePersonFromVCM_ZK(Object source, String transactionId, String personIdList) throws DoQueryErrorException {
		ResultStatus result = new ResultStatus();
		try {						
			StoredProcedureQuery storedProc = this.entityManager.createStoredProcedureQuery("removePersonFromVCM");
			storedProc.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter(2, Byte.class, ParameterMode.IN);			
			storedProc.setParameter(1, personIdList);
			storedProc.setParameter(2, PersonInfo.STATUS_REMOVE_FROM_VCM_ZK);
			storedProc.executeUpdate();			
		}catch (Exception ex) {			
			//result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "call store:updatePersonStatus:"+ex.toString());
			throw ex;
		}		
		return result;
	}
	
	//require to clear all cache of personInfo
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@CacheEvict(value=CacheName.CACHE_PERSONINFO, allEntries=true)
	public ResultStatus dbAddPersonToVCM_ZK(Object source, String transactionId, String personIdList, String hwPeopleIdList) throws DoQueryErrorException {
		ResultStatus result = new ResultStatus();
		try {						
			StoredProcedureQuery storedProc = this.entityManager.createStoredProcedureQuery("addPersonToVCM");
			storedProc.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter(3, Byte.class, ParameterMode.IN);			
			storedProc.setParameter(1, personIdList);
			storedProc.setParameter(2, hwPeopleIdList);
			storedProc.setParameter(3, PersonInfo.STATUS_UPDATE_TO_VCM_ZK);
			storedProc.executeUpdate();			
		}catch (Exception ex) {			
			//result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "call store:updatePersonStatus:"+ex.toString());
			throw ex;
		}		
		return result;
	}
	
	
	//require cache
	@Override	
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationalityCode")
	public PersonNationality findNationalityByCode(String transactionId, String nationalityCode) {		
		return this.personNationalityDAO.findByNationalityCode(nationalityCode);
	}

	//require to clear cache
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'pnat_'+#nationality.nationalityCode")
	public ResultStatus updateNationality(String transactionId, String logonUserName, PersonNationality nationality) {						
		ResultStatus result = new ResultStatus();
		try {
			this.personNationalityDAO.save(nationality);
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update person nationality error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}

	//require cache
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'ptit_'+#titleId")
	public PersonTitle findPersonTitleById(String transactionId, Integer titleId) {
		// TODO Auto-generated method stub
		return this.personTitleDAO.findOne(titleId);
	}

	//require cache
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcert_'+#certificateId")
	public PersonCertificate findPersonCertificateById(String transactionId, Integer certificateId) {
		return this.personCertificateDAO.findOne(certificateId);
	}

	//require cache
	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'pcate_'+#categoryId")
	public PersonCategory findPersonCategoryById(String transactionId, Integer categoryId) {
		return this.personCategoryDAO.findOne(categoryId);
	}

	
	@Override
	public PersonInfo resetPersonRegDateId(String transactionId, PersonInfo personInfo) {
		for (PersonRegisterDate personRegDate: personInfo.getPersonRegisterDateList()) {
			personRegDate.setPersonInfo(personInfo);			
		}
		return personInfo;		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#customerInfo.personId"),
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#customerInfo.certificateId"),
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#customerInfo.hwPeopleId")
			}
		)
	public ResultStatus updatePersonInfoToDB(String transactionId, PersonInfo personInfo, CustomerRegister customerInfo) {				
		ResultStatus result = new ResultStatus();
		try {
			this.personInfoDAO.save(personInfo);
		}catch (Exception ex) {
			Logger.error(this,LogUtil.getLogError(transactionId, "error while update personinfo error:"+ex.toString(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
		}
		return result;
	}
	
	@Override
	public PersonInfo addPersonRegDate(PersonInfo personInfo, Date regDate, String agentName, String logonUserName) {
		//check travel date already in register date or not
		Iterator<PersonRegisterDate> personRegDateList = personInfo.getPersonRegisterDateList().iterator();
		PersonRegisterDate personRegDate = null;
		boolean foundDate = false;
		while (personRegDateList.hasNext()) {
			personRegDate = personRegDateList.next();
			if (personRegDate.getRegisterDate().compareTo(regDate)==0) {
				personRegDate.setAgentName(agentName);				
				foundDate = true;
				break;
			}
		}
		if (!foundDate) {
			personRegDate = new PersonRegisterDate();
			personRegDate.setRegisterDate(regDate);
			personRegDate.setUserCreated(logonUserName);
			personRegDate.setAgentName(agentName);
			personInfo.getPersonRegisterDateList().add(personRegDate);
		}
		return personInfo;
	}

	@Override
	public void createSystemAudit(String transactionId, String module, String subModule, String description, String result, String userName){
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

	@Override
	public LandingPageInfo getLandingPageInfo(String transactionId) {
		LandingPageInfo landingPageInfo = new LandingPageInfo();
		//1. get list of VCN alarm
		landingPageInfo.setAlarmInfoList(this.getListOfVCNAlarm(transactionId));
		//2. get list of boat no passenger check in, no passenger check out
		landingPageInfo.setBoatInfoList(this.getListOfBoatInfo(transactionId));
		//3. get list gate no passenger in, no passenger out
		landingPageInfo.setGateInfoList(this.getListOfPassengerByGate(transactionId));
		//4. get list IPC no passenger match, no passenger unmatch 
		landingPageInfo.setIpcInfoList(this.getListOfFaceByIPC(transactionId));
		//5. get gateId , set color camera
		landingPageInfo.setIpcStatusInfoList(this.getListOfIPCStatus(transactionId));
		
		return landingPageInfo;
	}	
	// get top 10 list of VCN alarm
	private DailyAlarmInfoList getListOfVCNAlarm(String transactionId) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getListOfVCNAlarm"));
		ApplicationCfg appAlarmSize = this.findACByAppKey(transactionId, ApplicationCfg.KEY_LANDING_PAGE_VCN_ALARM_SIZE);
		String queryStmt = "SELECT ah.alarmh_id,ipc.ipc_name,DATE_FORMAT(ah.alarm_time,\"%H:%i\") AS alarm_time FROM tbl_alarm_hist ah INNER JOIN tbl_ipc ipc"
				+ "	ON ah.ipc_id=ipc.ipc_id WHERE ah.alarm_source=? AND ah.alarm_time BETWEEN ? AND ? ORDER BY alarm_time DESC LIMIT 0, ?";		
		List<Object[]> queryResultList = null;				
		String startDate = StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYY_MM_DD); //morning		
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		DailyAlarmInfoList alarmInfoList = new DailyAlarmInfoList();
		try {					
			paramList.add(HWAlarmHist.HW_VCN_ALARM);
			paramList.add(StringUtil.stringToDate(startDate, StringUtil.DATE_FORMAT_YYYY_MM_DD));
			paramList.add(StringUtil.stringToDate(startDate+ " 23:59:59", StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
			paramList.add(StringUtil.stringToInteger(appAlarmSize.getAppValue1(), 20));
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					alarmInfoList.addList(new DailyAlarmInfoField(queryResult));																												
				}								
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_alarm_hist", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_alarm_hist");
		}
		alarmInfoList.setResult(resultStatus);
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getListOfVCNAlarm with result:"+resultStatus.toString()));
		return alarmInfoList;
	}
	
	
	
	// get IPC Id & Status
	private DailyIPCStatusInfoList getListOfIPCStatus(String transactionId) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in DailyIPCStatusInfoList"));
		//String queryStmt = "SELECT ipc_code,ipc_status FROM tbl_ipc";		
		String queryStmt = "SELECT ipc_code,gate_id,ipc_status FROM tbl_ipc WHERE gate_id IS ?";
		List<Object[]> queryResultList = null;		
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		DailyIPCStatusInfoList ipcInfoList = new DailyIPCStatusInfoList();
		try {
			paramList.add(null);
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt,paramList);
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					ipcInfoList.addList(new DailyIPCStatusInfoField(queryResult));																												
				}								
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_ipc", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_ipc");
		}
		ipcInfoList.setResult(resultStatus);
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out DailyIPCStatusInfoList with result:"+resultStatus.toString()));
		return ipcInfoList;
	}
		
	
	// get boat arrival and departure with no of passenger
	private DailyBoatInfoList getListOfBoatInfo(String transactionId) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getListOfBoatInfo"));		
		String queryStmt = "SELECT bo.boat_code,bo.boat_short_name,DATE_FORMAT(sch.date_arrival,\"%H:%i\") date_arrival,DATE_FORMAT(sch.date_departure,\"%H:%i\") date_departure"
				+ ",equ.direction_code,sch.gate_id,COUNT(*) passenger_count FROM tbl_boat bo INNER JOIN tbl_boat_schedule sch ON bo.boat_id=sch.boat_id "
				+ "INNER JOIN tbl_gate_access_info ga ON sch.gate_id=ga.gate_id "
				+ "INNER JOIN tbl_equ_direction equ ON ga.eqdirection_id=equ.eqdirection_id "				
				+ "WHERE ((ga.event_time BETWEEN sch.date_arrival AND sch.date_departure) OR (ga.event_time >= sch.date_arrival AND sch.date_departure IS NULL)) "
				//+ "WHERE (ga.event_time >= sch.date_arrival AND ga.event_time <= IFNULL(sch.date_departure, NOW())) "
				+ "AND ga.event_time BETWEEN ? AND ? "
				+ "GROUP BY bo.boat_code,bo.boat_short_name"
				+ ",DATE_FORMAT(sch.date_arrival,\"%H:%i\") , DATE_FORMAT(sch.date_departure,\"%H:%i\"),equ.direction_code,sch.gate_id";		
		List<Object[]> queryResultList = null;				
		String startDate = StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYY_MM_DD); //morning		
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		DailyBoatInfoList boatInfoList = new DailyBoatInfoList();
		HashMap<String, DailyBoatInfoField> boatInfoMap = new HashMap<>();
		DailyBoatInfoField boatInfoDB = null;
		DailyBoatInfoField boatInfo = null;
		String mapKey = null;
		HWGateInfo hwGateInfo = null;
		try {					
			paramList.add(StringUtil.stringToDate(startDate, StringUtil.DATE_FORMAT_YYYY_MM_DD));
			paramList.add(StringUtil.stringToDate(startDate+ " 23:59:59", StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			//1 record of boatInfo contain both no passegner in and out. but result return from db 1 record contain only in or out
			//then we use hashmap with key boatcode+arrival+departure as a key to complete this condition.
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					boatInfoDB = new DailyBoatInfoField(queryResult);
					if (StringUtil.checkNull(boatInfoDB.getDepartureTime())) {
						boatInfoDB.setDepartureTime("");
						//fill location						
						hwGateInfo = this.hwGateInfoDAO.findOne(boatInfoDB.getGateId());
						if (hwGateInfo!=null) {
							boatInfoDB.setMapCode(hwGateInfo.getLocationMap().getMapCode());
							boatInfoDB.setBoatLocationX(hwGateInfo.getBoatLocationX());
							boatInfoDB.setBoatLocationY(hwGateInfo.getBoatLocationY());
							boatInfoDB.setBoatNameColor(hwGateInfo.getBoatNameColor());
							boatInfoDB.setBoatIconWidth(hwGateInfo.getBoatIconWidth());
							boatInfoDB.setBoatIconHeight(hwGateInfo.getBoatIconHeight());
							boatInfoDB.setBoatNameLocationX(hwGateInfo.getBoatNameLocationX());
							boatInfoDB.setBoatNameLocationY(hwGateInfo.getBoatNameLocationY());
							boatInfoDB.setBoatIconTransformX(hwGateInfo.getBoatIconTransformX());
							boatInfoDB.setBoatIconTransformY(hwGateInfo.getBoatIconTransformY());
							boatInfoDB.setBoatNameSize(hwGateInfo.getBoatNameSize());
						}
					}
					mapKey = boatInfoDB.getBoatCode()+boatInfoDB.getArrivalTime()+boatInfoDB.getDepartureTime();
					boatInfo = boatInfoMap.get(mapKey);
					if (boatInfo==null) {
						//not found then insert			
						if (boatInfoDB.getDirection()==EquipmentDirection.DIRECTION_IN) {
							boatInfoDB.setNoOfIN(boatInfoDB.getNoOfPassenger());
						}else {
							boatInfoDB.setNoOfOUT(boatInfoDB.getNoOfPassenger());
						}
						boatInfoMap.put(mapKey, boatInfoDB);
					}else {
						//found then update
						if (boatInfoDB.getDirection()==EquipmentDirection.DIRECTION_IN) {
							boatInfo.setNoOfIN(boatInfoDB.getNoOfPassenger());
						}else {
							boatInfo.setNoOfOUT(boatInfoDB.getNoOfPassenger());
						}
						boatInfoMap.put(mapKey, boatInfo);						
					}					
				}
				//go thought hashmap and insert to boatInfoList			
				for (String myKey:boatInfoMap.keySet()) {
					boatInfo = boatInfoMap.get(myKey);
					if (boatInfo.getNoOfIN()==null) {
						boatInfo.setNoOfIN(0);
					}
					if (boatInfo.getNoOfOUT()==null) {
						boatInfo.setNoOfOUT(0);
					}
					boatInfoList.addList(boatInfo);
				}
				//sort result by arrival, departure, boatcode
				Collections.sort(boatInfoList.getBoatInfoList(), new DailyBoatInfoFieldSort());
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_alarm_hist", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_alarm_hist");
		}
		boatInfoList.setResult(resultStatus);
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getListOfBoatInfo with result:"+resultStatus.toString()));
		return boatInfoList;
	}
	
	// get list of passenger by gate
	private DailyGateInfoList getListOfPassengerByGate(String transactionId) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getListOfPassengerByGate"));
		String queryStmt = "SELECT gin.gate_code,gin.gate_short_name,equ.eqdirection_id, COUNT(*) noOfTran "
				+ "FROM tbl_gate_access_info ga "
				+ "INNER JOIN tbl_equ_direction equ ON ga.eqdirection_id=equ.eqdirection_id "
				+ "INNER JOIN tbl_gate_info gin ON ga.gate_id=gin.gate_id "
				+ "WHERE ga.event_time BETWEEN ? AND ? "
				+ "GROUP BY gin.gate_code,gin.gate_desc,equ.eqdirection_id "
				+ "ORDER BY gin.gate_code,gin.gate_desc,equ.eqdirection_id";
		DailyGateInfoList gateInfoList = new DailyGateInfoList();
		List<Object[]> queryResultList = null;
		HashMap<String, DailyGateInfoField> gateInfoMap = new HashMap<>();
		DailyGateInfoField gateInfo = null;
		DailyGateInfoField gateInfoDB = null;		
		String startDate = StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYY_MM_DD); //morning		
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		String mapKey = null;
		try {					
			paramList.add(StringUtil.stringToDate(startDate, StringUtil.DATE_FORMAT_YYYY_MM_DD));
			paramList.add(StringUtil.stringToDate(startDate+ " 23:59:59", StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					gateInfoDB = new DailyGateInfoField(queryResult);					
					mapKey = gateInfoDB.getGateCode();
					gateInfo = gateInfoMap.get(mapKey);
					if (gateInfo==null) {
						//not found then insert			
						if (gateInfoDB.getDirection()==EquipmentDirection.DIRECTION_IN) {
							gateInfoDB.setNoOfIN(gateInfoDB.getNoOfPassenger());
						}else {
							gateInfoDB.setNoOfOut(gateInfoDB.getNoOfPassenger());
						}
						gateInfoMap.put(mapKey, gateInfoDB);
					}else {
						//found then update
						if (gateInfoDB.getDirection()==EquipmentDirection.DIRECTION_IN) {
							gateInfo.setNoOfIN(gateInfoDB.getNoOfPassenger());
						}else {
							gateInfo.setNoOfOut(gateInfoDB.getNoOfPassenger());
						}
						gateInfoMap.put(mapKey, gateInfo);						
					}					
				}
				//go thought hashmap and insert to boatInfoList				
				for (String myKey:gateInfoMap.keySet()) {				
					gateInfo = gateInfoMap.get(myKey);
					if (gateInfo.getNoOfIN()==null) {
						gateInfo.setNoOfIN(0);
					}
					if (gateInfo.getNoOfOut()==null) {
						gateInfo.setNoOfOut(0);
					}
					gateInfoList.addList(gateInfo);
				}
				//sort result by arrival, departure, boatcode
				Collections.sort(gateInfoList.getGateInfoList(), new DailyGateInfoFieldSort());				
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getListOfPassengerByGate with result:"+gateInfoList.getGateInfoList().size()+" record"));				
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_gate_access_info get passenger IN-OUT", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_gate_access_info get passenger IN-OUT");
		}
		gateInfoList.setResult(resultStatus);
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getListOfPassengerByGate with result:"+resultStatus.toString()));
		return gateInfoList;		
	}
	
	
	
	//get list of face match, unmatch by IPC
	//IPC which doesn't have relation with Gate
	private DailyIPCInfoList getListOfFaceByIPC(String transactionId) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getListOfFaceByIPC"));
		  String queryStmt = "SELECT ipc.ipc_short_name,SUM(rec_un_match) AS rec_unmatch, SUM(rec_match) AS rec_match,ipc.ipc_code "
				+ "FROM tbl_alarm_hist his INNER JOIN tbl_ipc ipc ON his.ipc_id=ipc.ipc_id "
				+ "WHERE ipc.gate_id IS NULL AND his.alarm_source=? AND his.alarm_time BETWEEN ? AND ? "
				+ "GROUP BY ipc.ipc_code,ipc.ipc_short_name "
				+ "ORDER BY ipc.ipc_code,ipc.ipc_short_name";
		DailyIPCInfoList faceInfoList = new DailyIPCInfoList();
		List<Object[]> queryResultList = null;
		String startDate = StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYY_MM_DD); //morning		
		ArrayList<Object> paramList = new ArrayList<>();		
		ResultStatus resultStatus = new ResultStatus();
		try {					
			paramList.add(HWAlarmHist.HW_VCM_ALARM);
			paramList.add(StringUtil.stringToDate(startDate, StringUtil.DATE_FORMAT_YYYY_MM_DD));
			paramList.add(StringUtil.stringToDate(startDate+ " 23:59:59", StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					faceInfoList.addList(new DailyFaceChartField(queryResult));															
				}				
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getListOfFaceByIPC with result:"+faceInfoList.getIPCInfoList().size()+" record"));				
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_alarm_hist", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_alarm_hist");
		}
		faceInfoList.setResult(resultStatus);
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getListOfFaceByIPC with result:"+resultStatus.toString()));
		return faceInfoList;
	}

	@Override
	public ResultStatus updateBoatSchedule(String transactionId, PassengerBoatActivity boatActivity) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in updateBoatSchedule"));
		ResultStatus resultStatus = new ResultStatus();
		Boat boat = this.boatDAO.findByCardNo(boatActivity.getBoatDriverCardId());
		HWGateInfo hwGateInfo = this.hwGateInfoDAO.findByGateCode(boatActivity.getGateId());
		BoatSchedule boatSchedule = null;
		if (boat==null) {
			Logger.info(this,  LogUtil.getLogInfo(transactionId, "Cannot find boat associate to card no:"+boatActivity.getBoatDriverCardId()));
			resultStatus.setStatusCode(ResultStatus.BOAT_DRIVER_CARD_NO_NOT_LINK_BOAT_CODE, boatActivity.getBoatDriverCardId());
		}else if (hwGateInfo==null) {
			Logger.info(this,  LogUtil.getLogInfo(transactionId, "Cannot find gate associate to gate id:"+boatActivity.getGateId()));
			resultStatus.setStatusCode(ResultStatus.HW_GATE_CODE_NOT_FOUND_CODE, boatActivity.getGateId());
		}else {
			if (PassengerBoatActivity.ACTIVITY_BOAT_DIRECTION_IN.equals(boatActivity.getDirection())) {
				//check in
				//1. check if there is pending check out boat							
				boatSchedule = this.boatScheduleDAO.findPendingDeparture(boat, hwGateInfo);
				if (boatSchedule!=null) {							
					//2. if found then force update check out time with eventtime-1minute
					long timeTemp = StringUtil.stringToDate(boatActivity.getEventDT(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS).getTime();
					timeTemp = timeTemp - 60000; //1 minute
					boatSchedule.setDateDeparture(new Date(timeTemp));
					this.boatScheduleDAO.save(boatSchedule);
				}							
				//3. create new record for boat check in with check out time is null
				boatSchedule = new BoatSchedule();
				boatSchedule.setBoat(boat);
				boatSchedule.setDateArrival(StringUtil.stringToDate(boatActivity.getEventDT(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));
				boatSchedule.setHwGateInfo(hwGateInfo);
				this.boatScheduleDAO.save(boatSchedule);				
			}else {
				//check out
				//1. check if there is pending check out boat				
				boatSchedule = this.boatScheduleDAO.findPendingDeparture(boat, hwGateInfo);
				if (boatSchedule==null) {							
					//2. not found create new record with check in and check out time
					boatSchedule = new BoatSchedule();
					boatSchedule.setBoat(boat);
					long timeTemp = StringUtil.stringToDate(boatActivity.getEventDT(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS).getTime();
					timeTemp = timeTemp - 60000; //1 minute
					boatSchedule.setDateArrival(new Date(timeTemp));
					boatSchedule.setDateDeparture(StringUtil.stringToDate(boatActivity.getEventDT(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));
					boatSchedule.setHwGateInfo(hwGateInfo);					
				}else {
					//3. if found update check out time
					boatSchedule.setDateDeparture(StringUtil.stringToDate(boatActivity.getEventDT(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS));
					this.boatScheduleDAO.save(boatSchedule);
				}				
			}
		}
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out updateBoatSchedule with status:"+resultStatus.toString()));
		return resultStatus;
	}

	
	
	
	/*
	
	//getList CameraInfoList
	////////////////////
		private DailyCameraInfoList getListOfCameraInfoList(String transactionId) {
			Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getListOf CameraInfoList"));
			String queryStmt = "SELECT gin.gate_code,gin.gate_short_name,equ.eqdirection_id, COUNT(*) noOfTran "
					+ "FROM tbl_gate_access_info ga "
					+ "INNER JOIN tbl_equ_direction equ ON ga.eqdirection_id=equ.eqdirection_id "
					+ "INNER JOIN tbl_gate_info gin ON ga.gate_id=gin.gate_id "
					+ "WHERE ga.event_time BETWEEN ? AND ? "
					+ "GROUP BY gin.gate_code,gin.gate_desc,equ.eqdirection_id "
					+ "ORDER BY gin.gate_code,gin.gate_desc,equ.eqdirection_id";
			//create obj cameraInfoList
			DailyCameraInfoList cameraInfoList = new DailyCameraInfoList();
			//create obj queryResultList
			List<Object[]> queryResultList = null;
			//create object Hash map cameraInfoField 
			HashMap<String, DailyCameraInfoField> cameraInfoMap = new HashMap<>();
			//Create define variable cameraInfo and cameraInfoDB
			DailyCameraInfoField cameraInfo = null;
			DailyCameraInfoField cameraInfoDB = null;		
			
			String startDate = StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYY_MM_DD); //morning		
			ArrayList<Object> paramList = new ArrayList<>();		
			ResultStatus resultStatus = new ResultStatus();
			String mapKey = null;
			try {					
				paramList.add(StringUtil.stringToDate(startDate, StringUtil.DATE_FORMAT_YYYY_MM_DD));
				paramList.add(StringUtil.stringToDate(startDate+ " 23:59:59", StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
				queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
				
				//Check condition queryResultSize 
				if (queryResultList.size()>0) {
					//Loop for check result queryResult 
					for (Object[] queryResult: queryResultList) {
						//crate obj cameraInfo 
						cameraInfoDB = new DailyCameraInfoField(queryResult);					
						//create Map key cameraInfoMap
						mapKey = cameraInfoDB.getGateCode();
						cameraInfo = cameraInfoMap.get(mapKey);
						
						if (cameraInfo==null) {
							//not found then insert			
							if (cameraInfoDB.getDirection()==HWIPC.DIRECTION_IN) {
								cameraInfoDB.setNoOfIN(cameraInfoDB.getNoOfPassenger());
							}else {
								cameraInfoDB.setNoOfOut(cameraInfoDB.getNoOfPassenger());
							}
							cameraInfoMap.put(mapKey, cameraInfoDB);
						}else {
							//found then update
							if (gateInfoDB.getDirection()==HWIPC.DIRECTION_IN) {
								gateInfo.setNoOfIN(gateInfoDB.getNoOfPassenger());
							}else {
								gateInfo.setNoOfOut(gateInfoDB.getNoOfPassenger());
							}
							gateInfoMap.put(mapKey, gateInfo);						
						}					
					}
					//go thought hashmap and insert to boatInfoList				
					for (String myKey:gateInfoMap.keySet()) {				
						gateInfo = gateInfoMap.get(myKey);
						if (gateInfo.getNoOfIN()==null) {
							gateInfo.setNoOfIN(0);
						}
						if (gateInfo.getNoOfOut()==null) {
							gateInfo.setNoOfOut(0);
						}
						gateInfoList.addList(gateInfo);
					}
					//sort result by arrival, departure, boatcode
					Collections.sort(gateInfoList.getGateInfoList(), new DailyGateInfoFieldSort());				
					Logger.debug(this, LogUtil.getLogDebug(transactionId, "getListOfPassengerByGate with result:"+gateInfoList.getGateInfoList().size()+" record"));				
				}
			}catch (Exception ex) {
				Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_gate_access_info get passenger IN-OUT", ex));
				resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_gate_access_info get passenger IN-OUT");
			}
			gateInfoList.setResult(resultStatus);
			Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getListOfPassengerByGate with result:"+resultStatus.toString()));
			return gateInfoList;		
		}
		
		*/
	
	
	
	
//	private DailyGateInfoList getListOfPassengerByGateV2(String transactionId) {
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getListOfPassengerByGate"));
//		String queryStmt = "SELECT gin.gate_code,gin.gate_desc,equ.eqdirection_id, COUNT(*) noOfTran FROM tbl_gate_access_info ga " 
//		+ "INNER JOIN tbl_equ_direction equ ON ga.eqdirection_id=equ.eqdirection_id "
//		+ "INNER JOIN tbl_gate_info gin ON ga.gate_id=gin.gate_id "
//		+ "WHERE record_time BETWEEN ? AND ? "
//		+ "GROUP BY gin.gate_code,gin.gate_desc,equ.eqdirection_id "
//		+ "ORDER BY gin.gate_code,gin.gate_desc,equ.eqdirection_id";
//		DailyGateInfoList gateInfoList = new DailyGateInfoList();
//		List<Object[]> queryResultList = null;
//		DailyGateInfoField gateInfoField = null;
//		DailyGateInfoField gateInfoDB = null;
//		Date endDate = new Date();
//		Date startDate = StringUtil.stringToDate(StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDD), StringUtil.DATE_FORMAT_YYYYMMDD);		
//		ArrayList<Object> paramList = new ArrayList<>();		
//		ResultStatus resultStatus = new ResultStatus();
//		String prevGateCode = null;
//		try {					
//			paramList.add(startDate);
//			paramList.add(endDate);
//			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);
//			if (queryResultList.size()>0) {
//				for (Object[] queryResult: queryResultList) {
//					gateInfoDB = new DailyGateInfoField(queryResult);					
//					if (prevGateCode==null||!prevGateCode.equals(gateInfoDB.getGateCode())) {
//						gateInfoField = new DailyGateInfoField();
//						gateInfoField.setGateCode(gateInfoDB.getGateCode());
//						gateInfoField.setGateName(gateInfoDB.getGateName());
//						if (gateInfoDB.getDirection() == EquipmentDirection.DIRECTION_IN) {
//							gateInfoField.setNoOfIN(gateInfoDB.getNoOfPassenger());
//							gateInfoField.setNoOfOut(0);
//						}else {
//							gateInfoField.setNoOfIN(0);
//							gateInfoField.setNoOfOut(gateInfoDB.getNoOfPassenger());
//						}												
//						prevGateCode = gateInfoDB.getGateCode();
//						gateInfoList.addList(gateInfoField);
//					}else {
//						if (gateInfoDB.getDirection() == EquipmentDirection.DIRECTION_IN) {
//							gateInfoField.setNoOfIN(gateInfoDB.getNoOfPassenger());
//						}else {
//							gateInfoField.setNoOfOut(gateInfoDB.getNoOfPassenger());
//						}
//					}																			
//				}					
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getListOfPassengerByGate with result:"+gateInfoList.getGateInfoList().size()+" record"));				
//			}
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data tbl_gate_access_info get passenger IN-OUT", ex));
//			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "tbl_gate_access_info get passenger IN-OUT");
//		}
//		gateInfoList.setResult(resultStatus);
//		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getListOfPassengerByGate with result:"+resultStatus.toString()));
//		return gateInfoList;		
//	}
	
}
