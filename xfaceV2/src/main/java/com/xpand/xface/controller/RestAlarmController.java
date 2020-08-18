package com.xpand.xface.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.log.Logger;
import com.xpand.xface.bean.PassengerBoatActivity;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.entity.HWAlarmHist;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@RestController
@RequestMapping("/rest/alarm")
public class RestAlarmController {	
	public static String CLASS_NAME=RestAlarmController.class.getName();
	@Autowired
	GlobalVarService globalVarService;
	
	@RequestMapping(value="/recvVCMAlarm", produces= {"*/*"})
	@ResponseBody
	public String recvVCMAlarm(@RequestBody String content) {
		Logger.debug(this, LogUtil.getLogDebug(LogUtil.getWebSessionId(), "receive alarm from VCM server"));
		this.globalVarService.pushAlarm(HWAlarmHist.HW_VCM_ALARM+content);
		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
	}	
	@RequestMapping(value="/recvVCNAlarm", produces= {"*/*"})
	@ResponseBody
	public String recvVCNAlarm(@RequestBody String content) {
		Logger.debug(this, LogUtil.getLogDebug(LogUtil.getWebSessionId(), "receive alarm from VCN server"));
		this.globalVarService.pushAlarm(HWAlarmHist.HW_VCN_ALARM+content);
		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
	}
	@RequestMapping("/boatCheckInOut")
	@PostMapping
	public ResultStatus boatCheckInOut(HttpServletRequest request, @RequestBody String boatActivity) {
		//direction (arrival, departure), timestamp (yyyymmddhh24miss), gate id (pad id), boat driver card id, system id (fixed to ZKBioSecurity)	
//		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";		
		PassengerBoatActivity objectActivity = null;
		ObjectMapper mapper = new ObjectMapper();		 		
 		String transactionId = request.getSession().getId(); 		
 		ResultStatus result = new ResultStatus();
 		Logger.debug(this, LogUtil.getLogDebug(transactionId, "receive boatCheckInOut"));
 		try {			
 			objectActivity = mapper.readValue(boatActivity, PassengerBoatActivity.class); 			
			if (StringUtil.checkNull(objectActivity.getSystemId())) {
				result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "System Id");
			}else {
				objectActivity.setActivityType(PassengerBoatActivity.ACTIVITY_BOAT_CHECK_IN_OUT);
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "convert string to json success:"+objectActivity.toString()));
				this.globalVarService.pushGateActivity(objectActivity);
			}			
 		}catch (JsonMappingException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+boatActivity, ex)); 		
 			result.setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}catch (JsonParseException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+boatActivity, ex));
 			result.setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}catch (IOException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+boatActivity, ex)); 			
 			result.setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}catch(Exception ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+boatActivity, ex)); 			 			
 			result.setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, null);
 		}
		return result;
	}
	@RequestMapping("/passengerPassGate")
	@PostMapping
	public ResultStatus passengerPassGate(HttpServletRequest request, @RequestBody String passengerActivity) {
		//direction (in, out), timestamp (yyyymmddhh24miss), gate id (pad id), boat driver card id, system id (fixed to ZKBioSecurity)
		//,passenger live photo (base64), passenger certificate id (thaiid, passort id)		
//		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
		PassengerBoatActivity objectActivity = null;
		ObjectMapper mapper = new ObjectMapper();		 		
 		String transactionId = request.getSession().getId();
 		ResultStatus result = new ResultStatus();
		Logger.debug(this, LogUtil.getLogDebug(transactionId, "receive passengerPassGate")); 		
 		try {			
 			objectActivity = mapper.readValue(passengerActivity, PassengerBoatActivity.class); 			
			if (StringUtil.checkNull(objectActivity.getSystemId())) {
				result.setStatusCode(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE, "System Id");				
			}else {
				objectActivity.setActivityType(PassengerBoatActivity.ACTIVITY_PASSENGER_PASS_GATE);
				this.globalVarService.pushGateActivity(objectActivity);
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "convert string to json success:"+objectActivity.toString()));
			}
 		}catch (JsonMappingException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+passengerActivity, ex)); 		
 			result.setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}catch (JsonParseException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+passengerActivity, ex));
 			result.setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}catch (IOException ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+passengerActivity, ex)); 			
 			result.setStatusCode(ResultStatus.JSON_PARSER_ERROR_CODE, null);
 		}catch(Exception ex) {
 			Logger.error(this, LogUtil.getLogError(transactionId, "error while rev boat checkin-out parser boatActivity:"+passengerActivity, ex)); 			 			
 			result.setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, null);
 		}
		return result;
	}		
}
