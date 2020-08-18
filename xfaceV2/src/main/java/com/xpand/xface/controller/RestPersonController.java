package com.xpand.xface.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.log.Logger;
import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.bean.query.photo.ResultPersonRespList;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.PersonCertificateService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.PersonNationalityService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@RestController
@RequestMapping("/rest/person")
public class RestPersonController {
	
	public static String CLASS_NAME=RestPersonController.class.getName();	
	@Autowired
	PersonInfoService personInfoService;
	@Autowired
	PersonTitleService personTitleService;
	@Autowired
	PersonCategoryService personCategoryService;
	@Autowired
	PersonCertificateService personCertificateService;
	@Autowired
	PersonNationalityService personNationalityService;
	@Autowired
	SystemAuditService sysAuditService;
	
	
	@RequestMapping("/getAllPersonInfo")	
	@GetMapping
	public List<PersonInfo> getAllPersonInfo(HttpServletRequest request) {
		return this.personInfoService.findAll(LogUtil.getWebSessionId());			
	}
	
	@RequestMapping("/getPersonInfoList")	
	@PostMapping
	public @ResponseBody TablePage getPersonInfoList(HttpServletRequest request, @RequestBody PaginationCriteria treq) throws Exception{							
		String transactionId = request.getSession().getId();
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in getPersonInfoList:certNo:"+treq.getCertificateNo()));		
		return this.personInfoService.getPersonInfoList(transactionId, treq);
	}
	
	@RequestMapping("/getPersonInfo")	
	@PostMapping
	public PersonInfo getPersonInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		String transactionId = request.getSession().getId();
		PersonInfo personInfo = this.personInfoService.findByCertificateNo(transactionId, webFEParam.getPersonCertificateNo());
		personInfo = this.personInfoService.clearSomeObject(transactionId, personInfo);
		return personInfo;	
	}
	
	@RequestMapping("/updatePersonInfo")	
	@PostMapping
	public ResultStatus updatePersonInfo(HttpServletRequest request, @RequestPart("personInfo") String personInfo, @RequestPart(value="personPhoto", required=false) MultipartFile personPhoto) {		
		PersonInfo objectPersonInfo = null;
		ObjectMapper mapper = new ObjectMapper();		
 		ResultStatus result = null;
 		String transactionId = request.getSession().getId();
 		String logonUserName =  request.getUserPrincipal().getName(); 		
 		try {			
 			objectPersonInfo = mapper.readValue(personInfo, PersonInfo.class);
 			if (StringUtil.checkNull(objectPersonInfo.getPersonTitleCode())) {
 				objectPersonInfo.setPersonTitle(null);
 			}else {
 				objectPersonInfo.setPersonTitle(this.personTitleService.findByTitleCode(transactionId, objectPersonInfo.getPersonTitleCode()));
 			}
 			if (StringUtil.checkNull(objectPersonInfo.getPersonCertificateCode())) {
 				objectPersonInfo.setPersonCertificate(null);
 			}else {
 				objectPersonInfo.setPersonCertificate(this.personCertificateService.findByCertificateCode(transactionId, objectPersonInfo.getPersonCertificateCode()));
 			}
 			if (StringUtil.checkNull(objectPersonInfo.getPersonCategoryCode())) {
 				objectPersonInfo.setPersonCategory(null);
 			}else {
 				objectPersonInfo.setPersonCategory(this.personCategoryService.findByCategoryCode(transactionId, objectPersonInfo.getPersonCategoryCode()));
 			}
 			if (StringUtil.checkNull(objectPersonInfo.getPersonNationalityCode())) {
 				objectPersonInfo.setNationality(null);
 			}else {
 				objectPersonInfo.setNationality(this.personNationalityService.findByNationalityCode(transactionId, objectPersonInfo.getPersonNationalityCode()));
 			}
			result = this.personInfoService.update(transactionId, logonUserName, objectPersonInfo, personPhoto);
 		}catch (JsonMappingException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser personInfo:"+personInfo, ex));
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, personInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
 		}catch (JsonParseException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser personInfo:"+personInfo, ex));
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, personInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
 		}catch (IOException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser personInfo:"+personInfo, ex));
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, personInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
 		}catch(Exception ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update personInfo:"+objectPersonInfo.getFullName(), ex));
 			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "personInfo name:"+objectPersonInfo.getFullName());
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
 		}
		return result;
	}
	
	@RequestMapping("/deletePersonInfo")	
	@PostMapping
	public ResultStatus deletePersonInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			PersonInfo personInfo = this.personInfoService.findByCertificateNo(transactionId, webFEParam.getPersonCertificateNo());
			if (personInfo==null) {
				personInfo = new PersonInfo();
			}
			result = this.personInfoService.delete(transactionId, logonUserName, personInfo, webFEParam.getPersonCertificateNo());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete personInfo code:"+webFEParam.getPersonCertificateNo(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete personInfo code:"+webFEParam.getPersonCertificateNo());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}	
	
	@RequestMapping("/registerCustomer")	
	@PostMapping
	public ResultStatus registerCustomer(HttpServletRequest request, @RequestBody CustomerRegister customerInfo) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();		
		try {			
			PersonInfo existingPersonInfo = this.personInfoService.findByCertificateNo(transactionId, customerInfo.getCertificateId());
			if (existingPersonInfo==null) {
				existingPersonInfo = new PersonInfo();
			}
			result = this.personInfoService.register(transactionId, logonUserName, customerInfo, existingPersonInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while register customer certificateId:"+customerInfo.getCertificateId(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "PersonInfo certificateId:"+customerInfo.getCertificateId());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;			
	}
	@RequestMapping("/registerCustomerV2")	
	@PostMapping
	public ResultStatus registerCustomerV2(HttpServletRequest request, @RequestBody CustomerRegister customerInfo) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();		
		//Logger.info(this,  LogUtil.getLogInfo(transactionId, "xxxxx "+customerInfo.getLogonUserName()));
		String logonUserName = "auto"; //request.getUserPrincipal().getName();		
		try {			
			PersonInfo existingPersonInfo = this.personInfoService.findByCertificateNo(transactionId, customerInfo.getCertificateId());
			if (existingPersonInfo==null) {
				existingPersonInfo = new PersonInfo();
			}
			result = this.personInfoService.registerV2(transactionId, logonUserName, customerInfo, existingPersonInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while register customer certificateId:"+customerInfo.getCertificateId(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "PersonInfo certificateId:"+customerInfo.getCertificateId());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;			
	}
	
	// call from PersonTrace.js -> reloadPageData //
	@RequestMapping("/personTrace")	
	@PostMapping
	public ResultPersonRespList personTrace(HttpServletRequest request, @RequestPart("webFEParam") String webFEParam, @RequestPart(value="searchPhoto", required=false) MultipartFile searchPhoto) {		
		WebFEParam objectWebFEParam = null;
		ObjectMapper mapper = new ObjectMapper();		
		ResultPersonRespList personByPhotoListResp = new ResultPersonRespList();
 		String transactionId = request.getSession().getId(); 		
 		Logger.info(this, LogUtil.getLogInfo(transactionId, "personTrace recv request"));
 		try {			
 			objectWebFEParam = mapper.readValue(webFEParam, WebFEParam.class); 			
 			personByPhotoListResp = this.personInfoService.personTrace(transactionId, objectWebFEParam, searchPhoto);
 			personByPhotoListResp.setResultStatus(new ResultStatus());
 		}catch (JsonMappingException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while parser webFEParam:"+webFEParam, ex));
 			personByPhotoListResp.setResultStatus(new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, "webFEParam:"+webFEParam));
 		}catch (JsonParseException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while parser webFEParam:"+webFEParam, ex));
 			personByPhotoListResp.setResultStatus(new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, "webFEParam:"+webFEParam));
 		}catch (IOException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while parser webFEParam:"+webFEParam, ex));
 			personByPhotoListResp.setResultStatus(new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, "webFEParam:"+webFEParam));
 		}catch(Exception ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while search person by photo:"+webFEParam, ex));
 			personByPhotoListResp.setResultStatus(new ResultStatus(ResultStatus.DB_READ_FAIL_ERROR_CODE, "webFEParam:"+webFEParam));
 		} 		
 		return personByPhotoListResp;
	}	
	//////////////////////////////////////////////////
	
	@RequestMapping("/getPersonSummaryInfoList")	
	@GetMapping
	public List<PersonSummaryInfo> getPersonSummaryInfoList(HttpServletRequest request) {
		return this.personInfoService.findPersonSummaryInfo(request.getSession().getId());				
	}
}
