package com.xpand.xface.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xpand.xface.service.IPCGroupService;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.service.UserInfoService;

@Controller
public class UserController {
	public static String CLASS_NAME=UserController.class.getName();
	@Autowired
	IPCGroupService ipcGroupService;
	@Autowired
	PermissionListService permissionListService;
	
	@Autowired
	UserInfoService userInfoService;
	
    @RequestMapping("/user")    
    public String index() {    	
    	return "user/index";
    }
    
    @RequestMapping("/user/create")
    public String create(Model model) {    	
    	model.addAttribute("permissionList", this.permissionListService.findAll());
    	model.addAttribute("ipcGroup", this.ipcGroupService.findAll());
        return "user/create";
    }    
    @RequestMapping("/user/edit/{userName}")
    public String edit(Model model, @PathVariable String userName) {    	
    	model.addAttribute("userInfo", this.userInfoService.findByUserName(userName, UserController.CLASS_NAME));
    	model.addAttribute("permissionList", this.permissionListService.findAll());
    	model.addAttribute("ipcGroup", this.ipcGroupService.findAll());
        return "user/edit";
    }    
}
