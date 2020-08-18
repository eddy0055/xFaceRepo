package com.xpand.xface.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.xpand.xface.bean.CustomerRegister;
import com.xpand.xface.bean.PersonNotification;
import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.entity.IPCGroup;
import com.xpand.xface.entity.LocationArea;
import com.xpand.xface.entity.LocationBuilding;
import com.xpand.xface.entity.LocationFloor;
import com.xpand.xface.entity.PermissionList;
import com.xpand.xface.entity.PersonCategory;
import com.xpand.xface.entity.PersonCertification;
import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonTitle;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.entity.SystemAudit;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.IPCGroupService;
import com.xpand.xface.service.LocationAreaService;
import com.xpand.xface.service.LocationBuildingService;
import com.xpand.xface.service.LocationFloorService;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.service.PersonCategoryService;
import com.xpand.xface.service.PersonCertificationService;
import com.xpand.xface.service.PersonInfoService;
import com.xpand.xface.service.PersonNotificationService;
import com.xpand.xface.service.PersonTitleService;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.service.SystemAuditService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@RestController
@RequestMapping("/rest")
public class RestAPIController {
	
	public static String CLASS_NAME=RestAPIController.class.getName();
	@Autowired
	PersonNotificationService personNotificationService;
	@Autowired
	PersonInfoService personInfoService;
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	RoleDetailInfoService roleDetailInfoService;
	@Autowired
	IPCGroupService ipcGroupService;
	@Autowired
	PermissionListService permissionListService;
	@Autowired
	SystemAuditService sysAuditService;
	@Autowired
	LocationBuildingService locationBuildingService;
	@Autowired
	LocationFloorService locationFloorService;
	@Autowired
	LocationAreaService locationAreaService;
	@Autowired
	XFaceServerService xfaceServerService;
	@Autowired
	PersonTitleService personTitleService;
	@Autowired
	PersonCategoryService personCategoryService;
	@Autowired
	PersonCertificationService personCertificationService;
	
	
	@RequestMapping("/getPersonNotificationByAlarm/{alarmId}")
	@ResponseBody
	public PersonNotification getPersonNotificationByAlarm(@PathVariable("alarmId") String alarmId) {
		return this.personNotificationService.getPersonByAlarmId(StringUtil.stringToInteger(alarmId,0));
	}
	@RequestMapping("/getListPersonNotificationByDate/{inquiryDate}")
	@ResponseBody
	public ArrayList<PersonNotification> getListPersonNotificationByDate(@PathVariable("inquiryDate") String inquiryDate) {
		return this.personNotificationService.getListPersonByDate(inquiryDate);
	}
	@RequestMapping("/getListPersonNotificationByDate")
	@ResponseBody
	public ArrayList<PersonNotification> getListPersonNotification() {
		return this.personNotificationService.getListPersonByDate(null);
	}
	@RequestMapping("/getPersonNotificationByPersonId/{personOrPassport}")
	@ResponseBody
	public PersonNotification getPersonNotificationByPersonPassport(@PathVariable("personOrPassport") String personOrPassport) {
		return this.personNotificationService.getPersonByAlarmId(StringUtil.stringToInteger(personOrPassport,0));
	}
			
	@RequestMapping(value="/getUserInfoList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<UserInfo> getUserInfoList(Pageable pageable) {				
		return this.userInfoService.getUserInfoList(pageable, RestAPIController.CLASS_NAME);
	}
	@RequestMapping("/getUserInfo")	
	@PostMapping
	public UserInfo getUserInfo(@RequestBody WebFEParam webFEParam) {			
		return this.userInfoService.findByUserName(webFEParam.getUserName(), RestAPIController.CLASS_NAME);				
	}
	@RequestMapping("/getAllRole")	
	@GetMapping
	public List<RoleInfo> getAllRole() {
		return this.roleInfoService.findAll();				
	}
	@RequestMapping("/getRoleInfoList")	
	@GetMapping
	public Page<RoleInfo> getRoleInfoList(Pageable pageable) {			
		return this.roleInfoService.getRoleInfoList(pageable);
	}
	@RequestMapping("/getRoleInfo")	
	@PostMapping
	public RoleInfo getRoleInfo(@RequestBody WebFEParam webFEParam) {
		return this.roleInfoService.findOneByRoleName(webFEParam.getRoleName());				
	}
	@RequestMapping("/getRoleDetailInfoList")	
	@PostMapping
	public List<RoleDetailInfo> getRoleDetailInfoList(@RequestBody WebFEParam webFEParam) {
		return this.roleDetailInfoService.findByRoleInfoRoleName(webFEParam.getRoleName());				
	}
	@RequestMapping("/getPersonInfo")	
	@PostMapping
	public PersonInfo getPersonInfo(@RequestBody WebFEParam webFEParam) {
		//return this.personInfoService.findByCertificationNo(webFEParam.getSearchPersonConsitionList().get(0).getSearchValue(), RestAPIController.CLASS_NAME);				
		return this.personInfoService.findOneByWebFEParam(webFEParam, RestAPIController.CLASS_NAME);
	}
	@RequestMapping("/getAllIPCGroup")	
	@GetMapping
	public List<IPCGroup> getAllIPCGroup() {
		return this.ipcGroupService.findAll();				
	}
	@RequestMapping("/getAllPermissionList")	
	@GetMapping
	public List<PermissionList> getAllPermissionList() {
		return this.permissionListService.findAll();				
	}
	@RequestMapping(value="/getLocationBuildingList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<LocationBuilding> getLocationBuildingList(Pageable pageable) {			
		return this.locationBuildingService.getLocationBuildingList(pageable,RestAPIController.CLASS_NAME);
	}
	
	@RequestMapping("/getLocationBuilding")	
	@PostMapping
	public LocationBuilding getLocationBuilding(@RequestBody WebFEParam webFEParam) {
		return this.locationBuildingService.findByBuildingName(webFEParam.getBuildingName(), RestAPIController.CLASS_NAME);				
	}
	@RequestMapping("/getLocationFloorByBuildingName")	
	@PostMapping
	public LocationBuilding getLocationFloorByBuildingName(@RequestBody WebFEParam webFEParam) {
		return this.locationBuildingService.findByBuildingNameAndFloor(webFEParam.getBuildingName(), RestAPIController.CLASS_NAME);				
	}
	@RequestMapping("/getAllLocationBuilding")	
	@PostMapping
	public List<LocationBuilding> getAllLocationBuilding() {
		return this.locationBuildingService.findAll();				
	}
	@RequestMapping("/getAllLocationFloor")	
	@PostMapping
	public List<LocationFloor> getAllLocationFloor() {
		return this.locationFloorService.findAll();				
	}
	
	@RequestMapping("/getAllPersonCategory")	
	@PostMapping
	public List<PersonCategory> getAllPersonCategory() {
		return this.personCategoryService.findAll(RestAPIController.CLASS_NAME);				
	}
	@RequestMapping("/getLocationFloor")	
	@PostMapping
	public LocationFloor getLocationFloor (@RequestBody WebFEParam webFEParam) {
		return this.locationFloorService.findByFloorName(webFEParam.getFloorName(),webFEParam.getBuildingId(), RestAPIController.CLASS_NAME);				
	}
	@RequestMapping("/getLocationArea")	
	@PostMapping
	public LocationArea getLocationArea(@RequestBody WebFEParam webFEParam) {
		return this.locationAreaService.findByAreaName(webFEParam.getAreaName(),webFEParam.getFloorId(),RestAPIController.CLASS_NAME);			
	}
	@RequestMapping("/getPersonTitle")	
	@PostMapping
	public PersonTitle getPersonTitle (@RequestBody WebFEParam webFEParam) {
		return this.personTitleService.findByTitleName(webFEParam.getPersonTitle(), RestAPIController.CLASS_NAME);
	}
	@RequestMapping("/getAllPersonTitle")	
	@GetMapping
	public List<PersonTitle> getAllPersonTitle() {
		return this.personTitleService.findAll();			
	}
	
	@RequestMapping("/getPersonCategory")	
	@PostMapping
	public PersonCategory getPersonCategory (@RequestBody WebFEParam webFEParam) {
		return this.personCategoryService.findByCategoryName(webFEParam.getCategoryName(), RestAPIController.CLASS_NAME);				
	}
	@RequestMapping("/getPersonCertification")	
	@PostMapping
	public PersonCertification getPersonCertification (@RequestBody WebFEParam webFEParam) {
		return this.personCertificationService.findByCertificationName(webFEParam.getCertificationName(), RestAPIController.CLASS_NAME);				
	}
	
	@RequestMapping("/getAllLocationArea")	
	@GetMapping
	public List<LocationArea> getAllLocationArea() {
		return this.locationAreaService.findAll();				
	}
	@RequestMapping("/getAllPersonCertification")	
	@GetMapping
	public List<PersonCertification> getAllPersonCertification() {
		return this.personCertificationService.findAll();			
	}
		
	@RequestMapping(value="/getLocationFloorList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<LocationFloor> getLocationFloorList(Pageable pageable) {			
		return this.locationFloorService.getLocationFloorList(pageable, RestAPIController.CLASS_NAME);
	}
	@RequestMapping(value="/getLocationAreaList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<LocationArea> getLocationAreaList(Pageable pageable) {			
		return this.locationAreaService.getLocationAreaList(pageable, RestAPIController.CLASS_NAME);
	}
	@RequestMapping(value="/getPersonTitleList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<PersonTitle> getPersonTitleList(Pageable pageable) {			
		return this.personTitleService.getPersonTitleList(pageable,RestAPIController.CLASS_NAME);
	}
	@RequestMapping(value="/getPersonCategoryList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<PersonCategory> getPersonCategoryList(Pageable pageable) {			
		return this.personCategoryService.getPersonCategoryList(pageable,RestAPIController.CLASS_NAME);
	}
	@RequestMapping("/getAllPersonInfo")	
	@GetMapping
	public List<PersonInfo> getAllPersonInfo() {
		return this.personInfoService.findAll(RestAPIController.CLASS_NAME);			
	}
	@RequestMapping("/getPersonInfoList")	
	@GetMapping
	public Page<PersonInfo> getPersonInfoList(Pageable pageable) {
		return this.personInfoService.getPersonInfoList(pageable, RestAPIController.CLASS_NAME);
	}
	@RequestMapping(value="/getPersonCertificationList",method=RequestMethod.GET)
	@GetMapping
	@ResponseBody
	public Page<PersonCertification> getPersonCertificationList(Pageable pageable) {			
		return this.personCertificationService.getPersonCertificationList(pageable,RestAPIController.CLASS_NAME);
	}
	
	
	@RequestMapping("/updateUserInfo")	
	@PostMapping
	public ResultStatus updateUserInfo(@RequestBody UserInfo userInfo) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.userInfoService.update(transactionId, logonUserName, userInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update userInfo:"+userInfo.getUserName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "user name:"+userInfo.getUserName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	@RequestMapping("/deleteUserInfo")	
	@PostMapping
	public ResultStatus deleteUserInfo(@RequestBody WebFEParam webFEParam) {		
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();
		try {
			result = this.userInfoService.delete(transactionId, logonUserName, webFEParam.getUserName());
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete username "+webFEParam.getUserName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete username:"+webFEParam.getUserName());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_USER, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;
	}
	@RequestMapping("/updateRoleInfo")	
	@PostMapping
	public ResultStatus updateRoleInfo(@RequestBody RoleInfo roleInfo) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.roleInfoService.update(transactionId, logonUserName, roleInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update RoleInfo:"+roleInfo.getRoleName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "role name:"+roleInfo.getRoleName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;
	}
	@RequestMapping("/deleteRoleInfo")	
	@PostMapping
	public ResultStatus deleteRoleInfo(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();
		try {
			result = this.roleInfoService.delete(transactionId, logonUserName, webFEParam.getRoleName());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete role name "+webFEParam.getRoleName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete roleName:"+webFEParam.getRoleName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_ROLE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/deleteLocationBuilding")	
	@PostMapping
	public ResultStatus deleteLocationBuilding(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();
		try {
			result = this.locationBuildingService.delete(transactionId,logonUserName,webFEParam.getBuildingName());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete locationbuilding name "+webFEParam.getBuildingName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete locationbuilding:"+webFEParam.getBuildingName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_BUILDING, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/deleteLocationFloor")	
	@PostMapping
	public ResultStatus deleteLocationFloor(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();
		try {
			result = this.locationFloorService.delete(transactionId,logonUserName,webFEParam.getFloorName(),webFEParam.getBuildingId());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete locationfloor name "+webFEParam.getFloorName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete locationfloor:"+webFEParam.getFloorName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_FLOOR, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/deleteLocationArea")	
	@PostMapping
	public ResultStatus deleteLocationArea(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();
		try {
			result = this.locationAreaService.delete(transactionId,logonUserName,webFEParam.getAreaName(),webFEParam.getFloorId());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete locationarea name "+webFEParam.getAreaName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete locationarea:"+webFEParam.getAreaName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_AREA, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	public class PersonContext{
	    private LocationBuilding locationBuilding;
	    private WebFEParam webFEParam;
	    // getters and setters
	}
	@RequestMapping("/updateLocationBuilding")	//amountOfFloor @RequestParam @RequestBody 
	@PostMapping
	public ResultStatus updateLocationBuilding(@RequestBody LocationBuilding locationBuilding) {//(@RequestBody LocationBuilding locationBuilding) {//,@RequestBody WebFEParam webFEParam {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();		
		try {
			result = this.locationBuildingService.update(transactionId, logonUserName,locationBuilding);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update locationBuilding: "+null,ex));//locationBuilding.getBuildingName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "update locationBuildingname: "+null);//locationBuilding.getBuildingName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_BUILDING, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	@RequestMapping("/updateLocationFloor")	
	@PostMapping
	public ResultStatus updateLocationFloor(@RequestBody LocationFloor locationFloor) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();	
		try {
			result = this.locationFloorService.update(transactionId, logonUserName, locationFloor);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update locationFloor: "+locationFloor.getFloorName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "update locationFloor: "+locationFloor.getFloorName());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_FLOOR, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;		
	}	
	@RequestMapping("/updateLocationArea")	
	@PostMapping
	public ResultStatus updateLocationArea(@RequestBody LocationArea locationArea) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();	
		try {
			result = this.locationAreaService.update(transactionId, logonUserName, locationArea);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update locationArea: "+locationArea.getAreaName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "update locationArea: "+locationArea.getAreaName());
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_AREA, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;		
	}
	@RequestMapping("/restartXFaceServer")	
	@PostMapping
	public ResultStatus restartXFaceServer() {
		this.xfaceServerService.restart();
		return new ResultStatus();
	}
	@RequestMapping("/updatePersonInfo")	
	@PostMapping
	public ResultStatus updatePersonInfo(@RequestPart("personInfo") String personInfo, @RequestPart(value="personPhoto", required=false) MultipartFile personPhoto) {		
		PersonInfo objectPersonInfo = null;
		ObjectMapper mapper = new ObjectMapper();		
 		ResultStatus result = null;
 		String transactionId = LogUtil.getWebSessionId();
 		String logonUserName =  LogUtil.getCurrentLogOnUserName(); 		
 		try {			
 			objectPersonInfo = mapper.readValue(personInfo, PersonInfo.class);
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
	public ResultStatus deletePersonInfo(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();
		try {
			result = this.personInfoService.delete(transactionId,logonUserName,webFEParam.getPersonCertificationNo());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete personInfo code:"+webFEParam.getPersonCertificationNo(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete personInfo code:"+webFEParam.getPersonCertificationNo());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_INFO, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/deletePersonTitle")	
	@PostMapping
	public ResultStatus deletePersonTitle(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();
		try {
			result =this.personTitleService.delete(transactionId,logonUserName, webFEParam.getPersonTitle());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete personTitle name:"+webFEParam.getPersonTitle(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete personTitle name:"+webFEParam.getPersonTitle());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/updatePersonTitle")	
	@PostMapping
	public ResultStatus updatePersonTitle(@RequestBody PersonTitle personTitle) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.personTitleService.update(transactionId, logonUserName, personTitle);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update personTitle:"+personTitle.getTitleName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "personTitle name:"+personTitle.getTitleName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_TITLE, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	@RequestMapping("/deletePersonCategory")	
	@PostMapping
	public ResultStatus deletePersonCategory(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();
		try {
			result =this.personCategoryService.delete(transactionId,logonUserName, webFEParam.getCategoryName());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete PersonCategory name:"+webFEParam.getCategoryName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete PersonCategory name:"+webFEParam.getCategoryName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CATEGORY, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/updatePersonCategory")	
	@PostMapping
	public ResultStatus updatePersonCategory(@RequestBody PersonCategory personCategory) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
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
	@RequestMapping("/deletePersonCertification")	
	@PostMapping
	public ResultStatus deletePersonCertification(@RequestBody WebFEParam webFEParam) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName = LogUtil.getCurrentLogOnUserName();
		try {
			result =this.personCertificationService.delete(transactionId,logonUserName, webFEParam.getCertificationName());
		}catch (Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete PersonCertification name:"+webFEParam.getCertificationName(), ex));			
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "delete PersonCertification name:"+webFEParam.getCertificationName());			
		}
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATION, SystemAudit.MOD_SUB_ALL
				, result.toString(), SystemAudit.RES_FAIL, logonUserName);
		}
		return result;		
	}
	@RequestMapping("/updatePersonCertification")	
	@PostMapping
	public ResultStatus updatePersonCertification(@RequestBody PersonCertification personCertification) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.personCertificationService.update(transactionId, logonUserName, personCertification);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while update PersonCertification:"+personCertification.getCertificationName(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "PersonCertification name:"+personCertification.getCertificationName());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATION, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;				
	}
	//temp
	@RequestMapping("/registerCustomer")	
	@PostMapping
	public ResultStatus registerCustomer(@RequestBody CustomerRegister customerInfo) {
		ResultStatus result = null;
		String transactionId = LogUtil.getWebSessionId();
		String logonUserName =  LogUtil.getCurrentLogOnUserName();		
		try {			
			result = this.personInfoService.register(transactionId, logonUserName, customerInfo);
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while register customer certificateId:"+customerInfo.getCertificateId(), ex));
			result = new ResultStatus(ResultStatus.DB_UPDATE_ERROR_CODE, "PersonInfo certificateId:"+customerInfo.getCertificateId());
		}	
		if (!result.getStatusCode().equals(ResultStatus.SUCCESS_CODE)) {
			//not success then log to audit
			this.sysAuditService.createAudit(transactionId, SystemAudit.MOD_PERSON_CERTIFICATION, SystemAudit.MOD_SUB_ALL
					, result.toString(), SystemAudit.RES_FAIL , logonUserName);
		}
		return result;			
	}

	@RequestMapping("/getPersonSummaryInfoList")	
	@GetMapping
	public List<PersonSummaryInfo> getPersonSummaryInfoList() {
		return this.personInfoService.findPersonSummaryInfo();				
	}
	////////////
	@RequestMapping("/getCameraInfo")	
	@PatchMapping
	public List<String> getCameraInfo() {
		
		List<String> list = new ArrayList<>();
		   	
		list.add("192.168.2.200");
		list.add("9900");
		list.add("xpandapp");
		list.add("Xpand@456");
		list.add("02117790000000000101#ab8df621bf3f4d91b61ce8cf5100c01a");
		
		return list;
	}	
}
