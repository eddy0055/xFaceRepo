

package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcabi.log.Logger;

@Controller
public class DashboardController {	
    @RequestMapping("/")    
    public String index() {    	
        return "dashboard/index";
    }
    
    @RequestMapping("/auth/login")
    public String login() {
        return "auth/login";
    }
    
    @RequestMapping("/reception")
    public String reception() {    	
        return "reception/index";
    }
    
    @RequestMapping("/face")
    public String face(Model model) {
    	model.addAttribute("vcnIp", "192.168.2.200");
    	model.addAttribute("vcnPort", "9900");
    	model.addAttribute("vcnUserName", "xpandapp");
    	model.addAttribute("vcnPassword", "Xpand@456");
    	model.addAttribute("cameraId", "02117790000000000101#ab8df621bf3f4d91b61ce8cf5100c01a");
        return "face/index";
    }    
    
    @RequestMapping("/test/index")
    public String testPage() {
        return "test/index";
    }
    
    // Added to test 500 page
    @RequestMapping(path = "/tigger-error", produces = MediaType.APPLICATION_JSON_VALUE)
    public void error500() throws Exception {
        throw new Exception();
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
    public String invalidSession() {
        return "error/invalidsession";
    }    
}
