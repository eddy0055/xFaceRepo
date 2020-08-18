package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xpand.xface.util.ConstUtil;

@Controller
@RequestMapping("/user")
public class UserManageController {	
	public static String CLASS_NAME=UserManageController.class.getName();
	@RequestMapping("/roleInfo")    
    public String roleInfo(HttpServletRequest request, Model model) {
		model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_USER_MNG);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_USER_MNG_ROLE);
    	return "user/RoleInfo";
    }
	@RequestMapping("/userManage")    
    public String userManage(HttpServletRequest request, Model model) {    	
		model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_USER_MNG);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_USER_MNG_USER);
    	return "user/userManage";
    }

}
