package com.xpand.xface.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IPCController {

    @RequestMapping("/ipc")    
    public String index() {    	
    	return "ipc/index";
    }
   	
}
