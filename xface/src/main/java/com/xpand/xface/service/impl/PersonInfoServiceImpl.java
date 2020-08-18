package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.SearchPersonCondition;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.controller.RestAPIController;
import com.xpand.xface.dao.PersonInfoDAO;
import com.xpand.xface.dao.spec.PersonInfoSpecification;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertification;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.HWAlarmHistService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.PersonCertificationService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Component
public class PersonInfoServiceImpl implements PersonInfoService{
	@Autowired
	PersonInfoDAO personInfoDAO;	
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	HWAPISessionServiceImpl hwAPIService;
	@Autowired
	PersonCategoryService personCategoryService;
	@Autowired
	GlobalVarService globalVarService; 
	@Autowired
	ApplicationCfgService applicationCfgService;
	@Autowired
	PersonCertificationService personCertificationService;
	@Autowired
	HWAlarmHistService hwAlarmHistService; 
	@Autowired
	PersonTitleService personTitleService; 
	
	
	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="#root.methodName+'_'+#personId")
	public PersonInfo findByPersonId(Integer personId, String className) {
		return this.clearSomeObject(className, this.personInfoDAO.findByPersonId(personId));		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="#root.methodName+'_'+#certificationNo")
	public PersonInfo findByCertificationNo(String certificationNo, String className) {		
		return this.clearSomeObject(className, this.personInfoDAO.findByCertificationNo(certificationNo));		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="#root.methodName+'_'+#hwPeopleId")
	public PersonInfo findByHwPeopleId(String hwPeopleId, String className) {
		return this.clearSomeObject(className, this.personInfoDAO.findByHwPeopleId(hwPeopleId));
	}
		
	@Override
	public List<PersonInfo> findAll(String className) {
		return this.clearSomeObject(className, this.personInfoDAO.findAll());
	}

	@Override
	public Page<PersonInfo> getPersonInfoList(Pageable pageable, String className) {
		Page<PersonInfo> pagePersonInfo = this.personInfoDAO.findAll(pageable);
		this.clearSomeObject(className, pagePersonInfo.getContent());
		return pagePersonInfo;
		
	}
	
	public List<PersonInfo> clearSomeObject(String className, List<PersonInfo> personInfoList) {
		if (personInfoList==null) {
			return null;
		}		
		for (int i=0;i<personInfoList.size();i++) {
			this.clearSomeObject(className, personInfoList.get(i));					
		}
		return personInfoList;
	}
	private PersonInfo clearSomeObject(String className, PersonInfo personInfo) {
		if (personInfo==null) {
			return null;
		}
		if (RestAPIController.CLASS_NAME.equals(className)) {
			if (personInfo!=null && personInfo.getPersonCategory()!=null) {			
//				personInfo.getPersonCategory().getHwIPCAnalyzeList().setHwIPCs(null);				
//				personInfo.getPersonCategory().getHwIPCAnalyzeList().setHwVCM(null);
				personInfo.getPersonCategory().setHwIPCAnalyzeList(null);
			}
		}		
		return personInfo;
	}

	@Override
	public ResultStatus update(String transactionId, String logonUserName, PersonInfo personInfo, MultipartFile personPhoto) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo start"));
		if (personInfo.getPersonTitle()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "title");
		}else if (StringUtil.checkNull(personInfo.getPersonTitle().getTitleId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "title");
		}else if (personInfo.getPersonCertification()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Certification Type");
		}else if (StringUtil.checkNull(personInfo.getPersonCertification().getCertificationId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Certification Type");
		}else if (StringUtil.checkNull(personInfo.getCertificationNo())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CertificationNo");
		}else if (StringUtil.checkNull(personInfo.getFirstName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "FirstName");
		}else if (StringUtil.checkNull(personInfo.getLastName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "LastName");
		}else if (personInfo.getPersonCategory()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Category");
		}else if (StringUtil.checkNull(personInfo.getPersonCategory().getCategoryId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Category");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;		
		PersonInfo existingPersonInfo = this.personInfoDAO.findByCertificationNo(personInfo.getCertificationNo());
		if (existingPersonInfo==null) {
			if (personPhoto==null) {
				result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Photo");
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
				return result;
			}
		}
		if (existingPersonInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificationNo "+personInfo.getCertificationNo()+" not found then create"));
			personInfo = new PersonInfo(personInfo);
			personInfo.setPersonPhoto(ImageUtil.getImageFromMultipartFile(personPhoto));
			personInfo.setUserCreated(logonUserName);	
			personInfo.setUserUpdated(logonUserName);
			personInfo.setPersonId(null);			
			result = this.createToVCM(transactionId, personInfo);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.createToDB(personInfo);
				oldValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personInfo:"+oldValue+" by "+logonUserName));					
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, "create personInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
		}else {
			boolean updatePhoto = false;
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found person certificationNo "+personInfo.getCertificationNo()+" then update"));
			personInfo.setPersonId(existingPersonInfo.getPersonId());
			if (personPhoto==null) {
				personInfo.setPersonPhoto(existingPersonInfo.getPersonPhoto());
			}else {
				updatePhoto = true;
				personInfo.setPersonPhoto(ImageUtil.getImageFromMultipartFile(personPhoto));
			}
			personInfo.setUserUpdated(logonUserName);
			personInfo.setHwPeopleId(existingPersonInfo.getHwPeopleId());
			result = this.updateToVCM(transactionId, personInfo, existingPersonInfo, updatePhoto);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.updateToDB(personInfo);
				oldValue = existingPersonInfo.toString();
				String newValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personInfo oldValue:"+oldValue+", newValue:"+newValue));
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update person certificationNo "+personInfo.getCertificationNo()+" success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
							, "update personInfo oldValue:"+oldValue+", newValue:"+newValue
							, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo done with result "+result.toString()));
		return result;
	}

	@Override
	@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="#root.methodName+'_'+#certificationNo")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	public ResultStatus delete(String transactionId, String logonUserName, String certificationNo) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personInfo start"));
		ResultStatus result = new ResultStatus();
		PersonInfo personInfo = this.personInfoDAO.findByCertificationNo(certificationNo);
		if (personInfo!=null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found person certificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] then delete"));
			String oldValue = personInfo.toString();
			//update alarm personinfo = null
			this.hwAlarmHistService.removeRelationshipPersonInfo(personInfo);
			this.personInfoDAO.delete(personInfo.getPersonId());
			this.deleteFromVCM(transactionId, personInfo);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personInfo oldValue:"+oldValue+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete person certificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
					, "delete personInfo oldValue:"+oldValue+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "personInfo certificationNo "+certificationNo+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "certificationNo:"+certificationNo);
		}			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personInfo done with result "+result.toString()));
		return result;
	}	
	
	@CachePut(value=CacheName.CACHE_PERSONINFO, key="#root.methodName+'_'+#result.certificationNo")
	public PersonInfo createToDB(PersonInfo personInfo) {
		this.personInfoDAO.save(personInfo);
		return personInfo;
	}
	
	private ResultStatus createToVCM(String transactionId, PersonInfo personInfo){
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start create personInfo to VCM"));
		HWWSField result = null;		
		//get vcm
		PersonCategory pc = this.personCategoryService.findById(personInfo.getPersonCategory().getCategoryId(), null);
		if (pc==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}			
		PersonCertification pct = this.personCertificationService.findById(personInfo.getPersonCertification().getCertificationId(), null);
		if (pct==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CERTIFICATION_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}
		personInfo.setPersonCategory(pc);
		personInfo.setPersonCertification(pct);
//		String fileName = personPhoto.getOriginalFilename();
//		String fileSize = personPhoto.getSize()+"";
//		String fileType = ImageUtil.getImageTypeFromMultipartFile(personPhoto);		
//		//1. get upload image		
//		result = this.hwAPIService.getUploadFileURL(fileName, fileSize+"");
//		if (result.getResult().getStatusCode()!=ResultStatus.SUCCESS_CODE) {
//			return result.getResult();
//		}
//		//2. upload image
//		InputStream inputStream = null;
//		try {
//			inputStream = personPhoto.getInputStream();
//			result = this.hwAPIService.uploadFileToServer(fileName, fileSize, result.getUploadFileFileId(), fileType, result.getUploadFileURL(), personPhoto.getInputStream());
//			if (result.getResult().getStatusCode()!=ResultStatus.SUCCESS_CODE) {
//				return result.getResult();
//			}
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while get photo inputstream:"+personPhoto.getOriginalFilename(), ex));
// 			return new ResultStatus(ResultStatus.FILE_NOT_FOUND_ERROR_CODE, "file name:"+personPhoto.getOriginalFilename());
//		}finally {
//			ImageUtil.closeInputStream(inputStream);
//		}
//		//3. public image
//		result = this.hwAPIService.publishUploadFile(fileName, fileSize, result.getUploadFileFileId());
//		if (result.getResult().getStatusCode()!=ResultStatus.SUCCESS_CODE) {
//			return result.getResult();
//		}
		//4. add face to list
//		result = this.hwAPIService.addFaceToList(personInfo.getPersonCategory().getHwIPCAnalyzeList(), personInfo, null, result.getUploadCaseFileId());
		//1.add face to list by base64 image
		this.hwAPIService.initialClass(transactionId, this.applicationCfgService.getAllInHashMap(), pc.getHwIPCAnalyzeList().getHwVCM(), this.globalVarService);
		result = this.hwAPIService.addFaceToList(personInfo.getPersonCategory().getHwIPCAnalyzeList(), personInfo, ImageUtil.getBase64StringWithOutHeader(personInfo.getPersonPhoto()), null);
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			personInfo.setHwPeopleId(result.getAddFaceToListId());
		}else {
			return result.getResult();
		}		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create personInfo on VCM:"+ personInfo.getPersonCategory().getHwIPCAnalyzeList().getHwVCM().getVcmName()+" success"));
		return result.getResult();				
	}
	private ResultStatus updateToVCM(String transactionId, PersonInfo personInfo, PersonInfo existingPersonInfo, boolean updatePhoto) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start update personInfo on VCM"));
		if (personInfo.getCertificationNo().equals(existingPersonInfo.getCertificationNo())
			&& personInfo.getPersonCertification().getCertificationId()==existingPersonInfo.getPersonCertification().getCertificationId()
			&& personInfo.getFullName().equals(existingPersonInfo.getFullName())
			&& !updatePhoto) {
			//certNo same, certType same, full name same, not update photo
			Logger.info(this, LogUtil.getLogInfo(transactionId, "person certification no:"+personInfo.getCertificationNo()+" nothing need to update to VCM"));
			return new ResultStatus();
		}
		HWWSField result = null;				
		//get vcm
		PersonCategory pc = this.personCategoryService.findById(personInfo.getPersonCategory().getCategoryId(), null);
		if (pc==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}			
		PersonCertification pct = this.personCertificationService.findById(personInfo.getPersonCertification().getCertificationId(), null);
		if (pct==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CERTIFICATION_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}
		personInfo.setPersonCategory(pc);
		personInfo.setPersonCertification(pct);
		//1.add face to list by base64 image
		this.hwAPIService.initialClass(transactionId, this.applicationCfgService.getAllInHashMap(), pc.getHwIPCAnalyzeList().getHwVCM(), this.globalVarService);
		result = this.hwAPIService.modifyFaceToList(personInfo.getPersonCategory().getHwIPCAnalyzeList(), personInfo, ImageUtil.getBase64StringWithOutHeader(personInfo.getPersonPhoto()), personInfo.getHwPeopleId());
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			return result.getResult();
		}		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create personInfo on VCM:"+ personInfo.getPersonCategory().getHwIPCAnalyzeList().getHwVCM().getVcmName()+" success"));
		return result.getResult();	
	}
	private ResultStatus deleteFromVCM(String transactionId, PersonInfo personInfo) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start delete personInfo from VCM"));
		HWWSField result = null;
		this.hwAPIService.initialClass(transactionId, this.applicationCfgService.getAllInHashMap(), personInfo.getPersonCategory().getHwIPCAnalyzeList().getHwVCM(), this.globalVarService);
		result = this.hwAPIService.removeFaceFromList(personInfo.getPersonCategory().getHwIPCAnalyzeList() , personInfo);		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personInfo from VCM with result:"+result.getResult().toString()));
		return result.getResult();
	}
	
	@CachePut(value=CacheName.CACHE_PERSONINFO, key="#root.methodName+'_'+#personInfo.certificationNo")
	public PersonInfo updateToDB(PersonInfo personInfo) {
		this.personInfoDAO.save(personInfo);
		return personInfo;
	}

	@Override
	public PersonInfo findOneByWebFEParam(WebFEParam webFEParam, String className) {
		List<PersonInfo> personInfoList = null;
		//personInfoList = this.personInfoDAO.findAll(new PersonInfoSpecification(webFEParam));
		Specifications<PersonInfo> specPersonInfo = null;
		for (SearchPersonCondition spc: webFEParam.getSearchPersonConditionList()) {
			if (!(StringUtil.checkNull(spc.getSearchField())||StringUtil.checkNull(spc.getSearchValue()))) {
				PersonInfoSpecification spec1 = new PersonInfoSpecification(new SearchPersonCondition(spc.getSearchField(), spc.getSearchOperation(), spc.getSearchValue()));
				if (specPersonInfo==null) {
					specPersonInfo = Specifications.where(spec1);
				}else {
					specPersonInfo = specPersonInfo.and(spec1);
				}
			}					
		}	
		personInfoList = this.personInfoDAO.findAll(specPersonInfo);		
		if (personInfoList!=null && personInfoList.size()>0) {
			PersonInfo personInfo = personInfoList.get(0);
			if (RestAPIController.CLASS_NAME.equals(className)) {					
				personInfo.setHwAlarmHist(null);			
			}			
			return this.clearSomeObject(className, personInfo);			
		}else {
			return null;
		}		
	}

	@Override
	public ResultStatus register(String transactionId, String logonUserName, CustomerRegister customerInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "receive request:"+customerInfo.toString()));
//		String fileType = ImageUtil.getImageTypeFromBase64(customerInfo.getCustomerImage());
//		String fileName = "c://temp//file_"+StringUtil.dateToString(new Date(), StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)+"."+fileType;
//		String base64Data = ImageUtil.getBase64StringWithOutHeader(customerInfo.getCustomerImage());
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "base64 data is "+base64Data));
//		boolean resultImage = ImageUtil.base64ToFile(base64Data, fileName);
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "convert image to file "+fileName+" with result:"+resultImage));
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo start"));
		if (customerInfo.getCertificateId()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CertificateId");
		}else if (StringUtil.checkNull(customerInfo.getCustomerName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CustomerName");
		}else if (StringUtil.checkNull(customerInfo.getTravelDate())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "TravelDate");
		}else if (StringUtil.checkNull(customerInfo.getCustomerImage())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CustomerImage");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
			return result;
		}		
		String oldValue = null;		
		PersonInfo existingPersonInfo = this.personInfoDAO.findByCertificationNo(customerInfo.getCertificateId());
		PersonInfo personInfo = null;
		if (existingPersonInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificationNo "+customerInfo.getCertificateId()+" not found then create"));
			personInfo = new PersonInfo();
			personInfo.setFirstName(customerInfo.getCustomerName());
			personInfo.setLastName("");
			personInfo.setCertificationNo(customerInfo.getCertificateId());			
			personInfo.setPersonCode(customerInfo.getCertificateId());			
			personInfo.setPersonPhoto(customerInfo.getCustomerImage());
			personInfo.setAddressInfo(customerInfo.getCustomerAddress());
			personInfo.setUserCreated(logonUserName);	
			personInfo.setUserUpdated(logonUserName);
			//title
			personInfo.setPersonTitle(this.personTitleService.findById(1, null));
			//cert type
			personInfo.setPersonCertification(this.personCertificationService.findById(1, null));
			//category
			personInfo.setPersonCategory(this.personCategoryService.findById(1, null));			
			personInfo.setPersonId(null);			
			result = this.createToVCM(transactionId, personInfo);
			result = new ResultStatus(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS_DESC);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.createToDB(personInfo);
				oldValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personInfo:"+oldValue+" by "+logonUserName));					
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, "create personInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {
				result.setStatusCode(ResultStatus.SUCCESS_ADDNEW_CUSTOMER_CODE, ResultStatus.SUCCESS_ADDNEW_CUSTOMER_DESC, "CertificationId:"+customerInfo.getCertificateId());
			}
		}else {			
			//update
			personInfo = new PersonInfo();
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found person certificationNo "+customerInfo.getCertificateId()+" then update"));
			personInfo.setPersonId(existingPersonInfo.getPersonId());
			personInfo.setFirstName(customerInfo.getCustomerName());
			personInfo.setLastName("");
			personInfo.setCertificationNo(customerInfo.getCertificateId());			
			personInfo.setPersonCode(customerInfo.getCertificateId());			
			personInfo.setPersonPhoto(customerInfo.getCustomerImage());
			//title
			personInfo.setPersonTitle(this.personTitleService.findById(1, null));
			//cert type
			personInfo.setPersonCertification(this.personCertificationService.findById(1, null));
			//category
			personInfo.setPersonCategory(this.personCategoryService.findById(1, null));								
			personInfo.setUserUpdated(logonUserName);
			personInfo.setHwPeopleId(existingPersonInfo.getHwPeopleId());
			personInfo.setAddressInfo(customerInfo.getCustomerAddress());
			result = this.updateToVCM(transactionId, personInfo, existingPersonInfo, true);
			result = new ResultStatus(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS_DESC);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.updateToDB(personInfo);
				oldValue = existingPersonInfo.toString();
				String newValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personInfo oldValue:"+oldValue+", newValue:"+newValue));
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update person certificationNo "+personInfo.getCertificationNo()+" success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
							, "update personInfo oldValue:"+oldValue+", newValue:"+newValue
							, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificationNo "+personInfo.getCertificationNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {				
				result.setStatusCode(ResultStatus.SUCCESS_UPDATE_CUSTOMER_CODE, ResultStatus.SUCCESS_UPDATE_CUSTOMER_DESC, "CertificationId:"+customerInfo.getCertificateId());
			}
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo done with result "+result.toString()));		
		return result;
	}

	@Override
	public List<PersonSummaryInfo> findPersonSummaryInfo() {
		return this.personInfoDAO.findPersonSummaryInfo();
	}
}
