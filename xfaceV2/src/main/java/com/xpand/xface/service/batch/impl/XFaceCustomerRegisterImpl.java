package com.xpand.xface.service.batch.impl;

import java.util.Date;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.queue.QueueCustomerRegister;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertificate;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.entity.PersonRegisterDate;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.XFaceBatchService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;


/*
 * class to check alarm in queue and process
 * 1. send notification to UI
 * 2. execute action code
 * 3. move alarm from queue to db
 */
public class XFaceCustomerRegisterImpl extends Thread implements BaseXFaceThreadService{
	private boolean isLoop = false;
	private boolean isTerminate = true;
	private String transactionId;	
	private int timeThreadSleep;			
	private GlobalVarService globalVarService;			
	private XFaceBatchService xFaceBatchService;
	private PersonCertificate defaultCertificate;
	private PersonTitle defaultTitle;
	private PersonCategory defaultCategory;
	public XFaceCustomerRegisterImpl(String transactionId, int threadNo, GlobalVarService globalVarService
				, XFaceBatchService xFaceBatchService, int timeThreadSleep
				, int defaultCertificateId, int defaultTitleId, int defaultCategoryId) {
		super(transactionId+"_XFaceCustomerRegisterImpl_"+threadNo);		
		this.transactionId = super.getName();
		this.globalVarService = globalVarService;		
		this.timeThreadSleep = timeThreadSleep;		
		this.xFaceBatchService = xFaceBatchService;	
		this.defaultTitle = this.xFaceBatchService.findPersonTitleById(this.transactionId, defaultTitleId);
		//cert type
		this.defaultCertificate = this.xFaceBatchService.findPersonCertificateById(this.transactionId, defaultCertificateId);
		//category
		this.defaultCategory = this.xFaceBatchService.findPersonCategoryById(this.transactionId, defaultCategoryId);
	}
	@Override
	public void run() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start XFaceCustomerRegisterImpl thread ["+super.getName()+"]"));
		this.isTerminate = false;		
		this.isLoop = true;
		QueueCustomerRegister queueContent;	
		ApplicationCfg appTmp = this.xFaceBatchService.findACByAppKey(this.transactionId, ApplicationCfg.KEY_DEMONTASK_LIMIT_PRINT_LOG);
		int limitPrintLog = StringUtil.stringToInteger(appTmp.getAppValue1(), 3000); 
		int cntPrintLog = 0;
		ResultStatus resultStatus = null;
		////////////////////		
		while (this.isLoop || (this.globalVarService.getSizeOfCustomerRegisterQueue()>0)) {
			cntPrintLog++;
			if (cntPrintLog>limitPrintLog) {
				Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "get customer register from queue"));
			}			
			queueContent = this.globalVarService.popCustomerRegister();
			if (queueContent==null) {
				if (cntPrintLog>limitPrintLog) {
					Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "no customer register in queue"));
				}
				OtherUtil.waitMilliSecond(this, this.timeThreadSleep);
			}else {								
				resultStatus = this.updateCustomerRegister(queueContent.getCustomerRegister(), queueContent.getExistingPersonInfo());									
				Logger.info(this, LogUtil.getLogInfo(this.transactionId, "insert data:"+queueContent.getCustomerRegister().getCertificateId()+", rseult is:"+resultStatus.toString()));				
			}		
			if (cntPrintLog>limitPrintLog) {				
				cntPrintLog = 0;
			}
		}
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "stop XFaceCustomerRegisterImpl thread ["+super.getName()+"]"));
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
		// TODO Auto-generated method stub
		return false;
	}
	
	private ResultStatus updateCustomerRegister(CustomerRegister customerInfo, PersonInfo existingPersonInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "update personInfo start"));		
		//code is lower case		
		PersonNationality nationality = null;
		if (!StringUtil.checkNull(customerInfo.getNationality())) {
			nationality = this.xFaceBatchService.findNationalityByCode(this.transactionId, customerInfo.getNationality().toLowerCase());
			if (nationality==null) {
				nationality = new PersonNationality();
				nationality.setNationalityCode(customerInfo.getNationality().toLowerCase());
				nationality.setNationalityName(customerInfo.getNationality());
				nationality.setUserCreated(customerInfo.getLogonUserName());
				nationality.setUserUpdated(customerInfo.getLogonUserName());
				this.xFaceBatchService.updateNationality(this.transactionId, customerInfo.getLogonUserName(), nationality);
			}			
		}
		String oldValue = null;		
		PersonInfo personInfo = null;
		PersonRegisterDate personRegDate = null;
		Date regDate = StringUtil.stringToDate(customerInfo.getTravelDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
		if (customerInfo.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "create person for certificateNo "+customerInfo.getCertificateId()));
			personInfo = new PersonInfo();
			personInfo.setFullName(customerInfo.getCustomerName());			
			personInfo.setCertificateNo(customerInfo.getCertificateId());			
			personInfo.setPersonCode(customerInfo.getCertificateId());			
			personInfo.setPersonPhoto(customerInfo.getCustomerImage());
			personInfo.setAddressInfo(customerInfo.getCustomerAddress());
			personInfo.setContactNo(customerInfo.getContactNo());
			personInfo.setNationality(nationality);			
			personInfo.setUserCreated(customerInfo.getLogonUserName());	
			personInfo.setUserUpdated(customerInfo.getLogonUserName());
			personRegDate = new PersonRegisterDate();
			personRegDate.setRegisterDate(regDate);
			personRegDate.setUserCreated(customerInfo.getLogonUserName());
			personRegDate.setAgentName(customerInfo.getAgentName());
			personInfo.getPersonRegisterDateList().add(personRegDate);
			personInfo.setPersonVCMStatus(PersonInfo.STATUS_NEW);			
			//title
			personInfo.setPersonTitle(this.defaultTitle);
			//cert type
			personInfo.setPersonCertificate(this.defaultCertificate);
			//category
			personInfo.setPersonCategory(this.defaultCategory);			
			personInfo.setPersonId(null);			
//			/////////////
			//personInfo.setHwPeopleId(personInfo.getCertificateNo());			
			////////////
			this.xFaceBatchService.resetPersonRegDateId(this.transactionId, personInfo);
			this.xFaceBatchService.updatePersonInfoToDB(this.transactionId, personInfo, customerInfo);
			oldValue = personInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(this.transactionId, "create personInfo:"+oldValue+" by "+customerInfo.getLogonUserName()));					
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] success"));
			this.xFaceBatchService.createSystemAudit(this.transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
					, "create personInfo:"+oldValue, SystemAudit.RES_SUCCESS, customerInfo.getLogonUserName());
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {
				result.setStatusCode(ResultStatus.SUCCESS_ADDNEW_CUSTOMER_CODE, ResultStatus.SUCCESS_ADDNEW_CUSTOMER_DESC, "CertificateId:"+customerInfo.getCertificateId());
			}
		}else {			
			//update
			personInfo = new PersonInfo();
			Logger.info(this, LogUtil.getLogInfo(transactionId, "person certificateNo "+customerInfo.getCertificateId()+" then update"));
			personInfo.setPersonId(customerInfo.getPersonId());
			personInfo.setFullName(customerInfo.getCustomerName());			
			personInfo.setCertificateNo(customerInfo.getCertificateId());			
			personInfo.setPersonCode(customerInfo.getCertificateId());			
			personInfo.setPersonPhoto(customerInfo.getCustomerImage());
			personInfo.setContactNo(customerInfo.getContactNo());
			personInfo.setNationality(nationality);
			personInfo.setPersonVCMStatus(PersonInfo.STATUS_UPDATE_ON_XFACE);
			
			//title
			personInfo.setPersonTitle(this.defaultTitle);
			//cert type
			personInfo.setPersonCertificate(this.defaultCertificate);
			//category
			personInfo.setPersonCategory(this.defaultCategory);	
			
			personInfo.setUserUpdated(customerInfo.getLogonUserName());
			//personInfo.setHwPeopleId(customerInfo.getHwPeopleId());
			personInfo.setAddressInfo(customerInfo.getCustomerAddress());
			personInfo.setPersonRegisterDateList(customerInfo.getPersonRegisterDateList());
			personInfo = this.xFaceBatchService.addPersonRegDate(personInfo, regDate, customerInfo.getAgentName(), customerInfo.getLogonUserName());						
			this.xFaceBatchService.resetPersonRegDateId(this.transactionId, personInfo);
			this.xFaceBatchService.updatePersonInfoToDB(this.transactionId, personInfo, customerInfo);
			oldValue = existingPersonInfo.toString();
			String newValue = personInfo.toString();
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personInfo oldValue:"+oldValue+", newValue:"+newValue));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update person certificateNo "+personInfo.getCertificateNo()+" success"));
			this.xFaceBatchService.createSystemAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, "update personInfo oldValue:"+oldValue+", newValue:"+newValue
						, SystemAudit.RES_SUCCESS, customerInfo.getLogonUserName());
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {				
				result.setStatusCode(ResultStatus.SUCCESS_UPDATE_CUSTOMER_CODE, ResultStatus.SUCCESS_UPDATE_CUSTOMER_DESC, "CertificateId:"+customerInfo.getCertificateId());
			}
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo done with result "+result.toString()));		
		return result;		
	}
}
