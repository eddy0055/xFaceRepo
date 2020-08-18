package com.xpand.xface.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RoleController {

    @RequestMapping("/role")    
    public String index() {    	
    	return "role/index";
    }
}
