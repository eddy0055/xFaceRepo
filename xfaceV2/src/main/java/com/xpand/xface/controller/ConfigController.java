package com.xpand.xface.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xpand.xface.util.ConstUtil;



@Controller
@RequestMapping("/cfg")
public class ConfigController {
	@RequestMapping("/appCfg")    
    public String appCfg(HttpServletRequest request, Model model) {
		model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_CFG);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_CFG_APP);
    	return "cfg/ApplicationCfg";
    }
	@RequestMapping("/hwIPC")    
    public String hwIPC(HttpServletRequest request, Model model) {
		model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_CFG);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_CFG_IPC);
    	return "cfg/HWIPC";
    }
    @RequestMapping("/gateInfo")    
    public String gateInfo(HttpServletRequest request, Model model) {
    	model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_CFG);
    	model.addAttribute("activeSubMenu", ConstUtil.MAIN_MENU_CFG_GATE_INFO);
    	return "cfg/GateInfo";
    }
    
    @RequestMapping("/boatInfo")
    public String boatInfo(HttpServletRequest request,Model model) {
    	model.addAttribute("activeMenu",ConstUtil.MAIN_MENU_CFG);
    	model.addAttribute("activeSubMenu",ConstUtil.MAIN_MENU_CFG_BOAT_INFO);
    	return "cfg/BoatInfo";
    }
	
	@RequestMapping("/personTitle")
	public String personTitle(HttpServletRequest request,Model model) {
		model.addAttribute("activeMenu",ConstUtil.MAIN_MENU_CFG);
		model.addAttribute("activeSubMenu",ConstUtil.MAIN_MENU_PERSON_TITLE);
		return "cfg/PersonTitle";
	}
	
	@RequestMapping("/personCertificate")
	public String personCertificate(HttpServletRequest request,Model model) {
		model.addAttribute("activeMenu",ConstUtil.MAIN_MENU_CFG);
		model.addAttribute("activeSubMenu",ConstUtil.MAIN_MENU_PERSON_CER);
		return "cfg/PersonCertificate";
	}
	
	@RequestMapping("/personCatgory")
	public String personCategory(HttpServletRequest request,Model model) {
		model.addAttribute("activeMenu",ConstUtil.MAIN_MENU_CFG);
		model.addAttribute("activeSubmenu", ConstUtil.MAIN_MENU_PERSON_CAT);
		return "cfg/PersonCategory";
	}
	
	@RequestMapping("/personNationality")
	public String personNationality(HttpServletRequest request,Model model) {
		model.addAttribute("activeMenu", ConstUtil.MAIN_MENU_CFG);
		model.addAttribute("activeSubmenu",ConstUtil.MAIN_MENU_PERSON_NAT);
		return "cfg/PersonNationality";
	}   
    @RequestMapping("/htmlPage")
    public String htmlPage(HttpServletRequest request,Model model) {
    	model.addAttribute("activeMenu",ConstUtil.MAIN_MENU_CFG);
    	model.addAttribute("activeSubmenu",ConstUtil.MAIN_MENU_HTML_PAGE);
    	return "cfg/htmlPage";
    }
    @RequestMapping("/locationMap")
    public String locationMap(HttpServletRequest request,Model model) {
    	model.addAttribute("activeMenu",ConstUtil.MAIN_MENU_CFG);
    	model.addAttribute("activeSubMenu",ConstUtil.MAIN_MENU_LOCATION_MAP);
    	return "cfg/locationMap";
    }
    
}
