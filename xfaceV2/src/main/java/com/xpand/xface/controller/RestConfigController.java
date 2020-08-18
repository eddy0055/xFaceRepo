package com.xpand.xface.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.page.PaginationCriteria;
import com.xpand.xface.bean.page.TablePage;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.entity.HWIPC;
import com.xpand.xface.entity.HtmlPageInfo;
import com.xpand.xface.entity.LocationMap;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertificate;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.BoatService;
import com.xpand.xface.service.EquipmentDirectionService;
import com.xpand.xface.service.HWGateInfoService;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.service.HWVCMService;
import com.xpand.xface.service.HtmlPageInfoService;
import com.xpand.xface.service.LocationMapService;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.PersonCertificateService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.PersonNationalityService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.util.LogUtil;

@RestController
@RequestMapping("/rest/cfg")
public class RestConfigController {
	@Autowired
	PersonCertificateService personCertificateService;
	@Autowired
	SystemAuditService sysAuditService;
	@Autowired
	PersonTitleService personTitleService;
	@Autowired
	PersonCategoryService personCategoryService;
	@Autowired
	HWIPCService hwIPCService;
	@Autowired
	HWGateInfoService hwGateInfoService; 
	@Autowired
	PersonNationalityService personNationalityService;
	@Autowired
	XFaceServerService xFaceServerService;
	@Autowired
	ApplicationCfgService appCfg;
	@Autowired
	EquipmentDirectionService eqDirectionService;
	@Autowired
	HWVCMService hwVCMService;
	@Autowired
	LocationMapService locationMapService;
	@Autowired
	PermissionListService permissionListService;
	@Autowired
	PersonInfoService personInfoService;
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	RoleDetailInfoService roleDetailInfoService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	HtmlPageInfoService htmlPageInfoService;
	@Autowired
	BoatService boatService;
	
	@Autowired 
	ApplicationCfgService applicationCfgService;
	
	@RequestMapping("/getPersonCertificate")	
	@PostMapping
	public PersonCertificate getPersonCertificate (HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		return this.personCertificateService.findByCertificateCode(request.getSession().getId(), webFEParam.getCertificateCode());				
	}
	
	@RequestMapping("/getAllPersonCertificate")	
	@GetMapping
	public List<PersonCertificate> getAllPersonCertificate(HttpServletRequest request) {
		return this.personCertificateService.findAll(request.getSession().getId());			
	}
	@RequestMapping(value="/getPersonCertificateList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<PersonCertificate> getPersonCertificateList(HttpServletRequest request, Pageable pageable) {			
		return this.personCertificateService.getPersonCertificateList(request.getSession().getId(), pageable);
	}
	@RequestMapping("/deletePersonCertificate")	
	@PostMapping
	public ResultStatus deletePersonCertificate(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result =this.personCertificateService.delete(transactionId, logonUserName, webFEParam.getCertificateCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete PersonCertificate code:"+webFEParam.getCertificateCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete PersonCertificate code:"+webFEParam.getCertificateCode());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	//Update PersonCertificate 
	@RequestMapping("/updatePersonCertificate")	
	@PostMapping
	public ResultStatus updatePersonCertificate(HttpServletRequest request, @RequestBody PersonCertificate personCertificate) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();		
		try {			
			result = this.personCertificateService.update(transactionId, logonUserName, personCertificate);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update PersonCertificate:"+personCertificate.getCertificateName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "PersonCertificate name:"+personCertificate.getCertificateName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATE, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}	
	
	
	//Get Person Title
	@RequestMapping("/getPersonTitle")	
	@PostMapping
	public PersonTitle getPersonTitle (HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		return this.personTitleService.findByTitleCode(request.getSession().getId(), webFEParam.getPersonTitleCode());
	}
	//Get All Person Title
	@RequestMapping("/getAllPersonTitle")	
	@GetMapping
	public List<PersonTitle> getAllPersonTitle(HttpServletRequest request) {
		return this.personTitleService.findAll(request.getSession().getId());
	}
	@RequestMapping(value="/getPersonTitleList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<PersonTitle> getPersonTitleList(HttpServletRequest request, Pageable pageable) {			
		return this.personTitleService.getPersonTitleList(request.getSession().getId(), pageable);
	}
	
	
	@RequestMapping("/deletePersonTitle")	
	@PostMapping
	public ResultStatus deletePersonTitle(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		
		try {
			result =this.personTitleService.delete(transactionId, logonUserName, webFEParam.getPersonTitleCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete personTitle code:"+webFEParam.getPersonTitleCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete personTitle code:"+webFEParam.getPersonTitleCode());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	
	@RequestMapping("/updatePersonTitle")	
	@PostMapping
	public ResultStatus updatePersonTitle(HttpServletRequest request, @RequestBody PersonTitle personTitle) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();		
		try {			
			result = this.personTitleService.update(transactionId, logonUserName, personTitle);

		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update personTitle:"+personTitle.getTitleName() + personTitle.getTitleCode() + personTitle.getTitleDesc(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "personTitle name:"+personTitle.getTitleName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	
	@RequestMapping("/getAllPersonCategory")	
	@PostMapping
	public List<PersonCategory> getAllPersonCategory(HttpServletRequest request) {
		return this.personCategoryService.findAll(request.getSession().getId());				
	}
	
	
	@RequestMapping("/getPersonCategory")	
	@PostMapping
	public PersonCategory getPersonCategory (HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		return this.personCategoryService.findByCategoryCode(request.getSession().getId(), webFEParam.getCategoryCode());				
	}
	
	@RequestMapping(value="/getPersonCategoryList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<PersonCategory> getPersonCategoryList(HttpServletRequest request, Pageable pageable) {			
		return this.personCategoryService.getPersonCategoryList(request.getSession().getId(), pageable);
	}
	
	@RequestMapping("/deletePersonCategory")	
	@PostMapping
	public ResultStatus deletePersonCategory(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result =this.personCategoryService.delete(transactionId, logonUserName, webFEParam.getCategoryCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete PersonCategory code:"+webFEParam.getCategoryCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete PersonCategory code:"+webFEParam.getCategoryCode());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	
	@RequestMapping("/updatePersonCategory")	
	@PostMapping
	public ResultStatus updatePersonCategory(HttpServletRequest request, @RequestBody PersonCategory personCategory) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();		
		try {			
			result = this.personCategoryService.update(transactionId, logonUserName, personCategory);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update personCategory:"+personCategory.getCategoryName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "personCategory name:"+personCategory.getCategoryName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	//Update Person Nationality
	@RequestMapping("/updatePersonNationality")	
	@PostMapping
	public ResultStatus updatePersonNationality(HttpServletRequest request, @RequestBody PersonNationality personNationality) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();		
		try {			
			result = this.personNationalityService.update(transactionId, logonUserName, personNationality);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update personNationality:"+personNationality.getNationalityCode(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "personNationality name:"+personNationality.getNationalityName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_NATIONALITY, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	
	
	//update updatePageHtml 
	@RequestMapping("/updatePageHtml")
	@PostMapping
	public ResultStatus updatePageHtml(HttpServletRequest request,@RequestBody HtmlPageInfo htmlPageInfo) {     
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result = this.htmlPageInfoService.update(transactionId, logonUserName, htmlPageInfo);
		}catch(Exception ex){
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update htmlPageInfo:"+htmlPageInfo.getPageCode(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "htmlPageInfo name:"+ htmlPageInfo.getPageCode());
		}
		if(!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PAGE_HTML,SystemAudit.MOD_SUB_ALL
					,result.toString(),SystemAudit.RES_FAIL,logonUserName);
		}
	return result;
	}
	
	//Delete Html Page
	@RequestMapping("/deleteHtmlPage")	
	@PostMapping
	public ResultStatus deleteHtmlPage(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result =this.htmlPageInfoService.delete(transactionId, logonUserName, webFEParam.getPageCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete htmlPageCode code:"+webFEParam.getPageCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete htmlPage code:"+webFEParam.getPageCode());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PAGE_HTML, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	
	//Delete Person Nationality
	@RequestMapping("/deletePersonNationality")	
	@PostMapping
	public ResultStatus deletePersonNationality(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result =this.personNationalityService.delete(transactionId, logonUserName, webFEParam.getPersonNationalityCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete Person Nationality code:"+webFEParam.getPersonNationalityCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete Person Nationality Name:"+webFEParam.getPersonNationalityName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	
	//get ALL IPC
	@RequestMapping("/getAllHWIPC")	
	@PostMapping
	public List<HWIPC> getAllHWIPC(HttpServletRequest request) {
		List<HWIPC> hwIPCList = this.hwIPCService.findAll(request.getSession().getId());
		hwIPCList = this.hwIPCService.removeSomeObject(request.getSession().getId(), hwIPCList);
		return hwIPCList;				
	}
	
	@RequestMapping("/getHWIPCByGate")	
	@PostMapping
	public List<HWIPC> getHWIPCByGate(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		List<HWIPC> hwIPCList = this.hwIPCService.getHWIPCByGate(request.getSession().getId(), webFEParam);
		hwIPCList = this.hwIPCService.removeSomeObject(request.getSession().getId(), hwIPCList);
		return hwIPCList;				
	}
	
	//Gate
	@RequestMapping("/getAllHWGate")	
	@PostMapping
	public List<HWGateInfo> getAllHWGate(HttpServletRequest request) {
		List<HWGateInfo> hwGateList = this.hwGateInfoService.findAll(request.getSession().getId());
		hwGateList = this.hwGateInfoService.removeSomeObject(request.getSession().getId(), hwGateList);
		return hwGateList;				
	}

	//get hwIPC without map with gate
	@RequestMapping("/getHWIPCWOGate")	
	@PostMapping
	public List<HWIPC> getHWIPCWOGate(HttpServletRequest request) {
		List<HWIPC> hwIPCList = this.hwIPCService.getHWIPCWOGate(request.getSession().getId());		
		return hwIPCList;				
	}
	
	//nationality
	@RequestMapping("/getAllPersonNationality")	
	@PostMapping
	public List<PersonNationality> getAllPersonNationality(HttpServletRequest request) {
		List<PersonNationality> nationalityList = this.personNationalityService.findAll(request.getSession().getId());
		nationalityList = this.personNationalityService.removeSomeObject(nationalityList);
		return nationalityList;
	}
	
	//html page info
	@RequestMapping("/getAllHtmlPageInfo")	
	@PostMapping
	public List<HtmlPageInfo> getAllHtmlPageInfo(HttpServletRequest request) {
		List<HtmlPageInfo> htmlPageInfoList = this.htmlPageInfoService.findAll(request.getSession().getId());
		htmlPageInfoList = this.htmlPageInfoService.removeSomeObject(htmlPageInfoList);
		return htmlPageInfoList;
	}
	
	//Boat
	@RequestMapping("/getAllBoat")	
	@PostMapping
	public List<Boat> getAllBoat(HttpServletRequest request) {
		List<Boat> boatList = this.boatService.findAll(request.getSession().getId());
		boatList = this.boatService.removeSomeObject(request.getSession().getId(), boatList);
		return boatList;				
	}
		
	
	
	//for page to check is session expire
	@RequestMapping("/isSessionExpire")	
	@PostMapping
	public String getIsSessionExpire() {
		return "OK";				
	}
	
	@RequestMapping("/restartXFaceServer")	
	@PostMapping
	@PreAuthorize(value = "hasRole('RESTART_XFACE_SERVER')")
	public ResultStatus restartXFaceServer() {
		this.xFaceServerService.restart();
		return new ResultStatus();
	}
	@RequestMapping("/purgeCache")	
	@PostMapping	
	public ResultStatus purgeCache() throws Exception {		
		this.appCfg.purgeCache();		
		this.hwIPCService.purgeCache();										
		this.personInfoService.purgeCache();		
		this.userInfoService.purgeCache();		
		//shared cache other with below entity
		this.personTitleService.purgeCache();
		//this.personCategoryService.purgeCache();
		//this.personCertificateService.purgeCache();
		//this.eqDirectionService.purgeCache();
		//this.hwGateInfoService.purgeCache();
		//this.personNationalityService.purgeCache();
		//this.hwVCMService.purgeCache();
		//this.locationMapService.purgeCache();
		//this.permissionListService.purgeCache();
		//this.roleInfoService.purgeCache();
		//this.roleDetailInfoService.purgeCache();
		return new ResultStatus();
	}
	//Table Page ApplicationCfg
	@RequestMapping("/getAppCfgInfoList")	
	@PostMapping
	public @ResponseBody TablePage getApplicationCfgInfoList(HttpServletRequest request, @RequestBody PaginationCriteria treq) throws Exception{									
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in getApplicationInfoList"));		
		return this.applicationCfgService.getApplicationCfgInfoList(request.getSession().getId(), treq);
	}
	//TablePage for PersonTitle
	@RequestMapping("/getPersonTitleInfoList")
	@PostMapping 
	public @ResponseBody TablePage getPersonTitleInfoList(HttpServletRequest request,@RequestBody PaginationCriteria pc) throws Exception {
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in get TablePage person Title InfoList"));
		return this.personTitleService.getPersonTitleInfoList(request.getSession().getId(), pc);
	}
	//TablePage for PersonCertificate 
	@RequestMapping("/getPersonCertificateInfoList")
	@PostMapping 
	public @ResponseBody TablePage getPersonCertificateInfoList(HttpServletRequest request, @RequestBody PaginationCriteria pc) throws Exception{
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in get TablePage personCertificateInfoList"));
		return this.personCertificateService.getPersonCertificateInfoList(request.getSession().getId(), pc);
	}
	//TablePage for PersonNationality
	@RequestMapping("/getPersonNationalityInfoList")
	@PostMapping
	public @ResponseBody TablePage getPersonNationalityInfoList(HttpServletRequest request,@RequestBody PaginationCriteria pc) throws Exception{
		Logger.info(this,LogUtil.getLogInfo(request.getSession().getId(),"in get TablePage personNationality InfoList"));
		return this.personNationalityService.getPersonNationalityInfoList(request.getSession().getId(), pc);
	}
	//TablePage for PersonCategory 
	@RequestMapping("/getPersonCategoryInfoList")
	@PostMapping 
	public @ResponseBody TablePage getPersonCategoryInfoList(HttpServletRequest request, @RequestBody PaginationCriteria pc) throws Exception{
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in get TablePage personCategoryInfoList"));
		return this.personCategoryService.getPersonCategoryInfoList(request.getSession().getId(), pc);
	}
	//TablePage for BoatInfo 
	@RequestMapping("/getBoatInfo")
	@PostMapping 
	public @ResponseBody TablePage getBoatInfo(HttpServletRequest request,@RequestBody PaginationCriteria pc) throws Exception{
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in get TablePage BoatInfo"));
		return this.boatService.getBoatInfo(request.getSession().getId(),pc);
	}
	
	//TablePage for locationMap 
	@RequestMapping("/getLocationMapList")	
	@PostMapping
	public @ResponseBody TablePage getLocationMapList(HttpServletRequest request, @RequestBody PaginationCriteria treq) throws Exception{									
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in getLocationMapList"));		
		return this.locationMapService.getLocationMapList(request.getSession().getId(), treq);
	}
	
	//TablePage for HtmlPage 
	@RequestMapping("/getHtmlPageInfoList")
	@PostMapping 
	public @ResponseBody TablePage getHTmlPageInfoList(HttpServletRequest request, @RequestBody PaginationCriteria pc) throws Exception{
		Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "in get TablePage htmlPageInfoList"));
		return this.htmlPageInfoService.getHtmlPageInfoList(request.getSession().getId(), pc);
	}
		
	@RequestMapping("/getHtmlPageInfo")	
	@PostMapping
	public HtmlPageInfo getHtmlPageInfo(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {		
		HtmlPageInfo htmlPageInfo = this.htmlPageInfoService.findByPageCode(request.getSession().getId(), webFEParam.getPageCode());
		if (htmlPageInfo!=null) {
			htmlPageInfo.setRoleInfoList(null);
			//htmlPageInfo.getRoleInfo().setRoleDetailInfoList(null);
			//htmlPageInfo.getRoleInfo().setHtmlPageInfo(null);			
			//htmlPageInfo.getRoleInfo().setUserInfoList(null);
		}
		return htmlPageInfo;				
	}
	
	
	
	
	//Get AppKey Show in Modal
	@RequestMapping("/getAppKeyDetail")
	@PostMapping
	public ApplicationCfg getAppKeyDetail(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {		
		return this.applicationCfgService.findByAppKey(request.getSession().getId(), webFEParam.getAppKey());				
	}
	
	//Update AppKeyInfo MultiplePart
	@RequestMapping("/updateAppKeyInfo")	
	@PostMapping
	public ResultStatus updateAppKeyInfo(HttpServletRequest request, @RequestPart("applicationCfgInfo") String applicationCfgInfo, @RequestPart(value="personPhoto", required=false) MultipartFile personPhoto) {		
		ApplicationCfg objectApplicationCfgInfo = null;
		ObjectMapper mapper = new ObjectMapper();		
 		ResultStatus result = null;
 		String transactionId = request.getSession().getId();
 		String logonUserName =  request.getUserPrincipal().getName(); 		
 		try {			
 			objectApplicationCfgInfo = mapper.readValue(applicationCfgInfo, ApplicationCfg.class);
 			Logger.debug(this, LogUtil.getLogDebug(transactionId, "in updateAppKeyInfo objectApplicationCfgInfo.getAppDesc()"+ objectApplicationCfgInfo.getAppDesc()));
 			Logger.debug(this, LogUtil.getLogDebug(transactionId, "in updateAppKeyInfo objectApplicationCfgInfo.getAppKey()"+ objectApplicationCfgInfo.getAppKey()));
 			Logger.debug(this, LogUtil.getLogDebug(transactionId, "in updateAppKeyInfo objectApplicationCfgInfo.getAppAppValue1()"+ objectApplicationCfgInfo.getAppValue1()));
 			Logger.debug(this, LogUtil.getLogDebug(transactionId, "in updateAppKeyInfo objectApplicationCfgInfo.getAppAppValue2()"+ objectApplicationCfgInfo.getAppValue2()));
 			Logger.debug(this, LogUtil.getLogDebug(transactionId, "in updateAppKeyInfo objectApplicationCfgInfo.getAppAppValue3()"+ objectApplicationCfgInfo.getAppValue3()));
			result = this.applicationCfgService.update(transactionId, logonUserName, objectApplicationCfgInfo, personPhoto);
 		}catch (JsonMappingException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser applicationCfgInfo:"+applicationCfgInfo, ex));
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, applicationCfgInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
 		}catch (JsonParseException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser applicationCfgInfo:"+applicationCfgInfo, ex));
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, applicationCfgInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
 		}catch (IOException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser applicationCfgInfo:"+applicationCfgInfo, ex));
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, applicationCfgInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
 		}catch(Exception ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while update applicationCfgInfo:"+objectApplicationCfgInfo.getAppDesc(), ex));
 			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "applicationCfgInfo name:"+objectApplicationCfgInfo.getAppDesc());
 			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
 					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
 		}
		return result;
	}

	//Location Map
	@RequestMapping("/getAllMap")	
	@PostMapping
	public List<LocationMap> getAllMap(HttpServletRequest request) {
		List<LocationMap> locationMapList = this.locationMapService.findAll(request.getSession().getId());
		locationMapList = this.locationMapService.removeSomeObject(request.getSession().getId(), locationMapList);
		return locationMapList;				
	}		
	
	//Update MultiplePart Location Map 
	@RequestMapping("/updateLocationMap")
	@PostMapping
	public ResultStatus updateLocationMap(HttpServletRequest request, @RequestPart("mapInfo") String mapInfo, @RequestPart(value="mapPhoto", required=false) MultipartFile mapPhoto) {		
		LocationMap objectLocationMapInfo = null;
		ObjectMapper mapper = new ObjectMapper();	
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			objectLocationMapInfo = mapper.readValue(mapInfo, LocationMap.class);
			result = this.locationMapService.update(transactionId, logonUserName, objectLocationMapInfo,mapPhoto);
		}catch (JsonMappingException ex) {
	 		Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser LocationMapInfo:"+objectLocationMapInfo, ex));
	 		this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
	 				, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, mapInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
	 	}catch (JsonParseException ex) {
	 		Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser LocationMapInfo:"+objectLocationMapInfo, ex));
	 		this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
	 				, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, mapInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
	 	}catch (IOException ex) {
	 		Logger.error(this, LogUtil.getLogError(transactionId, "error while update parser LocationMapInfo:"+objectLocationMapInfo, ex));
	 		this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
	 				, new ResultStatus(ResultStatus.JSON_PARSER_ERROR_CODE, mapInfo).toString(), SystemAudit.RES_FAIL , logonUserName);
	 	}catch(Exception ex) {
	 		Logger.error(this, LogUtil.getLogError(transactionId, "error while update LocationMapInfo:"+ objectLocationMapInfo.getMapName(), ex));
	 		result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "objectLocationMap name:"+ objectLocationMapInfo.getMapName());
	 		this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
	 				, result.toString(), SystemAudit.RES_FAIL , logonUserName);
	 	}
		return result;
	}
	//Update BoatInfo 
	@RequestMapping("/updateBoatInfo")	
	@PostMapping
	public ResultStatus updateBoatInfo(HttpServletRequest request, @RequestBody Boat boatInfo) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName =  request.getUserPrincipal().getName();		
		Logger.debug(this,LogUtil.getLogDebug(transactionId,"Parameter Actioncommand ::" + boatInfo.getActionCommand()));
		Logger.debug(this,LogUtil.getLogDebug(transactionId, "Parameter BoatCode::" + boatInfo.getBoatCode()));
		try {			
			result = this.boatService.update(transactionId, logonUserName, boatInfo);
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "Result Update BoatInfo ::" + result));
			
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update Boat:"+ boatInfo.getBoatName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "Boat name:"+boatInfo.getBoatName());
		}	
		
		
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			Logger.debug(this, LogUtil.getLogDebug(transactionId, "Return Error ::" + result.toString()));
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "Result update BoatInfo ::" + result.toString()));
		return result;				
	}	
	//Delete Location Map
	@RequestMapping("/deleteLocationMap")	
	@PostMapping
	public ResultStatus deleteLocationMap(HttpServletRequest request, @RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result =this.locationMapService.delete(transactionId, logonUserName, webFEParam.getMapCode());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete Map code:"+webFEParam.getMapCode(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete Map code:"+webFEParam.getMapCode());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_LOCATION_MAP, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/deleteBoatInfo")
	@PostMapping
	public ResultStatus deleteBoatInfo(HttpServletRequest request,@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null; 
		String transactionId = request.getSession().getId();
		String logonUserName = request.getUserPrincipal().getName();
		try {
			result = this.boatService.delete(transactionId,logonUserName, webFEParam.getBoatCode());
		}catch(Exception ex){
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete boat code :", webFEParam.getBoatCode(),ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE,"delete Boat code:" + webFEParam.getBoatCode());
		}
		if(!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_BOAT_INFO,SystemAudit.MOD_SUB_ALL
				, result.toString(),SystemAudit.RES_FAIL,logonUserName);
		}
		return result;
	}
	
		
		
}

