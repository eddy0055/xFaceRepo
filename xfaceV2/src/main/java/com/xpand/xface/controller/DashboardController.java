

package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.util.ConstUtil;

@Controller
@RequestMapping(value= {"/dashboard","/"})
public class DashboardController {	   
	@Autowired
	ApplicationCfgService appCfgService;
	
    @RequestMapping(value= {"/landingPage","/"})
    public String landingPage(HttpServletRequest request, Model model) {
    	String transactionId = request.getSession().getId();    	
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_DASHBOARD);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_DASHBOARD_LANDING_PAGE);    	
    	model.addAttribute("webSockAccept", ConstUtil.MQ_WELCOME_MSG_FROM_SERVER);
    	model.addAttribute("webSockGoodbye", ConstUtil.MQ_GOODBYE_MSG_FROM_SERVER);
    	model.addAttribute("webSockEndPoint", "/xFace"+ConstUtil.WEBSOCKET_ENDPOINT);		
    	model.addAttribute("webSockSTU", ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSockUTS", ConstUtil.MQ_TOPIC_USER_TO_SERVER+"user/"+request.getUserPrincipal().getName());
    	model.addAttribute("webSocketModule", ConstUtil.WEBSOCKET_MODULE_LANDING_PAGE);
    	model.addAttribute("timerCheckWebSocket", this.appCfgService.findByAppKey(transactionId, ApplicationCfg.KEY_TIMER_CHECK_WEBSOCKET_CONNECTION).getAppValue1());
        return "dashboard/LandingPage";
    }
    @RequestMapping("/dailyStatistics")
    public String dailyStatistics(HttpServletRequest request, Model model) throws Exception{    	
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_DASHBOARD);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_DASHBOARD_DAILY_STATISTICS);
        return "dashboard/DailyStatistics";
    }
    @RequestMapping("/dailyStatisticsByTime")
    public String dailyStatisticsByTime(HttpServletRequest request, Model model) throws Exception{    	
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_DASHBOARD);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_DASHBOARD_DAILY_STATISTICS_BY_TIME);
    	model.addAttribute("RNPBT", ConstUtil.REPORT_NO_PASSENGER_BY_TIME_CODE);
    	model.addAttribute("RNPBTD", ConstUtil.REPORT_NO_PASSENGER_BY_TIME_DESC);
    	model.addAttribute("RNPGBT", ConstUtil.REPORT_NO_PASSENGER_GATE_BY_TIME_CODE);
    	model.addAttribute("RNPGBTD", ConstUtil.REPORT_NO_PASSENGER_GATE_BY_TIME_DESC);
    	model.addAttribute("RNFBT", ConstUtil.REPORT_NO_FACE_BY_TIME_CODE);
    	model.addAttribute("RNFBTD", ConstUtil.REPORT_NO_FACE_BY_TIME_DESC);    	
    	model.addAttribute("RNFGBT", ConstUtil.REPORT_NO_FACE_GATE_BY_TIME_CODE);
    	model.addAttribute("RNFGBTD", ConstUtil.REPORT_NO_FACE_GATE_BY_TIME_DESC);
    	model.addAttribute("RNPBBT", ConstUtil.REPORT_NO_PASSENGER_BOAT_BY_TIME_CODE);
    	model.addAttribute("RNPBBTD", ConstUtil.REPORT_NO_PASSENGER_BOAT_BY_TIME_DESC);    	    	
        return "dashboard/DailyStatisticsByTime";
    }         
    @RequestMapping("/gateDMYStatistics")
    public String gateDMYStatistics(HttpServletRequest request, Model model) {
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_DASHBOARD);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_DASHBOARD_GATE_STATISTICS);
    	Logger.info(this, request.getSession().getId()+"|getDMYStatistics fight");
        return "dashboard/GateDMYStatistics";
    }
                
}
