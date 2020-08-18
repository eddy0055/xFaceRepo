package com.xpand.xface.config;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.entity.HtmlPageInfo;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.service.HtmlPageInfoService;
import com.xpand.xface.service.UserInfoService;
import com.xpand.xface.util.LogUtil;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	HtmlPageInfoService htmlPageInfoService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);			
		UserInfo user = this.userInfoService.findByUserName(request.getSession().getId(), authentication.getName());
		if (user.getPasswordExpire().getTime() < (new Date().getTime())) {
			String redirectURL = this.htmlPageInfoService.findByCode(request.getSession().getId(), HtmlPageInfo.HTML_PAGE_FORCE_CHANGE_PWD_CODE).getPageURL();
			Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "user:"+authentication.getName()+" authen sucess but password expire redirect to "+redirectURL));
			response.sendRedirect(redirectURL);
		}else {
			RoleInfo roleInfo = user.getRoleInfo();
			HtmlPageInfo htmlPage = roleInfo.getHtmlPageInfo();			
			Logger.info(this, LogUtil.getLogInfo(request.getSession().getId(), "user:"+authentication.getName()+" authen sucess and redirect to "+htmlPage.getPageDesc()));		
			response.sendRedirect(htmlPage.getPageURL());
		}			
	}

}
