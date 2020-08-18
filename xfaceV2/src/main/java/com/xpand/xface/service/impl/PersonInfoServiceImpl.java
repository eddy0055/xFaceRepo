package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.HWWSField;
import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.SearchPersonCondition;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.bean.query.QueryPersonByPhotoListRespV1;
import com.xpand.xface.bean.query.QueryPersonByPhotoRespV1;
import com.xpand.xface.bean.query.WebFEPersonByPhotoRespV1;
import com.xpand.xface.bean.query.photo.ResultDataFace;
import com.xpand.xface.bean.query.photo.ResultDataIPC;
import com.xpand.xface.bean.query.photo.ResultDataMap;
import com.xpand.xface.bean.query.photo.ResultDataPerson;
import com.xpand.xface.bean.query.photo.ResultPersonRespList;
import com.xpand.xface.bean.query.photo.ResultQueryPersonTrace;
import com.xpand.xface.bean.report.ReportFEParam;
import com.xpand.xface.bean.report.google.PassengerRegisterField;
import com.xpand.xface.dao.PersonInfoDAO;
import com.xpand.xface.dao.spec.PersonInfoSpecification;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertificate;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.entity.PersonRegisterDate;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.HWAlarmHistService;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.service.HWVCMService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.PersonCertificateService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.PersonNationalityService;
import com.xpand.xface.service.PersonRegDateService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.hwapi.HWAPISessionService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.ImageUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.OtherUtil;
import com.xpand.xface.util.StringUtil;

@SessionScope
@Component
public class PersonInfoServiceImpl implements PersonInfoService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	PersonInfoDAO personInfoDAO;	
	@Autowired
	SystemAuditService systemAuditService;
	@Autowired
	HWAPISessionService hwAPIService;
	@Autowired
	PersonCategoryService personCategoryService;
	@Autowired
	GlobalVarService globalVarService; 
	@Autowired
	ApplicationCfgService applicationCfgService;
	@Autowired
	PersonCertificateService personCertificateService;
	@Autowired
	HWAlarmHistService hwAlarmHistService; 
	@Autowired
	PersonTitleService personTitleService;
	@Autowired
	HWIPCService hwIPCService; 
	@Autowired
	HWVCMService hwVCMService;
	@Autowired
	PersonRegDateService personRegDateService;
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	PersonNationalityService personNationalityService;
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="'key_'+#personId")
	public PersonInfo findByPersonId(String transactionId, Integer personId) {
		PersonInfo personInfo = this.personInfoDAO.findByPersonId(personId);
		return personInfo;		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="'key_'+#certificateNo")
	public PersonInfo findByCertificateNo(String transactionId, String certificateNo) {
		PersonInfo personInfo = this.personInfoDAO.findByCertificateNo(certificateNo);
		return personInfo;		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_PERSONINFO, key="'key_'+#hwPeopleId")
	public PersonInfo findByHwPeopleId(String transactionId, String hwPeopleId) {
		PersonInfo personInfo = this.personInfoDAO.findByHwPeopleId(hwPeopleId);
		return personInfo;
	}
		
	@Override
	public List<PersonInfo> findAll(String transactionId) {
		return this.clearSomeObject(transactionId, this.personInfoDAO.findAll());
	}

	@Override
	public TablePage getPersonInfoList(String transactionId, PaginationCriteria pc) {	
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getPersonInfoList with certNo:"+pc.getCertificateNo()+", fullName:"+pc.getFullName()+", start:"+pc.getStart()+", pageSize:"+pc.getLength()));
		String queryStmtCount = "SELECT COUNT(*)"; 
		String queryStmtSelect = "SELECT per.person_id, CONVERT(AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS certificate_no, CONVERT(AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS full_name, per.person_code, per.hw_people_id, per.person_photo, nat.nationality_name ";
		String queryStmtCondition = "FROM tbl_person_info per INNER JOIN tbl_person_nationality nat ON per.nationality_id=nat.nationality_id ";
		
		String queryStmtOrder = "";
		boolean isWhereCause = false;
		if (!StringUtil.checkNull(pc.getPersonRegisterDate())) {
			queryStmtCondition += "LEFT JOIN tbl_person_reg_date dat ON per.person_id=dat.person_id "; 
		}
		if (!StringUtil.checkNull(pc.getCertificateNo())) {
			queryStmtCondition += "WHERE AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"')=?";
			isWhereCause = true;
		}else if (!StringUtil.checkNull(pc.getFullName())) {
			queryStmtCondition += "WHERE AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') LIKE ?";
			isWhereCause = true;
		}	
		
		//nationality
		String arrayNationalityCodeList[] = null;
		if (!StringUtil.checkNull(pc.getNationalityCodeList())) {
			arrayNationalityCodeList = pc.getNationalityCodeList().split(ConstUtil.STRING_ID_DELIMINATOR);
			if (isWhereCause) {
				queryStmtCondition += " AND ";
			}else {
				queryStmtCondition += "WHERE ";
				isWhereCause = true;
			}
			queryStmtCondition += " nat.nationality_code IN (";
			for (String nationalityCode: arrayNationalityCodeList) {
				queryStmtCondition += "?,";
			}
			queryStmtCondition = queryStmtCondition.substring(0, queryStmtCondition.length()-1)+")";
		}
		//register date
		if (!StringUtil.checkNull(pc.getPersonRegisterDate())) {
			if (isWhereCause) {
				queryStmtCondition += " AND dat.register_date=? ";
			}else {
				queryStmtCondition += "WHERE dat.register_date=? ";
			}			
		}
		List<PersonInfo> personInfoList = new ArrayList<PersonInfo>();
		String sortOrder = "ASC";
		String sortColumn = "person_id";
		long numberOfRecord = 0;
//		int pageNumber = pc.getStart() / pc.getLength();
		TablePage tablePage = new TablePage();
		if (pc.getOrder().size()>0){
			Logger.info(this, LogUtil.getLogInfo(transactionId, "sort column:"+pc.getColumns().get(pc.getOrder().get(0).getColumn()).getName())+" order:"+pc.getOrder().get(0).getDir());			
			sortOrder = pc.getOrder().get(0).getDir().toUpperCase();
			sortColumn = pc.getColumns().get(pc.getOrder().get(0).getColumn()).getName(); 			
		}	
		queryStmtOrder = "ORDER BY "+sortColumn+" "+sortOrder+" LIMIT "+pc.getStart()+","+pc.getLength();		
		try {			
			ArrayList<Object> paramList = new ArrayList<>();			
			if (!StringUtil.checkNull(pc.getCertificateNo())) {
				paramList.add(pc.getCertificateNo());
			}else if (!StringUtil.checkNull(pc.getFullName())) {
				paramList.add("%"+pc.getFullName()+"%");
			}
			if (arrayNationalityCodeList!=null) {
				for (String nationalityCode: arrayNationalityCodeList) {
					paramList.add(nationalityCode);
				}
			}
			if (!StringUtil.checkNull(pc.getPersonRegisterDate())) {
				paramList.add(StringUtil.stringToDate(pc.getPersonRegisterDate(), StringUtil.DATE_FORMAT_YYYYMMDD));
			}
			List<Object[]> queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmtSelect+" "+queryStmtCondition+" "+queryStmtOrder, paramList);						
			PersonInfo personInfo = null;
			for (Object[] queryResult : queryResultList) {
				personInfo = new PersonInfo();
				personInfo.createPersonInfo(queryResult);
				personInfoList.add(personInfo);				
			}
			numberOfRecord = OtherUtil.doQueryCount(this, transactionId, this.entityManager, queryStmtCount+" "+queryStmtCondition, paramList);				
			tablePage.setDraw(pc.getDraw());		
	        tablePage.setRecordsTotal(numberOfRecord);        
	        tablePage.setRecordsFiltered(numberOfRecord);	        
	        Logger.info(this, LogUtil.getLogInfo(transactionId, "get data"));
	        tablePage.setData(tablePage.getPageEntries(transactionId, personInfoList));	        
			Logger.info(this, LogUtil.getLogInfo(transactionId, "getPersonInfoList no of result:"+ personInfoList.size() + " record from total:"+numberOfRecord));
		} catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data personInfoList with certNo:"+pc.getCertificateNo()+", fullName:"+pc.getFullName(), ex));			
		}			                 
		return tablePage;		
	}
	
	@Override
	public List<PersonInfo> clearSomeObject(String transactionId, List<PersonInfo> personInfoList) {
		if (personInfoList==null) {
			return null;
		}		
		for (int i=0;i<personInfoList.size();i++) {
			this.clearSomeObject(transactionId, personInfoList.get(i));					
		}
		return personInfoList;
	}
	
	@Override
	public PersonInfo clearSomeObject(String transactionId, PersonInfo personInfo) {
		if (personInfo==null) {
			return null;
		}		
		if (personInfo!=null && personInfo.getPersonCategory()!=null) {			
			personInfo.getPersonCategory().setHwCheckPointLibrary(null);
		}		
		return personInfo;
	}

	@Override
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#personInfo.personId"),
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#personInfo.certificateNo"),
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#personInfo.hwPeopleId")
			}
		)
	public ResultStatus update(String transactionId, String logonUserName, PersonInfo personInfo, MultipartFile personPhoto) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo start"));
		if (personInfo.getPersonTitle()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "title");
		}else if (StringUtil.checkNull(personInfo.getPersonTitle().getTitleId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "title");
		}else if (personInfo.getPersonCertificate()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Certificate Type");
		}else if (StringUtil.checkNull(personInfo.getPersonCertificate().getCertificateId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Certificate Type");
		}else if (StringUtil.checkNull(personInfo.getCertificateNo())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "CertificateNo");
		}else if (StringUtil.checkNull(personInfo.getFullName())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "FullName");
		}else if (personInfo.getPersonCategory()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Category");
		}else if (StringUtil.checkNull(personInfo.getPersonCategory().getCategoryId().toString())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Category");
		}else if (personInfo.getNationality()==null) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Nationality");
		}else if (StringUtil.checkNull(personInfo.getNationality().getNationalityCode())) {
			result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Nationality");
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
			return result;
		}
		String oldValue = null;		
		//certificate
		PersonInfo existingPersonInfo = this.personInfoDAO.findByCertificateNo(personInfo.getCertificateNo());		
		if (personInfo.getActionCommand().equals(ConstUtil.ACTION_COMMAND_ADD)) {
			if (existingPersonInfo!=null) {
				//error certificateNo already exist				
				result.setStatusCode(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE, "CertificateNo="+personInfo.getCertificateNo());
			}
		}else if (existingPersonInfo==null) { 
			//error cannot find certificateNo for update
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "CertificateNo="+personInfo.getCertificateNo());
		}
		
		
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo fail with result "+result.toString()));
			return result;
		}				
		if (existingPersonInfo==null) {
			if (personPhoto==null) {
				result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "Photo");
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update userinfo fail with result "+result.toString()));
				return result;
			}
		}
		if (existingPersonInfo==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificateNo "+personInfo.getCertificateNo()+" not found then create"));
			personInfo = new PersonInfo(personInfo);
			personInfo.setPersonPhoto(ImageUtil.getImageFromMultipartFile(personPhoto));
			personInfo.setUserCreated(logonUserName);	
			personInfo.setUserUpdated(logonUserName);
			personInfo.setPersonId(null);
			personInfo.setPersonVCMStatus(PersonInfo.STATUS_NEW);
			//result = this.createToVCM(transactionId, personInfo);
			result = new ResultStatus();
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.createToDB(personInfo);
				oldValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personInfo:"+oldValue+" by "+logonUserName));					
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, "create personInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
		}else {
			boolean updatePhoto = false;
			//update
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found person certificateNo "+personInfo.getCertificateNo()+" then update"));
			personInfo.setPersonId(existingPersonInfo.getPersonId());
			if (personPhoto==null) {
				personInfo.setPersonPhoto(existingPersonInfo.getPersonPhoto());
			}else {
				updatePhoto = true;
				personInfo.setPersonPhoto(ImageUtil.getImageFromMultipartFile(personPhoto));
			}
			personInfo.setUserUpdated(logonUserName);
			personInfo.setHwPeopleId(existingPersonInfo.getHwPeopleId());
			personInfo.setPersonVCMStatus(PersonInfo.STATUS_UPDATE_ON_XFACE);
//			result = this.updateToVCM(transactionId, personInfo, existingPersonInfo, updatePhoto);
			result = new ResultStatus();
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.updateToDB(transactionId, personInfo);
				oldValue = existingPersonInfo.toString();
				String newValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personInfo oldValue:"+oldValue+", newValue:"+newValue));
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update person certificateNo "+personInfo.getCertificateNo()+" success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
							, "update personInfo oldValue:"+oldValue+", newValue:"+newValue
							, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo done with result "+result.toString()));
		return result;
	}
	
	
	@Override	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
				@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#personInfo.personId"),
				@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#certificateNo"),
				@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#personInfo.hwPeopleId")
			}
		)
	public ResultStatus delete(String transactionId, String logonUserName, PersonInfo personInfo, String certificateNo) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personInfo start"));
		ResultStatus result = new ResultStatus();				
		if (personInfo.getPersonId()==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "personInfo certificateNo "+certificateNo+" not found"));
			result.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "certificateNo:"+certificateNo);
		}else {		
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found person certificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] then delete"));
			String oldValue = personInfo.toString();
			//update alarm personinfo = null
			this.hwAlarmHistService.removeRelationshipPersonInfo(transactionId, personInfo);
			this.deleteFromVCM(transactionId, personInfo);
			this.personInfoDAO.delete(personInfo.getPersonId());			
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "delete personInfo oldValue:"+oldValue+" by "+logonUserName));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete person certificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] success"));
			this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
					, "delete personInfo oldValue:"+oldValue+" by "+logonUserName
					, SystemAudit.RES_SUCCESS, logonUserName);
		}			
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personInfo done with result "+result.toString()));
		return result;
	}	
		
	public PersonInfo createToDB(PersonInfo personInfo) {
		this.personRegDateService.resetPersonRegDateId(personInfo);
		this.personInfoDAO.save(personInfo);
		return personInfo;
	}
	
	private ResultStatus createToVCM(String transactionId, PersonInfo personInfo){
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start create personInfo to VCM"));
		HWWSField result = null;		
		//get vcm
		PersonCategory pc = this.personCategoryService.findById(transactionId, personInfo.getPersonCategory().getCategoryId());
		if (pc==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}			
		PersonCertificate pct = this.personCertificateService.findById(transactionId, personInfo.getPersonCertificate().getCertificateId());
		if (pct==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CERTIFICATE_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}
		personInfo.setPersonCategory(pc);
		personInfo.setPersonCertificate(pct);
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
		this.hwAPIService.initialClass(transactionId);
		result = this.hwAPIService.addFaceToFaceList(personInfo.getPersonCategory().getHwCheckPointLibrary(), personInfo, ImageUtil.getBase64StringWithOutHeader(personInfo.getPersonPhoto()), null);
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			personInfo.setHwPeopleId(result.getAddFaceToListId());
		}else {
			return result.getResult();
		}		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create personInfo on VCM:"+ personInfo.getPersonCategory().getHwCheckPointLibrary().getHwVCM().getVcmName()+" success"));
		return result.getResult();				
	}
	
	
	private ResultStatus updateToVCM(String transactionId, PersonInfo personInfo, PersonInfo existingPersonInfo, boolean updatePhoto) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start update personInfo on VCM"));
		if (personInfo.getCertificateNo().equals(existingPersonInfo.getCertificateNo())
			&& personInfo.getPersonCertificate().getCertificateId()==existingPersonInfo.getPersonCertificate().getCertificateId()
			&& personInfo.getFullName().equals(existingPersonInfo.getFullName())
			&& !updatePhoto) {
			//certNo same, certType same, full name same, not update photo
			Logger.info(this, LogUtil.getLogInfo(transactionId, "person certificate no:"+personInfo.getCertificateNo()+" nothing need to update to VCM"));
			return new ResultStatus();
		}
		HWWSField result = null;				
		//get vcm
		PersonCategory pc = this.personCategoryService.findById(transactionId, personInfo.getPersonCategory().getCategoryId());
		if (pc==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}			
		PersonCertificate pct = this.personCertificateService.findById(transactionId, personInfo.getPersonCertificate().getCertificateId());
		if (pct==null) {
			return new ResultStatus(ResultStatus.ENTITY_PERSON_CERTIFICATE_NOT_FOUND_ERROR_CODE, personInfo.getPersonCategory().getCategoryId()+"");
		}
		personInfo.setPersonCategory(pc);
		personInfo.setPersonCertificate(pct);
		//1.add face to list by base64 image
		this.hwAPIService.initialClass(transactionId);
		result = this.hwAPIService.modifyFaceToFaceList(personInfo.getPersonCategory().getHwCheckPointLibrary(), personInfo, ImageUtil.getBase64StringWithOutHeader(personInfo.getPersonPhoto()), personInfo.getHwPeopleId());
		if (result.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			return result.getResult();
		}		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "create personInfo on VCM:"+ personInfo.getPersonCategory().getHwCheckPointLibrary().getHwVCM().getVcmName()+" success"));
		return result.getResult();	
	}
	private ResultStatus deleteFromVCM(String transactionId, PersonInfo personInfo) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start delete personInfo from VCM"));		
		if (personInfo.getHwPeopleId()==null) {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "no need to delete person on VCM coz hwPeopleId is null"));
			return new ResultStatus();
		}			
		HWWSField result = null;
		this.hwAPIService.initialClass(transactionId);
		result = this.hwAPIService.removeFaceFromFaceList(personInfo.getPersonCategory().getHwCheckPointLibrary(), personInfo);		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "delete personInfo from VCM with result:"+result.getResult().toString()));
		return result.getResult();
	}		
	public PersonInfo updateToDB(String transactionId, PersonInfo personInfo) {
		this.personRegDateService.resetPersonRegDateId(personInfo);
		this.personInfoDAO.save(personInfo);		
		return personInfo;
	}

	@Override
	public PersonInfo findOneByWebFEParam(String transactionId, WebFEParam webFEParam) {
		List<PersonInfo> personInfoList = null;
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
			personInfo.setHwAlarmHistList(null);						
			return this.clearSomeObject(transactionId, personInfo);			
		}else {
			return null;
		}		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED)
	@Caching(
			evict = { 
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#existingPersonInfo.personId"),
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#customerInfo.certificateId"),
					@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#existingPersonInfo.hwPeopleId")
			}
		)
	public ResultStatus register(String transactionId, String logonUserName, CustomerRegister customerInfo, PersonInfo existingPersonInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "receive request:"+customerInfo.toStringNoImage()));
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
		//code is lower case		
		PersonNationality nationality = null;
		if (!StringUtil.checkNull(customerInfo.getNationality())) {
			nationality = this.personNationalityService.findByNationalityCode(transactionId, customerInfo.getNationality().toLowerCase());
			if (nationality==null) {
				nationality = new PersonNationality();
				nationality.setNationalityCode(customerInfo.getNationality().toLowerCase());
				nationality.setNationalityName(customerInfo.getNationality());
				this.personNationalityService.update(transactionId, logonUserName, nationality);
			}			
		}
		String oldValue = null;		
		PersonInfo personInfo = null;
		PersonRegisterDate personRegDate = null;
		Date regDate = StringUtil.stringToDate(customerInfo.getTravelDate(), StringUtil.DATE_FORMAT_YYYYMMDD);
		if (existingPersonInfo.getPersonId()==null) {
			//create
			Logger.info(this, LogUtil.getLogInfo(transactionId, "certificateNo "+customerInfo.getCertificateId()+" not found then create"));
			personInfo = new PersonInfo();
			personInfo.setFullName(customerInfo.getCustomerName());			
			personInfo.setCertificateNo(customerInfo.getCertificateId());			
			personInfo.setPersonCode(customerInfo.getCertificateId());			
			personInfo.setPersonPhoto(customerInfo.getCustomerImage());
			personInfo.setAddressInfo(customerInfo.getCustomerAddress());
			personInfo.setContactNo(customerInfo.getContactNo());
			personInfo.setNationality(nationality);			
			personInfo.setUserCreated(logonUserName);	
			personInfo.setUserUpdated(logonUserName);
			personRegDate = new PersonRegisterDate();
			personRegDate.setRegisterDate(regDate);
			personRegDate.setUserCreated(logonUserName);
			personRegDate.setAgentName(customerInfo.getAgentName());
			personInfo.getPersonRegisterDateList().add(personRegDate);
			personInfo.setPersonVCMStatus(PersonInfo.STATUS_NEW);
			//title
			personInfo.setPersonTitle(this.personTitleService.findById(transactionId, 1));
			//cert type
			personInfo.setPersonCertificate(this.personCertificateService.findById(transactionId, 1));
			//category
			personInfo.setPersonCategory(this.personCategoryService.findById(transactionId, 1));			
			personInfo.setPersonId(null);			
//			result = this.createToVCM(transactionId, personInfo);
			/////////////
			personInfo.setHwPeopleId(personInfo.getCertificateNo());
			////////////
			result = new ResultStatus(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS_DESC);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.createToDB(personInfo);
				oldValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create personInfo:"+oldValue+" by "+logonUserName));					
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, "create personInfo:"+oldValue, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {
				result.setStatusCode(ResultStatus.SUCCESS_ADDNEW_CUSTOMER_CODE, ResultStatus.SUCCESS_ADDNEW_CUSTOMER_DESC, "CertificateId:"+customerInfo.getCertificateId());
			}
		}else {			
			//update
			personInfo = new PersonInfo();
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found person certificateNo "+customerInfo.getCertificateId()+" then update"));
			personInfo.setPersonId(existingPersonInfo.getPersonId());
			personInfo.setFullName(customerInfo.getCustomerName());			
			personInfo.setCertificateNo(customerInfo.getCertificateId());			
			personInfo.setPersonCode(customerInfo.getCertificateId());			
			personInfo.setPersonPhoto(customerInfo.getCustomerImage());
			personInfo.setContactNo(customerInfo.getContactNo());
			personInfo.setNationality(nationality);
			personInfo.setPersonVCMStatus(PersonInfo.STATUS_UPDATE_ON_XFACE);
			//title
			personInfo.setPersonTitle(this.personTitleService.findById(transactionId, 1));
			//cert type
			personInfo.setPersonCertificate(this.personCertificateService.findById(transactionId, 1));
			//category
			personInfo.setPersonCategory(this.personCategoryService.findById(transactionId, 1));											
			personInfo.setUserUpdated(logonUserName);
			personInfo.setHwPeopleId(existingPersonInfo.getHwPeopleId());
			personInfo.setAddressInfo(customerInfo.getCustomerAddress());
			personInfo.setPersonRegisterDateList(existingPersonInfo.getPersonRegisterDateList());
			personInfo = this.personRegDateService.addPersonRegDate(personInfo, regDate, customerInfo.getAgentName(), logonUserName);
//			result = this.updateToVCM(transactionId, personInfo, existingPersonInfo, true);
			result = new ResultStatus(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS_DESC);
			if (result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
				this.updateToDB(transactionId, personInfo);
				oldValue = existingPersonInfo.toString();
				String newValue = personInfo.toString();
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "update personInfo oldValue:"+oldValue+", newValue:"+newValue));
				Logger.info(this, LogUtil.getLogInfo(transactionId, "update person certificateNo "+personInfo.getCertificateNo()+" success"));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
							, "update personInfo oldValue:"+oldValue+", newValue:"+newValue
							, SystemAudit.RES_SUCCESS, logonUserName);
			}else {
				Logger.info(this, LogUtil.getLogInfo(transactionId, "create personCertificateNo "+personInfo.getCertificateNo()+"["+personInfo.getPersonId()+"] to VCM fail with result:"+result.toString()));
				this.systemAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
						, result.toString(), SystemAudit.RES_FAIL, logonUserName);
			}
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {				
				result.setStatusCode(ResultStatus.SUCCESS_UPDATE_CUSTOMER_CODE, ResultStatus.SUCCESS_UPDATE_CUSTOMER_DESC, "CertificateId:"+customerInfo.getCertificateId());
			}
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "update personInfo done with result "+result.toString()));		
		return result;
	}
	
	//update to queue
	@Override
	public ResultStatus registerV2(String transactionId, String logonUserName, CustomerRegister customerInfo, PersonInfo existingPersonInfo) {
		ResultStatus result = new ResultStatus();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "receive request:"+customerInfo.toStringNoImage()));
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
		customerInfo.setLogonUserName(logonUserName);		
		if (existingPersonInfo.getPersonId()==null) {
			//create			
			customerInfo.setActionCommand(ConstUtil.ACTION_COMMAND_ADD);
			this.globalVarService.pushCustomerRegister(customerInfo, null);			
			result.setStatusCode(ResultStatus.SUCCESS_ADDNEW_CUSTOMER_CODE, ResultStatus.SUCCESS_ADDNEW_CUSTOMER_DESC, "CertificateId:"+customerInfo.getCertificateId());
			Logger.info(this, LogUtil.getLogInfo(transactionId, "push certificateNo "+customerInfo.getCertificateId()+" to queue for create"));
			
		}else {			
			//update
			customerInfo.setActionCommand(ConstUtil.ACTION_COMMAND_EDIT);
			customerInfo.setPersonId(existingPersonInfo.getPersonId());
			customerInfo.setPersonRegisterDateList(existingPersonInfo.getPersonRegisterDateList());
			this.globalVarService.pushCustomerRegister(customerInfo, existingPersonInfo);
			if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {				
				result.setStatusCode(ResultStatus.SUCCESS_UPDATE_CUSTOMER_CODE, ResultStatus.SUCCESS_UPDATE_CUSTOMER_DESC, "CertificateId:"+customerInfo.getCertificateId());
			}
		}				
		Logger.info(this, LogUtil.getLogInfo(transactionId, "push customer register to queue with result:"+result.toString()));		
		return result;
	}

	@Override
	public List<PersonSummaryInfo> findPersonSummaryInfo(String transactionId) {
		return this.personInfoDAO.findPersonSummaryInfo();
	}
	
	@Override
	public ResultPersonRespList personTrace(String transactionId, WebFEParam webFEParam, MultipartFile personPhoto) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in personTrace"));
		ResultPersonRespList personRespList = null;
		if (StringUtil.checkNull(webFEParam.getPersonCertificateNo()) && StringUtil.checkNull(webFEParam.getFullName())) {
			//search by photo
			//personRespList = this.personTraceByPhoto(transactionId, webFEParam, personPhoto);
			personRespList = this.personTraceByAttribute(transactionId, webFEParam);
		}else {
			//search by attribute
			personRespList = this.personTraceByAttribute(transactionId, webFEParam);
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "out personTrace found "+personRespList.getDataPersonList().size()+" person"));
		return personRespList;
	}
	public ResultPersonRespList personTraceByAttribute(String transactionId, WebFEParam webFEParam) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in personTraceByAttribute"));
		//1. get all alarm which match to condition		
		String queryStmt = "SELECT ah.alarm_time, ah.live_photo, per.person_photo, CONVERT(AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS full_name, CONVERT(AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"') USING UTF8) AS certificate_no,tit.title_name,cert.certificate_name"
				+",cam.ipc_code, cam.ipc_name,cam.map_locationx,cam.map_locationy,map.map_code, map.map_name, map.map_photo,ah.alarm_code,map.map_id,cam.ipc_id "
				+"FROM Tbl_alarm_hist ah LEFT JOIN Tbl_person_info per ON ah.person_id=per.person_id "
				+"LEFT JOIN tbl_person_title tit ON per.title_id=tit.title_id "
				+"LEFT JOIN tbl_person_certificate cert ON per.certificate_id=cert.certificate_id "
				+"INNER JOIN tbl_ipc cam ON ah.ipc_id=cam.ipc_id "
				+"INNER JOIN tbl_location_map map ON cam.map_id=map.map_id "
				+"WHERE ah.alarm_time >= ? AND ah.alarm_time < ? ";
		ArrayList<Object> paramList = new ArrayList<>();
		//start, end in format yyyyMMddhhmm
		Date startDate = StringUtil.stringToDate(webFEParam.getStartDate()+"00", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		Date endDate = StringUtil.stringToDate(webFEParam.getEndDate()+"00", StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
		paramList.add(startDate);
		paramList.add(endDate);
		if (!StringUtil.checkNull(webFEParam.getPersonCertificateNo())) {
			queryStmt += "AND AES_DECRYPT(FROM_BASE64(per.certificate_no),'"+ConstUtil.MY_VALUE_TEST+"')=? ";
			paramList.add(webFEParam.getPersonCertificateNo());
		}else if(!StringUtil.checkNull(webFEParam.getFullName())) {
			queryStmt += "AND AES_DECRYPT(FROM_BASE64(per.full_name),'"+ConstUtil.MY_VALUE_TEST+"') LIKE ? ";			
			paramList.add("%"+webFEParam.getFullName()+"%");
		}
		queryStmt += "ORDER BY per.person_id,map.map_id, cam.ipc_id,ah.alarm_time DESC";
		ResultStatus resultStatus = new ResultStatus();
		List<Object[]> queryResultList = null;
		ResultQueryPersonTrace personTrace = null;
		ResultPersonRespList personRespList = new ResultPersonRespList();
		int maximumAllowNoPerson = StringUtil.stringToInteger(this.applicationCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_FACE_PERSON_TRACE_SIZE).getAppValue1(), 3);
		String unknownPersonDBPhoto = this.applicationCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_IMAGE_UNKNOWN_PERSON).getAppLobValue(); 
		try {
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, paramList);								
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {					
					if (personRespList.getDataPersonList().size() < maximumAllowNoPerson) {
						personTrace = new ResultQueryPersonTrace(queryResult);
						if (ConstUtil.UNKNOWN_PERSON_DBPHOTO.equals(personTrace.getDbPhoto())){
							personTrace.setDbPhoto(unknownPersonDBPhoto);
						}
						//create data
						personRespList = this.createAttributeResponseData(personTrace, personRespList);
					}								
				}						
				Logger.debug(this, LogUtil.getLogDebug(transactionId,  "found peson trace start:"+StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)
				+"end:"+StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)+" "+personRespList.getDataPersonList().size()+" record and limit is:"+maximumAllowNoPerson));
				personRespList = this.getLatestLocation(personRespList);
			}else {
				//record no found
				Logger.debug(this, LogUtil.getLogDebug(transactionId,  "person trace start:"+StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)
						+"end:"+StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)+" not found."));
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data person trace with start:"+StringUtil.dateToString(startDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS)
			+"end:"+StringUtil.dateToString(endDate, StringUtil.DATE_FORMAT_YYYYMMDDHHMMSS), ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "person trace");
		}
		personRespList.setResultStatus(resultStatus);
		Logger.info(this, LogUtil.getLogInfo(transactionId, "out personTraceByAttribute"));
		return personRespList;
	}
	private ResultPersonRespList createAttributeResponseData(ResultQueryPersonTrace personTrace, ResultPersonRespList personRespList) {
		ResultDataPerson dataPerson = null;
		ResultDataMap dataMap = null;
		ResultDataIPC dataIPC = null;
		ResultDataFace dataFace = null;
		int personIndex = 0;
		int mapIndex = 0;
		int ipcIndex = 0;
		//check person 
		personIndex = personRespList.getDataPerson(personTrace.getCertificateNo());
		if (personIndex==-1) {
			dataPerson = new ResultDataPerson();
			dataPerson.setCertificateNo(personTrace.getCertificateNo());
			dataPerson.setCertificateType(personTrace.getCertificateType());
			dataPerson.setDbPhoto(personTrace.getDbPhoto());
			dataPerson.setFullName(personTrace.getFullName());
			dataPerson.setTitle(personTrace.getTitle());
			personRespList.getDataPersonList().add(dataPerson);
		}else {
			dataPerson = personRespList.getDataPersonList().get(personIndex);
		}
		//check map
		mapIndex = dataPerson.getDataMap(personTrace.getMapCode());
		if (mapIndex==-1) {						
			//not found then add new
			dataMap = new ResultDataMap();
			dataMap.setMapCode(personTrace.getMapCode());
			dataMap.setMapPhoto(personTrace.getMapPhoto());
			dataMap.setMapName(personTrace.getMapName());
			dataMap.setMapId(personTrace.getMapId());
			dataPerson.getDataMapList().add(dataMap);						
		}else {
			dataMap = dataPerson.getDataMapList().get(mapIndex);
		}
		//check IPC		
		ipcIndex = dataMap.getDataIpc(personTrace.getIpcCode());
		if (ipcIndex==-1) {						
			//not found then add new
			dataIPC = new ResultDataIPC();
			dataIPC.setIpcName(personTrace.getIpcName());
			dataIPC.setIpcCode(personTrace.getIpcCode());
			dataIPC.setLocationX(personTrace.getMapLocationX());
			dataIPC.setLocationY(personTrace.getMapLocationY());
			dataIPC.setIpcId(personTrace.getIpcId());
			dataMap.getDataIpcList().add(dataIPC);
		}else {
			dataIPC = dataMap.getDataIpcList().get(ipcIndex);
		}
		//allow to add only 1 face for each person, map, ipc
		if(dataIPC.getDataFaceList().size() > 0) {
			dataIPC.getDataFaceList().get(0).increaseNoOfFace(personTrace.getAlarmCode());
		}else {
			dataFace = new ResultDataFace();
			dataFace.setAlarmCode(personTrace.getAlarmCode());
			dataFace.setFacePhoto(personTrace.getLivePhoto());
			dataFace.setPhotoDate(personTrace.getAlarmDate());
			dataFace.setNoOfFace(1);
			dataFace.setAlarmCodeList(personTrace.getAlarmCode());
			dataIPC.getDataFaceList().add(dataFace);
		}		
		return personRespList;
	}
	public ResultPersonRespList personTraceByPhoto(String transactionId, WebFEParam webFEParam, MultipartFile personPhoto) {
		//fake result
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in/out personTraceByPhoto"));
		return this.personTraceByAttribute(transactionId, webFEParam);
	}	
	
	//check for latest location of each person
	private ResultPersonRespList getLatestLocation(ResultPersonRespList personRespList) {
		ResultDataIPC tmpIPC = null;
		for (ResultDataPerson dataPerson: personRespList.getDataPersonList()) {
			for (ResultDataMap dataMap: dataPerson.getDataMapList()) {
				for (ResultDataIPC dataIPC: dataMap.getDataIpcList()) {
					if (tmpIPC==null ||
						tmpIPC.getDataFaceList().get(0).getPhotoDate().getTime() < dataIPC.getDataFaceList().get(0).getPhotoDate().getTime()) {
						tmpIPC = dataIPC;
					}					
				}				
			}
			tmpIPC.setLatest(true);
			tmpIPC = null;
		}
		return personRespList;
	}
	
	public WebFEPersonByPhotoRespV1 findPersonByPhoto(String transactionId, Date startDate, Date endDate, String searchPhoto, int confidenceThreshold) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in findPersonByPhoto with photo:"+searchPhoto));		
		List<HWVCM> hwVCMList = this.hwVCMService.getAll(transactionId);		
		Iterator<HWIPC> hwIPCList = null;		
		ArrayList<String> hwIPCCodeList = new ArrayList<>();		
		HWWSField searchResult = null;
		HashMap<String, QueryPersonByPhotoListRespV1> searchResultList = new HashMap<String, QueryPersonByPhotoListRespV1>();
		int pageNo = 0;
		QueryPersonByPhotoListRespV1 queryPersonList = null;
		boolean isLoop = true;
		for (HWVCM hwVCM: hwVCMList) {			
			Logger.info(this, LogUtil.getLogInfo(transactionId, "check VCM:"+hwVCM.getVcmName()));
			hwIPCList = hwVCM.getHwIPCList().iterator();
			while(hwIPCList.hasNext()) {
				hwIPCCodeList.add(hwIPCList.next().getIpcCode());
			}			
			this.hwAPIService.initialClass(transactionId);			
			searchResult = null;			
			pageNo = 0;
			isLoop = true;
			while(isLoop) {
				pageNo++;
				searchResult = this.hwAPIService.queryPersonByPhoto(hwVCM, hwIPCCodeList, searchPhoto, pageNo, startDate, endDate, confidenceThreshold); 
				if (searchResult.getResult().getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
					queryPersonList = searchResult.getQueryPersonByPhotoListResp();
					for (QueryPersonByPhotoRespV1 queryPerson:searchResult.getQueryPersonByPhotoListResp().getResultList()) {
						queryPersonList.addResultList(queryPerson);
					}																		
					if (queryPersonList.getTotalRecord()==queryPersonList.getResultList().size()) {
						isLoop = false;
					}
				}else {
					isLoop = false;
				}
			}						
		} //for (HWVCM hwVCM: hwVCMList) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "queryPerson done then generate result"));
		WebFEPersonByPhotoRespV1 webFEPersonByPhotoResp = new WebFEPersonByPhotoRespV1(transactionId, searchResultList);
		return webFEPersonByPhotoResp;
	}
	
	@Override
	public ArrayList<PassengerRegisterField> getPassengerRegistered(String transactionId, ReportFEParam param) {
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "in getPassengerRegistered:"+param.getStartDate()));
		List<Object[]> queryResultList = null;
		PassengerRegisterField passengerField = null;
		ArrayList<PassengerRegisterField> resultList = new ArrayList<>();
		Date startDate = StringUtil.stringToDate(param.getStartDate(), StringUtil.DATE_FORMAT_YYYYMMDD);		
		String queryStmt = "SELECT nat.nationality_name,0 AS noOfVisited, COUNT(per.person_id) AS noOfRegistered " 
				+ "FROM tbl_person_info per "
				+ "INNER JOIN tbl_person_reg_date reg ON per.person_id=reg.person_id "
				+ "INNER JOIN tbl_person_nationality nat ON per.nationality_id=nat.nationality_id "
				+ "WHERE register_date = ? GROUP BY nat.nationality_name ORDER BY nat.nationality_name";				
		ResultStatus resultStatus = new ResultStatus();
		try {					
			queryResultList = OtherUtil.doQuery(this, transactionId, this.entityManager, queryStmt, startDate);								
			if (queryResultList.size()>0) {
				for (Object[] queryResult: queryResultList) {
					passengerField = new PassengerRegisterField(queryResult);
					resultList.add(passengerField);
				}						
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerRegistered with result:"+resultList.size()+" record"));				
			}else {
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "getPassengerRegistered 0 record"));
			}
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while read data personInfo get passenger registerd", ex));
			resultStatus.setStatusCode(ResultStatus.DB_READ_FAIL_ERROR_CODE, "personInfo get passenger registerd");
		}							
		Logger.info(this,  LogUtil.getLogInfo(transactionId, "out getPassengerRegistered with result:"+resultStatus.toString()));
		return resultList;
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_PERSONINFO, key="'key_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_PERSONINFO, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub		
	}

	
}
