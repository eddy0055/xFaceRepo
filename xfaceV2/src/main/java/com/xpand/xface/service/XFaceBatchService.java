package com.xpand.xface.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.LastAlarmPersonDateTime;
import com.xpand.xface.bean.PassengerBoatActivity;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.landing.LandingPageInfo;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWTaskList;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertificate;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.exception.DoQueryErrorException;

public interface XFaceBatchService {
	//gateaccess
	public ResultStatus updateHWGateAccessInfo(String transactionId, PassengerBoatActivity passengerBoatActivity);
	//alarm
	public ResultStatus updateAlarmHistory(String transactionid, HWAlarmHist hwAlarmHist);
	public List<LastAlarmPersonDateTime> findMaxAlarmHistoryGroupByPerson(Date startDate);
	//personinfo
	public PersonInfo findPersonByHwPeopleId(String transactionId, String hwPeopleId);
	public PersonInfo findPersonByCertificateNo(String transactionId, String certificateNo);
	//hw ipc IPC
	public HWIPC findIPCByCode(String ipcCode);
	public ResultStatus updateIPC(String transactionId, HWIPC hwIPC);
	public void purgeIPCCache();
	public List<HWIPC> getIPCNeverCreateTaskList(HWVCM hwVCM);
	public List<HWIPC> getIPCNeverAddToCheckPoint(HWVCM hwVCM);
	public ResultStatus updateIPCTaskId(String transactionId, HWIPC hwIPC);
	public ResultStatus updateIPCCheckPointId(String transactionId, HWIPC hwIPC);
	public ResultStatus updateIPCStatus(String transactionId, HWIPC hwIPC);
	
	//app cfg AC
	public ApplicationCfg findACByAppKey(String transactionId, String appKey);
	public HashMap<String, ApplicationCfg> getAllACHashMap(String transactionId);
	public void purgeACCache();
	//checkpointlib CPL
	public HWCheckPointLibrary getCPLOneObject();
	public List<HWCheckPointLibrary> getCPLNeverCreateLibrary(HWVCM hwVCM);
	public List<HWCheckPointLibrary> getCPLNeverCreateCheckPoint(HWVCM hwVCM);
	public ResultStatus updateCPLLibraryId(String transactionId, HWCheckPointLibrary hwCheckPointLibrary);
	public ResultStatus updateCPLCheckPointId(String transactionId, HWCheckPointLibrary hwCheckPointLibrary);
	
	//tasklist TL
	public List<HWTaskList> getTLNeverCreateTaskList(HWVCM hwVCM);
	public ResultStatus updateTLTaskId(String transactionId, HWTaskList hwTaskList);
	
	//VCM VCM
	public List<HWVCM> getVCMAll();
	
	//custom query
	public List<Object[]> doQuery(Object source, String transactionId, String queryStmt, ArrayList<?> paramList) throws DoQueryErrorException;
	public ResultStatus dbRemovePersonFromVCM_ZK(Object source, String transactionId, String personIdList) throws DoQueryErrorException;
	public ResultStatus dbAddPersonToVCM_ZK(Object source, String transactionId, String personIdList, String hwPeopleIdList) throws DoQueryErrorException;
	
	//nationality
	public PersonNationality findNationalityByCode(String transactionId, String nationalityCode);
	public ResultStatus updateNationality(String transactionId, String logonUserName, PersonNationality nationality);
	
	public PersonTitle findPersonTitleById(String transactionId, Integer titleId);
	public PersonCertificate findPersonCertificateById(String transactionId, Integer certificateId);
	public PersonCategory findPersonCategoryById(String transactionId, Integer categoryId);
	public PersonInfo resetPersonRegDateId(String transactionId, PersonInfo personInfo);
	public ResultStatus updatePersonInfoToDB(String transactionId, PersonInfo personInfo, CustomerRegister customerInfo);
	public PersonInfo addPersonRegDate(PersonInfo personInfo, Date regDate, String agentName, String logonUserName);

	public void createSystemAudit(String transactionId, String module, String subModule, String description, String result, String userName);
	
	public LandingPageInfo getLandingPageInfo(String transactionId);
	
	public ResultStatus updateBoatSchedule(String transactionId, PassengerBoatActivity boatActivity);
	
}
