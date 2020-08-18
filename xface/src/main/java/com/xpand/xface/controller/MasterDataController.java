package com.xpand.xface.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MasterDataController {
	@RequestMapping("/area")
    public String area() {
        return "area/index";
    }
	@RequestMapping("/Register")
    public String Register() {
        return "Register/index";
    }
	@RequestMapping("/DeleteRegister")
    public String DeleteRegister() {
        return "DeleteRegister/index";
    }
	
}