

package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.EquipmentDirection;
import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.entity.PersonNationality;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.BoatService;
import com.xpand.xface.service.EquipmentDirectionService;
import com.xpand.xface.service.HWGateInfoService;
import com.xpand.xface.service.HWIPCService;
import com.xpand.xface.service.PersonNationalityService;
import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.StringUtil;

@Controller
@RequestMapping("/person")
public class PersonController {
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	HWIPCService hwIPCService;
	@Autowired
	HWGateInfoService hwGateInfoService;	
	@Autowired
	PersonNationalityService personNationalityService;
	@Autowired
	EquipmentDirectionService equipmentDirectionService; 
	@Autowired
	BoatService boatService; 
    @RequestMapping(value= {"/alarmMonitor","/"})
    public String alarmMonitor(HttpServletRequest request, Model model) {
    	String transactionId = request.getSession().getId();
    	model.addAttribute("noOfFace", this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_FACE_ALARM_MONITOR_PAGE_SIZE).getAppValue1());
    	model.addAttribute("timerCheckWebSocket", this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_TIMER_CHECK_WEBSOCKET_CONNECTION).getAppValue1());
    	model.addAttribute("webSockAccept", ConstUtil.MQ_WELCOME_MSG_FROM_SERVER);
    	model.addAttribute("webSockGoodbye", ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
    	model.addAttribute("webSockEndPoint", "/xFace"+ConstUtil.WEBSOCKET_ENDPOINT);		
    	model.addAttribute("webSockSTU", ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSockUTS", ConstUtil.MQ_TOPIC_USER_TO_SERVER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSocketModule", ConstUtil.WEBSOCKET_MODULE_ALARM_MONITOR);
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_PERSON);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_PERSON_ALARM_MONITOR);
    	
        return "person/AlarmMonitor";
    }
    
    //mnuPSAMIDV
    @RequestMapping(value= {"/alarmMonitorIDV","/"})
    public String alarmMonitorIDV(HttpServletRequest request, Model model) {
    	String transactionId = request.getSession().getId();
    	model.addAttribute("noOfFaceIDV", this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_FACE_ALARM_MONITOR_PAGE_SIZE_IDV).getAppValue1());
    	model.addAttribute("timerCheckWebSocket", this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_TIMER_CHECK_WEBSOCKET_CONNECTION).getAppValue1());
    	model.addAttribute("webSockAccept", ConstUtil.MQ_WELCOME_MSG_FROM_SERVER);
    	model.addAttribute("webSockGoodbye", ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
    	model.addAttribute("webSockEndPoint", "/xFace"+ConstUtil.WEBSOCKET_ENDPOINT);		
    	model.addAttribute("webSockSTU", ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSockUTS", ConstUtil.MQ_TOPIC_USER_TO_SERVER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSocketModule", ConstUtil.WEBSOCKET_MODULE_ALARM_MONITOR);
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_PERSON);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_PERSON_ALARM_MONITOR_IDV);
    	
        return "person/AlarmMonitorIDV";
    }
    
    
    @RequestMapping("/personRegister")
    public String personRegister(HttpServletRequest request, Model model) {
    	String nationalityName = request.getParameter("nation");
    	String registerDate = request.getParameter("date");
    	if (!StringUtil.checkNull(nationalityName)) {
    		PersonNationality nationality = this.personNationalityService.findByNationalityCode(request.getSession().getId(), nationalityName);
    		if (nationality==null) {
    			Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "nationality name:"+nationalityName+" not found"));
    		}else{
    			model.addAttribute("nationalityCodeList", nationality.getNationalityCode());
    		}        	
    	}    	
    	if (!StringUtil.checkNull(registerDate)) {
    		model.addAttribute("registerDate", registerDate);
    	}    	
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_PERSON);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_PERSON_REGISTER);
        return "person/personRegister";
    }      
    
    //possible parameter
    //cond = match, unmatch
    //startdate, enddate
    //direct = in, out
    //gate,boat
    @RequestMapping("/alarmHistory")
    public String alarmHistory(HttpServletRequest request, Model model) {    	
    	String condition = request.getParameter("cond");
    	String startDate = request.getParameter("startdate");
    	String endDate = request.getParameter("enddate");    	
    	String gateName = request.getParameter("gate"); 
    	String direction = request.getParameter("direct");
    	String boatCodeList = request.getParameter("boat");    
    	EquipmentDirection eqDirection = this.equipmentDirectionService.findByDirectionCode(request.getSession().getId(), EquipmentDirection.DIRECTION_IN);    	
    	model.addAttribute("directionINCode", eqDirection.getDirectionCode());
    	model.addAttribute("directionINDesc", eqDirection.getDirectionDesc());
    	eqDirection = this.equipmentDirectionService.findByDirectionCode(request.getSession().getId(), EquipmentDirection.DIRECTION_OUT);    	
    	model.addAttribute("directionOUTCode", eqDirection.getDirectionCode());
    	model.addAttribute("directionOUTDesc", eqDirection.getDirectionDesc());    	    
    	model.addAttribute("noOfTimePortion", this.appCfgService.findByAppKey(request.getSession().getId(), ApplicationCfg.KEY_FACE_ALARM_HISTORY_TIME_PORTION_SIZE).getAppValue1());    	    
    	if (!StringUtil.checkNull(gateName)) {
    		HWGateInfo gateInfo = this.hwGateInfoService.findByGateName(request.getSession().getId(), gateName);
    		if (gateInfo==null) {
    			Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "gate name:"+gateName+" not found"));
    		}else {
        		model.addAttribute("gateInfoCodeList", gateInfo.getGateCode()+"");        		        	    			
    		}    		
    	}
    	if (!StringUtil.checkNull(direction)) {
    		eqDirection = this.equipmentDirectionService.findByDirectionDesc(request.getSession().getId(), direction);
    		if (eqDirection==null) {
    			Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "direction desc:"+direction+" not found"));
    		}else {
    			model.addAttribute("direction", eqDirection.getDirectionCode());
    		}			
    	}
    	if (!StringUtil.checkNull(condition)) {
    		model.addAttribute("matchCondition", condition);
    	}    	
    	if (!StringUtil.checkNull(startDate)) {
	    	model.addAttribute("startDate", startDate);	    	
    	}
    	if (!StringUtil.checkNull(endDate)) {
	    	model.addAttribute("endDate", endDate);	    	
    	}    	
    	if (!StringUtil.checkNull(boatCodeList)) {    		
    		Boat boat = this.boatService.findByShortName(request.getSession().getId(), boatCodeList);
    		if (boat!=null) {
    			model.addAttribute("boatCodeList", boat.getBoatCode());
    		}    		
    	}
    	model.addAttribute("matchConditionValue", ConstUtil.ALARM_HISTORY_MATCH_CONDITION);
    	model.addAttribute("unMatchConditionValue", ConstUtil.ALARM_HISTORY_UNMATCH_CONDITION);    	
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_PERSON);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_PERSON_ALARM_HISTORY);
        return "person/alarmHistory";
    }
    
    @RequestMapping("/personTrace")
    public String personTrace(HttpServletRequest request, Model model) {    	
    	model.addAttribute("timerCheckWebSocket", this.appCfgService.findByAppKey(request.getSession().getId(), ApplicationCfg.KEY_TIMER_CHECK_WEBSOCKET_CONNECTION).getAppValue1());
    	model.addAttribute("webSockAccept", ConstUtil.MQ_WELCOME_MSG_FROM_SERVER);
    	model.addAttribute("webSockGoodbye", ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
    	model.addAttribute("webSockEndPoint", "/xFace"+ConstUtil.WEBSOCKET_ENDPOINT);		
    	model.addAttribute("webSockSTU", ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSockUTS", ConstUtil.MQ_TOPIC_USER_TO_SERVER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSocketModule", ConstUtil.WEBSOCKET_MODULE_PERSON_TRACE);
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_PERSON);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_PERSON_PERSON_TRACE);
        return "person/personTrace";
    }
                
}
