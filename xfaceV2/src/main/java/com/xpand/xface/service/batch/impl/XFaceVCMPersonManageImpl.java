package com.xpand.xface.service.batch.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonRegisterResult;
import com.xpand.xface.bean.PersonRegisterResultList;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWCheckPointLibrary;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.service.hwapi.HWAPIBatchService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;


/*
 * class to check checkin date of person and add/remove person from vcm
 * 1. get all person which status STATUS_UPDATE_TO_VCN and abs(now - register date) > X day of null 
 * 2. remove all person on point 1 from vcm and mark person status as STATUS_REMOVE_FROM_VCN
 * 3. get all person which status (STATUS_NEW,STATUS_REMOVE_FROM_VCN,STATUS_UPDATE_ON_XFACE) 
 * 	  and abs(now - register date) < X day
 * 4. add all person on point 3 to vcm and mark person status as STATUS_UPDATE_TO_VCN 
 */
public class XFaceVCMPersonManageImpl extends Thread implements BaseXFaceThreadService{		
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private String transactionId;		
	private HWAPIBatchService hwAPIService;					
	private XFaceBatchService xFaceBatchService;	
	private HWCheckPointLibrary hwCheckPointLibrary; 
	public XFaceVCMPersonManageImpl(String transactionId, XFaceBatchService xFaceBatchService, HWAPIBatchService hwAPIService, HWVCM hwVCM) {
		super(transactionId+"_XFaceVCMPersonManageImpl");		
		this.transactionId = super.getName();						
		this.xFaceBatchService = xFaceBatchService;
		this.hwAPIService = hwAPIService;
		Iterator<HWCheckPointLibrary> checkPointList = hwVCM.getHwCheckPointLibraryList().iterator();
		if (checkPointList.hasNext()) {
			//default hwCheckPointLibrary
			this.hwCheckPointLibrary = checkPointList.next();
		}		
	}
	@Override
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start XFaceVCMPersonManageImpl thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;		
		ApplicationCfg appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_NO_OF_DAY_ADDREMOVE_PERSON_VCM);
		int noOfDayAddPerson = StringUtil.stringToInteger(appTmp.getAppValue1(), 5);		
		int noOfDayRemovePerson = StringUtil.stringToInteger(appTmp.getAppValue2(), 0);
		/////////////////////////////////
		appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_ADDREMOVE_PERSON_VCM_WAIT);
		int timeThreadSleep = StringUtil.stringToInteger(appTmp.getAppValue1(), 300);
		appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_THREAD_NO_VCMAPI_REMOVE_CUSTOMER);
		int noThreadVCMRemoveAPI = StringUtil.stringToInteger(appTmp.getAppValue1(), 1);
		int timeThreadSleepVCMRemoveAPI = StringUtil.stringToInteger(appTmp.getAppValue2(), 1000);		
		appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_THREAD_NO_VCMAPI_ADD_CUSTOMER);
		int noThreadVCMAddAPI = StringUtil.stringToInteger(appTmp.getAppValue1(), 1);
		int timeThreadSleepVCMAddAPI = StringUtil.stringToInteger(appTmp.getAppValue2(), 1000);
		appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_NO_OF_TRAN_CALL_DB_ADDREMOVE_VCM);
		int noOfTranCallDBAddVCM = StringUtil.stringToInteger(appTmp.getAppValue1(), 20);
		int noOfTranCallDBRemoveVCM = StringUtil.stringToInteger(appTmp.getAppValue2(), 20);
		PersonRegisterResultList personRegisterList = null;
		
		////////////////////		
		while (this.isLoop) {
			//1. get all person which status STATUS_UPDATE_TO_VCN and abs(now - register date) > X day of null			
			personRegisterList = this.getRemovePersonList(noOfDayAddPerson, noOfDayRemovePerson);
			//2. remove all person on point 1 from vcm and mark person status as STATUS_REMOVE_FROM_VCN
			if (personRegisterList.getPersonRegisterResultList().size()>0) {
				this.removePersonFromVCM_ZK(personRegisterList, noThreadVCMRemoveAPI, timeThreadSleepVCMRemoveAPI);
				this.dbRemovePersonFromVCM_ZK(personRegisterList, noOfTranCallDBRemoveVCM);
			}
			//3.get all person which status (STATUS_NEW,STATUS_REMOVE_FROM_VCN,STATUS_UPDATE_ON_XFACE) 
			//and abs(now - register date) < X day
			personRegisterList = this.getAddPersonList(noOfDayAddPerson);
			//4. add all person on point 3 to vcm and mark person status as STATUS_UPDATE_TO_VCN
			if (personRegisterList.getPersonRegisterResultList().size()>0) {				
				this.addPersonToVCM_ZK(personRegisterList, noThreadVCMAddAPI, timeThreadSleepVCMAddAPI, noOfDayRemovePerson);
				this.dbAddPersonFromVCM_ZK(personRegisterList, noOfTranCallDBAddVCM);
			}
			OtherUtil.waitSecond(this, timeThreadSleep);
		}
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop XFaceVCMPersonManageImpl thread ["+super.getName()+"]"));
	}
	@Override
	public boolean isServiceRunning() {
		return this.isLoop;
	}	
	@Override
	public void stopServiceThread() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to stop [ThreadName:"+super.getName())+"]");		
		this.isLoop = false;
		this.isTerminate = false;
	}
	@Override
	public boolean isTerminate() {
		return this.isTerminate;
	}	
	@Override
	public boolean isServiceReStart() {
		return false;
	}	
	//get list of person which need to remove from VCM, ZK from db
	//status is PersonInfo.STATUS_UPDATE_TO_VCN_ZK (already update to VCM, ZK)
	//1. query get list of person which now-register between -1*noOfAdd AND noOfRemove
	//this person still require to keep in VCM and ZK
	//2. from result on 1 join with personinfo only person status UPDATE_TO_VCM_ZK, person id is null and people id no null
	//which mean only person no in the list of result 1
	public PersonRegisterResultList getRemovePersonList(int noOfDayAddPerson, int noOfDayRemovePerson) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in getRemovePersonList"));		
		ArrayList<Object> paramList = new ArrayList<>();
		String queryStmt = "SELECT per.person_id,CONVERT(AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS certificate_no, CONVERT(AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS full_name,person_photo,hw_people_id,third_party_code,personvcmstatus "
				 		+ "FROM tbl_person_info per "
				 		+ "INNER JOIN tbl_person_certificate cer ON per.certificate_id=cer.certificate_id "
				 		+ "LEFT OUTER JOIN ("
				 		+ "SELECT DISTINCT per.person_id " 
				 		+ "FROM tbl_person_info per "
				 		+ "INNER JOIN tbl_person_reg_date reg " 
				 		+ "ON per.person_id=reg.person_id "
				 		+ "WHERE  personvcmstatus=? "
				 		+ "AND DATEDIFF(NOW(), register_date) BETWEEN ? AND ?) tmp "
				 		+ "ON per.person_id=tmp.person_id "
				 		+ "WHERE  personvcmstatus=? AND tmp.person_id IS NULL AND per.hw_people_id IS NOT NULL";		
		paramList.add(PersonInfo.STATUS_UPDATE_TO_VCM_ZK);
		paramList.add(-1*noOfDayAddPerson);
		paramList.add(noOfDayRemovePerson);
		paramList.add(PersonInfo.STATUS_UPDATE_TO_VCM_ZK);				
		return this.getPersonList(queryStmt, paramList);		
	}
	//get list of person to update to vcm, zk
	//status should not equal to update to vcm zk
	//and now - register date between -1*add day and 0
	//example 
	//1. now is 14/07/2019, register date is 20/07/2019, add day is 5
	// 14/07/2019 - 20/07/2019 = -6, -6 not between -5 and 0 then no need to add
	//2. now is 16/07/2019, register date is 20/07/2019, add day is 5
	// 16/07/2019 - 20/07/2019 = -4, -4 between -5 and 0 then need to add
	//3. now is 21/07/2019, register date is 20/07/2019, add day is 5
	// 21/07/2019 - 20/07/2019 = 1, 1 between -5 and 0 then no need to add	
	public PersonRegisterResultList getAddPersonList(int noOfDayAddPerson) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in getAddPersonList"));
		ArrayList<Object> paramList = new ArrayList<>();
		String queryStmt = "SELECT DISTINCT per.person_id,CONVERT(AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS certificate_no, CONVERT(AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS full_name,person_photo,hw_people_id,third_party_code,personvcmstatus "
				 		+ "FROM tbl_person_info per "
				 		+ "INNER JOIN tbl_person_certificate cer ON per.certificate_id=cer.certificate_id "
				 		+ "LEFT OUTER JOIN tbl_person_reg_date reg ON per.person_id=reg.person_id "
				 		+ "WHERE per.personvcmstatus<>? "		
						+"AND DATEDIFF(NOW(), register_date) BETWEEN ? AND 0";
		paramList.add(PersonInfo.STATUS_UPDATE_TO_VCM_ZK);
		paramList.add(-1*noOfDayAddPerson);
		return this.getPersonList(queryStmt, paramList);
	}
	public PersonRegisterResultList getPersonList(String queryStmt, ArrayList<Object> paramList) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in getPersonList"));
		ResultStatus resultStatus = new ResultStatus();
		PersonRegisterResultList personResultList = new PersonRegisterResultList();		
		try {			
			List<Object[]> queryResultList = this.xFaceBatchService.doQuery(this, this.transactionId, queryStmt, paramList);			
			PersonRegisterResult personResult = null;
			for (Object[] queryResult: queryResultList) {
				personResult = new PersonRegisterResult();
				personResult.createPersonRegister(queryResult);
				personResultList.getPersonRegisterResultList().add(personResult);								
			}
			Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "getPersonList no of result:"+personResultList.getPersonRegisterResultList().size()+ " record."));			
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(this.transactionId, "error while read person register date", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "error read person register date"); 
		}		
		personResultList.setResult(resultStatus);
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "out getPersonList with result:"+resultStatus.toString()));
		return personResultList;
	}
	//remove person from VCM
	public void removePersonFromVCM_ZK(PersonRegisterResultList personRegisterList, int noOfThread, int waitExecuteAPI) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in removePersonFromVCM with "+personRegisterList.getPersonRegisterResultList().size()+" record need to remove"));		
		int cntFail = 0;		
		int cntThread = 0;				
		ArrayList<ExecuteRemoveVCM_ZK> threadList = new ArrayList<>();
		ExecuteRemoveVCM_ZK removeThread = null;
		boolean foundDeadThread = false;
		boolean isExecuteAPI = true;
		while (isExecuteAPI) {
			isExecuteAPI = false;
			for (PersonRegisterResult person: personRegisterList.getPersonRegisterResultList()) {
				if (person.getResult()==null) {
					//never execute api
					isExecuteAPI = true;
					cntThread = threadList.size();
					if (cntThread < noOfThread) {
						person.setResult(new ResultStatus(ResultStatus.PROCESS_STILL_RUNNING_CODE, null));
						removeThread = new ExecuteRemoveVCM_ZK(this.transactionId, "removeVCM_"+person.getHwPeopleId(), this.hwAPIService, this.hwCheckPointLibrary, person);
						removeThread.start();
						threadList.add(removeThread);
					}else {
						//wait for 1 thread finish					
						while(!foundDeadThread) {
							foundDeadThread = false;						
							for (int index=0; index<threadList.size(); index++) {
								removeThread = threadList.get(index);
								if (!removeThread.isAlive()) {								
									if (!removeThread.getResultStatus().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
										Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "removePersonFromVCM "+removeThread.getPerson().getFullName()+", peopleId:"+removeThread.getPerson().getHwPeopleId()
											+" fail coz:"+removeThread.getResultStatus().toString()));
										cntFail++;
									}
									threadList.remove(index);
									index--;
									foundDeadThread = true;
								}
							}
							if (!foundDeadThread) {
								//not found dead thread which mean thread still running
								//then sleep
								OtherUtil.waitMilliSecond(waitExecuteAPI);
							}
						}						
					}
				}		
			}
			if (isExecuteAPI) {
				OtherUtil.waitMilliSecond(waitExecuteAPI);
			}
		}				
		//wait for all thread
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "wait for all executeRemoveVCM thread complete"));
		try {
			for (ExecuteRemoveVCM_ZK thread: threadList) {
				thread.join();						
				if (!thread.getResultStatus().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
					Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "removePersonFromVCM_ZK "+thread.getPerson().getFullName()+", peopleId:"+thread.getPerson().getHwPeopleId()
						+" fail coz:"+removeThread.getResultStatus().toString()));
					cntFail++;
				}								
			}
		}catch (Exception ex) {}		
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "all executeRemoveVCM thread complete"));
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "out removePersonFromVCM remove success:"+(personRegisterList.getPersonRegisterResultList().size()-cntFail)+", fail:"+cntFail));
	}	
	//add person from VCM
	public void addPersonToVCM_ZK(PersonRegisterResultList personRegisterList, int noOfThread, int waitExecuteAPI, int noOfDayRemovePerson) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in addPersonToVCM with "+personRegisterList.getPersonRegisterResultList().size()+" record need to add"));		
		int cntFail = 0;
		int cntThread = 0;		
		ArrayList<ExecuteAddVCM_ZK> threadList = new ArrayList<>();
		ExecuteAddVCM_ZK addThread = null;
		boolean foundDeadThread = false;
		boolean isExecuteAPI = true;
		while (isExecuteAPI) {
			isExecuteAPI = false;
			for (PersonRegisterResult person: personRegisterList.getPersonRegisterResultList()) {
				//never call api and people id is null (mean never add to vcm)
				if (person.getResult()==null && StringUtil.checkNull(person.getHwPeopleId())) {
					//never execute api
					isExecuteAPI = true;
					cntThread = threadList.size();
					if (cntThread<noOfThread) {
						person.setResult(new ResultStatus(ResultStatus.PROCESS_STILL_RUNNING_CODE, null));
						addThread = new ExecuteAddVCM_ZK(this.transactionId, "addVCM_"+person.getCertificateNo(), this.hwAPIService, this.hwCheckPointLibrary, person, this.xFaceBatchService, noOfDayRemovePerson);
						addThread.start();
						threadList.add(addThread);
					}else {
						//wait for 1 thread finish					
						while(!foundDeadThread) {
							foundDeadThread = false;						
							for (int index=0; index<threadList.size(); index++) {
								addThread = threadList.get(index);
								if (!addThread.isAlive()) {								
									if (!addThread.getResultStatus().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
										Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "addPersonToVCM "+addThread.getPerson().getFullName()+", certificateNo:"+addThread.getPerson().getCertificateNo()
											+" fail coz:"+addThread.getResultStatus().toString()));
										cntFail++;
									}
									threadList.remove(index);
									index--;
									foundDeadThread = true;
								}
							}
							if (!foundDeadThread) {
								//not found dead thread which mean thread still running
								//then sleep
								OtherUtil.waitMilliSecond(waitExecuteAPI);
							}
						}						
					}
				}else if (!StringUtil.checkNull(person.getHwPeopleId())){
					Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "person cert: "+person.getCertificateNo()+" hwPeopleId is not null which mean already in VCM no need to call API to add to VCM"));
				}
			}
			if (isExecuteAPI) {
				OtherUtil.waitMilliSecond(waitExecuteAPI);
			}
		}				
		//wait for all thread
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "wait for all executeAddVCM thread complete"));
		try {
			for (ExecuteAddVCM_ZK thread: threadList) {
				thread.join();						
				if (!thread.getResultStatus().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
					Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "addPersonToVCM "+thread.getPerson().getFullName()+", peopleId:"+thread.getPerson().getHwPeopleId()
						+" fail coz:"+addThread.getResultStatus().toString()));
					cntFail++;
				}								
			}
		}catch (Exception ex) {}		
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "all executeAddVCM thread complete"));
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "out addPersonToVCM add success:"+(personRegisterList.getPersonRegisterResultList().size()-cntFail)+", fail:"+cntFail));
	}
	//update hwPeopleId = null and person vcm status
	//only person which success to remove from both VCM and ZK
	public void dbRemovePersonFromVCM_ZK(PersonRegisterResultList personRegisterList, int noOfTranCallDB) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in dbRemovePersonFromVCM_ZK with "+personRegisterList.getPersonRegisterResultList().size()+" record need to remove"));
		String personIdStringList = "";
		int cntRecord = 0;
		int personResultSize = personRegisterList.getPersonRegisterResultList().size();
		PersonRegisterResult person = null;
		for (int index=0; index < personResultSize; index++) {
			person = personRegisterList.getPersonRegisterResultList().get(index);
			//this comment will be remove once we connect to VCM
			//if (person.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				personIdStringList = personIdStringList + person.getPersonId() + ",";
				cntRecord++;
			//}			
			if ((cntRecord==noOfTranCallDB || index==personResultSize)
					&& personIdStringList.length()>0) {				
				personIdStringList = personIdStringList.substring(0, personIdStringList.length()-1);
				ResultStatus result = null;
				try {
					Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "update remove person from VCM personId: "+personIdStringList));
					result = this.xFaceBatchService.dbRemovePersonFromVCM_ZK(this, this.transactionId, personIdStringList);
				}catch (Exception ex) {			
					Logger.error(this, LogUtil.getLogError(this.transactionId, "error while update person status", ex));
					result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "update person status:"+ex.toString());
				}
				Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "complete dbRemovePersonFromVCM with result:"+result.toString()));
				cntRecord = 0;
				personIdStringList = "";
			}
		}
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "out dbRemovePersonFromVCM_ZK"));
	}
	//set value for hwPeopleId and person vcm status
	//only person which success add to VCM and ZK
	public void dbAddPersonFromVCM_ZK(PersonRegisterResultList personRegisterList, int noOfTranCallDB) {
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "in dbAddPersonFromVCM with "+personRegisterList.getPersonRegisterResultList().size()+" record need to add"));
		String personIdStringList = "";
		String hwPeopleIdStringList = "";		
		int cntRecord = 0;
		int personResultSize = personRegisterList.getPersonRegisterResultList().size();
		PersonRegisterResult person = null;
		for (int index=0; index < personResultSize; index++) {
			person = personRegisterList.getPersonRegisterResultList().get(index);
			//this comment will be remove once we connect to VCM
			//if (person.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				personIdStringList = personIdStringList + person.getPersonId() + ",";
				hwPeopleIdStringList = hwPeopleIdStringList + person.getHwPeopleId() + ",";
				cntRecord++;
			//}			
			if ((cntRecord==noOfTranCallDB || index==personResultSize)
					&& personIdStringList.length()>0) {				
				personIdStringList = personIdStringList.substring(0, personIdStringList.length()-1);
				hwPeopleIdStringList = hwPeopleIdStringList.substring(0, hwPeopleIdStringList.length()-1);
				ResultStatus result = null;
				try {
					Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "add person to VCM personId: "+personIdStringList+", peopleId:"+hwPeopleIdStringList));
					result = this.xFaceBatchService.dbAddPersonToVCM_ZK(this, this.transactionId, personIdStringList, hwPeopleIdStringList);
				}catch (Exception ex) {			
					Logger.error(this, LogUtil.getLogError(this.transactionId, "error while update person status", ex));
					result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "update person status:"+ex.toString());
				}
				Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "complete add person to VCM with result:"+result.toString()));
				cntRecord = 0;
				personIdStringList = "";
				hwPeopleIdStringList = "";
			}
		}
		Logger.info(this,  LogUtil.getLogInfo(this.transactionId, "out dbAddPersonFromVCM"));
	}
	//thread to execute command to remove person from VCM
	private class ExecuteRemoveVCM_ZK extends Thread{
		private HWCheckPointLibrary hwCheckPointLibrary;
		private PersonRegisterResult person;
		private ResultStatus resultStatus;		
		private HWAPIBatchService hwAPIService;
		private String transactionId;
		public ExecuteRemoveVCM_ZK(String transactionId, String threadName, HWAPIBatchService hwAPIService, HWCheckPointLibrary hwCheckPointLibrary, PersonRegisterResult person) {
			super(threadName);
			this.hwCheckPointLibrary = hwCheckPointLibrary;
			this.person = person;
			this.hwAPIService = hwAPIService;
			this.transactionId = transactionId;
		}
		@Override
		public void run() {		
			Logger.info(this,  LogUtil.getLogInfo(this.transactionId+"_"+super.getName(), "in executeRemoveVCM_ZK"));
			//remove face from face list and remove person zk function will ignore incase person not found
			//check if exist on hw then remove
			ResultStatus hwResult = this.hwAPIService.removeFaceFromFaceList(this.transactionId+"_"+super.getName(), this.hwCheckPointLibrary, this.person.getHwPeopleId(), null).getResult();
			//check if exist on zk then remove
			ResultStatus zkResult = this.hwAPIService.removePersonZK(this.transactionId+"_"+super.getName(), this.person.getCertificateNo()).getResult();
			if (ResultStatus.SUCCESS_CODE.equals(hwResult.getStatusCode()) && ResultStatus.SUCCESS_CODE.equals(zkResult.getStatusCode())) {
				this.resultStatus = hwResult;
			}else if (!(ResultStatus.SUCCESS_CODE.equals(hwResult.getStatusCode()) || ResultStatus.SUCCESS_CODE.equals(zkResult.getStatusCode()))) {
				hwResult.setStatusCode(ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_VCM_ZK_CODE, null);
				this.resultStatus = hwResult;
			}else if (!ResultStatus.SUCCESS_CODE.equals(hwResult.getStatusCode())) {
				hwResult.setStatusCode(ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_VCM_CODE, null);
				this.resultStatus = hwResult;
			}else {
				zkResult.setStatusCode(ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_ZK_CODE, null);
				this.resultStatus = zkResult;
			}
			this.person.setResult(this.resultStatus);
			Logger.info(this,  LogUtil.getLogInfo(this.transactionId+"_"+super.getName(), "out executeRemoveVCM_ZK"));
		}
		public ResultStatus getResultStatus() {
			return this.resultStatus;
		}
		public PersonRegisterResult getPerson() {
			return person;
		}			
	}
	//thread to execute command to add person from VCM
	private class ExecuteAddVCM_ZK extends Thread{
		private HWCheckPointLibrary hwCheckPointLibrary;
		private PersonRegisterResult person;
		private ResultStatus resultStatus;
		private HWAPIBatchService hwAPIService;
		private String transactionId;
		private XFaceBatchService xFaceBatchService;
		private int noOfDayRemovePerson;
		public ExecuteAddVCM_ZK(String transactionId, String threadName, HWAPIBatchService hwAPIService, HWCheckPointLibrary hwCheckPointLibrary, PersonRegisterResult person, XFaceBatchService xFaceBatchService, int noOfDayRemovePerson) {
			super(threadName);
			this.hwCheckPointLibrary = hwCheckPointLibrary;
			this.person = person;
			this.hwAPIService = hwAPIService;
			this.transactionId = transactionId;
			this.xFaceBatchService = xFaceBatchService;
			this.noOfDayRemovePerson = noOfDayRemovePerson;
		}
		@Override
		public void run() {		
			Logger.info(this,  LogUtil.getLogInfo(this.transactionId+"_"+super.getName(), "in executeAddVCM_ZK"));
			//check if exist on hw if exist then update if not exist then add
			PersonInfo personInfo = this.xFaceBatchService.findPersonByCertificateNo(this.transactionId, this.person.getCertificateNo());
			HWWSField queryResult = this.hwAPIService.queryFaceFromFaceList(this.hwCheckPointLibrary, personInfo);
			ResultStatus hwResult = null;
			//check with api if not found will api return success as well?
			if (ResultStatus.SUCCESS_CODE.equals(queryResult.getResult().getStatusCode())) {
				//update
				hwResult =  this.hwAPIService.modifyFaceToFaceList(this.transactionId+"_"+super.getName(), this.hwCheckPointLibrary, personInfo, this.person.getBase64Image(), queryResult.getHwPersonId()).getResult();
			}else {
				//add
				hwResult =  this.hwAPIService.addFaceToFaceList(this.transactionId+"_"+super.getName(), this.person, this.hwCheckPointLibrary).getResult();
			}											
			if (hwResult.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				//this.person.setHwPeopleId(this.hwWSField.getAddFaceToListId());
				this.person.setHwPeopleId(this.person.getCertificateNo());
			}			
			//no need to check exist on ZK or not coz we use certificateNo as a pin then let ZK check by themself			
			ResultStatus zkResult = this.hwAPIService.addPersonZK(this.transactionId+"_"+super.getName(), this.person, this.noOfDayRemovePerson).getResult();
			if (zkResult.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.person.setZkPin(this.person.getCertificateNo());
			}
			
			if (ResultStatus.SUCCESS_CODE.equals(hwResult.getStatusCode()) && ResultStatus.SUCCESS_CODE.equals(zkResult.getStatusCode())) {
				this.resultStatus = hwResult;
			}else if (!(ResultStatus.SUCCESS_CODE.equals(hwResult.getStatusCode()) || ResultStatus.SUCCESS_CODE.equals(zkResult.getStatusCode()))) {
				hwResult.setStatusCode(ResultStatus.FAIL_TO_ADD_PERSON_TO_VCM_ZK_CODE, null);
				this.resultStatus = hwResult;
			}else if (!ResultStatus.SUCCESS_CODE.equals(hwResult.getStatusCode())) {
				hwResult.setStatusCode(ResultStatus.FAIL_TO_ADD_PERSON_TO_VCM_CODE, null);
				this.resultStatus = hwResult;
			}else {
				zkResult.setStatusCode(ResultStatus.FAIL_TO_ADD_PERSON_TO_ZK_CODE, null);
				this.resultStatus = zkResult;
			}
			this.person.setResult(this.resultStatus);			
			Logger.info(this,  LogUtil.getLogInfo(this.transactionId+"_"+super.getName(), "out executeAddVCM_ZK"));
		}
		public ResultStatus getResultStatus() {
			return this.resultStatus;
		}
		public PersonRegisterResult getPerson() {
			return person;
		}			
	}
}
