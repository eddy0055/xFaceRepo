package com.xpand.xface.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.GlobalVarService;

public class XFaceInterceptor implements HandlerInterceptor{
	@Autowired
	ApplicationCfgService appCfgService;
	@Autowired
	GlobalVarService globalVarService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		String sessionId = request.getSession().getId();
		//this.globalVarService.getWebSocketHolderList().entrySet();		
		if (modelAndView==null) {
			//Logger.info(this, LogUtil.getLogInfo(sessionId, "model and view is null may be come from ajax request"));			
		}else if (request.getUserPrincipal()==null) {
			modelAndView.addObject("userName", "notLogOnUser");
		}else {
			ApplicationCfg appCfg = this.appCfgService.findByAppKey(sessionId, ApplicationCfg.KEY_ALERT_TIMER);
			modelAndView.addObject("tableStep", this.globalVarService.getGridJumpPage());		
			modelAndView.addObject("alertTimer", appCfg.getAppValue1()); //1 is timer
			modelAndView.addObject("alertDelay", appCfg.getAppValue2()); //2 is delay
		}				
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
