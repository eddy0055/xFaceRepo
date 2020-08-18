

package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LogonController {	   
    @RequestMapping("/auth/login")
    public String login() {
        return "auth/Login";
    }
    
    @RequestMapping("/auth/forceChangePwd")
    public String forceChangePwd() {
        return "auth/ForceChangePwd";
    }
    @RequestMapping("/auth/changePwd")
    public String changePwd() {
        return "auth/ChangePwd";
    }
    
    @RequestMapping(value="/auth/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response) {    	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
	}
        
    @RequestMapping("/error/invalidsession")
    public String invalidSession(HttpServletRequest request, HttpServletResponse response) {
    	request.getSession().invalidate();
        return "error/InvalidSession";
    }
    @RequestMapping("/error/403")
    public String error403(HttpServletRequest request, HttpServletResponse response) {
        return "error/403";
    }  
    @RequestMapping("/error/404")
    public String error404(HttpServletRequest request, HttpServletResponse response) {
        return "error/404";
    }
}
